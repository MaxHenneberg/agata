package agata.lcl.flows.delivery;

import agata.bol.dataholder.ItemRow;
import agata.bol.states.BillOfLadingState;
import agata.lcl.enums.TrackingStatus;
import agata.lcl.flows.FlowTestBase;
import agata.lcl.states.assignment.AssignmentState;
import agata.lcl.states.delivery.PackageDeliveryState;
import agata.lcl.states.tracking.ShippingTrackingState;
import agata.lcl.states.tracking.TrackingState;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.TransactionState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.testing.node.StartedMockNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class PackageDeliveryTest extends FlowTestBase {
    private StartedMockNode lclCompany;
    private StartedMockNode buyer;
    private StartedMockNode supplier;
    private StartedMockNode shippingLine;

    @Before
    public void setupDeliveryTest() {
        lclCompany = network.createPartyNode(new CordaX500Name("LCL Company A", "Rotterdam", "NL"));
        buyer = network.createPartyNode(new CordaX500Name("Buyer", "Rotterdam", "NL"));
        supplier = network.createPartyNode(new CordaX500Name("Supplier", "New York", "US"));
        shippingLine = network.createPartyNode(new CordaX500Name("Shipper", "New York", "US"));
        network.runNetwork();
    }

    @Test
    public void testPackageDeliveryFlows() throws ExecutionException, InterruptedException {

        Party lclCompanyParty = getParty(this.lclCompany);

        // Create fake house bill of lading
        String goodsId = "123";
        final UniqueIdentifier assignmentId = createAssignmentState(lclCompany, supplier, buyer, this.departureAddress, this.arrivalAddress, goodsId);
        final UniqueIdentifier trackingStateId = this.resolveStateId(AssignmentState.class, assignmentId, lclCompany, Vault.StateStatus.UNCONSUMED).getTrackingStateId();

        final UniqueIdentifier containerStateId = createContainerState(lclCompany, shippingLine, Collections.singletonList(trackingStateId));
        UniqueIdentifier houseBillOfLadingId = this.createHouseBol(goodsId, assignmentId, containerStateId, lclCompany, supplier);

        // PROPOSE
        ProposeDeliveryFlow.Initiator proposeFlow = new ProposeDeliveryFlow.Initiator(lclCompanyParty, houseBillOfLadingId);
        CordaFuture<UniqueIdentifier> future1 = this.buyer.startFlow(proposeFlow);
        network.runNetwork();
        UniqueIdentifier deliveryProposalId = future1.get();

        // SET GOODS
        List<StateAndRef<BillOfLadingState>> bolResults = this.lclCompany.getServices().getVaultService().queryBy(
                BillOfLadingState.class,
                new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(houseBillOfLadingId), Vault.StateStatus.UNCONSUMED, null)).getStates();
        assert (bolResults.size() == 1);
        BillOfLadingState bol = bolResults.get(0).getState().getData();
        // Use the goods mentioned in the house bill of lading as delivered goods (only this is a valid delivery request)
        String invoiceId = "123456ab";
        List<ItemRow> deliveredGoods = bol.getGoodsList();
        SetGoodsFlow.Initiator setGoodsFlow = new SetGoodsFlow.Initiator(deliveryProposalId, deliveredGoods, invoiceId);
        CordaFuture<SignedTransaction> future2 = this.lclCompany.startFlow(setGoodsFlow);
        network.runNetwork();
        future2.get();

        // ACCEPT
        AcceptDeliveryFlow.Initiator acceptFlow = new AcceptDeliveryFlow.Initiator(deliveryProposalId, trackingStateId);
        CordaFuture<SignedTransaction> future3 = this.buyer.startFlow(acceptFlow);
        network.runNetwork();
        SignedTransaction tx = future3.get();

        // Check that transaction is recorded in both vaults
        for (StartedMockNode node : Arrays.asList(this.lclCompany, this.buyer)) {
            SignedTransaction recordedTx = node.getServices().getValidatedTransactions().getTransaction(tx.getId());
            List<TransactionState<ContractState>> txOutputs = recordedTx.getTx().getOutputs();
            assert (txOutputs.size() == 1);
            assert (txOutputs.get(0).getData() instanceof PackageDeliveryState);

            PackageDeliveryState recordedState = (PackageDeliveryState) txOutputs.get(0).getData();
            assertEquals(lclCompanyParty, recordedState.getLclCompany());
            assertEquals(getParty(this.buyer), recordedState.getArrivalParty());
            assertEquals(deliveredGoods, recordedState.getDeliveredGoods());
            assertEquals(bol.getLinearId(), recordedState.getHouseBolId());

            TrackingState state = this.resolveStateId(ShippingTrackingState.class, trackingStateId, node, Vault.StateStatus.UNCONSUMED);
            Assert.assertEquals(TrackingStatus.GoodsDelivered, state.getStatus());
        }

    }

}

package agata.lcl.flows.deconsolidation;

import agata.bol.dataholder.Price;
import agata.lcl.enums.TrackingStatus;
import agata.lcl.flows.FlowTestBase;
import agata.lcl.states.assignment.AssignmentState;
import agata.lcl.states.deconsolidation.DeconsolidationState;
import agata.lcl.states.tracking.ShippingTrackingState;
import agata.lcl.states.tracking.TrackingState;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.TransactionState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.transactions.SignedTransaction;
import net.corda.testing.node.StartedMockNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class DeconsolidationTest extends FlowTestBase {

    private StartedMockNode lclCompany;
    private StartedMockNode shippingLine;
    private StartedMockNode supplier;
    private StartedMockNode buyer;

    @Before
    public void setupDeconsolidationTest() {
        lclCompany = network.createPartyNode(new CordaX500Name("LCL Company", "New York", "US"));
        shippingLine = network.createPartyNode(new CordaX500Name("Shipping Line", "New York", "US"));
        supplier = network.createPartyNode(new CordaX500Name("Supplier", "New York", "US"));
        buyer = network.createPartyNode(new CordaX500Name("Buyer", "Rotterdam", "NL"));
        network.runNetwork();
    }

    @Test
    public void testContainerDeconsolidation() throws ExecutionException, InterruptedException {
        Party lclCompanyParty = getParty(this.lclCompany);
        Party shippingLineParty = getParty(this.shippingLine);

        // Create fake house bill of lading
        final UniqueIdentifier assignmentId = createAssignmentState(lclCompany, supplier, buyer, this.departureAddress, this.arrivalAddress, "1234");
        final UniqueIdentifier trackingStateId = this.resolveStateId(AssignmentState.class, assignmentId, lclCompany, Vault.StateStatus.UNCONSUMED).getTrackingStateId();

        List<UniqueIdentifier> trackingStateIds = Collections.singletonList(trackingStateId);
        final UniqueIdentifier containerStateId = createContainerState(lclCompany, shippingLine, trackingStateIds);
        UniqueIdentifier houseBillOfLadingId = this.createHouseBol("1234", assignmentId, containerStateId, lclCompany, supplier);

        final UniqueIdentifier masterBolId = this.executeShiploading(shippingLine, lclCompany, trackingStateIds, containerStateId, Collections.singletonList(houseBillOfLadingId));

        // PROPOSE DECONSOLIDATION
        String containerNo = "123";
        ProposeDeconsolidationFlow.Initiator proposeFlow = new ProposeDeconsolidationFlow.Initiator(shippingLineParty, masterBolId, containerNo);
        CordaFuture<UniqueIdentifier> future1 = this.lclCompany.startFlow(proposeFlow);
        network.runNetwork();
        UniqueIdentifier deconsolidationProposalId = future1.get();

        // RELEASE CONTAINER
        Price collect = new Price(new BigDecimal("123"), Currency.getInstance("EUR"));
        ReleaseContainerFlow.Initiator releaseFlow = new ReleaseContainerFlow.Initiator(deconsolidationProposalId, collect);
        this.shippingLine.startFlow(releaseFlow);
        network.runNetwork();

        // ACCEPT
        AcceptDeconsolidationFlow.Initiator acceptFlow = new AcceptDeconsolidationFlow.Initiator(deconsolidationProposalId, trackingStateIds);
        CordaFuture<SignedTransaction> future3 = this.lclCompany.startFlow(acceptFlow);
        network.runNetwork();
        SignedTransaction tx = future3.get();

        // Check that transaction is recorded in both vaults
        for (StartedMockNode node : Arrays.asList(this.lclCompany, this.shippingLine)) {
            SignedTransaction recordedTx = node.getServices().getValidatedTransactions().getTransaction(tx.getId());
            List<TransactionState<ContractState>> txOutputs = recordedTx.getTx().getOutputs();
            assert (txOutputs.size() == 1);
            assert (txOutputs.get(0).getData() instanceof DeconsolidationState);

            DeconsolidationState recordedState = (DeconsolidationState) txOutputs.get(0).getData();
            assertEquals(lclCompanyParty, recordedState.getLclCompany());
            assertEquals(shippingLineParty, recordedState.getShippingLine());
            assertEquals(masterBolId, recordedState.getMasterBillOfLadingId());
            assertEquals(collect, recordedState.getCollect());
            assertEquals(containerNo, recordedState.getContainerNo());

            TrackingState state = this.resolveStateId(ShippingTrackingState.class, trackingStateId, node, Vault.StateStatus.UNCONSUMED);
            Assert.assertEquals(TrackingStatus.Deconsolidated, state.getStatus());
        }
    }

}

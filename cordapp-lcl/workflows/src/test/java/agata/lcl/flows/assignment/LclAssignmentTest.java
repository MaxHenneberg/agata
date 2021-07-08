package agata.lcl.flows.assignment;

import agata.bol.dataholder.Address;
import agata.bol.dataholder.DescriptionOfGoods;
import agata.bol.dataholder.ItemRow;
import agata.lcl.enums.TrackingStatus;
import agata.lcl.flows.FlowTestBase;
import agata.lcl.states.assignment.AssignmentState;
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
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class LclAssignmentTest extends FlowTestBase {
    private StartedMockNode lclCompany;
    private StartedMockNode buyer;
    private StartedMockNode supplier;

    @Before
    public void setupDeliveryTest() {
        lclCompany = network.createPartyNode(new CordaX500Name("LCL Company A", "Rotterdam", "NL"));
        buyer = network.createPartyNode(new CordaX500Name("Buyer", "Rotterdam", "NL"));
        supplier = network.createPartyNode(new CordaX500Name("Supplier", "Beijing", "CN"));
        network.runNetwork();
    }

    @Test
    public void testLclAssignment() throws ExecutionException, InterruptedException {

        Party lclCompanyParty = getParty(this.lclCompany);
        Party buyerParty = getParty(this.buyer);
        Party supplierParty = getParty(this.supplier);
        Address arrivalAddress = new Address("Arrival street 1", "Klm", "Def", "45678", "Bar");
        Address departureAddress = new Address("Departure street 1", "Abc", "Def", "12345", "Foo");
        List<ItemRow> expectedGoods = Collections.singletonList(
                new ItemRow("abc", "123", 3, new DescriptionOfGoods("iPhone", "pallet", 100), 12, 12, 456));

        // PROPOSE
        AssignmentProposalFlow.Initiator proposeFlow = new AssignmentProposalFlow.Initiator(buyerParty, supplierParty, supplierParty, departureAddress, arrivalAddress, expectedGoods);
        CordaFuture<UniqueIdentifier> future1 = this.lclCompany.startFlow(proposeFlow);
        network.runNetwork();
        UniqueIdentifier proposalId = future1.get();

        // ACCEPT
        AssignmentAcceptFlow.Initiator acceptFlow = new AssignmentAcceptFlow.Initiator(proposalId);
        CordaFuture<SignedTransaction> future2 = this.buyer.startFlow(acceptFlow);
        network.runNetwork();
        SignedTransaction tx = future2.get();

        for (StartedMockNode node : Arrays.asList(this.lclCompany, this.buyer)) {
            SignedTransaction recordedTx = node.getServices().getValidatedTransactions().getTransaction(tx.getId());
            List<TransactionState<ContractState>> txOutputs = recordedTx.getTx().getOutputs();
            assert (txOutputs.size() == 1);
            assert (txOutputs.get(0).getData() instanceof AssignmentState);
            AssignmentState recordedState = (AssignmentState) txOutputs.get(0).getData();

            assertEquals(lclCompanyParty, recordedState.getLclCompany());
            assertEquals(buyerParty, recordedState.getBuyer());
            assertEquals(supplierParty, recordedState.getSupplier());
            assertEquals(supplierParty, recordedState.getArrivalParty());
            assertEquals(departureAddress, recordedState.getDepartureAddress());
            assertEquals(arrivalAddress, recordedState.getArrivalAddress());
            assertEquals(expectedGoods, recordedState.getExpectedGoods());

            TrackingState trackingState = this.resolveStateId(TrackingState.class, recordedState.getTrackingStateId(), node, Vault.StateStatus.UNCONSUMED);
            assertEquals(buyerParty, trackingState.getBuyer());
            assertEquals(supplierParty, trackingState.getSupplier());
            assertEquals(lclCompanyParty, trackingState.getLclCompany());
            assertEquals(TrackingStatus.SlotBooked, trackingState.getStatus());
        }

    }
}

package agata.lcl.flows.pickup;

import agata.bol.dataholder.Address;
import agata.bol.dataholder.DescriptionOfGoods;
import agata.bol.dataholder.ItemRow;
import agata.lcl.flows.AcceptFlow;
import agata.lcl.flows.ModifyFlow;
import agata.lcl.flows.ProposalFlow;
import agata.lcl.states.Proposal;
import agata.lcl.states.assignment.AssignmentProposal;
import agata.lcl.states.assignment.AssignmentState;
import agata.lcl.states.pickup.PickupProposal;
import agata.lcl.states.pickup.PickupState;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.node.NetworkParameters;
import net.corda.core.transactions.SignedTransaction;
import net.corda.testing.node.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PickFlowTest {
    private MockNetwork network;
    private StartedMockNode lclCompany;
    private StartedMockNode supplier;
    private StartedMockNode buyer;
    Address address1 = new Address("Sample street 1", "New York", "", "", "US");
    Address address2 = new Address("Hafenstrasse", "Hamburg", "", "", "DE");

    @Before
    public void setup() {
        network = new MockNetwork(new MockNetworkParameters().withCordappsForAllNodes(Arrays.asList(
                TestCordapp.findCordapp("agata.lcl.contracts"),
                TestCordapp.findCordapp("agata.lcl.flows"))).withNetworkParameters(new NetworkParameters(4, Collections.emptyList(),
                10485760, 10485760 * 50, Instant.now(), 1,
                Collections.emptyMap()))
        );
        lclCompany = network.createPartyNode(new CordaX500Name("LCL Company", "New York", "US"));
        supplier = network.createPartyNode(new CordaX500Name("Supplier", "New York", "US"));
        buyer = network.createPartyNode(new CordaX500Name("Buyer", "New York", "US"));
        network.runNetwork();
    }

    @After
    public void tearDown() {
        network.stopNodes();
    }

    @Test
    public void testAddGoodsWithException() throws ExecutionException, InterruptedException {
        final UniqueIdentifier assignmentStateId = createAssignmentState();
        PickupProposalFlow.Initiator pickupProposalFlow = new PickupProposalFlow.Initiator(assignmentStateId);
        Future<UniqueIdentifier> future1 = this.lclCompany.startFlow(pickupProposalFlow);
        network.runNetwork();

        UniqueIdentifier pickupProposalId = future1.get();

        PickupAddGoodsFlow.Initiator modifyFlow = new PickupAddGoodsFlow.Initiator(pickupProposalId, Collections.singletonList(new ItemRow("abc", "123", 3, new DescriptionOfGoods("Not an Iphone", "pallet", 100), 12, 12, 456)), "test");
        Future future2 = this.supplier.startFlow(modifyFlow);
        network.runNetwork();
        ExecutionException exception = assertThrows(ExecutionException.class, future2::get);
        Assert.assertNotNull(exception);
        Assert.assertTrue(exception.getMessage().startsWith(
                "net.corda.core.contracts.TransactionVerificationException$ContractRejection: Contract verification failed: Failed requirement: Picked-up Goods must be Equals to Expected Goods, " +
                        "contract: agata.lcl.contracts.pickup.PickupContract"));
    }

    @Test
    public void testAccept() throws ExecutionException, InterruptedException {
        final UniqueIdentifier assignmentStateId = createAssignmentState();

        PickupProposalFlow.Initiator pickupProposeFlow = new PickupProposalFlow.Initiator(assignmentStateId);
        Future<UniqueIdentifier> future1 = this.lclCompany.startFlow(pickupProposeFlow);
        network.runNetwork();

        UniqueIdentifier pickupProposalId = future1.get();

        PickupAddGoodsFlow.Initiator modifyFlow = new PickupAddGoodsFlow.Initiator(pickupProposalId,
                Collections.singletonList(new ItemRow("abc", "123", 3, new DescriptionOfGoods("iPhone", "pallet", 100), 12, 12, 456)), "NotBlank");
        Future future2 = this.supplier.startFlow(modifyFlow);
        network.runNetwork();
        future2.get();
    }

    private UniqueIdentifier createAssignmentState() throws ExecutionException, InterruptedException {
        Party lclCompanyParty = getParty(this.lclCompany);
        Party supplierParty = getParty(this.supplier);
        Party buyerParty = getParty(this.buyer);
        List<ItemRow> goods = Collections.singletonList(new ItemRow("abc", "123", 3, new DescriptionOfGoods("iPhone", "pallet", 100), 12, 12, 456));
        AssignmentState assignmentState = new AssignmentState(lclCompanyParty, buyerParty, supplierParty, buyerParty, address1, address2, goods, AssignmentState.Status.SlotBooked);
        Proposal proposal = new AssignmentProposal(lclCompanyParty, buyerParty, assignmentState);
        ProposalFlow.Initiator proposeFlow = new ProposalFlow.Initiator(proposal);
        Future<UniqueIdentifier> future1 = this.lclCompany.startFlow(proposeFlow);
        network.runNetwork();

        UniqueIdentifier assignmentProposalUId = future1.get();

        AcceptFlow.Initiator acceptFlow = new AcceptFlow.Initiator(assignmentProposalUId);
        Future future = this.buyer.startFlow(acceptFlow);
        network.runNetwork();
        SignedTransaction pickUpStateTx = (SignedTransaction) future.get();

        final LinearState state = (LinearState) pickUpStateTx.getCoreTransaction().getOutputStates().get(0);

        return state.getLinearId();
    }

    protected Party getParty(StartedMockNode mockNode) {
        return mockNode.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();
    }

}

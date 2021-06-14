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
import net.corda.core.contracts.TransactionVerificationException;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.core.node.NetworkParameters;
import net.corda.testing.node.MockNetwork;
import net.corda.testing.node.MockNetworkParameters;
import net.corda.testing.node.StartedMockNode;
import net.corda.testing.node.TestCordapp;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PickFlowTest {
    private MockNetwork network;
    private StartedMockNode proposer;
    private StartedMockNode proposee;
    private StartedMockNode other;
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
        proposer = network.createPartyNode(null);
        proposee = network.createPartyNode(null);
        other = network.createPartyNode(null);
        network.runNetwork();
    }

    @After
    public void tearDown() {
        network.stopNodes();
    }

    @Test
    public void testAddGoodsWithException() throws ExecutionException, InterruptedException {
        Party lclCompany = getParty(proposer);
        Party supplier = getParty(proposee);
        Party exporter = getParty(other);
        List<ItemRow> goods = Collections.singletonList(new ItemRow("abc", "123", 3, new DescriptionOfGoods("iPhone", "pallet", 100), 12, 12, 456));
        AssignmentState assignmentState = new AssignmentState(lclCompany, exporter, supplier, exporter, address1, address2, goods, AssignmentState.Status.SlotBooked);
        Proposal proposal = new AssignmentProposal(lclCompany, exporter, assignmentState);
        ProposalFlow.ProposalFlowInitiator proposeFlow = new ProposalFlow.ProposalFlowInitiator(proposal);
        Future<UniqueIdentifier> future1 = other.startFlow(proposeFlow);
        network.runNetwork();

        UniqueIdentifier assignmentStateUId = future1.get();

        AcceptFlow.AcceptFlowInitiator acceptFlow = new AcceptFlow.AcceptFlowInitiator(assignmentStateUId);
        Future future = proposer.startFlow(acceptFlow);
        network.runNetwork();
        future.get();

        PickupState pickupState = new PickupState(exporter, supplier, lclCompany, Collections.emptyList(), assignmentState.getLinearId());
        Proposal pickupProposal = new PickupProposal(lclCompany, supplier, pickupState);

        proposeFlow = new ProposalFlow.ProposalFlowInitiator(pickupProposal);
        future1 = proposer.startFlow(proposeFlow);
        network.runNetwork();

        UniqueIdentifier pickupProposalId = future1.get();

        PickupState addGoodsState = new PickupState(exporter, supplier, lclCompany, Collections.singletonList("test"), assignmentState.getLinearId());
        Proposal addGoodsProposal = new PickupProposal(supplier, lclCompany, addGoodsState);

        ModifyFlow.ModifyFlowInitiator modifyFlow = new ModifyFlow.ModifyFlowInitiator(pickupProposalId, addGoodsProposal);
        Future future2 = proposee.startFlow(modifyFlow);
        network.runNetwork();
        ExecutionException exception = assertThrows(ExecutionException.class, future2::get);
        Assert.assertNotNull(exception);
        Assert.assertTrue(exception.getMessage().startsWith("net.corda.core.contracts.TransactionVerificationException$ContractRejection: Contract verification failed: Failed requirement: Picked-up Goods must be Equals to Expected Goods, contract: agata.lcl.contracts.pickup.PickupContract"));
    }

    protected Party getParty(StartedMockNode mockNode) {
        return mockNode.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();
    }

}

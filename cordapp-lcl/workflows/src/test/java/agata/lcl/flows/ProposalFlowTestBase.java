package agata.lcl.flows;

import agata.lcl.states.Proposal;
import agata.lcl.states.pickup.PickupProposal;
import agata.lcl.states.pickup.PickupState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.testing.node.MockNetwork;
import net.corda.testing.node.MockNetworkParameters;
import net.corda.testing.node.StartedMockNode;
import net.corda.testing.node.TestCordapp;
import org.junit.After;
import org.junit.Before;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

abstract class ProposalFlowTestBase {
    protected MockNetwork network;
    protected StartedMockNode proposer;
    protected StartedMockNode proposee;
    protected StartedMockNode other;

    @Before
    public void setup() {
        network = new MockNetwork(new MockNetworkParameters().withCordappsForAllNodes(Arrays.asList(
                TestCordapp.findCordapp("agata.lcl.contracts"),
                TestCordapp.findCordapp("agata.lcl.flows"))));
        proposer = network.createPartyNode(null);
        proposee = network.createPartyNode(null);
        other = network.createPartyNode(null);
        network.runNetwork();
    }

    @After
    public void tearDown() {
        network.stopNodes();
    }

    protected UniqueIdentifier createProposal(StartedMockNode proposer, StartedMockNode proposee, PickupState proposal) throws ExecutionException, InterruptedException {
        final Proposal<PickupState> proposalState = new PickupProposal(getParty(proposer), getParty(proposee),proposal);
        ProposalFlow.ProposalFlowInitiator flow = new ProposalFlow.ProposalFlowInitiator(proposalState);
        Future<UniqueIdentifier> future = proposer.startFlow(flow);
        network.runNetwork();

        return future.get();
    }

    protected Party getParty(StartedMockNode mockNode) {
        return mockNode.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();
    }
}

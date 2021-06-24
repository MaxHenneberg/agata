package agata.lcl.flows;

import agata.lcl.states.Proposal;
import agata.lcl.states.test.DummyProposal;
import agata.lcl.states.test.DummyState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.testing.node.StartedMockNode;
import org.junit.Before;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

abstract class ProposalFlowTestBase extends FlowTestBase {
    protected StartedMockNode proposer;
    protected StartedMockNode proposee;
    protected StartedMockNode other;

    @Before
    public void setupProposalFlowTest() {
        proposer = network.createPartyNode(null);
        proposee = network.createPartyNode(null);
        other = network.createPartyNode(null);
        network.runNetwork();
    }

    protected UniqueIdentifier createProposal(StartedMockNode proposer, StartedMockNode proposee, DummyState proposal) throws ExecutionException, InterruptedException {
        final Proposal<DummyState> proposalState = new DummyProposal(getParty(proposer), getParty(proposee), proposal);
        ProposalFlow.Initiator flow = new ProposalFlow.Initiator(proposalState);
        Future<UniqueIdentifier> future = proposer.startFlow(flow);
        network.runNetwork();

        return future.get();
    }
}

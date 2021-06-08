package agata.lcl.flows;

import agata.lcl.states.Proposal;
import agata.lcl.states.test.DummyState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AcceptFlowTest extends ProposalFlowTestBase {

    @Test
    public void acceptTest() throws ExecutionException, InterruptedException {
        final DummyState dummyState = new DummyState(getParty(proposer), getParty(proposee), "test", "test", 1, 1, "test", 1);


        UniqueIdentifier proposalId = createProposal(proposer, proposee, dummyState);

        Assert.assertNotNull(proposalId);

        AcceptFlow.AcceptFlowInitiator flow = new AcceptFlow.AcceptFlowInitiator(proposalId);
        Future future = proposee.startFlow(flow);
        network.runNetwork();
        future.get();

        Arrays.asList(proposer, proposee).forEach(node -> node.transaction(() -> {
            List<StateAndRef<Proposal>> proposals = node.getServices().getVaultService().queryBy(Proposal.class).getStates();
            Assert.assertEquals(0, proposals.size());

            List<StateAndRef<DummyState>> dummies = node.getServices().getVaultService().queryBy(DummyState.class).getStates();
            Assert.assertEquals(1, dummies.size());
            DummyState proposedPickupState = dummies.get(0).getState().getData();

            Assert.assertEquals(dummyState, proposedPickupState);
            return null;
        }));
    }

    @Test(expected = ExecutionException.class)
    public void acceptAsProposerTest() throws ExecutionException, InterruptedException {
        final DummyState pickupState = new DummyState(getParty(proposer), getParty(proposee), "test", "test", 1, 1, "test", 1);
        UniqueIdentifier proposalId = createProposal(proposer, proposee, pickupState);

        AcceptFlow.AcceptFlowInitiator flow = new AcceptFlow.AcceptFlowInitiator(proposalId);
        Future future = proposer.startFlow(flow);
        network.runNetwork();
        future.get();
    }
}

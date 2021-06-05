package agata.lcl.flows;

import agata.lcl.states.Proposal;
import agata.lcl.states.pickup.PickupState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AcceptFlowTest extends ProposalFlowTestBase {

    @Test
    public void acceptTest() throws ExecutionException, InterruptedException {
        final PickupState pickupState = new PickupState(getParty(other), getParty(proposee), getParty(proposer), Collections.emptyList(), new UniqueIdentifier());
        UniqueIdentifier proposalId = createProposal(proposer, proposee, pickupState);

        AcceptFlow.AcceptFlowInitiator flow = new AcceptFlow.AcceptFlowInitiator(proposalId);
        Future future = proposee.startFlow(flow);
        network.runNetwork();
        future.get();

        Arrays.asList(proposer, proposee).forEach(node -> node.transaction(() -> {
            List<StateAndRef<Proposal>> proposals = node.getServices().getVaultService().queryBy(Proposal.class).getStates();
            Assert.assertEquals(0, proposals.size());

            List<StateAndRef<PickupState>> pickups = node.getServices().getVaultService().queryBy(PickupState.class).getStates();
            Assert.assertEquals(1, pickups.size());
            PickupState proposedPickupState = pickups.get(0).getState().getData();

            Assert.assertEquals(pickupState, proposedPickupState);
            return null;
        }));
    }

    @Test(expected = ExecutionException.class)
    public void acceptAsProposerTest() throws ExecutionException, InterruptedException {
        final PickupState pickupState = new PickupState(getParty(other), getParty(proposee), getParty(proposer), Collections.emptyList(), new UniqueIdentifier());
        UniqueIdentifier proposalId = createProposal(proposer, proposee, pickupState);

        AcceptFlow.AcceptFlowInitiator flow = new AcceptFlow.AcceptFlowInitiator(proposalId);
        Future future = proposer.startFlow(flow);
        network.runNetwork();
        future.get();
    }
}

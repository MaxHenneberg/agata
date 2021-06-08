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

public class ProposalFlowTest extends ProposalFlowTestBase {

    @Test
    public void proposalTest() throws ExecutionException, InterruptedException {
        final PickupState pickupState = new PickupState(getParty(other), getParty(proposee), getParty(proposer), Collections.emptyList(), new UniqueIdentifier());
        UniqueIdentifier proposalId = createProposal(proposer, proposee, pickupState);

        //successful query means the state is stored at node b's vault. Flow went through.
        Arrays.asList(proposer, proposee).forEach(node -> node.transaction(() -> {
            List<StateAndRef<Proposal>> proposals = node.getServices().getVaultService()
                    .queryBy(Proposal.class).getStates();
            Assert.assertEquals(1, proposals.size());

            Proposal proposal = proposals.get(0).getState().getData();

            Assert.assertEquals(proposalId, proposal.getLinearId());

            Assert.assertEquals(proposer.getInfo().getLegalIdentitiesAndCerts().get(0).getParty(), proposal.getProposer());
            Assert.assertEquals(proposee.getInfo().getLegalIdentitiesAndCerts().get(0).getParty(), proposal.getProposee());
            Assert.assertEquals(pickupState, proposal.getProposedState());
            return null;
        }));
    }


}

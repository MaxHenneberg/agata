package agata.lcl.flows;

import agata.lcl.states.Proposal;
import agata.lcl.states.test.DummyProposal;
import agata.lcl.states.test.DummyState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ModifyFlowTest extends ProposalFlowTestBase {

    @Test
    public void modifyProposalAsProposee() throws ExecutionException, InterruptedException {
        final PickupState pickupProposal = new PickupState(getParty(other), getParty(proposee), getParty(proposer), Collections.emptyList(), new UniqueIdentifier());
        UniqueIdentifier proposalId = createProposal(proposer, proposee, pickupProposal);

        Assert.assertNotNull(proposalId);

        final PickupState modifiedPickupProposal = new PickupState(getParty(other), getParty(proposee), getParty(proposer), Collections.singletonList("Item"), new UniqueIdentifier(), pickupProposal.getLinearId());
        final Proposal<PickupState> counterProposal = new PickupProposal(getParty(proposee), getParty(proposer), modifiedPickupProposal, proposalId);

        ModifyFlow.ModifyFlowInitiator flow = new ModifyFlow.ModifyFlowInitiator(proposalId, counterProposal);
        Future future = proposee.startFlow(flow);
        network.runNetwork();
        future.get();

        Arrays.asList(proposee, proposer).forEach(node -> node.transaction(() -> {
            List<StateAndRef<Proposal>> proposals = node.getServices().getVaultService().queryBy(Proposal.class).getStates();
            Assert.assertEquals(1, proposals.size());

            Proposal proposal = proposals.get(0).getState().getData();

            Assert.assertEquals(getParty(proposee), proposal.getProposer());
            Assert.assertEquals(getParty(proposer), proposal.getProposee());
            Assert.assertEquals(modifiedPickupProposal, proposal.getProposedState());
            return null;
        }));
    }

    @Test(expected = ExecutionException.class)
    public void modifyProposalAsProposer() throws ExecutionException, InterruptedException {
        final PickupState pickupState = new PickupState(getParty(other), getParty(proposee), getParty(proposer), Collections.emptyList(), new UniqueIdentifier());
        UniqueIdentifier proposalId = createProposal(proposer, proposee, pickupState);

        Assert.assertNotNull(proposalId);

        final PickupState modifiedPickupState = new PickupState(getParty(other), getParty(proposee), getParty(proposer), Collections.singletonList("Item"), new UniqueIdentifier());
        final Proposal counterProposal = new PickupProposal(getParty(proposee), getParty(proposer), modifiedPickupState);

        ModifyFlow.ModifyFlowInitiator flow = new ModifyFlow.ModifyFlowInitiator(proposalId, counterProposal);
        Future future = proposer.startFlow(flow);
        network.runNetwork();
        future.get();
    }
}

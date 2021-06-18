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
        final DummyState dummyState = new DummyState(getParty(proposer), getParty(proposee), "test", "test", 1, 1, "test", 1);
        UniqueIdentifier proposalId = createProposal(proposer, proposee, dummyState);

        Assert.assertNotNull(proposalId);

        final DummyState modifiedDummyProposal = new DummyState(getParty(proposer), getParty(proposee), "test", "test", 1, 1, "test", 1);
        modifiedDummyProposal.setMandatoryStringField("other");
        final Proposal<DummyState> counterProposal = new DummyProposal(getParty(proposee), getParty(proposer), modifiedDummyProposal, proposalId);

        ModifyFlow.Initiator flow = new ModifyFlow.Initiator(proposalId, counterProposal);
        Future future = proposee.startFlow(flow);
        network.runNetwork();
        future.get();

        Arrays.asList(proposee, proposer).forEach(node -> node.transaction(() -> {
            List<StateAndRef<Proposal>> proposals = node.getServices().getVaultService().queryBy(Proposal.class).getStates();
            Assert.assertEquals(1, proposals.size());

            Proposal proposal = proposals.get(0).getState().getData();

            Assert.assertEquals(getParty(proposee), proposal.getProposer());
            Assert.assertEquals(getParty(proposer), proposal.getProposee());
            Assert.assertEquals(modifiedDummyProposal, proposal.getProposedState());
            return null;
        }));
    }

    @Test(expected = ExecutionException.class)
    public void modifyProposalAsProposer() throws ExecutionException, InterruptedException {
        final DummyState dummyState = new DummyState(getParty(proposer), getParty(proposee), "test", "test", 1, 1, "test", 1);
        UniqueIdentifier proposalId = createProposal(proposer, proposee, dummyState);

        Assert.assertNotNull(proposalId);

        final DummyState modifiedDummyState = new DummyState(getParty(proposer), getParty(proposee), "test", "test", 1, 1, "test", 1);
        modifiedDummyState.setMandatoryStringField("other");
        final Proposal<DummyState> counterProposal = new DummyProposal(getParty(proposee), getParty(proposer), modifiedDummyState);

        ModifyFlow.Initiator flow = new ModifyFlow.Initiator(proposalId, counterProposal);
        Future future = proposer.startFlow(flow);
        network.runNetwork();
        future.get();
    }
}

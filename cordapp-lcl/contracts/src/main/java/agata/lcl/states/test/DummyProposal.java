package agata.lcl.states.test;

import agata.lcl.contracts.test.DummyProposalContract;
import agata.lcl.states.Proposal;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;

@BelongsToContract(DummyProposalContract.class)
public class DummyProposal extends Proposal<DummyState> {

    public DummyProposal(Party proposer, Party proposee, DummyState proposedState) {
        super(proposer, proposee, proposedState);
    }

    @ConstructorForDeserialization
    public DummyProposal(Party proposer, Party proposee, DummyState proposedState, UniqueIdentifier linearId) {
        super(proposer, proposee, proposedState, linearId);
    }
}

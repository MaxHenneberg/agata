package agata.lcl.states.assignment;

import agata.lcl.contracts.assignment.AssignmentContract;
import agata.lcl.states.Proposal;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;

@BelongsToContract(AssignmentContract.class)
public class AssignmentProposal extends Proposal<AssignmentState> {

    public AssignmentProposal(Party proposer, Party proposee, AssignmentState proposedState) {
        super(proposer, proposee, proposedState);
    }

    @ConstructorForDeserialization
    public AssignmentProposal(Party proposer, Party proposee, AssignmentState proposedState, UniqueIdentifier linearId) {
        super(proposer, proposee, proposedState, linearId);
    }

}

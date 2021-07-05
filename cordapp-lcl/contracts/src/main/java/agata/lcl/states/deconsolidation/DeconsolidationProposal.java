package agata.lcl.states.deconsolidation;

import agata.lcl.contracts.deconsolidation.DeconsolidationContract;
import agata.lcl.states.Proposal;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;

@BelongsToContract(DeconsolidationContract.class)
public class DeconsolidationProposal extends Proposal<DeconsolidationState> {

    public DeconsolidationProposal(Party proposer, Party proposee, DeconsolidationState proposedState) {
        super(proposer, proposee, proposedState);
    }

    @ConstructorForDeserialization
    public DeconsolidationProposal(Party proposer, Party proposee, DeconsolidationState proposedState, UniqueIdentifier linearId) {
        super(proposer, proposee, proposedState, linearId);
    }
}

package agata.lcl.states.pickup;

import agata.lcl.contracts.pickup.PickupContract;
import agata.lcl.states.Proposal;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;

@BelongsToContract(PickupContract.class)
public class PickupProposal extends Proposal<PickupState> {

    public PickupProposal(Party proposer, Party proposee, PickupState proposedState) {
        super(proposer, proposee, proposedState);
    }

    @ConstructorForDeserialization
    public PickupProposal(Party proposer, Party proposee, PickupState proposedState, UniqueIdentifier linearId) {
        super(proposer, proposee, proposedState, linearId);
    }

}

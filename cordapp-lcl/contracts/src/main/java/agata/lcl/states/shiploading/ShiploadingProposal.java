package agata.lcl.states.shiploading;

import agata.bol.states.BillOfLadingState;
import agata.lcl.contracts.shiploading.ShiploadingContract;
import agata.lcl.states.Proposal;
import lombok.Getter;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;

@Getter
@BelongsToContract(ShiploadingContract.class)
public class ShiploadingProposal extends Proposal<BillOfLadingState> {
    private final UniqueIdentifier containerId;

    public ShiploadingProposal(Party proposer, Party proposee, BillOfLadingState proposedState, UniqueIdentifier containerId) {
        super(proposer, proposee, proposedState);
        this.containerId = containerId;
    }

    @ConstructorForDeserialization
    public ShiploadingProposal(Party proposer, Party proposee, BillOfLadingState proposedState, UniqueIdentifier containerId, UniqueIdentifier linearId) {
        super(proposer, proposee, proposedState, linearId);
        this.containerId = containerId;
    }
}

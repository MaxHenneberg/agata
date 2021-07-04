package agata.lcl.states.delivery;

import agata.lcl.contracts.delivery.PackageDeliveryContract;
import agata.lcl.states.Proposal;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;

@BelongsToContract(PackageDeliveryContract.class)
public class PackageDeliveryProposal extends Proposal<PackageDeliveryState> {

    public PackageDeliveryProposal(Party proposer, Party proposee, PackageDeliveryState proposedState) {
        super(proposer, proposee, proposedState);
    }

    @ConstructorForDeserialization
    public PackageDeliveryProposal(Party proposer, Party proposee, PackageDeliveryState proposedState, UniqueIdentifier linearId) {
        super(proposer, proposee, proposedState, linearId);
    }
}

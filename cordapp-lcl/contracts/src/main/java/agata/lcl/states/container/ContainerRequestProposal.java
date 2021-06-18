package agata.lcl.states.container;

import agata.lcl.contracts.container.ContainerRequestContract;
import agata.lcl.states.Proposal;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;

@BelongsToContract(ContainerRequestContract.class)
public class ContainerRequestProposal extends Proposal<ContainerRequestState> {

    public ContainerRequestProposal(Party proposer, Party proposee, ContainerRequestState proposedState) {
        super(proposer, proposee, proposedState);
    }

    @ConstructorForDeserialization
    public ContainerRequestProposal(Party proposer, Party proposee, ContainerRequestState proposedState, UniqueIdentifier linearId) {
        super(proposer, proposee, proposedState, linearId);
    }

}

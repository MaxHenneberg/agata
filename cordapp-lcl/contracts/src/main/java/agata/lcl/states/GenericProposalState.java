package agata.lcl.states;

import agata.lcl.contracts.GenericProposalContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@BelongsToContract(GenericProposalContract.class)
public class GenericProposalState<T extends ContractState> implements LinearState {
    private final UniqueIdentifier linearId;

    private final Party proposer;
    private final Party proposee;

    private final T proposal;

    public GenericProposalState(Party proposer, Party proposee, T proposal) {
        this.proposer = proposer;
        this.proposee = proposee;
        this.linearId = new UniqueIdentifier();
        this.proposal = proposal;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(proposer, proposee);
    }

    public Party getProposer() {
        return proposer;
    }

    public Party getProposee() {
        return proposee;
    }

    public T getProposal() {
        return proposal;
    }
}

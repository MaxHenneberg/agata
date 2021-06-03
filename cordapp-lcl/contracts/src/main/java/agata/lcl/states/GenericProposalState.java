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
    private final Party propose;

    private final T proposal;

    public GenericProposalState(Party proposer, Party propose, T proposal) {
        this.proposer = proposer;
        this.propose = propose;
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
        return Arrays.asList(proposer, propose);
    }

    public Party getProposer() {
        return proposer;
    }

    public Party getPropose() {
        return propose;
    }

    public T getProposal() {
        return proposal;
    }
}

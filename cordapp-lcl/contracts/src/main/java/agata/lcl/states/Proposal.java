package agata.lcl.states;

import agata.lcl.contracts.annotations.MandatoryForContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public abstract class Proposal<T extends LinearState> implements LinearState {

    @MandatoryForContract
    protected final Party proposee;
    @MandatoryForContract
    protected final Party proposer;
    @MandatoryForContract
    protected final T proposedState;
    protected UniqueIdentifier linearId;

    public Proposal(Party proposer, Party proposee, T proposedState) {
        this.proposee = proposee;
        this.proposer = proposer;
        this.proposedState = proposedState;
        this.linearId = new UniqueIdentifier();
    }

    public Proposal(Party proposer, Party proposee, T proposedState, UniqueIdentifier linearId) {
        this.proposee = proposee;
        this.proposer = proposer;
        this.proposedState = proposedState;
        this.linearId = linearId;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(proposee, proposer);
    }

    public Party getProposee() {
        return proposee;
    }

    public Party getProposer() {
        return proposer;
    }

    public T getProposedState() {
        return proposedState;
    }

}

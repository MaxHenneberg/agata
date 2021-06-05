package agata.lcl.states;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.LinearState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.CordaSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@CordaSerializable
public interface Proposal<T extends ContractState> extends LinearState {

    Party getProposee();

    Party getProposer();

    T getProposedState();

    CommandData getProposalCommand();

    boolean isContractionalEqual(T other);

    @NotNull
    @Override
    default List<AbstractParty> getParticipants() {
        return Arrays.asList(getProposee(), getProposer());
    }
}

package agata.lcl.contracts;

import agata.lcl.states.Proposal;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public abstract class BaseContract implements Contract {

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        final CommandData commandData = tx.getCommand(0).getValue();

        for (ContractState contractState : tx.getOutputStates()) {
            try {
                BaseContractUtils.checkMandatoryFields(contractState, commandData, false);
                if (contractState instanceof Proposal) {
                    BaseContractUtils.checkMandatoryFields(((Proposal) contractState).getProposedState(), commandData, true);
                }
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        verifyCommands(tx);
    }

    public abstract void verifyCommands(LedgerTransaction tx) throws IllegalArgumentException;

}

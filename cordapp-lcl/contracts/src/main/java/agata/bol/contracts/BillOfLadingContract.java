package agata.bol.contracts;

import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

public class BillOfLadingContract implements Contract {
    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        final Command command = tx.getCommand(0);
        if (command.getValue() instanceof BoLCommands) {
            commonChecks(tx, command);
            if (command.getValue() instanceof BoLCommands.CreateHouseBoL) {
                verifyCreateHouseBoL(tx, command);
            } else if (command.getValue() instanceof BoLCommands.CreateMasterBoL) {
                verifyCreateMasterBoL(tx, command);
            }
        }


    }

    private void commonChecks(@NotNull LedgerTransaction tx, @NotNull Command command) {

    }

    private void verifyCreateHouseBoL(@NotNull LedgerTransaction tx, @NotNull Command command) {

    }

    private void verifyCreateMasterBoL(@NotNull LedgerTransaction tx, @NotNull Command command) {

    }

    public interface BoLCommands extends CommandData {
        class CreateHouseBoL implements BoLCommands {
        }

        class CreateMasterBoL implements BoLCommands {
        }
    }
}

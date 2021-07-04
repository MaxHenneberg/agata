package agata.bol.contracts;

import agata.bol.enums.BillOfLadingType;
import agata.bol.states.BillOfLadingState;
import agata.lcl.states.pickup.PickupState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public class BillOfLadingContract implements Contract {

    public static String ID = "agata.bol.contracts.BillOfLadingContract";

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
        requireThat(require -> {
            // TODO: Fix this temporary solution
//            require.using("There is at least one input", tx.getInputStates().size() >= 1);
            return null;
        });
    }

    private void verifyCreateHouseBoL(@NotNull LedgerTransaction tx, @NotNull Command command) {
        requireThat(require -> {
            require.using("There is exactly one input", tx.getInputStates().size() == 1);
            require.using("PickupState was given as input", tx.inputsOfType(PickupState.class).size() == 1);
            require.using("A bill of loading is given as output", tx.outputsOfType(BillOfLadingState.class).size() == 1);
            BillOfLadingState bol = tx.outputsOfType(BillOfLadingState.class).get(0);
            require.using("Issued bill of lading needs to be a house bill of lading", bol.getType() == BillOfLadingType.House);

            PickupState input = tx.inputsOfType(PickupState.class).get(0);
            //TODO: More checks
            return null;
        });
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

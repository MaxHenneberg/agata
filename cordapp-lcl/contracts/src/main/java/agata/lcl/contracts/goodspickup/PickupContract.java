package agata.lcl.contracts.goodspickup;

import agata.lcl.contracts.GenericProposalContract;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

public class PickupContract implements Contract {
    public static String ID = "agata.lcl.contracts.goodspickup.PickupContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        final CommandWithParties command = tx.getCommands().get(0);

        if (command.getValue() instanceof Commands.Propose) {

        } else if (command.getValue() instanceof Commands.AddGoods) {

        } else if (command.getValue() instanceof Commands.Accept) {

        } else if (command.getValue() instanceof GenericProposalContract.Commands.Accept) {
            //States which can get Accepted by a Proposal need to implement the GenericProposalContract.Commands.Accept command
        } else {

            throw new IllegalArgumentException("Command of incorrect type");
        }
    }

    public interface Commands extends CommandData {
        class Propose implements Commands {
        }

        class AddGoods implements Commands {
        }

        class Accept implements Commands {
        }
    }
}

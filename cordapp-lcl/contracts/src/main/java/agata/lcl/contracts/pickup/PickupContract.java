package agata.lcl.contracts.pickup;

import agata.lcl.contracts.GenericProposalContract;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;


public class PickupContract extends GenericProposalContract{
    public static String ID = "agata.lcl.contracts.pickup.PickupContract";

    public void extendedVerify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        final Command command = tx.getCommand(0);
        if (command.getValue() instanceof PickupCommands.AddGoods) {

        }else{
            throw new IllegalArgumentException("Command of incorrect type");
        }
    }

    public interface PickupCommands extends GenericProposalContract.Commands {
        class AddGoods implements PickupCommands {
        }
    }
}

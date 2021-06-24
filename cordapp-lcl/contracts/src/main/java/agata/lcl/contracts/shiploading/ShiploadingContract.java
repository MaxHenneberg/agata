package agata.lcl.contracts.shiploading;

import agata.lcl.contracts.GenericProposalContract;
import agata.lcl.states.shiploading.ShiploadingProposal;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public class ShiploadingContract extends GenericProposalContract {
    public static String ID = "agata.lcl.contracts.shiploading.ShiploadingContract";

    @Override
    protected void extendedVerifyPropose(@NotNull LedgerTransaction tx, @NotNull Command command) {
        requireThat(require -> {
            ShiploadingProposal pickupProposal = tx.outputsOfType(ShiploadingProposal.class).get(0);
            require.using("Proposee must be Shipping Line", pickupProposal.getProposee().equals(pickupProposal.getProposedState().getShipper()));
            require.using("Proposer must be LCL-Company", pickupProposal.getProposer().equals(pickupProposal.getProposedState().getConsignee()));
            return null;
        });
    }

    public interface Commands extends CommandData {
        class ProposeWithInput implements GenericProposalContract.Commands {
        }
    }
}

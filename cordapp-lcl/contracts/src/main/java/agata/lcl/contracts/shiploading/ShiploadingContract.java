package agata.lcl.contracts.shiploading;

import agata.bol.enums.BillOfLadingType;
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
            ShiploadingProposal proposal = tx.outputsOfType(ShiploadingProposal.class).get(0);
            require.using("Proposee must be Shipping Line", proposal.getProposee().equals(proposal.getProposedState().getShipper()));
            require.using("Proposer must be LCL-Company", proposal.getProposer().equals(proposal.getProposedState().getConsignee()));
            require.using("Proposed bill of lading needs to be a master bill of lading", proposal.getProposedState().getType() == BillOfLadingType.Master);
            return null;
        });
    }

    @Override
    protected void extendedVerifyModify(@NotNull LedgerTransaction tx, @NotNull Command command) {
    }

    @Override
    protected void extendedVerifyAccept(@NotNull LedgerTransaction tx, @NotNull Command command) {
    }

    public interface Commands extends CommandData {
        class ProposeWithInput implements GenericProposalContract.Commands {
        }
    }
}

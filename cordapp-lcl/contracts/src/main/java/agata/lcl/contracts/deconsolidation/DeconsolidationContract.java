package agata.lcl.contracts.deconsolidation;

import agata.bol.enums.BillOfLadingType;
import agata.bol.states.BillOfLadingState;
import agata.lcl.contracts.GenericProposalContract;
import agata.lcl.states.deconsolidation.DeconsolidationProposal;
import agata.lcl.states.deconsolidation.DeconsolidationState;
import agata.utils.ContractUtils;
import net.corda.core.contracts.Command;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public class DeconsolidationContract extends GenericProposalContract {

    public static String ID = "agata.lcl.contracts.deconsolidation.DeconsolidationContract";

    @Override
    protected void extendedVerifyPropose(@NotNull LedgerTransaction tx, @NotNull Command command) {
        requireThat(require -> {
            DeconsolidationProposal proposal = tx.outputsOfType(DeconsolidationProposal.class).get(0);
            DeconsolidationState proposedState = proposal.getProposedState();

            require.using("Proposer must be the LCL company", proposal.getProposer().equals(proposedState.getLclCompany()));
            require.using("Proposee must be the shipping line", proposal.getProposee().equals(proposedState.getShippingLine()));
            // Non-null checks are handled by the annotations in the state

            BillOfLadingState bol = ContractUtils.resolveBillOfLadingReference(tx, proposedState.getMasterBillOfLadingId());
            require.using("The passed bill of lading needs to be a master bill of lading", bol.getType() == BillOfLadingType.Master);
            require.using("The LCL company must be the consignee in the master bill of lading", proposedState.getLclCompany().equals(bol.getConsignee()));
            require.using("The shipping line must be the shipper in the master bill of lading", proposedState.getShippingLine().equals(bol.getShipper()));

            require.using(
                    "The requested container number does not match one in the master bill of lading",
                    bol.getContainerInformationList().stream().anyMatch(x -> x.getContainerNo().equals(proposedState.getContainerNo())));

            return null;
        });
    }

    @Override
    protected void extendedVerifyModify(@NotNull LedgerTransaction tx, @NotNull Command command) {
    }

    @Override
    protected void extendedVerifyAccept(@NotNull LedgerTransaction tx, @NotNull Command command) {
    }

    @Override
    protected boolean verifyAdditionalCommands(@NotNull LedgerTransaction tx) throws IllegalArgumentException {

        final Command command = tx.getCommand(0);
        boolean isValidCommand = false;
        if (command.getValue() instanceof Commands.ReleaseContainer) {
            requireThat(require -> {
                DeconsolidationProposal proposal = tx.outputsOfType(DeconsolidationProposal.class).get(0);
                DeconsolidationState proposedState = proposal.getProposedState();

                require.using("Proposer must be the shipping line", proposal.getProposer().equals(proposedState.getShippingLine()));
                require.using("Proposee must be the LCL company", proposal.getProposee().equals(proposedState.getLclCompany()));

                // Verify that all other fields remain unchanged
                DeconsolidationState inputProposedState = tx.inputsOfType(DeconsolidationProposal.class).get(0).getProposedState();
                boolean otherFieldsUnchanged = Objects.equals(inputProposedState.getLinearId(), proposedState.getLinearId()) &&
                        Objects.equals(inputProposedState.getMasterBillOfLadingId(), proposedState.getMasterBillOfLadingId()) &&
                        Objects.equals(inputProposedState.getContainerNo(), proposedState.getContainerNo()) &&
                        Objects.equals(inputProposedState.getLclCompany(), proposedState.getLclCompany()) &&
                        Objects.equals(inputProposedState.getShippingLine(), proposedState.getShippingLine());
                require.using("Other fields must remain unchanged", otherFieldsUnchanged);

                return null;
            });

            isValidCommand = true;
        }
        return isValidCommand;
    }

    public interface Commands extends GenericProposalContract.Commands {
        class ReleaseContainer extends Modify {
        }
    }
}


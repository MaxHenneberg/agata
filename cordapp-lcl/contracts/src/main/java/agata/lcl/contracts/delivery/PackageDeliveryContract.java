package agata.lcl.contracts.delivery;

import agata.bol.enums.BillOfLadingType;
import agata.bol.states.BillOfLadingState;
import agata.lcl.contracts.GenericProposalContract;
import agata.lcl.states.delivery.PackageDeliveryProposal;
import agata.lcl.states.delivery.PackageDeliveryState;
import agata.utils.ContractUtils;
import net.corda.core.contracts.Command;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public class PackageDeliveryContract extends GenericProposalContract {

    public static final String ID = "agata.lcl.contracts.delivery.PackageDeliveryContract";

    @Override
    protected void extendedVerifyPropose(@NotNull LedgerTransaction tx, @NotNull Command command) {
        requireThat(require -> {
            PackageDeliveryProposal proposal = tx.outputsOfType(PackageDeliveryProposal.class).get(0);
            PackageDeliveryState proposedState = proposal.getProposedState();
            require.using("Proposer must be the arrival party", proposal.getProposer().equals(proposedState.getArrivalParty()));
            require.using("Proposee must be the LCL company", proposal.getProposee().equals(proposedState.getLclCompany()));
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
        if (command.getValue() instanceof Commands.SetDeliveredGoods) {
            requireThat(require -> {
                PackageDeliveryProposal proposal = tx.outputsOfType(PackageDeliveryProposal.class).get(0);
                PackageDeliveryState proposedState = proposal.getProposedState();
                require.using("Proposer must be the LCL company", proposal.getProposer().equals(proposedState.getLclCompany()));
                require.using("Proposee must be the arrival party", proposal.getProposee().equals(proposedState.getArrivalParty()));

                PackageDeliveryState inputState = tx.inputsOfType(PackageDeliveryProposal.class).get(0).getProposedState();
                BillOfLadingState bol = ContractUtils.resolveBillOfLadingReference(tx, inputState.getHouseBolId());
                require.using("The passed bill of lading needs to be a house bill of lading", bol.getType() == BillOfLadingType.House);
                require.using("The LCL company must be the shipper in the house bill of lading", proposedState.getLclCompany().equals(bol.getShipper()));
                require.using("The arrival party must be the consignee in the house bill of lading", proposedState.getArrivalParty().equals(bol.getConsignee()));

                // Verify that the delivered goods match the goods defined in the house bill of lading
                require.using(
                        "Delivered goods must be equal to the goods listed in the house bill of lading",
                        proposedState.getDeliveredGoods().equals(bol.getGoodsList()));

                // Verify that all fields except the delivered goods remain unchanged
                boolean otherFieldsUnchanged = Objects.equals(inputState.getLinearId(), proposedState.getLinearId()) &&
                        Objects.equals(inputState.getArrivalParty(), proposedState.getArrivalParty()) &&
                        Objects.equals(inputState.getHouseBolId(), proposedState.getHouseBolId()) &&
                        Objects.equals(inputState.getLclCompany(), proposedState.getLclCompany());
                require.using("Other fields must remain unchanged", otherFieldsUnchanged);

                return null;
            });
            isValidCommand = true;
        }
        return isValidCommand;
    }

    public interface Commands extends GenericProposalContract.Commands {
        class SetDeliveredGoods extends Modify {
        }
    }
}

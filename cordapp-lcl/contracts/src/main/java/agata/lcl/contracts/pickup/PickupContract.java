package agata.lcl.contracts.pickup;

import agata.lcl.contracts.GenericProposalContract;
import agata.lcl.states.assignment.AssignmentState;
import agata.lcl.states.pickup.PickupProposal;
import net.corda.core.contracts.Command;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.requireThat;


public class PickupContract extends GenericProposalContract {
    public static String ID = "agata.lcl.contracts.pickup.PickupContract";

    @Override
    protected void extendedVerifyPropose(@NotNull LedgerTransaction tx, @NotNull Command command) {
        requireThat(require -> {
            PickupProposal pickupProposal = tx.outputsOfType(PickupProposal.class).get(0);
            AssignmentState assignmentState = resolveAssignmentState(pickupProposal, tx);
            require.using("Proposee must be Supplier", pickupProposal.getProposee().equals(pickupProposal.getProposedState().getSupplier()));
            require.using("Proposer must be LCL-Company", pickupProposal.getProposer().equals(pickupProposal.getProposedState().getLclCompany()));
            require.using("Supplier must be same as in Assignment State", pickupProposal.getProposedState().getSupplier().equals(assignmentState.getSupplier()));
            require.using("LCL-Company must be same as in Assignment State", pickupProposal.getProposedState().getLclCompany().equals(assignmentState.getLclCompany()));
            require.using("Buyer of assignmentState must be Buyer of pickupState", pickupProposal.getProposedState().getBuyer().equals(assignmentState.getBuyer()));
            require.using("PickedUp Goods must be Empty", pickupProposal.getProposedState().getPickedUpGoods().isEmpty());
            return null;
        });
    }

    @Override
    protected void extendedVerifyModify(@NotNull LedgerTransaction tx, @NotNull Command command) {
        requireThat(require -> {
            PickupProposal pickupProposal = tx.outputsOfType(PickupProposal.class).get(0);
            AssignmentState assignmentState = resolveAssignmentState(pickupProposal, tx);
            require.using("Picked-up Goods must be Equals to Expected Goods", pickupProposal.getProposedState().getPickedUpGoods().equals(assignmentState.getExpectedGoods()));
            return null;
        });
    }

    private AssignmentState resolveAssignmentState(PickupProposal pickupProposal, LedgerTransaction tx) {
        return (AssignmentState) pickupProposal.getProposedState().getReferenceToAssignmentProposal().resolve(tx).getState().getData();
    }

    public interface PickupCommands extends GenericProposalContract.Commands {
        class AddGoods extends Modify {
        }
    }
}

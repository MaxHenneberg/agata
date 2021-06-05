package agata.lcl.contracts.assignment;

import agata.lcl.contracts.GenericProposalContract;
import agata.lcl.states.assignment.AssignmentProposal;
import agata.lcl.states.assignment.AssignmentState;
import net.corda.core.contracts.Command;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public class AssignmentContract extends GenericProposalContract {
    public static final String ID = "agata.lcl.contracts.assignment.AssignmentContract";

    @Override
    protected void verifyPropose(@NotNull LedgerTransaction tx, @NotNull Command command) {
        super.verifyPropose(tx, command);
        requireThat(require -> {
            AssignmentProposal output = tx.outputsOfType(AssignmentProposal.class).get(0);
            AssignmentState proposedState = output.getProposedState();
            require.using("The arrival address is set", !Objects.isNull(proposedState.getArrivalAddress()));
            require.using("The arrival party is set", !Objects.isNull(proposedState.getArrivalParty()));
            require.using("The buyer is set", !Objects.isNull(proposedState.getBuyer()));
            require.using("The departure address is set", !Objects.isNull(proposedState.getDepartureAddress()));
            require.using("The expected goods are set", !Objects.isNull(proposedState.getExpectedGoods()));
            require.using("The LCL company is set", !Objects.isNull(proposedState.getLclCompany()));
            require.using("The supplier is set", !Objects.isNull(proposedState.getSupplier()));
            require.using("The status is set to SlotBooked", proposedState.getStatus().equals(AssignmentState.Status.SlotBooked));
            return null;
        });
    }

    @Override
    public void extendedVerify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        // Currently, there are no additional commands for this specific contract on top of the generic commands
        throw new IllegalArgumentException("Command of incorrect type");
    }

    public interface Commands extends GenericProposalContract.Commands {
    }
}

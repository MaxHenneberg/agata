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
    protected void extendedVerifyPropose(@NotNull LedgerTransaction tx, @NotNull Command command) {
        requireThat(require -> {
            AssignmentProposal proposal = tx.outputsOfType(AssignmentProposal.class).get(0);
            AssignmentState proposedState = proposal.getProposedState();
            require.using("The status is set to SlotBooked", proposedState.getStatus().equals(AssignmentState.Status.SlotBooked));
            require.using("Proposer must be the LCL company", proposal.getProposer().equals(proposedState.getLclCompany()));
            require.using("Proposee must be the buyer", proposal.getProposee().equals(proposedState.getBuyer()));
            return null;
        });
    }

    public interface Commands extends GenericProposalContract.Commands {

    }
}

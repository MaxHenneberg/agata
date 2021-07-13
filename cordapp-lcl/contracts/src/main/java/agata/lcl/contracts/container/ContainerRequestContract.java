package agata.lcl.contracts.container;

import agata.lcl.contracts.GenericProposalContract;
import agata.lcl.states.container.ContainerRequestProposal;
import agata.lcl.states.container.ContainerRequestState;
import net.corda.core.contracts.Command;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public class ContainerRequestContract extends GenericProposalContract {
    public static final String ID = "agata.lcl.contracts.container.ContainerRequestContract";

    @Override
    protected void extendedVerifyPropose(@NotNull LedgerTransaction tx, @NotNull Command command) {
        requireThat(require -> {
            ContainerRequestProposal proposal = tx.outputsOfType(ContainerRequestProposal.class).get(0);
            ContainerRequestState proposedState = proposal.getProposedState();
            require.using("Proposer must be the LCL company", proposal.getProposer().equals(proposedState.getLclCompany()));
            require.using("Proposee must be the shipping line", proposal.getProposee().equals(proposedState.getShippingLine()));
            require.using("Vessel name must not be set initially", proposedState.getVesselName() == null);
            require.using("Assigned container must not be set initially", proposedState.getContainer() == null);
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
        if (command.getValue() instanceof Commands.AssignContainer) {
            requireThat(require -> {
                ContainerRequestProposal proposal = tx.outputsOfType(ContainerRequestProposal.class).get(0);
                ContainerRequestState proposedState = proposal.getProposedState();
                require.using("Proposer must be the shipping line", proposal.getProposer().equals(proposedState.getShippingLine()));
                require.using("Proposee must be the LCL company", proposal.getProposee().equals(proposedState.getLclCompany()));

                // Check that fields to be modified (such as vessel name and container) are set is already done with annotations in the state
                // Verify that all other fields remain unchanged
                // TODO: Use a generic approach for this
                ContainerRequestState inputState = tx.inputsOfType(ContainerRequestProposal.class).get(0).getProposedState();
                boolean otherFieldsUnchanged = Objects.equals(inputState.getLinearId(), proposedState.getLinearId()) &&
                        Objects.equals(inputState.getShippingLine(), proposedState.getShippingLine()) &&
                        Objects.equals(inputState.getLclCompany(), proposedState.getLclCompany()) &&
                        Objects.equals(inputState.getLclDestination(), proposedState.getLclDestination()) &&
                        Objects.equals(inputState.getPortOfLoading(), proposedState.getPortOfLoading()) &&
                        Objects.equals(inputState.getPortOfDischarge(), proposedState.getPortOfDischarge()) &&
                        Objects.equals(inputState.getForwardingAgentNo(), proposedState.getForwardingAgentNo()) &&
                        Objects.equals(inputState.getRequestedType(), proposedState.getRequestedType());
                require.using("Other fields must remain unchanged", otherFieldsUnchanged);

                require.using("The type of the assigned container must match the requested type", proposedState.getContainer().getType() == inputState.getRequestedType());

                return null;
            });

            isValidCommand = true;
        }
        return isValidCommand;
    }

    public interface Commands extends GenericProposalContract.Commands {
        class AssignContainer extends Modify {
        }
    }
}

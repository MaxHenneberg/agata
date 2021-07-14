package agata.lcl.contracts;

import agata.lcl.states.Proposal;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.Requirements;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public abstract class GenericProposalContract extends BaseContract {

    @Override
    public void verifyCommands(LedgerTransaction tx) throws IllegalArgumentException {
        Command command = tx.getCommand(0);
        CommandData commandData = command.getValue();

        boolean isValidCommand = false;
        if (commandData instanceof Commands.Propose) {
            verifyPropose(tx, command);
            extendedVerifyPropose(tx, command);
            isValidCommand = true;
        }
        if (commandData instanceof Commands.Modify) {
            verifyModify(tx, command);
            extendedVerifyModify(tx, command);
            isValidCommand = true;
        }
        if (commandData instanceof Commands.Accept) {
            verifyAccept(tx, command);
            extendedVerifyAccept(tx, command);
            isValidCommand = true;
        }
        boolean additionalCommandIsValid = verifyAdditionalCommands(tx);
        if (!isValidCommand && !additionalCommandIsValid) {
            throw new IllegalArgumentException("Command of incorrect type");
        }
    }

    // Can be overridden by subclasses when implementing additional commands
    protected boolean verifyAdditionalCommands(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        return true;
    }

    private void verifyPropose(@NotNull LedgerTransaction tx, @NotNull Command command) {
        requireThat(require -> {
            require.using("There are no inputs", tx.getInputs().isEmpty());
            require.using("Only one output state should be created", tx.getOutputs().size() == 1);

            Proposal output = getValidatedOutput(require, tx);
            checkSigners(require, command, output);

            return null;
        });
    }

    protected abstract void extendedVerifyPropose(@NotNull LedgerTransaction tx, @NotNull Command command);

    private void verifyModify(@NotNull LedgerTransaction tx, @NotNull Command command) {
        requireThat(require -> {
            require.using("There is exactly one input", tx.getInputStates().size() == 1);
            require.using("The single input is of type Proposal", tx.inputsOfType(Proposal.class).size() == 1);

            Proposal output = getValidatedOutput(require, tx);
            Proposal input = tx.inputsOfType(Proposal.class).get(0);
            require.using("New proposal needs to be the same type as old proposal", input.getProposedState().getClass().equals(output.getProposedState().getClass()));
            require.using("New proposal need to differ from old proposal", !input.getProposedState().equals(output.getProposedState()));

            checkSigners(require, command, input);
            return null;
        });
    }

    protected abstract void extendedVerifyModify(@NotNull LedgerTransaction tx, @NotNull Command command);

    private void verifyAccept(@NotNull LedgerTransaction tx, @NotNull Command command) {
        requireThat(require -> {
            require.using("There is at least one input", tx.getInputStates().size() >= 1);
            require.using("At least one Proposal was given as input", tx.inputsOfType(Proposal.class).size() == 1);

            Proposal input = tx.inputsOfType(Proposal.class).get(0);

            require.using("There is exactly one output", tx.getOutputs().size() == 1);
            require.using("The type of the output is that of the proposed state", tx.outputsOfType(input.getProposedState().getClass()).size() == 1);
            require.using("There is exactly one command", tx.getCommands().size() == 1);
            require.using("There is no timestamp", tx.getTimeWindow() == null);

            ContractState output = tx.getOutput(0);
            require.using("Output needs to be of same class as proposal ", output.getClass().equals(input.getProposedState().getClass()));
            require.using("Proposal needs to be equal to output", input.getProposedState().equals(output));

            checkSigners(require, command, input);
            return null;
        });
    }

    protected abstract void extendedVerifyAccept(@NotNull LedgerTransaction tx, @NotNull Command command);

    private void checkSigners(Requirements require, Command command, Proposal proposal) {
        require.using("The proposer is a required signer", command.getSigners().contains(proposal.getProposer().getOwningKey()));
        require.using("The proposee is a required signer", command.getSigners().contains(proposal.getProposee().getOwningKey()));
    }

    private Proposal getValidatedOutput(Requirements require, @NotNull LedgerTransaction tx) {
        require.using("There is exactly one command", tx.getCommands().size() == 1);
        require.using("There is exactly one output", tx.getOutputs().size() == 1);
        require.using("The single output is of type Proposal", tx.outputsOfType(Proposal.class).size() == 1);
        require.using("There is no timestamp", tx.getTimeWindow() == null);
        return tx.outputsOfType(Proposal.class).get(0);
    }

    public interface Commands extends CommandData {
        class All implements Commands {
        }

        class Propose implements Commands {
        }

        class Accept implements Commands {
        }

        class Modify implements Commands {
        }
    }
}

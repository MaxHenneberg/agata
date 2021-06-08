package agata.lcl.contracts;

import agata.lcl.contracts.annotations.MandatoryForContract;
import agata.lcl.contracts.annotations.NotBlankForContract;
import agata.lcl.states.Proposal;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public abstract class GenericProposalContract implements Contract {
    public static String ID = "agata.lcl.contracts.GenericProposalContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        final Command command = tx.getCommand(0);
        for (ContractState contractState : tx.getInputStates()) {
            try {
                GenericProposalContractUtils.checkMandatoryFields(contractState, command.getValue(), true);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        for (ContractState contractState : tx.getOutputStates()) {
            try {
                GenericProposalContractUtils.checkMandatoryFields(contractState, command.getValue(), false);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        boolean isValidCommand = false;
        if (command.getValue() instanceof Commands.Propose) {
            verifyPropose(tx, command);
            extendedVerifyPropose(tx, command);
            isValidCommand = true;
        }
        if (command.getValue() instanceof Commands.Modify) {
            verifyModify(tx, command);
            extendedVerifyModify(tx, command);
            isValidCommand = true;
        }
        if (command.getValue() instanceof Commands.Accept) {
            verifyAccept(tx, command);
            extendedVerifyAccept(tx, command);
            isValidCommand = true;
        }
        boolean additionalCommandIsValid = verifyAdditionalCommands(tx);
        if (!isValidCommand && !additionalCommandIsValid) {
            throw new IllegalArgumentException("Command of incorrect type");
        }
    }

    protected boolean verifyAdditionalCommands(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        return true;
    }

    private void verifyPropose(@NotNull LedgerTransaction tx, @NotNull Command command) {
        requireThat(require -> {
            require.using("There are no inputs", tx.getInputs().isEmpty());
            require.using("Only one output state should be created.", tx.getOutputs().size() == 1);
            require.using("The single output is of type Proposal", tx.getOutput(0) instanceof Proposal);
            require.using("There is exactly one command", tx.getCommands().size() == 1);
            require.using("There is no timestamp", tx.getTimeWindow() == null);
            Proposal output = tx.outputsOfType(Proposal.class).get(0);
            require.using("The proposer is a required signer", command.getSigners().contains(output.getProposer().getOwningKey()));
            require.using("The proposee is a required signer", command.getSigners().contains(output.getProposee().getOwningKey()));
            return null;
        });
    }

    protected void extendedVerifyPropose(@NotNull LedgerTransaction tx, @NotNull Command command) {

    }

    private void verifyModify(@NotNull LedgerTransaction tx, @NotNull Command command) {
        requireThat(require -> {
            require.using("There is exactly one input", tx.getInputStates().size() == 1);
            require.using("The single input is of type GenericProposalState", tx.inputsOfType(Proposal.class).size() == 1);
            require.using("There is exactly one output", tx.getOutputs().size() == 1);
            require.using("The single output is not of type GenericProposalState", tx.outputsOfType(Proposal.class).size() == 1);
            require.using("There is exactly one command", tx.getCommands().size() == 1);
            require.using("There is no timestamp", tx.getTimeWindow() == null);

            Proposal input = tx.inputsOfType(Proposal.class).get(0);
            Proposal output = tx.outputsOfType(Proposal.class).get(0);

            require.using("New Proposal needs to be the same type as old Proposal", input.getProposedState().getClass().equals(output.getProposedState().getClass()));
            require.using("New Proposal need to differ from old Proposal", !input.getProposedState().equals(output.getProposedState()));

            require.using("The proposer is a required signer", command.getSigners().contains(input.getProposer().getOwningKey()));
            require.using("The proposee is a required signer", command.getSigners().contains(input.getProposee().getOwningKey()));
            return null;
        });
    }

    protected void extendedVerifyModify(@NotNull LedgerTransaction tx, @NotNull Command command) {

    }

    private void verifyAccept(@NotNull LedgerTransaction tx, @NotNull Command command) {
        requireThat(require -> {
            require.using("There is exactly one input", tx.getInputStates().size() == 1);
            require.using("The single input is of type ProposalState", tx.inputsOfType(Proposal.class).size() == 1);
            Proposal input = tx.inputsOfType(Proposal.class).get(0);
            require.using("There is exactly one output", tx.getOutputs().size() == 1);
            require.using("The type of the output is that of the proposed state", tx.outputsOfType(input.getProposedState().getClass()).size() == 1);
            require.using("There is exactly one command", tx.getCommands().size() == 1);
            require.using("There is no timestamp", tx.getTimeWindow() == null);

            ContractState output = tx.getOutput(0);

            require.using("Output needs to be of same Class as Proposal ", output.getClass().equals(input.getProposedState().getClass()));
            require.using("Proposal needs to be equal to Output", input.getProposedState().equals(output));

            require.using("The proposer is a required signer", command.getSigners().contains(input.getProposer().getOwningKey()));
            require.using("The proposee is a required signer", command.getSigners().contains(input.getProposee().getOwningKey()));
            return null;
        });
    }

    protected void extendedVerifyAccept(@NotNull LedgerTransaction tx, @NotNull Command command) {

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

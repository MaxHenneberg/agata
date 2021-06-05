package agata.lcl.contracts;

import agata.lcl.states.Proposal;
import net.corda.core.contracts.*;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public abstract class GenericProposalContract implements Contract {
    public static String ID = "agata.lcl.contracts.GenericProposalContract";

    public abstract void extendedVerify(@NotNull LedgerTransaction tx) throws IllegalArgumentException;


    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        final Command command = tx.getCommand(0);

        if (command.getValue() instanceof Commands.Propose) {
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
        } else if (command.getValue() instanceof Commands.Modify) {
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
        } else if (command.getValue() instanceof Commands.Accept) {
            requireThat(require -> {
                require.using("There is exactly one input", tx.getInputStates().size() == 1);
                require.using("The single input is of type ProposalState", tx.inputsOfType(Proposal.class).size() == 1);
                Proposal input = tx.inputsOfType(Proposal.class).get(0);
                require.using("There is exactly one output", tx.getOutputs().size() == 1);
                require.using("The single output is of type TradeState", tx.outputsOfType(input.getProposedState().getClass()).size() == 1);
                require.using("There is exactly one command", tx.getCommands().size() == 1);
                require.using("There is no timestamp", tx.getTimeWindow() == null);

                ContractState output = tx.getOutput(0);

                require.using("Output needs to be of same Class as Proposal ", output.getClass().equals(input.getProposedState().getClass()));
                require.using("Proposal needs to be equal to Output", input.getProposedState().equals(output));

                require.using("The proposer is a required signer", command.getSigners().contains(input.getProposer().getOwningKey()));
                require.using("The proposee is a required signer", command.getSigners().contains(input.getProposee().getOwningKey()));
                return null;
            });
        } else {
            extendedVerify(tx);
        }
    }

    public interface Commands extends CommandData {
        class Propose implements Commands {
        }

        class Accept implements Commands {
        }

        class Modify implements Commands {
        }
    }
}

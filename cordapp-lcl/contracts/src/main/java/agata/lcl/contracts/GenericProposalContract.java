package agata.lcl.contracts;

import agata.lcl.contracts.goodspickup.PickupContract;
import agata.lcl.states.GenericProposalState;
import net.corda.core.contracts.*;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public class GenericProposalContract implements Contract {
    public static String ID = "agata.lcl.contracts.GenericProposalContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        final CommandWithParties command = tx.getCommands().get(0);
        if (command.getValue() instanceof GenericProposalContract.Commands.Propose) {
            requireThat(require -> {
                require.using("There are no inputs", tx.getInputs().isEmpty());
                require.using("Only one output state should be created.", tx.getOutputs().size() == 1);
                require.using("The single output is of type ProposalState", tx.outputsOfType(GenericProposalState.class).size() == 1);
                require.using("There is exactly one command", tx.getCommands().size() == 1);
                require.using("There is no timestamp", tx.getTimeWindow() == null);
                GenericProposalState output = tx.outputsOfType(GenericProposalState.class).get(0);
                require.using("The proposer is a required signer", command.getSigners().contains(output.getProposer().getOwningKey()));
                require.using("The proposee is a required signer", command.getSigners().contains(output.getPropose().getOwningKey()));
                return null;
            });
        } else if (command.getValue() instanceof GenericProposalContract.Commands.Modify) {
            requireThat(require -> {
                require.using("There is exactly one input", tx.getInputStates().size() == 1);
                require.using("The single input is of type GenericProposalState", tx.inputsOfType(GenericProposalState.class).size() == 1);
                require.using("There is exactly one output", tx.getOutputs().size() == 1);
                require.using("The single output is not of type GenericProposalState", tx.outputsOfType(GenericProposalState.class).size() == 1);
                require.using("There is exactly one command", tx.getCommands().size() == 1);
                require.using("There is no timestamp", tx.getTimeWindow() == null);

                GenericProposalState input = tx.inputsOfType(GenericProposalState.class).get(0);
                GenericProposalState output = tx.outputsOfType(GenericProposalState.class).get(0);

                require.using("New Proposal need to differ from old Proposal", !input.getProposal().equals(output.getProposal()));

                require.using("The proposer is a required signer", command.getSigners().contains(input.getProposer().getOwningKey()));
                require.using("The proposee is a required signer", command.getSigners().contains(input.getPropose().getOwningKey()));
                return null;
            });
        } else if (command.getValue() instanceof GenericProposalContract.Commands.Accept) {
            requireThat(require -> {
                require.using("There is exactly one input", tx.getInputStates().size() == 1);
                require.using("The single input is of type ProposalState", tx.inputsOfType(GenericProposalState.class).size() == 1);
                require.using("There is exactly one output", tx.getOutputs().size() == 1);
                require.using("The single output is of type TradeState", tx.outputsOfType(GenericProposalState.class).size() == 0);
                require.using("There is exactly one command", tx.getCommands().size() == 1);
                require.using("There is no timestamp", tx.getTimeWindow() == null);

                GenericProposalState input = tx.inputsOfType(GenericProposalState.class).get(0);
                ContractState output = tx.getOutput(0);

                require.using("Output needs to be of Class " + input.getProposal().getClass(), output.getClass().equals(input.getProposal().getClass()));
                require.using("Proposal needs to be equal to Output", input.getProposal().equals(output));

                require.using("The proposer is a required signer", command.getSigners().contains(input.getProposer().getOwningKey()));
                require.using("The proposee is a required signer", command.getSigners().contains(input.getPropose().getOwningKey()));
                return null;
            });
        } else {
            throw new IllegalArgumentException("Command of incorrect type");
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

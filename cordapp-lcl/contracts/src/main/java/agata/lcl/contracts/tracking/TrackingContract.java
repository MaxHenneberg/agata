package agata.lcl.contracts.tracking;

import agata.lcl.contracts.BaseContract;
import agata.lcl.enums.TrackingStatus;
import agata.lcl.states.tracking.ShippingTrackingState;
import agata.lcl.states.tracking.TrackingState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Requirements;
import net.corda.core.transactions.LedgerTransaction;

import java.util.Objects;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public class TrackingContract extends BaseContract {

    public static final String ID = "agata.lcl.contracts.tracking.TrackingContract";

    @Override
    public void verifyCommands(LedgerTransaction tx) throws IllegalArgumentException {

        Command command = tx.getCommand(0);
        CommandData commandData = command.getValue();

        if (commandData instanceof Commands.Create) {
            requireThat(require -> {
                require.using("No inputs should be consumed when issuing a tracking state", tx.getInputs().isEmpty());
                require.using("Only one output state should be created, which is of type TrackingState",
                        tx.getOutputs().size() == 1 && tx.getOutput(0) instanceof TrackingState);

                TrackingState output = tx.outputsOfType(TrackingState.class).get(0);
                require.using("The LCL company is a required signer", command.getSigners().contains(output.getLclCompany().getOwningKey()));
                require.using("The buyer is a required signer", command.getSigners().contains(output.getBuyer().getOwningKey()));
                require.using("The status is set to SlotBooked", output.getStatus() == TrackingStatus.SlotBooked);

                return null;
            });
        } else if (commandData instanceof Commands.SetPickupCompleted) {
            requireThat(require -> {
                require.using("There is exactly one input, which is of type TrackingState",
                        tx.getInputStates().size() == 1 && tx.getInput(0) instanceof TrackingState);
                require.using("Only one output state should be created, which is of type TrackingState",
                        tx.getOutputs().size() == 1 && tx.getOutput(0) instanceof TrackingState);

                TrackingState output = tx.outputsOfType(TrackingState.class).get(0);
                require.using("The status is set to PickupCompleted", output.getStatus() == TrackingStatus.PickupCompleted);

                require.using("The LCL company is a required signer", command.getSigners().contains(output.getLclCompany().getOwningKey()));
                require.using("The supplier is a required signer", command.getSigners().contains(output.getSupplier().getOwningKey()));

                TrackingState input = tx.inputsOfType(TrackingState.class).get(0);
                require.using("Except for the status previously set fields remain unchanged", isTrackingStateUnchanged(input, output));

                return null;
            });
        } else if (commandData instanceof Commands.SetContainerAssigned) {
            requireThat(require -> {
                require.using("There is exactly one input, which is of type TrackingState",
                        tx.getInputStates().size() == 1 && tx.getInput(0) instanceof TrackingState);
                require.using("Only one output state should be created, which is of type ShippingTrackingState",
                        tx.getOutputs().size() == 1 && tx.getOutput(0) instanceof ShippingTrackingState);

                ShippingTrackingState output = tx.outputsOfType(ShippingTrackingState.class).get(0);
                require.using("The status is set to ContainerAssigned", output.getStatus() == TrackingStatus.ContainerAssigned);
                require.using("The shipping line is set", output.getShippingLine() != null);
                require.using("The current port is set", output.getCurrentPort() != null);

                require.using("The LCL company is a required signer", command.getSigners().contains(output.getLclCompany().getOwningKey()));
                require.using("The shipping line is a required signer", command.getSigners().contains(output.getShippingLine().getOwningKey()));

                TrackingState input = tx.inputsOfType(TrackingState.class).get(0);
                require.using("Except for the status and new fields, previously set fields remain unchanged", isTrackingStateUnchanged(input, output));

                return null;
            });
        } else if (commandData instanceof Commands.SetLoadedOnShip) {
            requireThat(require -> {
                assertShipmentCommand(TrackingStatus.LoadedOnShip, require, tx, command);
                return null;
            });
        } else if (commandData instanceof Commands.SetDeconsolidated) {
            requireThat(require -> {
                assertShipmentCommand(TrackingStatus.Deconsolidated, require, tx, command);
                return null;
            });
        } else {
            throw new IllegalArgumentException("Command of incorrect type");
        }
    }

    private void assertShipmentCommand(TrackingStatus status, Requirements require, LedgerTransaction tx, Command command){
        require.using("There is exactly one input, which is of type ShippingTrackingState",
                tx.getInputStates().size() == 1 && tx.getInput(0) instanceof ShippingTrackingState);
        require.using("Only one output state should be created, which is of type ShippingTrackingState",
                tx.getOutputs().size() == 1 && tx.getOutput(0) instanceof ShippingTrackingState);

        ShippingTrackingState output = tx.outputsOfType(ShippingTrackingState.class).get(0);
        require.using("The status is set to " + status.toString(), output.getStatus() == status);

        require.using("The LCL company is a required signer", command.getSigners().contains(output.getLclCompany().getOwningKey()));
        require.using("The shipping line is a required signer", command.getSigners().contains(output.getShippingLine().getOwningKey()));

        ShippingTrackingState input = tx.inputsOfType(ShippingTrackingState.class).get(0);
        require.using("Except for the status previously set fields remain unchanged", isShippingTrackingStateUnchanged(input, output));
    }

    private boolean isTrackingStateUnchanged(TrackingState input, TrackingState output) {
        return Objects.equals(output.getLinearId(), input.getLinearId()) &&
                Objects.equals(output.getBuyer(), input.getBuyer()) &&
                Objects.equals(output.getLclCompany(), input.getLclCompany());
    }

    private boolean isShippingTrackingStateUnchanged(ShippingTrackingState input, ShippingTrackingState output) {
        boolean areBasePropertiesUnchanged = Objects.equals(output.getLinearId(), input.getLinearId()) &&
                Objects.equals(output.getBuyer(), input.getBuyer()) &&
                Objects.equals(output.getLclCompany(), input.getLclCompany());

        return areBasePropertiesUnchanged &&
                Objects.equals(output.getShippingLine(), input.getShippingLine()) &&
                Objects.equals(output.getCurrentPort(), input.getCurrentPort());
    }

    public interface Commands extends CommandData {
        class Create implements Commands {
        }

        class SetPickupCompleted implements Commands {
        }

        class SetContainerAssigned implements Commands {
        }

        class SetLoadedOnShip implements Commands {
        }

        class SetDeconsolidated implements Commands {
        }

    }
}

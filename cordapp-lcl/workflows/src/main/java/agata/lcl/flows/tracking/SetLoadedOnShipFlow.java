package agata.lcl.flows.tracking;

import agata.lcl.contracts.tracking.TrackingContract;
import agata.lcl.enums.TrackingStatus;
import agata.lcl.flows.LclFlowUtils;
import agata.lcl.states.tracking.ShippingTrackingState;
import agata.lcl.states.tracking.TrackingState;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.transactions.SignedTransaction;

public class SetLoadedOnShipFlow {

    @InitiatingFlow
    public static class Initiator extends FlowLogic<SignedTransaction> {

        private final UniqueIdentifier stateId;

        public Initiator(UniqueIdentifier stateId) {
            this.stateId = stateId;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {
            StateAndRef<ShippingTrackingState> input = LclFlowUtils.resolveIdToStateRef(stateId, this, ShippingTrackingState.class);
            ShippingTrackingState inputState = input.getState().getData();
            if (!getOurIdentity().equals(inputState.getShippingLine())) {
                throw new FlowException("Flow can only be executed by correct shipping line");
            }

            ShippingTrackingState output = new ShippingTrackingState(inputState);
            output.setStatus(TrackingStatus.LoadedOnShip);

            return subFlow(new GenericTrackingUpdateFlow.Initiator(input, output, new TrackingContract.Commands.SetLoadedOnShip()));
        }
    }

}

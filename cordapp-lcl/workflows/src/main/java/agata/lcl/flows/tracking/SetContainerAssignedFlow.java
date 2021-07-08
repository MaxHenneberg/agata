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
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;

public class SetContainerAssignedFlow {

    @InitiatingFlow
    public static class Initiator extends FlowLogic<SignedTransaction> {

        private final UniqueIdentifier stateId;
        private final Party shippingLine;
        private final String lastPort;

        public Initiator(UniqueIdentifier stateId, Party shippingLine, String lastPort) {
            this.stateId = stateId;
            this.shippingLine = shippingLine;
            this.lastPort = lastPort;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {
            StateAndRef<TrackingState> input = LclFlowUtils.resolveIdToStateRef(stateId, this, TrackingState.class);
            TrackingState inputState = input.getState().getData();
            if (!getOurIdentity().equals(inputState.getLclCompany())) {
                throw new FlowException("Flow can only be executed by correct LCL Company");
            }

            ShippingTrackingState output = new ShippingTrackingState(inputState.getLinearId(), inputState.getLclCompany(), inputState.getBuyer(), inputState.getSupplier(), shippingLine, lastPort, TrackingStatus.ContainerAssigned);

            return subFlow(new GenericTrackingUpdateFlow.Initiator(input, output, new TrackingContract.Commands.SetContainerAssigned()));
        }
    }
}

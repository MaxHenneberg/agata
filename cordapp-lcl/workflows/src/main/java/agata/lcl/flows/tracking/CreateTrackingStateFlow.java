package agata.lcl.flows.tracking;

import agata.lcl.contracts.tracking.TrackingContract;
import agata.lcl.states.tracking.TrackingState;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;

public class CreateTrackingStateFlow {

    @InitiatingFlow
    public static class Initiator extends FlowLogic<SignedTransaction> {

        private final Party buyer;
        private final Party supplier;

        public Initiator(Party buyer, Party supplier) {
            this.buyer = buyer;
            this.supplier = supplier;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {
            TrackingState output = new TrackingState(getOurIdentity(), this.buyer, this.supplier);
            return subFlow(new GenericTrackingUpdateFlow.Initiator(null, output, new TrackingContract.Commands.Create()));
        }
    }
}

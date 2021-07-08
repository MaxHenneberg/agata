package agata.lcl.flows.delivery;

import agata.bol.states.BillOfLadingState;
import agata.lcl.contracts.delivery.PackageDeliveryContract;
import agata.lcl.flows.AcceptFlow;
import agata.lcl.flows.LclFlowUtils;
import agata.lcl.flows.tracking.SetGoodsDeliveredFlow;
import agata.lcl.states.delivery.PackageDeliveryProposal;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.transactions.SignedTransaction;

import java.util.Collections;

public class AcceptDeliveryFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<SignedTransaction> {
        private final UniqueIdentifier proposalId;
        private final UniqueIdentifier trackingStateId;

        public Initiator(UniqueIdentifier proposalId, UniqueIdentifier trackingStateId) {
            this.proposalId = proposalId;
            this.trackingStateId = trackingStateId;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            PackageDeliveryProposal proposal = LclFlowUtils.resolveStateId(PackageDeliveryProposal.class, this, proposalId);

            if (!getOurIdentity().equals(proposal.getProposedState().getArrivalParty())) {
                throw new FlowException("Flow can only be executed by the defined arrival party");
            }

            // Update tracking state
            subFlow(new SetGoodsDeliveredFlow.Initiator(trackingStateId));

            // Add house bill of lading as additional input in order to consume this state
            final StateAndRef<BillOfLadingState> houseBolRef = LclFlowUtils.resolveIdToStateRef(proposal.getProposedState().getHouseBolId(), this, BillOfLadingState.class);
            return subFlow(new AcceptFlow.Initiator(
                    this.proposalId,
                    Collections.singletonList(houseBolRef),
                    new PackageDeliveryContract.Commands.Accept()));
        }
    }

}

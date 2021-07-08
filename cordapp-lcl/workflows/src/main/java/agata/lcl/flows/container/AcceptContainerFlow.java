package agata.lcl.flows.container;

import agata.lcl.flows.AcceptFlow;
import agata.lcl.flows.LclFlowUtils;
import agata.lcl.flows.tracking.SetContainerAssignedFlow;
import agata.lcl.states.container.ContainerRequestProposal;
import agata.lcl.states.container.ContainerRequestState;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.transactions.SignedTransaction;

public class AcceptContainerFlow {

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
            ContainerRequestProposal proposal = LclFlowUtils.resolveStateId(ContainerRequestProposal.class, this, this.proposalId);
            ContainerRequestState proposedState = proposal.getProposedState();

            if (!getOurIdentity().equals(proposedState.getLclCompany())) {
                throw new FlowException("Flow can only be executed by correct LCL company");
            }

            // Update responsibility in tracking state
            subFlow(new SetContainerAssignedFlow.Initiator(this.trackingStateId, proposedState.getShippingLine(), proposedState.getPortOfLoading()));

            return subFlow(new AcceptFlow.Initiator(this.proposalId));
        }

    }

}

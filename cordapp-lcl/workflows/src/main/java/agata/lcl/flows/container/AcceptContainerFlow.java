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

import java.util.List;

public class AcceptContainerFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<SignedTransaction> {

        private final UniqueIdentifier proposalId;
        private final List<UniqueIdentifier> trackingStateIds;

        public Initiator(UniqueIdentifier proposalId, List<UniqueIdentifier> trackingStateIds) {
            this.proposalId = proposalId;
            this.trackingStateIds = trackingStateIds;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            ContainerRequestProposal proposal = LclFlowUtils.resolveStateId(ContainerRequestProposal.class, this, this.proposalId);
            ContainerRequestState proposedState = proposal.getProposedState();

            if (!getOurIdentity().equals(proposedState.getLclCompany())) {
                throw new FlowException("Flow can only be executed by correct LCL company");
            }

            // Update responsibility in each tracking state of a package associated to this container
            for (UniqueIdentifier trackingStateId : this.trackingStateIds) {
                subFlow(new SetContainerAssignedFlow.Initiator(trackingStateId, proposedState.getShippingLine(), proposedState.getPortOfLoading()));
            }

            return subFlow(new AcceptFlow.Initiator(this.proposalId));
        }

    }

}

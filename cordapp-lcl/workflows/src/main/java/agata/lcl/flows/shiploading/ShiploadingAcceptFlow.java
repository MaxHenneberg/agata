package agata.lcl.flows.shiploading;

import agata.lcl.contracts.GenericProposalContract;
import agata.lcl.flows.AcceptFlow;
import agata.lcl.flows.LclFlowUtils;
import agata.lcl.flows.tracking.SetLoadedOnShipFlow;
import agata.lcl.states.container.ContainerRequestState;
import agata.lcl.states.shiploading.ShiploadingProposal;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.transactions.SignedTransaction;

import java.util.Collections;
import java.util.List;

public class ShiploadingAcceptFlow {
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
            ShiploadingProposal proposal = LclFlowUtils.resolveStateId(ShiploadingProposal.class, this, proposalId);
            final StateAndRef<ContainerRequestState> containerRequestStateStateAndRef = LclFlowUtils
                    .resolveIdToStateRef(proposal.getContainerId(), this, ContainerRequestState.class);
            final ContainerRequestState containerRequestState = containerRequestStateStateAndRef.getState().getData();

            if (!containerRequestState.getShippingLine().equals(getOurIdentity())) {
                throw new FlowException("Flow can only be executed by correct Shipping Line");
            }

            // Update responsibility in all tracking states associated to LCL contracts/assignments of goods within the container
            for (UniqueIdentifier stateId : trackingStateIds) {
                subFlow(new SetLoadedOnShipFlow.Initiator(stateId));
            }

            return subFlow(new AcceptFlow.Initiator(this.proposalId, Collections.singletonList(containerRequestStateStateAndRef),
                    new GenericProposalContract.Commands.Accept()));
        }
    }
}

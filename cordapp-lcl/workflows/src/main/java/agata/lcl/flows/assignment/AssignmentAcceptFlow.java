package agata.lcl.flows.assignment;

import agata.lcl.flows.AcceptFlow;
import agata.lcl.flows.LclFlowUtils;
import agata.lcl.states.assignment.AssignmentProposal;
import agata.lcl.states.assignment.AssignmentState;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.transactions.SignedTransaction;

public class AssignmentAcceptFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<net.corda.core.transactions.SignedTransaction> {

        private final UniqueIdentifier proposalId;

        public Initiator(UniqueIdentifier proposalId) {
            this.proposalId = proposalId;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            AssignmentState proposedState = LclFlowUtils.resolveStateId(AssignmentProposal.class, this, this.proposalId).getProposedState();

            if (!getOurIdentity().equals(proposedState.getBuyer())) {
                throw new FlowException("Flow can only be executed by correct buyer");
            }

            return subFlow(new AcceptFlow.Initiator(this.proposalId));
        }

    }

}

package agata.lcl.flows.container;

import agata.lcl.flows.AcceptFlow;
import agata.lcl.flows.LclFlowUtils;
import agata.lcl.states.container.ContainerRequestProposal;
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

        public Initiator(UniqueIdentifier proposalId) {
            this.proposalId = proposalId;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {

            ContainerRequestProposal state = LclFlowUtils.resolveStateId(ContainerRequestProposal.class, this, this.proposalId);
            if (!getOurIdentity().equals(state.getProposedState().getLclCompany())) {
                throw new FlowException("Flow can only be executed by correct LCL company");
            }

            return subFlow(new AcceptFlow.Initiator(this.proposalId));
        }

    }

}

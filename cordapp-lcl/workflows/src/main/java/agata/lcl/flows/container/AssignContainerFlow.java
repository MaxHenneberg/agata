package agata.lcl.flows.container;

import agata.bol.dataholder.ContainerInformation;
import agata.lcl.contracts.container.ContainerRequestContract;
import agata.lcl.flows.LclFlowUtils;
import agata.lcl.flows.ModifyFlow;
import agata.lcl.states.container.ContainerRequestProposal;
import agata.lcl.states.container.ContainerRequestState;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.transactions.SignedTransaction;

public class AssignContainerFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<SignedTransaction> {
        private final UniqueIdentifier proposalId;
        private final String vesselName;
        private final ContainerInformation container;

        public Initiator(UniqueIdentifier proposalId, String vesselName, ContainerInformation container) {
            this.proposalId = proposalId;
            this.vesselName = vesselName;
            this.container = container;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            final ContainerRequestProposal proposal = LclFlowUtils.resolveProposalId(ContainerRequestProposal.class, this, this.proposalId);

            final ContainerRequestState proposedState = proposal.getProposedState();
            final ContainerRequestState counterProposalState = new ContainerRequestState(proposedState);
            counterProposalState.setVesselName(this.vesselName);
            counterProposalState.setContainer(this.container);

            // Important: Use the constructor with which you can pass the linear id.
            // Otherwise a new ID would be generated and the counterproposal would no longer have any reference to the original proposal.
            ContainerRequestProposal counterProposal = new ContainerRequestProposal(getOurIdentity(), counterProposalState.getLclCompany(), counterProposalState, this.proposalId);
            return subFlow(new ModifyFlow.Initiator(this.proposalId, counterProposal, new ContainerRequestContract.Commands.AssignContainer()));
        }
    }
}

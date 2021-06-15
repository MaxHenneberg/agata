package agata.lcl.flows.container;

import agata.bol.enums.ContainerType;
import agata.lcl.flows.ProposalFlow;
import agata.lcl.states.container.ContainerRequestProposal;
import agata.lcl.states.container.ContainerRequestState;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;

public class ContainerRequestProposalFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<UniqueIdentifier> {

        private final Party shippingLine;
        private final String portOfLoading;
        private final String portOfDischarge;
        private final String forwardingAgentNo;
        private final ContainerType requestedType;
        private final Party lclDestination; // Can be the same as shipper (LCL company), but also another LCL company

        public Initiator(Party shippingLine, Party lclDestination, String portOfLoading, String portOfDischarge, String forwardingAgentNo, ContainerType requestedType) {
            this.shippingLine = shippingLine;
            this.lclDestination = lclDestination;
            this.portOfLoading = portOfLoading;
            this.portOfDischarge = portOfDischarge;
            this.forwardingAgentNo = forwardingAgentNo;
            this.requestedType = requestedType;
        }

        @Override
        @Suspendable
        public UniqueIdentifier call() throws FlowException {
            Party lclCompany = getOurIdentity();
            // Leave fields like vessel name and container unset because this is what the shipping line will modify
            ContainerRequestState state = new ContainerRequestState(this.shippingLine, lclCompany, this.lclDestination, this.portOfLoading, this.portOfDischarge, this.forwardingAgentNo, this.requestedType, null, null);
            ContainerRequestProposal proposal = new ContainerRequestProposal(lclCompany, this.shippingLine, state);
            return subFlow(new ProposalFlow.Initiator(proposal));
        }
    }

}

package agata.lcl.flows.deconsolidation;

import agata.bol.states.BillOfLadingState;
import agata.lcl.contracts.deconsolidation.DeconsolidationContract;
import agata.lcl.flows.LclFlowUtils;
import agata.lcl.flows.ProposalFlow;
import agata.lcl.states.deconsolidation.DeconsolidationProposal;
import agata.lcl.states.deconsolidation.DeconsolidationState;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;

import java.util.Collections;

public class ProposeDeconsolidationFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<UniqueIdentifier> {

        private final Party shippingLine;
        private final UniqueIdentifier masterBillOfLadingId;
        private final String containerNo;

        public Initiator(Party shippingLine, UniqueIdentifier masterBillOfLadingId, String containerNo) {
            this.shippingLine = shippingLine;
            this.masterBillOfLadingId = masterBillOfLadingId;
            this.containerNo = containerNo;
        }

        @Suspendable
        @Override
        public UniqueIdentifier call() throws FlowException {

            StateAndRef<BillOfLadingState> masterBillOfLadingStateRef = LclFlowUtils.resolveIdToStateRef(this.masterBillOfLadingId, this, BillOfLadingState.class);

            Party lclCompany = getOurIdentity();
            if (!lclCompany.equals(masterBillOfLadingStateRef.getState().getData().getConsignee())) {
                throw new FlowException("Flow can only be executed by correct consignee");
            }

            final DeconsolidationState proposedState = new DeconsolidationState(lclCompany, this.shippingLine, this.masterBillOfLadingId, containerNo);
            DeconsolidationProposal proposal = new DeconsolidationProposal(lclCompany, shippingLine, proposedState);

            return subFlow(new ProposalFlow.Initiator(
                    proposal,
                    new DeconsolidationContract.Commands.Propose(),
                    Collections.singletonList(masterBillOfLadingStateRef.referenced())));
        }
    }

}


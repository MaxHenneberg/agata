package agata.lcl.flows.deconsolidation;

import agata.bol.states.BillOfLadingState;
import agata.lcl.contracts.GenericProposalContract;
import agata.lcl.flows.AcceptFlow;
import agata.lcl.flows.LclFlowUtils;
import agata.lcl.flows.tracking.SetContainerDeconsolidatedFlow;
import agata.lcl.states.deconsolidation.DeconsolidationProposal;
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

public class AcceptDeconsolidationFlow {

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
            DeconsolidationProposal proposal = LclFlowUtils.resolveStateId(DeconsolidationProposal.class, this, proposalId);
            StateAndRef<BillOfLadingState> masterBillOfLadingRef = LclFlowUtils.resolveIdToStateRef(proposal.getProposedState().getMasterBillOfLadingId(), this, BillOfLadingState.class);

            BillOfLadingState masterBillOfLading = masterBillOfLadingRef.getState().getData();
            if (!getOurIdentity().equals(masterBillOfLading.getConsignee())) {
                throw new FlowException("Flow can only be executed by correct consignee");
            }

            // Update responsibility in each tracking state of a package associated to this container
            for (UniqueIdentifier trackingStateId : this.trackingStateIds) {
                subFlow(new SetContainerDeconsolidatedFlow.Initiator(trackingStateId, masterBillOfLading.getPortOfDischarge()));
            }

            // Consume the master bill of lading as input if the proposal is accepted (to avoid reuse during container release)
            return subFlow(new AcceptFlow.Initiator(
                    this.proposalId,
                    Collections.singletonList(masterBillOfLadingRef),
                    new GenericProposalContract.Commands.Accept()));
        }
    }

}

package agata.lcl.flows.deconsolidation;

import agata.bol.states.BillOfLadingState;
import agata.lcl.contracts.GenericProposalContract;
import agata.lcl.flows.AcceptFlow;
import agata.lcl.flows.LclFlowUtils;
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

public class AcceptDeconsolidationFlow {

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
            DeconsolidationProposal proposal = LclFlowUtils.resolveStateId(DeconsolidationProposal.class, this, proposalId);
            StateAndRef<BillOfLadingState> masterBillOfLadingRef = LclFlowUtils.resolveIdToStateRef(proposal.getProposedState().getMasterBillOfLadingId(), this, BillOfLadingState.class);

            if (!getOurIdentity().equals(masterBillOfLadingRef.getState().getData().getConsignee())) {
                throw new FlowException("Flow can only be executed by correct consignee");
            }

            // Consume the master bill of lading as input if the proposal is accepted (to avoid reuse during container release)
            return subFlow(new AcceptFlow.Initiator(
                    this.proposalId,
                    Collections.singletonList(masterBillOfLadingRef),
                    new GenericProposalContract.Commands.Accept()));
        }
    }

}

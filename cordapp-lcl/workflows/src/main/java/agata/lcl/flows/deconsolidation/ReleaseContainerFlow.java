package agata.lcl.flows.deconsolidation;

import agata.bol.dataholder.Price;
import agata.bol.states.BillOfLadingState;
import agata.lcl.contracts.deconsolidation.DeconsolidationContract;
import agata.lcl.flows.LclFlowUtils;
import agata.lcl.flows.ModifyFlow;
import agata.lcl.states.deconsolidation.DeconsolidationProposal;
import agata.lcl.states.deconsolidation.DeconsolidationState;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.ReferencedStateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.transactions.SignedTransaction;

import java.util.Collections;

public class ReleaseContainerFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<SignedTransaction> {

        private final UniqueIdentifier proposalId;
        private final Price collect;

        public Initiator(UniqueIdentifier proposalId, Price collect) {
            this.proposalId = proposalId;
            this.collect = collect;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {

            final DeconsolidationProposal inputProposal = LclFlowUtils.resolveStateId(DeconsolidationProposal.class, this, this.proposalId);
            final BillOfLadingState masterBillOfLading = LclFlowUtils.resolveStateId(BillOfLadingState.class, this, inputProposal.getProposedState().getMasterBillOfLadingId());

            if (!getOurIdentity().equals(masterBillOfLading.getShipper())) {
                throw new FlowException("Flow can only be executed by correct shipper");
            }

            final DeconsolidationState counterProposedState = new DeconsolidationState(inputProposal.getProposedState());
            counterProposedState.setCollect(this.collect);
            DeconsolidationProposal counterProposal = new DeconsolidationProposal(getOurIdentity(), counterProposedState.getLclCompany(), counterProposedState, proposalId);

            final ReferencedStateAndRef<BillOfLadingState> referenceState = LclFlowUtils.resolveIdToStateRef(inputProposal.getProposedState().getMasterBillOfLadingId(), this, BillOfLadingState.class).referenced();
            return subFlow(new ModifyFlow.Initiator(this.proposalId, counterProposal, new DeconsolidationContract.Commands.ReleaseContainer(), Collections.singletonList(referenceState)));
        }
    }
}

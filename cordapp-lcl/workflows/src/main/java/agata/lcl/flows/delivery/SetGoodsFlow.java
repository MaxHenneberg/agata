package agata.lcl.flows.delivery;

import agata.bol.dataholder.ItemRow;
import agata.bol.states.BillOfLadingState;
import agata.lcl.contracts.delivery.PackageDeliveryContract;
import agata.lcl.flows.LclFlowUtils;
import agata.lcl.flows.ModifyFlow;
import agata.lcl.states.delivery.PackageDeliveryProposal;
import agata.lcl.states.delivery.PackageDeliveryState;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.ReferencedStateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.transactions.SignedTransaction;

import java.util.Collections;
import java.util.List;

public class SetGoodsFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<SignedTransaction> {

        private final UniqueIdentifier proposalId;
        private final List<ItemRow> deliveredGoods;

        public Initiator(UniqueIdentifier proposalId, List<ItemRow> deliveredGoods, String invoiceId) {
            this.proposalId = proposalId;
            this.deliveredGoods = deliveredGoods;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            final PackageDeliveryState inputStateProposed = LclFlowUtils.resolveStateId(PackageDeliveryProposal.class, this, proposalId).getProposedState();

            if (!getOurIdentity().equals(inputStateProposed.getLclCompany())) {
                throw new FlowException("Flow can only be executed by the correct LCL company");
            }

            final PackageDeliveryState updatedState = new PackageDeliveryState(
                    inputStateProposed.getLinearId(),
                    inputStateProposed.getArrivalParty(),
                    inputStateProposed.getLclCompany(),
                    inputStateProposed.getHouseBolId(),
                    this.deliveredGoods);

            final PackageDeliveryProposal counterProposal = new PackageDeliveryProposal(getOurIdentity(), inputStateProposed.getArrivalParty(), updatedState, proposalId);
            final ReferencedStateAndRef<BillOfLadingState> referenceState = LclFlowUtils.resolveIdToStateRef(inputStateProposed.getHouseBolId(), this, BillOfLadingState.class).referenced();
            return subFlow(new ModifyFlow.Initiator(proposalId, counterProposal, new PackageDeliveryContract.Commands.SetDeliveredGoods(), Collections.singletonList(referenceState)));
        }
    }

}

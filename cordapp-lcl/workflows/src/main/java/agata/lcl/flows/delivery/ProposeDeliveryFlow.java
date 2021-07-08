package agata.lcl.flows.delivery;

import agata.bol.states.BillOfLadingState;
import agata.lcl.flows.LclFlowUtils;
import agata.lcl.flows.ProposalFlow;
import agata.lcl.states.delivery.PackageDeliveryProposal;
import agata.lcl.states.delivery.PackageDeliveryState;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;

import java.util.Collections;

public class ProposeDeliveryFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<UniqueIdentifier> {

        private final Party lclCompany;
        private final UniqueIdentifier houseBolId;

        public Initiator(Party lclCompany, UniqueIdentifier houseBolId) {
            this.lclCompany = lclCompany;
            this.houseBolId = houseBolId;
        }

        @Suspendable
        @Override
        public UniqueIdentifier call() throws FlowException {

            StateAndRef<BillOfLadingState> houseBillOfLadingStateRef = LclFlowUtils.resolveIdToStateRef(this.houseBolId, this, BillOfLadingState.class);

            if (!getOurIdentity().equals(houseBillOfLadingStateRef.getState().getData().getConsignee())) {
                throw new FlowException("Flow can only be executed by the consignee set in the house bill of lading");
            }

            final PackageDeliveryState state = new PackageDeliveryState(getOurIdentity(), this.lclCompany, this.houseBolId);
            PackageDeliveryProposal proposal = new PackageDeliveryProposal(state.getArrivalParty(), state.getLclCompany(), state);

            return subFlow(new ProposalFlow.Initiator(proposal, Collections.singletonList(houseBillOfLadingStateRef.referenced())));
        }
    }
}

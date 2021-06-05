package agata.lcl.flows.pickup;

import agata.lcl.flows.ProposalFlow;
import agata.lcl.states.pickup.PickupProposal;
import agata.lcl.states.pickup.PickupState;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;

import java.util.Collections;

public class PickupProposalFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class PickupProposalFlowInitiator extends FlowLogic<UniqueIdentifier> {
        private final Party exporter;
        private final Party supplier;

        private final UniqueIdentifier referenceToState1;

        public PickupProposalFlowInitiator(Party exporter, Party supplier, UniqueIdentifier referenceToState1) {
            this.exporter = exporter;
            this.supplier = supplier;
            this.referenceToState1 = referenceToState1;
        }

        @Suspendable
        @Override
        public UniqueIdentifier call() throws FlowException {
            Party lclCompany = getOurIdentity();
            PickupState pickupProposal = new PickupState(this.exporter, this.supplier, lclCompany, Collections.emptyList(), this.referenceToState1);
            PickupProposal proposalState = new PickupProposal(lclCompany, supplier, null, pickupProposal);

            return subFlow(new ProposalFlow.ProposalFlowInitiator(proposalState));
        }
    }
}

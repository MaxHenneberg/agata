package agata.lcl.flows.pickup;

import agata.lcl.flows.ProposalFlow;
import agata.lcl.states.assignment.AssignmentState;
import agata.lcl.states.pickup.PickupProposal;
import agata.lcl.states.pickup.PickupState;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;

import java.util.Collections;
import java.util.List;

public class PickupProposalFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<UniqueIdentifier> {
        private final UniqueIdentifier referenceToAssignmentState;

        public Initiator(Party exporter, Party supplier, UniqueIdentifier referenceToAssignmentState) {
            this.referenceToAssignmentState = referenceToAssignmentState;
        }

        @Suspendable
        @Override
        public UniqueIdentifier call() throws FlowException {
            QueryCriteria.LinearStateQueryCriteria inputCriteria = new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(referenceToAssignmentState), Vault.StateStatus.UNCONSUMED,
                    null);
            List<StateAndRef<AssignmentState>> assignmentStateList = getServiceHub().getVaultService().queryBy(AssignmentState.class, inputCriteria).getStates();
            if (assignmentStateList.size() > 1) {
                throw new FlowException("Incorrect amount of proposals found. Expected 1 got " + assignmentStateList.size());
            }
            final AssignmentState assignmentState = assignmentStateList.get(0).getState().getData();
            if (!getOurIdentity().equals(assignmentState.getLclCompany())) {
                throw new FlowException("Flow can only be executed by correct LCL Company");
            }
            PickupState pickupProposal = new PickupState(assignmentState.getBuyer(), assignmentState.getSupplier(), assignmentState.getLclCompany(), Collections.emptyList(),
                    this.referenceToAssignmentState, "");
            PickupProposal proposalState = new PickupProposal(assignmentState.getLclCompany(), assignmentState.getSupplier(), pickupProposal);

            return subFlow(new ProposalFlow.Initiator(proposalState));
        }
    }
}

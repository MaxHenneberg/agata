package agata.lcl.flows.pickup;

import agata.bol.dataholder.ItemRow;
import agata.lcl.flows.ModifyFlow;
import agata.lcl.states.pickup.PickupProposal;
import agata.lcl.states.pickup.PickupState;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;

import java.util.Collections;
import java.util.List;

public class PickupAddGoodsFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<SignedTransaction> {
        private final UniqueIdentifier proposalId;
        private final List<ItemRow> goods;
        private final String invoiceId;

        public Initiator(UniqueIdentifier proposalId, List<ItemRow> goods, String invoiceId) {
            this.proposalId = proposalId;
            this.goods = goods;
            this.invoiceId = invoiceId;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            QueryCriteria.LinearStateQueryCriteria inputCriteria = new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(proposalId), Vault.StateStatus.UNCONSUMED, null);
            StateAndRef inputStateAndRef = getServiceHub().getVaultService().queryBy(PickupProposal.class, inputCriteria).getStates().get(0);
            PickupProposal proposal = (PickupProposal) inputStateAndRef.getState().getData();

            final PickupState proposedPickupState = proposal.getProposedState();
            final PickupState counterProposal = new PickupState(proposedPickupState.getBuyer(),
                    proposedPickupState.getSupplier(), proposedPickupState.getLclCompany(),
                    this.goods, proposedPickupState.getReferenceToAssignmentState(), invoiceId, proposedPickupState.getLinearId());


            PickupProposal proposalState = new PickupProposal(getOurIdentity(), counterProposal.getLclCompany(), counterProposal, proposalId);

            return subFlow(new ModifyFlow.Initiator(proposalId, proposalState));
        }
    }
}

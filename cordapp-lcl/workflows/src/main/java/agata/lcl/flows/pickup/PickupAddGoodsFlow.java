package agata.lcl.flows.pickup;

import agata.lcl.contracts.pickup.PickupContract;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PickupAddGoodsFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class PickupAddGoodsFlowInitiator extends FlowLogic<SignedTransaction> {
        private final UniqueIdentifier proposalId;
        private final List<String> goods;

        public PickupAddGoodsFlowInitiator(UniqueIdentifier proposalId, List<String> goods) {
            this.proposalId = proposalId;
            this.goods = goods;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            QueryCriteria.LinearStateQueryCriteria inputCriteria = new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(proposalId), Vault.StateStatus.UNCONSUMED, null);
            StateAndRef inputStateAndRef = getServiceHub().getVaultService().queryBy(PickupProposal.class, inputCriteria).getStates().get(0);
            PickupProposal proposal = (PickupProposal) inputStateAndRef.getState().getData();

            final PickupState proposedPickupState = proposal.getProposedState();
            final PickupState counterProposal = new PickupState(proposedPickupState.getExporter(),
                    proposedPickupState.getSupplier(), proposedPickupState.getLclCompany(),
                    this.goods, proposedPickupState.getReferenceToState1(), proposedPickupState.getLinearId());


            PickupProposal proposalState = new PickupProposal(getOurIdentity(), counterProposal.getLclCompany(), new PickupContract.PickupCommands.AddGoods(), counterProposal);

            return subFlow(new ModifyFlow.ModifyFlowInitiator(proposalId, proposalState));
        }
    }
}

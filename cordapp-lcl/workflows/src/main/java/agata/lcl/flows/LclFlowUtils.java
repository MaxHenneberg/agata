package agata.lcl.flows;

import agata.lcl.states.Proposal;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;

import java.util.Collections;
import java.util.List;

public class LclFlowUtils {

    public static <T extends Proposal> T resolveProposalId(Class<T> clazz, FlowLogic flowLogic, UniqueIdentifier proposalId) throws FlowException {
        StateAndRef<T> stateAndRef = resolveIdToStateRef(proposalId, flowLogic, clazz);
        return stateAndRef.getState().getData();
    }

    public static <T extends ContractState> StateAndRef<T> resolveIdToStateRef(UniqueIdentifier id, FlowLogic flowLogic, Class<T> clazz) throws FlowException {
        QueryCriteria.LinearStateQueryCriteria inputCriteria =
                new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(id), Vault.StateStatus.UNCONSUMED, null);
        List<StateAndRef<T>> stateRefs = flowLogic.getServiceHub().getVaultService().queryBy(clazz, inputCriteria).getStates();
        if (stateRefs.size() != 1) {
            throw new FlowException("Expected one matching proposals for the given id but got " + stateRefs.size());
        }

        return stateRefs.get(0);
    }

}

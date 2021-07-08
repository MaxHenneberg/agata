package agata.lcl.flows;

import agata.bol.enums.BillOfLadingType;
import agata.bol.states.BillOfLadingState;
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

    public static <T extends ContractState> T resolveStateId(Class<T> clazz, FlowLogic flowLogic, UniqueIdentifier id) throws FlowException {
        StateAndRef<T> stateAndRef = resolveIdToStateRef(id, flowLogic, clazz);
        return stateAndRef.getState().getData();
    }

    public static <T extends ContractState> StateAndRef<T> resolveIdToStateRef(UniqueIdentifier id, FlowLogic flowLogic, Class<T> clazz) throws FlowException {
        QueryCriteria.LinearStateQueryCriteria inputCriteria =
                new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(id), Vault.StateStatus.UNCONSUMED, null);
        List<StateAndRef<T>> stateRefs = flowLogic.getServiceHub().getVaultService().queryBy(clazz, inputCriteria).getStates();
        if (stateRefs.size() != 1) {
            throw new FlowException("Expected one matching state of type " + clazz.toString() + " with id " + id.getId().toString() + " but got " + stateRefs.size());
        }

        return stateRefs.get(0);
    }

    public static BillOfLadingState resolveBillOfLadingId(FlowLogic flowLogic, UniqueIdentifier id, BillOfLadingType type) throws FlowException {
        BillOfLadingState bol = resolveIdToStateRef(id, flowLogic, BillOfLadingState.class).getState().getData();
        if (bol.getType() != type) {
            throw new FlowException("The id " + id.toString() + "is not associated to a " + type.toString() + " bill of lading");
        }
        return bol;
    }

}

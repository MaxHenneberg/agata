package agata.lcl.controllers;

import agata.lcl.errors.ResourceNotFoundException;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowLogic;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class BaseController {

    protected CordaRPCOps proxy;

    public BaseController(CordaRPCOps proxy) {
        this.proxy = proxy;
    }

    protected <T extends ContractState> List<T> getStates(Class<T> clazz) {
        return proxy.vaultQuery(clazz).getStates().stream().map(x -> x.getState().getData()).collect(Collectors.toList());
    }

    protected <T extends ContractState> T getMostRecentState(Class<T> clazz, UniqueIdentifier id) {
        List<StateAndRef<T>> result =
                proxy.vaultQueryByCriteria(new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(id), Vault.StateStatus.ALL, null), clazz)
                        .getStates();
        if (result.size() < 1) {
            return null;
        } else {
            return result.get(result.size() - 1).getState().getData();
        }
    }

    protected <T extends ContractState> T queryStateById(Class<T> clazz, UniqueIdentifier id) {
        return queryStateById(clazz, id, Vault.StateStatus.UNCONSUMED);
    }

    protected <T extends ContractState> T queryStateById(Class<T> clazz, UniqueIdentifier id, Vault.StateStatus stateStatus) {
        List<StateAndRef<T>> result =
                proxy.vaultQueryByCriteria(new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(id), stateStatus, null), clazz)
                        .getStates();
        if (result.size() == 0) {
            return null;
        } else if (result.size() == 1) {
            return result.get(0).getState().getData();
        } else {
            throw new RuntimeException("Found more than 1 states with the id " + id.getId().toString());
        }
    }

    protected <T extends ContractState> T getResourceById(Class<T> clazz, String id, Vault.StateStatus stateStatus) {
        T result = this.queryStateById(clazz, this.toUniqueIdentifier(id), stateStatus);
        if (result == null) {
            throw new ResourceNotFoundException(clazz, id);
        }
        return result;
    }

    protected UniqueIdentifier toUniqueIdentifier(String s) {
        return new UniqueIdentifier(null, UUID.fromString(s));
    }

    protected <S, T extends FlowLogic<S>> S startFlow(Class<T> clazz, Object... args) {
        // It is important to call getReturnValue().get() even if the value is not used.
        // This is the only way the exception will be thrown if, for example, the contract validation fails.
        try {
            return this.proxy.startFlowDynamic(clazz, args).getReturnValue().get();
        } catch (InterruptedException | ExecutionException e) {
            if (Pattern.matches(".*Do not provide flow sessions for the local node.*", e.getCause().getMessage())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attempted to start a flow where one of the recipients is the current node itself");
            } else if (e instanceof ExecutionException) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getCause().getMessage());
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred:");
            }
        }
    }

}

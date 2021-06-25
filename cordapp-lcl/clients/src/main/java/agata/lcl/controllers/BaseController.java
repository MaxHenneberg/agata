package agata.lcl.controllers;

import agata.lcl.flows.AcceptFlow;
import net.corda.core.contracts.ContractState;
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
import java.util.stream.Collectors;

public abstract class BaseController {

    protected CordaRPCOps proxy;

    public BaseController(CordaRPCOps proxy) {
        this.proxy = proxy;
    }

    protected <T extends ContractState> List<T> getStates(Class<T> clazz) {
        return proxy.vaultQuery(clazz).getStates().stream().map(x -> x.getState().getData()).collect(Collectors.toList());
    }

    protected <T extends ContractState> T queryStateById(Class<T> clazz, UniqueIdentifier id) {
        return proxy.vaultQueryByCriteria(new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(id), Vault.StateStatus.UNCONSUMED, null), clazz).getStates().get(0).getState().getData();
    }

    protected <T extends ContractState> T startGenericAcceptFlow(String proposalId, Class<T> returnClass) {
        UniqueIdentifier id = toUniqueIdentifier(proposalId);
        this.startFlow(AcceptFlow.Initiator.class, id);
        return this.queryStateById(returnClass, id);
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
            // TODO: Is this always a client error?
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getCause().getMessage());
        }
    }

}

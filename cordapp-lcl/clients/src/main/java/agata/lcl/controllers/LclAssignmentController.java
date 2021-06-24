package agata.lcl.controllers;

import agata.lcl.bodies.LclAssignment;
import agata.lcl.flows.AcceptFlow;
import agata.lcl.flows.assignment.AssignmentProposalFlow;
import agata.lcl.states.assignment.AssignmentProposal;
import agata.lcl.states.assignment.AssignmentState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lcl-assignments")
public class LclAssignmentController {

    private final CordaRPCOps proxy;

    @Autowired
    public LclAssignmentController(CordaRPCOps proxy) {
        this.proxy = proxy;
    }

    @GetMapping()
    public List<AssignmentState> getFinalizedAssignments() {
        return proxy.vaultQuery(AssignmentState.class).getStates().stream().map(x -> x.getState().getData()).collect(Collectors.toList());
    }

    // TODO: Handle exception more explicitly
    @PostMapping()
    public AssignmentProposal addLclCompanyAssignment(@RequestBody LclAssignment newAssignment) throws Exception {
        UniqueIdentifier proposalId = this.proxy.startFlowDynamic(AssignmentProposalFlow.Initiator.class, newAssignment.getBuyer(), newAssignment.getSupplier(), newAssignment.getArrivalParty(), newAssignment.getDepartureAddress(), newAssignment.getArrivalAddress(), newAssignment.getExpectedGoods()).getReturnValue().get();
        return queryAssignmentProposal(proposalId);
    }


    // TODO: One common endpoint for all proposals and then via query param of which type? Then the finalized are in sth like lcl-assigments
    //  But then for acceptance, it is not clear what to do explicitly for this case, isn't it?
    @GetMapping("/proposals")
    public List<AssignmentProposal> getLclAssignmentProposals() {
        return proxy.vaultQuery(AssignmentProposal.class).getStates().stream().map(x -> x.getState().getData()).collect(Collectors.toList());
    }

    @PostMapping("/proposals/{proposalId}/acceptance")
    public AssignmentState acceptAssignment(@PathVariable String proposalId) throws ExecutionException, InterruptedException {
        UniqueIdentifier id = UniqueIdentifier.Companion.fromString(proposalId);
        this.proxy.startFlowDynamic(AcceptFlow.Initiator.class, id);
        return queryAssignmentProposal(id).getProposedState();
    }

    private AssignmentProposal queryAssignmentProposal(UniqueIdentifier proposalId) {
        return proxy.vaultQueryByCriteria(new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(proposalId), Vault.StateStatus.UNCONSUMED, null), AssignmentProposal.class).getStates().get(0).getState().getData();
    }

}

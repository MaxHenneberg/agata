package agata.lcl.controllers;

import agata.lcl.bodies.LclAssignment;
import agata.lcl.flows.assignment.AssignmentProposalFlow;
import agata.lcl.states.assignment.AssignmentProposal;
import agata.lcl.states.assignment.AssignmentState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.messaging.CordaRPCOps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lcl-assignments")
public class LclAssignmentController extends BaseController {

    @Autowired
    public LclAssignmentController(CordaRPCOps proxy) {
        super(proxy);
    }

    @GetMapping()
    public List<AssignmentState> getFinalizedAssignments() {
        return this.getStates(AssignmentState.class);
    }

    @GetMapping("/{id}")
    public AssignmentState getFinalizedAssignment(@PathVariable String id) {
        return this.queryStateById(AssignmentState.class, this.toUniqueIdentifier(id));
    }

    // TODO: One common endpoint for all proposals and then via query param of which type? Then the finalized are in sth like lcl-assigments
    //  But then for acceptance, it is not clear what to do explicitly for this case, isn't it?
    @GetMapping("/proposals")
    public List<AssignmentProposal> getLclAssignmentProposals() {
        return this.getStates(AssignmentProposal.class);
    }

    // TODO: Handle exception more explicitly
    @PostMapping("/proposals")
    public AssignmentProposal addLclCompanyAssignment(@RequestBody LclAssignment newAssignment) throws Exception {
        UniqueIdentifier proposalId = this.proxy.startFlowDynamic(AssignmentProposalFlow.Initiator.class, newAssignment.getBuyer(), newAssignment.getSupplier(), newAssignment.getArrivalParty(), newAssignment.getDepartureAddress(), newAssignment.getArrivalAddress(), newAssignment.getExpectedGoods()).getReturnValue().get();
        return this.queryStateById(AssignmentProposal.class, proposalId);
    }

    @PostMapping("/proposals/{proposalId}/acceptance")
    public AssignmentState acceptAssignment(@PathVariable String proposalId) {
        return this.startGenericAcceptFlow(proposalId, AssignmentProposal.class).getProposedState();
    }

}

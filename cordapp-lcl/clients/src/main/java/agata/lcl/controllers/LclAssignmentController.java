package agata.lcl.controllers;

import agata.bol.states.BillOfLadingState;
import agata.lcl.dto.LclAssignment;
import agata.lcl.flows.assignment.AssignmentAcceptFlow;
import agata.lcl.flows.assignment.AssignmentProposalFlow;
import agata.lcl.states.assignment.AssignmentProposal;
import agata.lcl.states.assignment.AssignmentState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
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
        return this.getResourceById(AssignmentState.class, id, Vault.StateStatus.CONSUMED);
    }

    // TODO: These are not only house BoLs as we cannot distinguish for now
    @GetMapping("/{id}/house-bols")
    public List<BillOfLadingState> getFinalizedHouseBillOfLadings() {
        return this.getStates(BillOfLadingState.class);
    }

    // TODO: One common endpoint for all proposals and then via query param of which type? Then the finalized are in sth like lcl-assigments
    //  But then for acceptance, it is not clear what to do explicitly for this case, isn't it?
    @GetMapping("/proposals")
    public List<AssignmentProposal> getLclAssignmentProposals() {
        return this.getStates(AssignmentProposal.class);
    }

    @PostMapping("/proposals")
    public AssignmentProposal addLclCompanyAssignment(@RequestBody LclAssignment newAssignment) {
        UniqueIdentifier proposalId =
                this.startFlow(AssignmentProposalFlow.Initiator.class, newAssignment.getBuyer(), newAssignment.getSupplier(), newAssignment.getArrivalParty(),
                        newAssignment.getDepartureAddress(), newAssignment.getArrivalAddress(), newAssignment.getExpectedGoods());
        return this.queryStateById(AssignmentProposal.class, proposalId);
    }

    @PostMapping("/proposals/{proposalId}/acceptance")
    public AssignmentState acceptAssignment(@PathVariable String proposalId) {
        UniqueIdentifier id = this.toUniqueIdentifier(proposalId);
        this.startFlow(AssignmentAcceptFlow.Initiator.class, id);
        return this.getMostRecentState(AssignmentProposal.class, id).getProposedState();
    }

}

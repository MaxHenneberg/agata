package agata.lcl.controllers;

import agata.lcl.bodies.DeconsolidationRequest;
import agata.lcl.bodies.DeconsolidationUpdateRequest;
import agata.lcl.bodies.TrackingStateReferenceList;
import agata.lcl.errors.ResourceNotFoundException;
import agata.lcl.flows.deconsolidation.AcceptDeconsolidationFlow;
import agata.lcl.flows.deconsolidation.ProposeDeconsolidationFlow;
import agata.lcl.flows.deconsolidation.ReleaseContainerFlow;
import agata.lcl.states.deconsolidation.DeconsolidationProposal;
import agata.lcl.states.deconsolidation.DeconsolidationState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/deconsolidate")
public class DeconsolidateController extends BaseController {

    @Autowired
    public DeconsolidateController(CordaRPCOps proxy) {
        super(proxy);
    }

    @GetMapping()
    public List<DeconsolidationState> getFinalizedRequests() {
        return this.getStates(DeconsolidationState.class);
    }

    @GetMapping("/proposals")
    public List<DeconsolidationProposal> getProposals() {
        return this.getStates(DeconsolidationProposal.class);
    }

    @PostMapping("/proposals")
    public DeconsolidationProposal addProposal(@RequestBody DeconsolidationRequest request) {
        UniqueIdentifier proposalId = this.startFlow(
                ProposeDeconsolidationFlow.Initiator.class,
                request.getShippingLine(),
                request.getMasterBolId(),
                request.getContainerNo());
        return this.queryStateById(DeconsolidationProposal.class, proposalId);
    }

    @PatchMapping("/proposals/{id}")
    public DeconsolidationProposal updateProposal(@PathVariable String id, @RequestBody DeconsolidationUpdateRequest request) {
        UniqueIdentifier proposalId = this.toUniqueIdentifier(id);

        // Check if a resource with the given id exists before executing the actual flow logic
        if (this.queryStateById(DeconsolidationProposal.class, proposalId) == null) {
            throw new ResourceNotFoundException(DeconsolidationProposal.class, id);
        }

        // The flow returns the proposal id, which remains unchanged and is therefore not used
        this.startFlow(
                ReleaseContainerFlow.Initiator.class,
                proposalId,
                request.getCollect());
        return this.queryStateById(DeconsolidationProposal.class, proposalId);
    }

    @PostMapping("/proposals/{proposalId}/acceptance")
    public DeconsolidationState acceptAssignment(@PathVariable String proposalId, @RequestBody TrackingStateReferenceList body) {
        UniqueIdentifier id = this.toUniqueIdentifier(proposalId);
        this.startFlow(AcceptDeconsolidationFlow.Initiator.class, id, body.getTrackingStateIds());
        return this.queryStateById(DeconsolidationProposal.class, id, Vault.StateStatus.CONSUMED).getProposedState();
    }
}

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
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/deconsolidations")
public class DeconsolidationController extends BaseController {

    @Autowired
    public DeconsolidationController(CordaRPCOps proxy) {
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
                this.toUniqueIdentifier(request.getMasterBolId()),
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
    public DeconsolidationState acceptDeconsolidation(@PathVariable String proposalId, @RequestBody TrackingStateReferenceList body) {
        UniqueIdentifier id = this.toUniqueIdentifier(proposalId);
        List<UniqueIdentifier> trackingStateIds = body.getTrackingStateIds().stream().map(this::toUniqueIdentifier).collect(Collectors.toList());
        this.startFlow(AcceptDeconsolidationFlow.Initiator.class, id, trackingStateIds);
        return this.getMostRecentState(DeconsolidationProposal.class, id).getProposedState();
    }
}

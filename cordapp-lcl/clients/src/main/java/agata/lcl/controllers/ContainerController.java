package agata.lcl.controllers;

import agata.lcl.bodies.ContainerAssignment;
import agata.lcl.bodies.ContainerRequest;
import agata.lcl.bodies.TrackingStateReferenceList;
import agata.lcl.errors.ResourceNotFoundException;
import agata.lcl.flows.container.AcceptContainerFlow;
import agata.lcl.flows.container.AssignContainerFlow;
import agata.lcl.flows.container.ContainerRequestProposalFlow;
import agata.lcl.states.container.ContainerRequestProposal;
import agata.lcl.states.container.ContainerRequestState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.messaging.CordaRPCOps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/container-requests")
public class ContainerController extends BaseController {

    @Autowired
    public ContainerController(CordaRPCOps proxy) {
        super(proxy);
    }

    @GetMapping()
    public List<ContainerRequestState> getFinalizedRequests() {
        return this.getStates(ContainerRequestState.class);
    }

    @GetMapping("/proposals")
    public List<ContainerRequestProposal> getProposals() {
        return this.getStates(ContainerRequestProposal.class);
    }

    @GetMapping("/containerStateById/{containerId}")
    public ContainerRequestState getContainerStateByContainerId(@PathVariable String containerId) {
        List<ContainerRequestState> finalizedState = getFinalizedRequests();
        return finalizedState.stream().filter(state -> state.getContainer().getContainerNo().equals(containerId)).findFirst().get();
    }

    @PostMapping("/proposals")
    public ContainerRequestProposal addProposal(@RequestBody ContainerRequest request) {
        UniqueIdentifier proposalId = this.startFlow(
                ContainerRequestProposalFlow.Initiator.class,
                request.getShippingLine(),
                request.getLclDestination(),
                request.getPortOfLoading(),
                request.getPortOfDischarge(),
                request.getForwardingAgentNo(),
                request.getRequestedType());
        return this.queryStateById(ContainerRequestProposal.class, proposalId);
    }

    @PatchMapping("/proposals/{id}")
    public ContainerRequestProposal updateProposal(@PathVariable String id, @RequestBody ContainerAssignment requestUpdate) {
        UniqueIdentifier proposalId = this.toUniqueIdentifier(id);

        // Check if a resource with the given id exists before executing the actual flow logic
        if (this.queryStateById(ContainerRequestProposal.class, proposalId) == null) {
            throw new ResourceNotFoundException(ContainerRequestProposal.class, id);
        }

        // The flow returns the proposal id, which remains unchanged and is therefore not used
        this.startFlow(
                AssignContainerFlow.Initiator.class,
                proposalId,
                requestUpdate.getVesselName(),
                requestUpdate.getContainer());
        return this.queryStateById(ContainerRequestProposal.class, proposalId);
    }

    @PostMapping("/proposals/{proposalId}/acceptance")
    public ContainerRequestState acceptAssignment(@PathVariable String proposalId, @RequestBody TrackingStateReferenceList body) {
        UniqueIdentifier id = this.toUniqueIdentifier(proposalId);
        List<UniqueIdentifier> trackingIds = body.getTrackingStateIds().stream().map(this::toUniqueIdentifier).collect(Collectors.toList());
        this.startFlow(AcceptContainerFlow.Initiator.class, id, trackingIds);
        return this.getMostRecentState(ContainerRequestProposal.class, id).getProposedState();
    }

}

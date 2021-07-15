package agata.lcl.controllers;

import agata.bol.states.BillOfLadingState;
import agata.lcl.dto.ShipmentDetails;
import agata.lcl.dto.TrackingStateReferenceList;
import agata.lcl.flows.shiploading.ShiploadingAcceptFlow;
import agata.lcl.flows.shiploading.ShiploadingProposalFlow;
import agata.lcl.states.shiploading.ShiploadingProposal;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.messaging.CordaRPCOps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/loadings")
public class LoadingController extends BaseController {

    @Autowired
    public LoadingController(CordaRPCOps proxy) {
        super(proxy);
    }

    @GetMapping()
    public List<BillOfLadingState> getFinalizedStates() {
        return this.getStates(BillOfLadingState.class);
    }

    @GetMapping("/proposals")
    public List<ShiploadingProposal> getLoadingProposals() {
        return this.getStates(ShiploadingProposal.class);
    }

    @GetMapping("/proposals/masterBol/{containerId}")
    public BillOfLadingState getLoadingProposals(@PathVariable String containerId) {
        //Happy Path
        return this.getStates(ShiploadingProposal.class).stream()
                .filter((proposal) -> proposal.getProposedState().getContainerInformationList().get(0).getContainerNo().equals(containerId)).findFirst().get()
                .getProposedState();
    }


    @PostMapping("/proposals")
    public ShiploadingProposal addLoadingProposal(@RequestBody ShipmentDetails details) {
        List<UniqueIdentifier> houseBolIds = details.getHouseBolIds().stream().map(this::toUniqueIdentifier).collect(Collectors.toList());
        UniqueIdentifier proposalId = this.startFlow(
                ShiploadingProposalFlow.Initiator.class,
                this.toUniqueIdentifier(details.getContainerStateId()),
                details.getShippingLine(),
                houseBolIds,
                details.getModeOfInitialCarriage(),
                details.getPlaceOfInitialReceipt(),
                details.getBookingNo(),
                details.getBillOfLadingNo(),
                details.getExportReference(),
                details.getFreightPayableAt(),
                details.getTypeOfMovement(),
                details.getFreightChargesList(),
                details.getPrepaid(),
                details.getCollect()
        );
        return this.queryStateById(ShiploadingProposal.class, proposalId);
    }

    @PostMapping("/proposals/{proposalId}/acceptance")
    public BillOfLadingState acceptLoadingProposal(@PathVariable String proposalId, @RequestBody TrackingStateReferenceList body) {
        UniqueIdentifier id = this.toUniqueIdentifier(proposalId);
        List<UniqueIdentifier> trackingStateIds = body.getTrackingStateIds().stream().map(this::toUniqueIdentifier).collect(Collectors.toList());
        this.startFlow(ShiploadingAcceptFlow.Initiator.class, id, trackingStateIds);
        return this.getMostRecentState(ShiploadingProposal.class, id).getProposedState();
    }


}

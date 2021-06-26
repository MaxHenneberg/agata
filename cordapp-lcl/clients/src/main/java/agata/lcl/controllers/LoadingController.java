package agata.lcl.controllers;

import agata.bol.states.BillOfLadingState;
import agata.lcl.bodies.ShipmentDetails;
import agata.lcl.flows.shiploading.ShiploadingAcceptFlow;
import agata.lcl.flows.shiploading.ShiploadingProposalFlow;
import agata.lcl.states.shiploading.ShiploadingProposal;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    public BillOfLadingState acceptLoadingProposal(@PathVariable String proposalId) {
        UniqueIdentifier id = this.toUniqueIdentifier(proposalId);
        this.startFlow(ShiploadingAcceptFlow.Initiator.class, id);
        return this.queryStateById(ShiploadingProposal.class, id, Vault.StateStatus.CONSUMED).getProposedState();
    }


}

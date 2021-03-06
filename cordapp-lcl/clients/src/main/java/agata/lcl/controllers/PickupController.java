package agata.lcl.controllers;

import agata.bol.states.BillOfLadingState;
import agata.lcl.dto.PickupFinalization;
import agata.lcl.dto.PickupInitialization;
import agata.lcl.dto.UpdatePickupGoods;
import agata.lcl.flows.pickup.PickupAcceptFlow;
import agata.lcl.flows.pickup.PickupAddGoodsFlow;
import agata.lcl.flows.pickup.PickupProposalFlow;
import agata.lcl.states.pickup.PickupProposal;
import agata.lcl.states.pickup.PickupState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/pickups")
public class PickupController extends BaseController {

    @Autowired
    public PickupController(CordaRPCOps proxy) {
        super(proxy);
    }

    @GetMapping()
    public List<PickupState> getFinalizedPickups() {
        return this.getStates(PickupState.class);
    }

    @GetMapping("/proposals")
    public List<PickupProposal> getProposals() {
        return this.getStates(PickupProposal.class);
    }

    @GetMapping("/proposals/{proposalId}")
    public PickupProposal getProposal(@PathVariable String proposalId) {
        return this.queryStateById(PickupProposal.class, this.toUniqueIdentifier(proposalId), Vault.StateStatus.ALL);
    }

    @PostMapping("/proposals")
    public PickupProposal addPickupProposal(@RequestBody PickupInitialization pickup) {
        UniqueIdentifier assignmentId = this.toUniqueIdentifier(pickup.getAssignmentId());
        UniqueIdentifier proposalId = this.startFlow(PickupProposalFlow.Initiator.class, assignmentId);
        return this.queryStateById(PickupProposal.class, proposalId, Vault.StateStatus.UNCONSUMED);
    }

    @PatchMapping("/proposals/{proposalId}")
    public PickupProposal updateProposal(@PathVariable String proposalId, @RequestBody UpdatePickupGoods update) {
        this.startFlow(PickupAddGoodsFlow.Initiator.class, toUniqueIdentifier(proposalId), update.getGoods(), update.getInvoiceId());
        return this.queryStateById(PickupProposal.class, this.toUniqueIdentifier(proposalId), Vault.StateStatus.UNCONSUMED);
    }

    @PostMapping("/proposals/{proposalId}/acceptance")
    public BillOfLadingState acceptAssignment(@PathVariable String proposalId, @RequestBody PickupFinalization update) {
        UniqueIdentifier id = this.toUniqueIdentifier(proposalId);
        UniqueIdentifier billOfLadingId = this.startFlow(
                PickupAcceptFlow.Initiator.class,
                id,
                this.toUniqueIdentifier(update.getContainerRequestId()),
                this.toUniqueIdentifier(update.getTrackingStateId()),
                update.getModeOfInitialCarriage(),
                update.getPlaceOfInitialReceipt(),
                update.getPlaceOfDeliveryByCarrier(),
                update.getBookingNo(),
                update.getBillOfLadingNo(),
                update.getExportReference(),
                update.getFreightPayableAt(),
                update.getTypeOfMovement(),
                update.getFreightChargesList(),
                update.getPrepaid(),
                update.getCollect()
        );
        return this.queryStateById(BillOfLadingState.class, billOfLadingId);
    }

}

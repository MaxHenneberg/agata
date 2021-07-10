package agata.lcl.controllers;

import agata.bol.states.BillOfLadingState;
import agata.lcl.bodies.DeliveryRequest;
import agata.lcl.bodies.DeliveryUpdateRequest;
import agata.lcl.errors.ResourceNotFoundException;
import agata.lcl.flows.delivery.ProposeDeliveryFlow;
import agata.lcl.flows.delivery.SetGoodsFlow;
import agata.lcl.states.delivery.PackageDeliveryProposal;
import agata.lcl.states.delivery.PackageDeliveryState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.messaging.CordaRPCOps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/delivery")
public class DeliveryController extends BaseController {

    @Autowired
    public DeliveryController(CordaRPCOps proxy) {
        super(proxy);
    }

    @GetMapping()
    public List<PackageDeliveryState> getFinalizedRequests() {
        return this.getStates(PackageDeliveryState.class);
    }

    @GetMapping("/proposals")
    public List<PackageDeliveryProposal> getProposals() {
        return this.getStates(PackageDeliveryProposal.class);
    }

    @GetMapping("/houseBol/{linearId}")
    public BillOfLadingState getBillOfLadingById(@PathVariable String linearId){
        // Happy Path
        return this.getStates(BillOfLadingState.class).stream().filter((bol) -> bol.getLinearId().equals(linearId)).findFirst().get();
    }

    @PostMapping("/proposals")
    public PackageDeliveryProposal addProposal(@RequestBody DeliveryRequest request) {
        UniqueIdentifier proposalId = this.startFlow(
                ProposeDeliveryFlow.Initiator.class,
                request.getLclCompany(),
                request.getHouseBolId());
        return this.queryStateById(PackageDeliveryProposal.class, proposalId);
    }

    @PatchMapping("/proposals/{id}")
    public PackageDeliveryProposal updateProposal(@PathVariable String id, @RequestBody DeliveryUpdateRequest request) {
        UniqueIdentifier proposalId = this.toUniqueIdentifier(id);

        // Check if a resource with the given id exists before executing the actual flow logic
        if (this.queryStateById(PackageDeliveryProposal.class, proposalId) == null) {
            throw new ResourceNotFoundException(PackageDeliveryProposal.class, id);
        }

        // The flow returns the proposal id, which remains unchanged and is therefore not used
        this.startFlow(
                SetGoodsFlow.Initiator.class,
                proposalId,
                request.getDeliveredGoods());
        return this.queryStateById(PackageDeliveryProposal.class, proposalId);
    }

    @PostMapping("/proposals/{proposalId}/acceptance")
    public PackageDeliveryState acceptProposal(@PathVariable String proposalId) {
        return this.startGenericAcceptFlow(proposalId, PackageDeliveryState.class);
    }
}

package agata.lcl.controllers;

import agata.lcl.dto.TrackingStateDto;
import agata.lcl.errors.ResourceNotFoundException;
import agata.lcl.states.tracking.ShippingTrackingState;
import agata.lcl.states.tracking.TrackingState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/tracking")
public class TrackingStateController extends BaseController {

    @Autowired
    public TrackingStateController(CordaRPCOps proxy) {
        super(proxy);
    }

    @GetMapping("/{id}")
    public List<TrackingStateDto> getTrackingStates(@PathVariable String id) {

        Vault.Page<TrackingState> results = proxy.vaultQueryByCriteria(
                new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(this.toUniqueIdentifier(id)), Vault.StateStatus.ALL, null), TrackingState.class);

        if (results.getStates().size() == 0) {
            throw new ResourceNotFoundException(TrackingState.class, id.toString());
        }

        List<TrackingStateDto> dtos = new ArrayList<>();
        int index = 0;
        for (StateAndRef<TrackingState> stateAndRef : results.getStates()) {
            TrackingState state = stateAndRef.getState().getData();
            TrackingStateDto tracking = new TrackingStateDto();
            tracking.setStatus(state.getStatus());
            tracking.setBuyer(state.getBuyer());
            tracking.setLclCompany(state.getLclCompany());
            tracking.setSupplier(state.getSupplier());
            if (state instanceof ShippingTrackingState) {
                ShippingTrackingState shippingState = (ShippingTrackingState) state;
                tracking.setLastPort(shippingState.getLastPort());
                tracking.setShippingLine(shippingState.getShippingLine());
            }

            LocalDateTime timestamp = LocalDateTime.ofInstant(results.getStatesMetadata().get(index).getRecordedTime(), ZoneOffset.UTC);
            tracking.setUpdatedOn(timestamp);

            dtos.add(tracking);
            index++;
        }
        return dtos;
    }

}

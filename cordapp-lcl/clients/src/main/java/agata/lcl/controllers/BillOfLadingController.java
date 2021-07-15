package agata.lcl.controllers;

import agata.bol.enums.BillOfLadingType;
import agata.bol.states.BillOfLadingState;
import net.corda.core.messaging.CordaRPCOps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/bill-of-ladings")
public class BillOfLadingController extends BaseController {

    @Autowired
    public BillOfLadingController(CordaRPCOps proxy) {
        super(proxy);
    }

    @GetMapping("/{id}")
    public BillOfLadingState getById(@PathVariable String id) {
        return this.getMostRecentState(BillOfLadingState.class, this.toUniqueIdentifier(id));
    }

    @GetMapping()
    public List<BillOfLadingState> getAll(@RequestParam BillOfLadingType type) {
        return this.getStates(BillOfLadingState.class).stream().filter(bol -> bol.getType() == type).collect(Collectors.toList());
    }

}

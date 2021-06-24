package agata.lcl.controllers;

import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RootController {

    private final CordaRPCOps proxy;

    @Autowired
    public RootController(CordaRPCOps proxy) {
        this.proxy = proxy;
    }

    @GetMapping("/info")
    public NodeInfo getNodeInfo() {
        return this.proxy.nodeInfo();
    }
}

package agata.lcl.bodies;

import lombok.Data;
import net.corda.core.identity.Party;

@Data
public class DeconsolidationRequest {
    private Party shippingLine;
    private String masterBolId;
    private String containerNo;
}

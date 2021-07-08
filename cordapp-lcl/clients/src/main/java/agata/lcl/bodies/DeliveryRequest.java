package agata.lcl.bodies;

import lombok.Data;
import net.corda.core.identity.Party;

@Data
public class DeliveryRequest {
    private Party lclCompany;
    private String houseBolId;
}

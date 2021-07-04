package agata.lcl.bodies;

import agata.bol.enums.ContainerType;
import lombok.*;
import net.corda.core.identity.Party;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ContainerRequest {

    private Party shippingLine;
    private Party lclDestination;
    private String portOfLoading;
    private String portOfDischarge;
    private String forwardingAgentNo;
    private ContainerType requestedType;
}

package agata.bol.dataholder;

import agata.bol.enums.ContainerType;
import lombok.*;
import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ContainerInformation {

    private String containerNo;
    private String sealNo;
    private ContainerType type;
}

package agata.bol.dataholder;

import lombok.*;
import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class FreightCharges {
    private String chargeReason;
    private Price charge;
}

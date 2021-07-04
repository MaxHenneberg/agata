package agata.bol.dataholder;

import lombok.*;
import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class DescriptionOfGoods {
    private String product;
    private String typeOfPackage;
    private int quantity;
}

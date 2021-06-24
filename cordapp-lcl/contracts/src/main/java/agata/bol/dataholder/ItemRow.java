package agata.bol.dataholder;

import lombok.*;
import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ItemRow {
    private String mark;
    private String identityNumber;

    private int noOfPackages;

    private DescriptionOfGoods descriptionOfGoods;

    private int netWeight;
    private int grossWeight;
    private int measurement;
}

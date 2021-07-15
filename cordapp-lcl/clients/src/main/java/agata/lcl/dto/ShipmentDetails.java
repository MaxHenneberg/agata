package agata.lcl.dto;

import agata.bol.dataholder.FreightCharges;
import agata.bol.dataholder.Price;
import agata.bol.enums.Payable;
import agata.bol.enums.TypeOfMovement;
import lombok.*;
import net.corda.core.identity.Party;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ShipmentDetails {
    private String containerStateId;
    private Party shippingLine;
    private List<String> houseBolIds;
    private String modeOfInitialCarriage;
    private String placeOfInitialReceipt;
    private String bookingNo;
    private String billOfLadingNo;
    private List<String> exportReference;
    private Payable freightPayableAt;
    private TypeOfMovement typeOfMovement;
    private List<FreightCharges> freightChargesList;
    private Price prepaid;
    private Price collect;
}

package agata.lcl.dto;

import agata.bol.dataholder.ItemRow;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UpdatePickupGoods {
    private List<ItemRow> goods;
    private String invoiceId;
}

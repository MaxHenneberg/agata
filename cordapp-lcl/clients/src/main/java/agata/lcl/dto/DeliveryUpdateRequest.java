package agata.lcl.dto;

import agata.bol.dataholder.ItemRow;
import lombok.Data;

import java.util.List;

@Data
public class DeliveryUpdateRequest {
    private List<ItemRow> deliveredGoods;

}

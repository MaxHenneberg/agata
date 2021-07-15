package agata.lcl.dto;

import agata.bol.dataholder.Price;
import lombok.Data;

@Data
public class DeconsolidationUpdateRequest {
    private Price collect;
}

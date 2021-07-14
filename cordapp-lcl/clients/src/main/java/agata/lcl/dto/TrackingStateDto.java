package agata.lcl.dto;

import agata.lcl.enums.TrackingStatus;
import lombok.*;
import net.corda.core.identity.Party;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TrackingStateDto {

    private TrackingStatus status;

    private LocalDateTime updatedOn;

    private Party lclCompany;

    private Party buyer;

    private Party supplier;

    private Party shippingLine;

    private String lastPort;

}

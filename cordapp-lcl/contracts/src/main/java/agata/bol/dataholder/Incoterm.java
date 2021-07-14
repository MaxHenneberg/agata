package agata.bol.dataholder;

import agata.bol.enums.IncotermCode;
import lombok.*;
import net.corda.core.serialization.CordaSerializable;

@ToString
@CordaSerializable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Incoterm {

    private IncotermCode incotermCode;
    private String location;
}

package agata.bol.dataholder;

import lombok.*;
import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Address {
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
}

package agata.lcl.bodies;

import agata.bol.dataholder.Address;
import agata.bol.dataholder.ItemRow;
import lombok.*;
import net.corda.core.identity.Party;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class LclAssignment {

    // The party type properties here are automatically deserialized from the Corda extension for Jackson.
    // In the JSON body, a string like "O=PartyA,L=London,C=GB" is simply passed, from which the object is parsed.

    private Party buyer;

    private Party supplier;

    private Party arrivalParty;

    private Address departureAddress;

    private Address arrivalAddress;

    private List<ItemRow> expectedGoods;


}

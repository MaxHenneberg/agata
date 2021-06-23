package agata.bol.dataholder;

import agata.bol.enums.IncotermCode;
import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
public class Incoterm {
    private IncotermCode incotermCode;
    private String location;
}

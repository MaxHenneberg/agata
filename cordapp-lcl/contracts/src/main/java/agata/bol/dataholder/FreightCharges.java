package agata.bol.dataholder;

import lombok.ToString;
import net.corda.core.serialization.CordaSerializable;

@ToString
@CordaSerializable
public class FreightCharges {

    private final String chargeReason;
    private final Price charge;

    public FreightCharges(String chargeReason, Price charge) {
        this.chargeReason = chargeReason;
        this.charge = charge;
    }

    public String getChargeReason() {
        return chargeReason;
    }

    public Price getCharge() {
        return charge;
    }
}

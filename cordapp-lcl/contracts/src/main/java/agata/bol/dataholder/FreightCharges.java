package agata.bol.dataholder;

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

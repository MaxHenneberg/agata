package agata.sample.container.dataholder;

import net.corda.core.identity.Party;

public class ReservedSlot {
    private final Party buyer;
    private final int boughtSlots;

    public ReservedSlot(Party buyer, int boughtSlots) {
        this.buyer = buyer;
        this.boughtSlots = boughtSlots;
    }

    public Party getBuyer() {
        return buyer;
    }

    public int getBoughtSlots() {
        return boughtSlots;
    }
}

package agata.sample.container.schema;

import agata.sample.container.dataholder.ReservedSlot;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "reserved_slot")
public class ReservedSlotSchema {
    @Column(name="BUYER")
    private final String buyer;
    @Column(name="AMOUNT")
    private final int amount;

    public ReservedSlotSchema(String buyer, int amount) {
        this.buyer = buyer;
        this.amount = amount;
    }

    public ReservedSlotSchema(){
        this.buyer = null;
        this.amount = 0;
    }

    public static ReservedSlotSchema of(ReservedSlot reservedSlot){
        return new ReservedSlotSchema(reservedSlot.getBuyer().getName().toString(), reservedSlot.getBoughtSlots());
    }
}

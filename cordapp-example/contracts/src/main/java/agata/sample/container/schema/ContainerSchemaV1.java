package agata.sample.container.schema;

import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.*;

public class ContainerSchemaV1 extends MappedSchema {
    public ContainerSchemaV1() {
        super(ContainerSchema.class, 1, Arrays.asList(PersistentContainer.class));
    }

    @Entity
    @Table(name = "container_state")
    public static class PersistentContainer extends PersistentState {
        @Column(name="CONTAINER_ID")
        private final String containerId;
        @Column(name="OWNER")
        private final String owner;
        @Column(name="FREE_SLOTS")
        private final int freeSlots;
        @Column(name="PRICE_PER_SLOT")
        private final double pricePerSlot;
        @Column(name="END_AUCTION_DATE")
        private final LocalDateTime endAuctionDate;
        @Column(name="SHIPPING_DATE")
        private final LocalDateTime shippingDate;
        @Column(name="LINEAR_ID")
        @Type(type="uuid-char")
        private final UUID linearId;

        public PersistentContainer(String containerId, String owner, int freeSlots, double pricePerSlot, LocalDateTime endAuctionDate, LocalDateTime shippingDate, UUID linearId) {
            this.containerId = containerId;
            this.owner = owner;
            this.freeSlots = freeSlots;
            this.pricePerSlot = pricePerSlot;
            this.endAuctionDate = endAuctionDate;
            this.shippingDate = shippingDate;
            this.linearId = linearId;
        }

        public PersistentContainer(){
            this.containerId = null;
            this.owner = null;
            this.freeSlots = 0;
            this.pricePerSlot = 0;
            this.endAuctionDate = null;
            this.shippingDate = null;
            this.linearId = null;

        }

        public String getContainerId() {
            return containerId;
        }

        public String getOwner() {
            return owner;
        }

        public int getFreeSlots() {
            return freeSlots;
        }

        public double getPricePerSlot() {
            return pricePerSlot;
        }

        public LocalDateTime getEndAuctionDate() {
            return endAuctionDate;
        }

        public LocalDateTime getShippingDate() {
            return shippingDate;
        }

        public UUID getLinearId() {
            return linearId;
        }
    }
}

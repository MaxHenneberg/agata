package agata.sample.container.states;

import agata.sample.container.contracts.ContainerContract;
import agata.sample.container.dataholder.ReservedSlot;
import agata.sample.container.schema.ContainerSchemaV1;
import agata.sample.container.schema.ReservedSlotSchema;
import javafx.util.Pair;
import net.corda.core.contracts.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@BelongsToContract(ContainerContract.class)
public class ContainerState implements LinearState, QueryableState {
    private final String containerId;
    private final AbstractParty owner;
    private final Integer freeSlots;
    private final Double pricePerSlot;
    private final LocalDateTime endAuctionDate;
    private final LocalDateTime shippingDate;
    private final UniqueIdentifier linearId;

    public ContainerState(String containerId, AbstractParty owner, Integer freeSlots, Double pricePerSlot, LocalDateTime endAuchtionDate, LocalDateTime shippingDate, UniqueIdentifier linearId) {
        this.containerId = containerId;
        this.owner = owner;
        this.freeSlots = freeSlots;
        this.pricePerSlot = pricePerSlot;
        this.endAuctionDate = endAuchtionDate;
        this.shippingDate = shippingDate;
        this.linearId = linearId;
    }

    public String getContainerId() {
        return containerId;
    }

    public AbstractParty getOwner() {
        return owner;
    }

    public Integer getFreeSlots() {
        return freeSlots;
    }

    public Double getPricePerSlot() {
        return pricePerSlot;
    }

    public LocalDateTime getEndAuchtionDate() {
        return endAuctionDate;
    }

    public LocalDateTime getShippingDate() {
        return shippingDate;
    }


    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    @NotNull
    @Override
    public PersistentState generateMappedObject(@NotNull MappedSchema schema) {
        if (schema instanceof ContainerSchemaV1) {
            return new ContainerSchemaV1.PersistentContainer(this.containerId,
                    this.owner.nameOrNull().getX500Principal().getName().toString(), this.freeSlots, this.pricePerSlot,
                    this.endAuctionDate, this.shippingDate, this.linearId.getId());
        }else{
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }

    @NotNull
    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        return Collections.singletonList(new ContainerSchemaV1());
    }


    @Override
    public List<AbstractParty> getParticipants() {
        return Collections.singletonList(owner);
    }

    @Override
    public String toString() {
        return "ContainerState{" +
                "containerId='" + containerId + '\'' +
                ", owner=" + owner +
                ", freeSlots=" + freeSlots +
                ", pricePerSlot=" + pricePerSlot +
                ", endAuchtionDate=" + endAuctionDate +
                ", shippingDate=" + shippingDate +
                ", linearId=" + linearId +
                '}';
    }
}

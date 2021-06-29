package agata.lcl.states.tracking;

import agata.lcl.contracts.annotations.MandatoryForContract;
import agata.lcl.contracts.tracking.TrackingContract;
import agata.lcl.enums.TrackingStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@BelongsToContract(TrackingContract.class)
@Getter
@EqualsAndHashCode
public class TrackingState implements LinearState {

    private final UniqueIdentifier linearId;

    @Setter
    @MandatoryForContract
    private TrackingStatus status;

    @MandatoryForContract
    private final Party lclCompany;

    @MandatoryForContract
    private final Party buyer;

    @MandatoryForContract
    private final Party supplier;

    public TrackingState(Party lclCompany, Party buyer, Party supplier) {
        this(new UniqueIdentifier(), lclCompany, buyer, supplier, TrackingStatus.SlotBooked);
    }

    public TrackingState(TrackingState stateToCopy) {
        this(stateToCopy.getLinearId(), stateToCopy.getLclCompany(), stateToCopy.getBuyer(), stateToCopy.getSupplier(), stateToCopy.getStatus());
    }

    @ConstructorForDeserialization
    public TrackingState(UniqueIdentifier linearId, Party lclCompany, Party buyer, Party supplier, TrackingStatus status) {
        this.linearId = linearId;
        this.lclCompany = lclCompany;
        this.buyer = buyer;
        this.status = status;
        this.supplier = supplier;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(lclCompany, buyer, supplier);
    }
}

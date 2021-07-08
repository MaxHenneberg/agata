package agata.lcl.states.tracking;

import agata.lcl.contracts.annotations.MandatoryForContract;
import agata.lcl.contracts.tracking.TrackingContract;
import agata.lcl.enums.TrackingStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@BelongsToContract(TrackingContract.class)
@Getter
@EqualsAndHashCode(callSuper = true)
public class ShippingTrackingState extends TrackingState {

    @MandatoryForContract
    private final Party shippingLine;

    @MandatoryForContract
    private final String lastPort;

    public ShippingTrackingState(ShippingTrackingState toCopy) {
        this(toCopy.getLinearId(), toCopy.getLclCompany(), toCopy.getBuyer(), toCopy.getSupplier(), toCopy.getShippingLine(), toCopy.getLastPort(), toCopy.getStatus());
    }

    @ConstructorForDeserialization
    public ShippingTrackingState(UniqueIdentifier linearId, Party lclCompany, Party buyer, Party supplier, Party shippingLine, String lastPort, TrackingStatus status) {
        super(linearId, lclCompany, buyer, supplier, status);
        this.shippingLine = shippingLine;
        this.lastPort = lastPort;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        // Important: Create a new list instead of adding new elements to the refrence retrieved from super.getParticipants())
        List<AbstractParty> participants = new ArrayList<>(super.getParticipants());
        participants.add(shippingLine);
        return participants;
    }
}

package agata.lcl.states.assignment;

import agata.bol.dataholder.Address;
import agata.bol.dataholder.ItemRow;
import agata.lcl.contracts.GenericProposalContract;
import agata.lcl.contracts.annotations.MandatoryForContract;
import agata.lcl.contracts.assignment.AssignmentContract;
import agata.lcl.states.tracking.TrackingState;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@BelongsToContract(AssignmentContract.class)
@Getter
@EqualsAndHashCode
public class AssignmentState implements LinearState {

    private final UniqueIdentifier linearId;

    @MandatoryForContract
    private final Party lclCompany;

    @MandatoryForContract
    private final Party buyer;

    @MandatoryForContract
    private final Party supplier;

    @MandatoryForContract
    private final Party arrivalParty; // Can be the buyer (if they want to ship the goods to their own warehouse) or another party (e.g. an associated warehouse)

    @MandatoryForContract
    private final Address departureAddress;

    @MandatoryForContract
    private final Address arrivalAddress;

    @MandatoryForContract
    private final List<ItemRow> expectedGoods;

    @MandatoryForContract(value = GenericProposalContract.Commands.Accept.class)
    protected LinearPointer<TrackingState> status;

    @ConstructorForDeserialization
    public AssignmentState(UniqueIdentifier linearId, Party lclCompany, Party buyer, Party supplier, Party arrivalParty, Address departureAddress, Address arrivalAddress, List<ItemRow> expectedGoods, LinearPointer<TrackingState> status) {
        this.linearId = linearId;
        this.buyer = buyer;
        this.departureAddress = departureAddress;
        this.supplier = supplier;
        this.arrivalAddress = arrivalAddress;
        this.lclCompany = lclCompany;
        this.expectedGoods = expectedGoods;
        this.arrivalParty = arrivalParty;
        this.status = status;
    }

    public AssignmentState(Party lclCompany, Party buyer, Party supplier, Party arrivalParty, Address departureAddress, Address arrivalAddress, List<ItemRow> expectedGoods, UniqueIdentifier trackingStateId) {
        this.linearId = new UniqueIdentifier();
        this.buyer = buyer;
        this.departureAddress = departureAddress;
        this.supplier = supplier;
        this.arrivalAddress = arrivalAddress;
        this.lclCompany = lclCompany;
        this.expectedGoods = expectedGoods;
        this.arrivalParty = arrivalParty;
        this.status = new LinearPointer<>(trackingStateId, TrackingState.class);
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(buyer, lclCompany);
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }

}

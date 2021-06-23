package agata.lcl.states.assignment;

import agata.bol.dataholder.Address;
import agata.bol.dataholder.ItemRow;
import agata.lcl.contracts.annotations.MandatoryForContract;
import agata.lcl.contracts.assignment.AssignmentContract;
import agata.lcl.enums.LclAssignmentStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.corda.core.contracts.BelongsToContract;
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

    protected final UniqueIdentifier linearId;

    @MandatoryForContract
    protected final Party lclCompany;

    @MandatoryForContract
    protected final Party buyer;

    @MandatoryForContract
    protected final Party supplier;

    @MandatoryForContract
    protected final Party arrivalParty; // Can be the buyer (if they want to ship the goods to their own warehouse) or another party (e.g. an associated warehouse)

    @MandatoryForContract
    protected final Address departureAddress;

    @MandatoryForContract
    protected final Address arrivalAddress;

    @MandatoryForContract
    protected final LclAssignmentStatus status;

    @MandatoryForContract
    protected final List<ItemRow> expectedGoods;

    @ConstructorForDeserialization
    public AssignmentState(UniqueIdentifier linearId, Party lclCompany, Party buyer, Party supplier, Party arrivalParty, Address departureAddress, Address arrivalAddress, List<ItemRow> expectedGoods, LclAssignmentStatus status) {
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

    public AssignmentState(Party lclCompany, Party buyer, Party supplier, Party arrivalParty, Address departureAddress, Address arrivalAddress, List<ItemRow> expectedGoods, LclAssignmentStatus status) {
        this.linearId = new UniqueIdentifier();
        this.buyer = buyer;
        this.departureAddress = departureAddress;
        this.supplier = supplier;
        this.arrivalAddress = arrivalAddress;
        this.lclCompany = lclCompany;
        this.expectedGoods = expectedGoods;
        this.arrivalParty = arrivalParty;
        this.status = status;
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

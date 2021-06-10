package agata.lcl.states.assignment;

import agata.bol.dataholder.Address;
import agata.lcl.contracts.annotations.MandatoryForContract;
import agata.lcl.contracts.assignment.AssignmentContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import net.corda.core.serialization.CordaSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@BelongsToContract(AssignmentContract.class)
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
    protected final Status status;

    @MandatoryForContract
    protected final List<String> expectedGoods;

    @ConstructorForDeserialization
    public AssignmentState(UniqueIdentifier linearId, Party lclCompany, Party buyer, Party supplier, Party arrivalParty, Address departureAddress, Address arrivalAddress, List<String> expectedGoods, Status status) {
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

    public AssignmentState(Party lclCompany, Party buyer, Party supplier, Party arrivalParty, Address departureAddress, Address arrivalAddress, List<String> expectedGoods, Status status) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignmentState that = (AssignmentState) o;
        return Objects.equals(linearId, that.linearId) &&
                Objects.equals(buyer, that.buyer) &&
                Objects.equals(lclCompany, that.lclCompany) &&
                Objects.equals(departureAddress, that.departureAddress) &&
                Objects.equals(supplier, that.supplier) &&
                Objects.equals(arrivalAddress, that.arrivalAddress) &&
                Objects.equals(arrivalParty, that.arrivalParty) &&
                status == that.status &&
                Objects.equals(expectedGoods, that.expectedGoods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(linearId, buyer, lclCompany, departureAddress, supplier, arrivalAddress, arrivalParty, status, expectedGoods);
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

    public Party getBuyer() {
        return buyer;
    }

    public Address getDepartureAddress() {
        return departureAddress;
    }

    public Party getSupplier() {
        return supplier;
    }

    public Address getArrivalAddress() {
        return arrivalAddress;
    }

    public Party getLclCompany() {
        return lclCompany;
    }

    public List<String> getExpectedGoods() {
        return expectedGoods;
    }

    public Party getArrivalParty() {
        return arrivalParty;
    }

    public Status getStatus() {
        return status;
    }

    @CordaSerializable
    public enum Status {
        SlotBooked,
        GoodsPickedUp,
    }
}

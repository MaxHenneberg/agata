package agata.lcl.states.pickup;

import agata.bol.dataholder.ItemRow;
import agata.lcl.contracts.GenericProposalContract;
import agata.lcl.contracts.annotations.MandatoryForContract;
import agata.lcl.contracts.annotations.NotEmptyForContract;
import agata.lcl.contracts.pickup.PickupContract;
import agata.lcl.states.assignment.AssignmentState;
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

@BelongsToContract(PickupContract.class)
public class PickupState implements LinearState {

    protected final UniqueIdentifier linearId;

    @MandatoryForContract
    protected final Party buyer;
    @MandatoryForContract
    protected final Party supplier;
    @MandatoryForContract
    protected final Party lclCompany;

    @NotEmptyForContract(value = GenericProposalContract.Commands.Modify.class)
    protected final List<ItemRow> pickedUpGoods;

    @MandatoryForContract
    protected final LinearPointer<AssignmentState> referenceToAssignmentState;

    @NotEmptyForContract(value = GenericProposalContract.Commands.Modify.class)
    protected final String invoiceId;

    @ConstructorForDeserialization
    public PickupState(Party buyer, Party supplier, Party lclCompany, List<ItemRow> pickedUpGoods, LinearPointer<AssignmentState> referenceToAssignmentProposal, String invoiceId,
                       UniqueIdentifier linearId) {
        this.linearId = linearId;
        this.buyer = buyer;
        this.supplier = supplier;
        this.lclCompany = lclCompany;
        this.pickedUpGoods = pickedUpGoods;
        this.referenceToAssignmentState = referenceToAssignmentProposal;
        this.invoiceId = invoiceId;
    }

    public PickupState(Party buyer, Party supplier, Party lclCompany, List<ItemRow> pickedUpGoods, UniqueIdentifier referenceToAssignmentProposal, String invoiceId) {
        this.linearId = new UniqueIdentifier();
        this.buyer = buyer;
        this.supplier = supplier;
        this.lclCompany = lclCompany;
        this.pickedUpGoods = pickedUpGoods;
        this.referenceToAssignmentState = new LinearPointer<>(new UniqueIdentifier(null, referenceToAssignmentProposal.getId()), AssignmentState.class);
        this.invoiceId = invoiceId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PickupState) {
            return this.buyer.equals(((PickupState) obj).buyer)
                    && this.supplier.equals(((PickupState) obj).supplier)
                    && this.lclCompany.equals(((PickupState) obj).lclCompany)
                    && this.pickedUpGoods.equals(((PickupState) obj).pickedUpGoods)
                    && this.referenceToAssignmentState.equals(((PickupState) obj).referenceToAssignmentState)
                    && this.invoiceId.equals(((PickupState) obj).invoiceId);
        }

        return false;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(supplier, lclCompany);
    }

    public Party getBuyer() {
        return buyer;
    }

    public Party getSupplier() {
        return supplier;
    }

    public Party getLclCompany() {
        return lclCompany;
    }

    public List<ItemRow> getPickedUpGoods() {
        return pickedUpGoods;
    }

    public LinearPointer<AssignmentState> getReferenceToAssignmentState() {
        return referenceToAssignmentState;
    }
}

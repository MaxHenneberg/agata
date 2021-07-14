package agata.lcl.states.pickup;

import agata.bol.dataholder.ItemRow;
import agata.lcl.contracts.GenericProposalContract;
import agata.lcl.contracts.annotations.MandatoryForContract;
import agata.lcl.contracts.annotations.NotEmptyForContract;
import agata.lcl.contracts.pickup.PickupContract;
import agata.lcl.states.assignment.AssignmentState;
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

@Getter
@EqualsAndHashCode
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
    public PickupState(Party buyer, Party supplier, Party lclCompany, List<ItemRow> pickedUpGoods, LinearPointer<AssignmentState> referenceToAssignmentState, String invoiceId,
                       UniqueIdentifier linearId) {
        this.linearId = linearId;
        this.buyer = buyer;
        this.supplier = supplier;
        this.lclCompany = lclCompany;
        this.pickedUpGoods = pickedUpGoods;
        this.referenceToAssignmentState = referenceToAssignmentState;
        this.invoiceId = invoiceId;
    }

    public PickupState(Party buyer, Party supplier, Party lclCompany, List<ItemRow> pickedUpGoods, UniqueIdentifier referenceToAssignmentState, String invoiceId) {
        this.linearId = new UniqueIdentifier();
        this.buyer = buyer;
        this.supplier = supplier;
        this.lclCompany = lclCompany;
        this.pickedUpGoods = pickedUpGoods;
        this.referenceToAssignmentState = new LinearPointer<>(new UniqueIdentifier(null, referenceToAssignmentState.getId()), AssignmentState.class);
        this.invoiceId = invoiceId;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(supplier, buyer, lclCompany);
    }
}

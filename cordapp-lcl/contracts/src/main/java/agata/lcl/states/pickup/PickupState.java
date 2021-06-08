package agata.lcl.states.pickup;

import agata.lcl.contracts.annotations.MandatoryForContract;
import agata.lcl.contracts.pickup.PickupContract;
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
    protected final Party exporter;
    @MandatoryForContract
    protected final Party supplier;
    @MandatoryForContract
    protected final Party lclCompany;

    @MandatoryForContract
    protected final List<String> pickedUpGoods;

    @MandatoryForContract
    protected final LinearPointer<LinearState> referenceToAssignmentProposal;

    @ConstructorForDeserialization
    public PickupState(Party exporter, Party supplier, Party lclCompany, List<String> pickedUpGoods, LinearPointer<LinearState> referenceToState1, UniqueIdentifier linearId) {
        this.linearId = linearId;
        this.exporter = exporter;
        this.supplier = supplier;
        this.lclCompany = lclCompany;
        this.pickedUpGoods = pickedUpGoods;
        this.referenceToAssignmentProposal = referenceToState1;
    }

    public PickupState(Party exporter, Party supplier, Party lclCompany, List<String> pickedUpGoods, UniqueIdentifier referenceToState1) {
        this.linearId = new UniqueIdentifier();
        this.exporter = exporter;
        this.supplier = supplier;
        this.lclCompany = lclCompany;
        this.pickedUpGoods = pickedUpGoods;
        this.referenceToAssignmentProposal = new LinearPointer<>(new UniqueIdentifier(null, referenceToState1.getId()), LinearState.class);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PickupState) {
            return this.exporter.equals(((PickupState) obj).exporter)
                    && this.supplier.equals(((PickupState) obj).supplier)
                    && this.lclCompany.equals(((PickupState) obj).lclCompany)
                    && this.pickedUpGoods.equals(((PickupState) obj).pickedUpGoods)
                    && this.referenceToAssignmentProposal.equals(((PickupState) obj).referenceToAssignmentProposal);
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

    public Party getExporter() {
        return exporter;
    }

    public Party getSupplier() {
        return supplier;
    }

    public Party getLclCompany() {
        return lclCompany;
    }

    public List<String> getPickedUpGoods() {
        return pickedUpGoods;
    }

    public LinearPointer<LinearState> getReferenceToAssignmentProposal() {
        return referenceToAssignmentProposal;
    }
}

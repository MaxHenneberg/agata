package agata.lcl.states.pickup;

import agata.lcl.contracts.pickup.PickupContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@BelongsToContract(PickupContract.class)
public class PickupState implements LinearState {

    private final UniqueIdentifier linearId;

    private final Party exporter;
    private final Party supplier;
    private final Party lclCompany;

    private final List<String> pickedUpGoods;

    private final UniqueIdentifier referenceToState1;

    public PickupState(Party exporter, Party supplier, Party lclCompany, List<String> pickedUpGoods, UniqueIdentifier referenceToState1) {
        this.linearId = new UniqueIdentifier();
        this.exporter = exporter;
        this.supplier = supplier;
        this.lclCompany = lclCompany;
        this.pickedUpGoods = pickedUpGoods;
        this.referenceToState1 = referenceToState1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PickupState) {
            return this.exporter.equals(((PickupState) obj).exporter)
                    && this.supplier.equals(((PickupState) obj).supplier)
                    && this.lclCompany.equals(((PickupState) obj).lclCompany)
                    && this.pickedUpGoods.equals(((PickupState) obj).pickedUpGoods)
                    && this.referenceToState1.equals(((PickupState) obj).referenceToState1);
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

    public UniqueIdentifier getReferenceToState1() {
        return referenceToState1;
    }
}

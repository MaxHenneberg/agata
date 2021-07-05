package agata.lcl.states.delivery;

import agata.bol.dataholder.ItemRow;
import agata.lcl.contracts.annotations.MandatoryForContract;
import agata.lcl.contracts.delivery.PackageDeliveryContract;
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

import static agata.lcl.contracts.GenericProposalContract.Commands.Accept;
import static agata.lcl.contracts.GenericProposalContract.Commands.Modify;

@Getter
@EqualsAndHashCode
@BelongsToContract(PackageDeliveryContract.class)
public class PackageDeliveryState implements LinearState {

    protected final UniqueIdentifier linearId;

    @MandatoryForContract
    protected final Party arrivalParty;

    @MandatoryForContract
    private final Party lclCompany;

    @MandatoryForContract
    private final UniqueIdentifier houseBolId;

    @Setter
    @MandatoryForContract(value = {Modify.class, Accept.class})
    private List<ItemRow> deliveredGoods;

    public PackageDeliveryState(Party arrivalParty, Party lclCompany, UniqueIdentifier houseBolId) {
        this(new UniqueIdentifier(), arrivalParty, lclCompany, houseBolId, null);
    }

    @ConstructorForDeserialization
    public PackageDeliveryState(UniqueIdentifier linearId, Party arrivalParty, Party lclCompany, UniqueIdentifier houseBolId, List<ItemRow> deliveredGoods) {
        this.linearId = linearId;
        this.arrivalParty = arrivalParty;
        this.lclCompany = lclCompany;
        this.houseBolId = houseBolId;
        this.deliveredGoods = deliveredGoods;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(this.arrivalParty, this.lclCompany);
    }
}

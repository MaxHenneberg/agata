


package agata.lcl.states.deconsolidation;

import agata.bol.dataholder.Price;
import agata.lcl.contracts.GenericProposalContract;
import agata.lcl.contracts.annotations.MandatoryForContract;
import agata.lcl.contracts.deconsolidation.DeconsolidationContract;
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

@Getter
@EqualsAndHashCode
@BelongsToContract(DeconsolidationContract.class)
public class DeconsolidationState implements LinearState {

    protected final UniqueIdentifier linearId;

    @MandatoryForContract
    private final Party lclCompany;

    @MandatoryForContract
    private final Party shippingLine;

    @MandatoryForContract
    private final UniqueIdentifier masterBillOfLadingId;

    @MandatoryForContract
    private final String containerNo;

    @Setter
    @MandatoryForContract(value = {DeconsolidationContract.Commands.ReleaseContainer.class, GenericProposalContract.Commands.Accept.class})
    private Price collect; // Additional fees paid at the deconsolidation port

    public DeconsolidationState(Party lclCompany, Party shippingLine, UniqueIdentifier masterBillOfLadingId, String containerNo) {
        this.linearId = new UniqueIdentifier();
        this.lclCompany = lclCompany;
        this.shippingLine = shippingLine;
        this.masterBillOfLadingId = masterBillOfLadingId;
        this.containerNo = containerNo;
    }

    @ConstructorForDeserialization
    public DeconsolidationState(UniqueIdentifier linearId, Party lclCompany, Party shippingLine, Price collect, String containerNo, UniqueIdentifier masterBillOfLadingId) {
        this.linearId = linearId;
        this.lclCompany = lclCompany;
        this.shippingLine = shippingLine;
        this.collect = collect;
        this.containerNo = containerNo;
        this.masterBillOfLadingId = masterBillOfLadingId;
    }

    // Copy constructor
    public DeconsolidationState(DeconsolidationState toCopy) {
        this(toCopy.getLinearId(), toCopy.getLclCompany(), toCopy.getShippingLine(), toCopy.getCollect(), toCopy.getContainerNo(), toCopy.getMasterBillOfLadingId());
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(this.lclCompany, this.shippingLine);
    }
}


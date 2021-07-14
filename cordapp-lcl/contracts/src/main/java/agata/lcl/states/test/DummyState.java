package agata.lcl.states.test;

import agata.lcl.contracts.GenericProposalContract;
import agata.lcl.contracts.annotations.MandatoryForContract;
import agata.lcl.contracts.annotations.NotEmptyForContract;
import agata.lcl.contracts.test.DummyProposalContract;
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
@Setter
@EqualsAndHashCode
@BelongsToContract(DummyProposalContract.class)
public class DummyState implements LinearState {

    protected final Party a;
    protected final Party b;

    @NotEmptyForContract
    protected String mandatoryStringField;

    protected final String notMandatoryStringField;

    @MandatoryForContract
    protected final Integer mandatoryObjectField;

    protected final Integer notMandatoryField;

    @NotEmptyForContract(value = GenericProposalContract.Commands.Modify.class)
    protected final String mandatoryStringForModify;

    @MandatoryForContract(value = DummyProposalContract.Commands.Modify.class)
    protected final Integer mandatoryForModify;

    protected final UniqueIdentifier linearId;

    @ConstructorForDeserialization
    public DummyState(Party a, Party b, String mandatoryStringField, String notMandatoryStringField, Integer mandatoryObjectField, Integer notMandatoryField, String mandatoryStringForModify,
                      Integer mandatoryForModify,
                      UniqueIdentifier linearId) {
        this.a = a;
        this.b = b;
        this.mandatoryStringField = mandatoryStringField;
        this.notMandatoryStringField = notMandatoryStringField;
        this.mandatoryObjectField = mandatoryObjectField;
        this.notMandatoryField = notMandatoryField;
        this.mandatoryStringForModify = mandatoryStringForModify;
        this.mandatoryForModify = mandatoryForModify;
        this.linearId = linearId;
    }

    public DummyState(Party a, Party b, String mandatoryStringField, String notMandatoryStringField, Integer mandatoryObjectField, Integer notMandatoryField, String mandatoryStringForModify,
                      Integer mandatoryForModify) {
        this.a = a;
        this.b = b;
        this.mandatoryStringField = mandatoryStringField;
        this.notMandatoryStringField = notMandatoryStringField;
        this.mandatoryObjectField = mandatoryObjectField;
        this.notMandatoryField = notMandatoryField;
        this.mandatoryStringForModify = mandatoryStringForModify;
        this.mandatoryForModify = mandatoryForModify;
        this.linearId = new UniqueIdentifier();
    }


    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(a, b);
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }
}

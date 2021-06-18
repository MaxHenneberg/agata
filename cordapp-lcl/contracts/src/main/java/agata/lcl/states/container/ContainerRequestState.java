package agata.lcl.states.container;

import agata.bol.dataholder.ContainerInformation;
import agata.bol.enums.ContainerType;
import agata.lcl.contracts.annotations.MandatoryForContract;
import agata.lcl.contracts.annotations.NotEmptyForContract;
import agata.lcl.contracts.container.ContainerRequestContract;
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

import static agata.lcl.contracts.container.ContainerRequestContract.Commands.Accept;
import static agata.lcl.contracts.container.ContainerRequestContract.Commands.AssignContainer;

@BelongsToContract(ContainerRequestContract.class)
@Getter
@EqualsAndHashCode
public class ContainerRequestState implements LinearState {

    private final UniqueIdentifier linearId;

    @MandatoryForContract
    private final Party shippingLine;

    @MandatoryForContract
    private final Party lclCompany;

    @MandatoryForContract
    private final Party lclDestination; // Can be the same as shipper (LCL company), but also another LCL company

    @NotEmptyForContract
    private final String portOfLoading;

    @NotEmptyForContract
    private final String portOfDischarge;

    @NotEmptyForContract
    private final String forwardingAgentNo;

    @MandatoryForContract
    private final ContainerType requestedType;

    @Setter
    // Can (and will) be empty e.g. when executing the Propose command
    @NotEmptyForContract(value = {AssignContainer.class, Accept.class})
    private String vesselName;

    @Setter
    // Can (and will) be empty e.g. when executing the Propose command
    @NotEmptyForContract(value = {AssignContainer.class, Accept.class})
    private ContainerInformation container;

    public ContainerRequestState(Party shippingLine, Party lclCompany, Party lclDestination, String portOfLoading, String portOfDischarge, String forwardingAgentNo, ContainerType requestedType, String vesselName, ContainerInformation container) {
        this.linearId = new UniqueIdentifier();
        this.shippingLine = shippingLine;
        this.lclCompany = lclCompany;
        this.lclDestination = lclDestination;
        this.portOfLoading = portOfLoading;
        this.portOfDischarge = portOfDischarge;
        this.forwardingAgentNo = forwardingAgentNo;
        this.requestedType = requestedType;
        this.vesselName = vesselName;
        this.container = container;
    }

    // Copy constructor
    public ContainerRequestState(ContainerRequestState state) {
        this(state.linearId, state.shippingLine, state.lclCompany, state.lclDestination, state.portOfLoading, state.portOfDischarge, state.forwardingAgentNo, state.requestedType, state.vesselName, state.container);
    }

    @ConstructorForDeserialization
    public ContainerRequestState(UniqueIdentifier linearId, Party shippingLine, Party lclCompany, Party lclDestination, String portOfLoading, String portOfDischarge, String forwardingAgentNo, ContainerType requestedType, String vesselName, ContainerInformation container) {
        this.linearId = linearId;
        this.shippingLine = shippingLine;
        this.lclCompany = lclCompany;
        this.lclDestination = lclDestination;
        this.portOfLoading = portOfLoading;
        this.portOfDischarge = portOfDischarge;
        this.forwardingAgentNo = forwardingAgentNo;
        this.requestedType = requestedType;
        this.vesselName = vesselName;
        this.container = container;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(lclCompany, shippingLine);
    }


}

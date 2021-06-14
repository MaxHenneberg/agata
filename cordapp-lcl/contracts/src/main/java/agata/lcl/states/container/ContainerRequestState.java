package agata.lcl.states.container;

import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

// @BelongsToContract(ContainerRequestContract.class)
public class ContainerRequestState implements LinearState {

    private final UniqueIdentifier linearId;
    private final Party shippingLine;
    private final Party lclCompany;
    private final Party lclDestination; // Can be the same as shipper (LCL company), but also another LCL company
    private final String portOfLoading;
    private final String portOfDischarge;
    private final String forwardingAgentNo;
    private final ContainerSize size;
    private final String vesselName;
    private final List<String> containerNumbers;

    public ContainerRequestState(Party shippingLine, Party lclCompany, Party lclDestination, String portOfLoading, String portOfDischarge, String forwardingAgentNo, ContainerSize size, String vesselName, List<String> containerNumbers) {
        this.linearId = new UniqueIdentifier();
        this.shippingLine = shippingLine;
        this.lclCompany = lclCompany;
        this.lclDestination = lclDestination;
        this.portOfLoading = portOfLoading;
        this.portOfDischarge = portOfDischarge;
        this.forwardingAgentNo = forwardingAgentNo;
        this.size = size;
        this.vesselName = vesselName;
        this.containerNumbers = containerNumbers;
    }

    @ConstructorForDeserialization
    public ContainerRequestState(UniqueIdentifier linearId, Party shippingLine, Party lclCompany, Party lclDestination, String portOfLoading, String portOfDischarge, String forwardingAgentNo, ContainerSize size, String vesselName, List<String> containerNumbers) {
        this.linearId = linearId;
        this.shippingLine = shippingLine;
        this.lclCompany = lclCompany;
        this.lclDestination = lclDestination;
        this.portOfLoading = portOfLoading;
        this.portOfDischarge = portOfDischarge;
        this.forwardingAgentNo = forwardingAgentNo;
        this.size = size;
        this.vesselName = vesselName;
        this.containerNumbers = containerNumbers;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        // TODO: Add lclDestination here as well?
        return Arrays.asList(lclCompany, shippingLine);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContainerRequestState that = (ContainerRequestState) o;
        return Objects.equals(linearId, that.linearId) &&
                Objects.equals(shippingLine, that.shippingLine) &&
                Objects.equals(lclCompany, that.lclCompany) &&
                Objects.equals(lclDestination, that.lclDestination) &&
                Objects.equals(portOfLoading, that.portOfLoading) &&
                Objects.equals(portOfDischarge, that.portOfDischarge) &&
                Objects.equals(forwardingAgentNo, that.forwardingAgentNo) &&
                size == that.size &&
                Objects.equals(vesselName, that.vesselName) &&
                Objects.equals(containerNumbers, that.containerNumbers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(linearId, shippingLine, lclCompany, lclDestination, portOfLoading, portOfDischarge, forwardingAgentNo, size, vesselName, containerNumbers);
    }

    public Party getShippingLine() {
        return shippingLine;
    }

    public Party getLclCompany() {
        return lclCompany;
    }

    public Party getLclDestination() {
        return lclDestination;
    }

    public String getPortOfLoading() {
        return portOfLoading;
    }

    public String getPortOfDischarge() {
        return portOfDischarge;
    }

    public String getForwardingAgentNo() {
        return forwardingAgentNo;
    }

    public ContainerSize getSize() {
        return size;
    }

    public String getVesselName() {
        return vesselName;
    }

    public List<String> getContainerNumbers() {
        return containerNumbers;
    }

    public enum ContainerSize {
        Small, // 20 foot
        Large // 40 foot
    }
}

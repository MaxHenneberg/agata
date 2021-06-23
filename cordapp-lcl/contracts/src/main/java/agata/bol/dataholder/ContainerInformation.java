package agata.bol.dataholder;

import agata.bol.enums.ContainerType;
import net.corda.core.serialization.CordaSerializable;

import java.util.Objects;

@CordaSerializable
public class ContainerInformation {

    private final String containerNo;
    private final ContainerType type;
    private String sealNo;

    public ContainerInformation(String containerNo, String sealNo, ContainerType type) {
        this.containerNo = containerNo;
        this.sealNo = sealNo;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContainerInformation that = (ContainerInformation) o;
        return Objects.equals(containerNo, that.containerNo) &&
                type == that.type &&
                Objects.equals(sealNo, that.sealNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(containerNo, type, sealNo);
    }

    public String getContainerNo() {
        return containerNo;
    }

    public String getSealNo() {
        return sealNo;
    }

    public void setSealNo(String sealNo) {
        this.sealNo = sealNo;
    }

    public ContainerType getType() {
        return type;
    }
}

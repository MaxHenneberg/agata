package agata.bol.dataholder;

import agata.bol.enums.ContainerType;

public class ContainerInformation {

    private final String containerNo;
    private final ContainerType type;
    private String sealNo;

    public ContainerInformation(String containerNo, String sealNo, ContainerType type) {
        this.containerNo = containerNo;
        this.sealNo = sealNo;
        this.type = type;
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

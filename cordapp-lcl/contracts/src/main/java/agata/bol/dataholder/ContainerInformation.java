package agata.bol.dataholder;

public class ContainerInformation {
    private final String containerNo;
    private final String sealNo;
    private final int type;

    public ContainerInformation(String containerNo, String sealNo, int type) {
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

    public int getType() {
        return type;
    }
}

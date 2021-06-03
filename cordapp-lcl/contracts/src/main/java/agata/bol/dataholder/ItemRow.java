package agata.bol.dataholder;

public class ItemRow {
    private final String mark;
    private final String identityNumber;

    private final int noOfPackages;

    private final DescriptionOfGoods descriptionOfGoods;

    private final int netWeight;
    private final int grossWeight;
    private final int measurement;

    public ItemRow(String mark, String identityNumber, int noOfPackages, DescriptionOfGoods descriptionOfGoods, int netWeight, int grossWeight, int measurement) {
        this.mark = mark;
        this.identityNumber = identityNumber;
        this.noOfPackages = noOfPackages;
        this.descriptionOfGoods = descriptionOfGoods;
        this.netWeight = netWeight;
        this.grossWeight = grossWeight;
        this.measurement = measurement;
    }

    public String getMark() {
        return mark;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public int getNoOfPackages() {
        return noOfPackages;
    }

    public DescriptionOfGoods getDescriptionOfGoods() {
        return descriptionOfGoods;
    }

    public int getNetWeight() {
        return netWeight;
    }

    public int getGrossWeight() {
        return grossWeight;
    }

    public int getMeasurement() {
        return measurement;
    }
}

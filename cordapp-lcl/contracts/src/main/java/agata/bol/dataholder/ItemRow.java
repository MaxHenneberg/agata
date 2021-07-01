package agata.bol.dataholder;

import lombok.ToString;
import net.corda.core.serialization.CordaSerializable;

import java.util.Objects;

@ToString
@CordaSerializable
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemRow itemRow = (ItemRow) o;
        return noOfPackages == itemRow.noOfPackages &&
                netWeight == itemRow.netWeight &&
                grossWeight == itemRow.grossWeight &&
                measurement == itemRow.measurement &&
                Objects.equals(mark, itemRow.mark) &&
                Objects.equals(identityNumber, itemRow.identityNumber) &&
                Objects.equals(descriptionOfGoods, itemRow.descriptionOfGoods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mark, identityNumber, noOfPackages, descriptionOfGoods, netWeight, grossWeight, measurement);
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

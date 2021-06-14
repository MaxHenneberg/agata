package agata.bol.dataholder;

import net.corda.core.serialization.CordaSerializable;

import java.util.Objects;

@CordaSerializable
public class DescriptionOfGoods {

    private final String product;
    private final String typeOfPackage;
    private final int quantity;

    public DescriptionOfGoods(String product, String typeOfPackage, int quantity) {
        this.product = product;
        this.typeOfPackage = typeOfPackage;
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DescriptionOfGoods that = (DescriptionOfGoods) o;
        return quantity == that.quantity &&
                Objects.equals(product, that.product) &&
                Objects.equals(typeOfPackage, that.typeOfPackage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, typeOfPackage, quantity);
    }

    public String getProduct() {
        return product;
    }

    public String getTypeOfPackage() {
        return typeOfPackage;
    }

    public int getQuantity() {
        return quantity;
    }
}

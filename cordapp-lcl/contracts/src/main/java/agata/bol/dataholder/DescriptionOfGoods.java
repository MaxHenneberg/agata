package agata.bol.dataholder;

import java.util.List;

public class DescriptionOfGoods {

    private String freeTextDescription;
    private final String product;
    private final String typeOfPackage;
    private final int quantity;
    private final List<String> handlingInstructions;

    public DescriptionOfGoods(String product, String typeOfPackage, int quantity, List<String> handlingInstructions) {
        this.product = product;
        this.typeOfPackage = typeOfPackage;
        this.quantity = quantity;
        this.handlingInstructions = handlingInstructions;
    }

    public DescriptionOfGoods(String freeTextDescription, String product, String typeOfPackage, int quantity, List<String> handlingInstructions) {
        this.freeTextDescription = freeTextDescription;
        this.product = product;
        this.typeOfPackage = typeOfPackage;
        this.quantity = quantity;
        this.handlingInstructions = handlingInstructions;
    }

    public String getFreeTextDescription() {
        return freeTextDescription;
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

    public List<String> getHandlingInstructions() {
        return handlingInstructions;
    }
}

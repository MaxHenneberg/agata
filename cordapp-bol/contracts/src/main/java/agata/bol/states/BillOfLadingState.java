package agata.bol.states;

import agata.bol.dataholder.*;
import agata.bol.enums.Payable;
import agata.bol.enums.TypeOfMovement;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BillOfLadingState implements ContractState {

    private Company shipper;
    private Company consignee;
    private Company notifyParty;

    private String modeOfInitialCarriage;
    private String placeOfInitialReceipt;

    private String vesselName;
    private String portOfLoading;
    private String portOfDischarge;
    private String placeOfDeliveryByCarrier;

    private String bookingNo;
    private String billOfLadingNo;

    private List<String> exportReference;

    //What does Full Details Mean ?
    //13
    private Company forwardingAgent;
    private String fmcNo;

    //What does Point mean ?
    //14
    private String pointAndCountry;

    //15
    private Company cargoReleaser;

    private String domesticRoutingInstructions;

    private Payable freightPayableAt;
    private TypeOfMovement typeOfMovement;

    private List<ItemRow> goodsList;

    //Is this a list of tasks performed and the price for it ?
    //25
    private List<FreightCharges> freightChargesList;

    private Price prepaid;

    private Price collect;

    private List<Incoterm> incotermList;

    private List<ContainerInformation> containerInformationList;

    /**
     * Will be Overridden by LcL subclasses to define participants correctly.
     * @return List of Participants
     */
    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return List.of(shipper.getCordaParty(), consignee.getCordaParty());
    }

    public int getConsignmentTotalNetWeight() {
        return goodsList.stream().reduce(0, (subtotal, element) -> subtotal + element.getNetWeight(), Integer::sum);
    }

    public int getConsignmentTotalGrossWeight() {
        return goodsList.stream().reduce(0, (subtotal, element) -> subtotal + element.getGrossWeight(), Integer::sum);
    }

    public int getConsignmentTotalMeasurements() {
        return goodsList.stream().reduce(0, (subtotal, element) -> subtotal + element.getMeasurement(), Integer::sum);
    }
}

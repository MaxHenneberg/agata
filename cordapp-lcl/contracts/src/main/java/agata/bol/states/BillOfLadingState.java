package agata.bol.states;

import agata.bol.contracts.BillOfLadingContract;
import agata.bol.dataholder.*;
import agata.bol.enums.Payable;
import agata.bol.enums.TypeOfMovement;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@BelongsToContract(BillOfLadingContract.class)
public class BillOfLadingState implements ContractState, LinearState {

    private final UniqueIdentifier linearId;

    private final Party shipper;
    private final Party consignee;
    private final Party notifyParty;

    private final String modeOfInitialCarriage;
    private final String placeOfInitialReceipt;

    private final String vesselName;
    private final String portOfLoading;
    private final String portOfDischarge;
    private final String placeOfDeliveryByCarrier;

    private final String bookingNo;
    private final String billOfLadingNo;

    private final List<String> exportReference;

    //13
    private final Party forwardingAgent;
    private final String fmcNo;

    //14
    private final Address pointAndCountry;

    //15
    private final Party cargoReleaser;

    private final String domesticRoutingInstructions;

    private final Payable freightPayableAt;
    private final TypeOfMovement typeOfMovement;

    private final List<ItemRow> goodsList;

    //25
    private final List<FreightCharges> freightChargesList;

    private final Price prepaid;

    private final Price collect;

    private final List<Incoterm> incotermList;

    private final List<ContainerInformation> containerInformationList;

    public BillOfLadingState(Party shipper, Party consignee, Party notifyParty, String modeOfInitialCarriage, String placeOfInitialReceipt, String vesselName, String portOfLoading,
                             String portOfDischarge, String placeOfDeliveryByCarrier, String bookingNo, String billOfLadingNo, List<String> exportReference, Party forwardingAgent, String fmcNo,
                             Address pointAndCountry, Party cargoReleaser, String domesticRoutingInstructions, Payable freightPayableAt, TypeOfMovement typeOfMovement, List<ItemRow> goodsList,
                             List<FreightCharges> freightChargesList, Price prepaid, Price collect, List<Incoterm> incotermList, List<ContainerInformation> containerInformationList) {
        this.linearId = new UniqueIdentifier();
        this.shipper = shipper;
        this.consignee = consignee;
        this.notifyParty = notifyParty;
        this.modeOfInitialCarriage = modeOfInitialCarriage;
        this.placeOfInitialReceipt = placeOfInitialReceipt;
        this.vesselName = vesselName;
        this.portOfLoading = portOfLoading;
        this.portOfDischarge = portOfDischarge;
        this.placeOfDeliveryByCarrier = placeOfDeliveryByCarrier;
        this.bookingNo = bookingNo;
        this.billOfLadingNo = billOfLadingNo;
        this.exportReference = exportReference;
        this.forwardingAgent = forwardingAgent;
        this.fmcNo = fmcNo;
        this.pointAndCountry = pointAndCountry;
        this.cargoReleaser = cargoReleaser;
        this.domesticRoutingInstructions = domesticRoutingInstructions;
        this.freightPayableAt = freightPayableAt;
        this.typeOfMovement = typeOfMovement;
        this.goodsList = goodsList;
        this.freightChargesList = freightChargesList;
        this.prepaid = prepaid;
        this.collect = collect;
        this.incotermList = incotermList;
        this.containerInformationList = containerInformationList;
    }

    /**
     * Will be Overridden by LcL subclasses to define participants correctly.
     *
     * @return List of Participants
     */
    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(shipper, consignee);
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

    public Party getShipper() {
        return shipper;
    }

    public Party getConsignee() {
        return consignee;
    }

    public Party getNotifyParty() {
        return notifyParty;
    }

    public String getModeOfInitialCarriage() {
        return modeOfInitialCarriage;
    }

    public String getPlaceOfInitialReceipt() {
        return placeOfInitialReceipt;
    }

    public String getVesselName() {
        return vesselName;
    }

    public String getPortOfLoading() {
        return portOfLoading;
    }

    public String getPortOfDischarge() {
        return portOfDischarge;
    }

    public String getPlaceOfDeliveryByCarrier() {
        return placeOfDeliveryByCarrier;
    }

    public String getBookingNo() {
        return bookingNo;
    }

    public String getBillOfLadingNo() {
        return billOfLadingNo;
    }

    public List<String> getExportReference() {
        return exportReference;
    }

    public Party getForwardingAgent() {
        return forwardingAgent;
    }

    public String getFmcNo() {
        return fmcNo;
    }

    public Address getPointAndCountry() {
        return pointAndCountry;
    }

    public Party getCargoReleaser() {
        return cargoReleaser;
    }

    public String getDomesticRoutingInstructions() {
        return domesticRoutingInstructions;
    }

    public Payable getFreightPayableAt() {
        return freightPayableAt;
    }

    public TypeOfMovement getTypeOfMovement() {
        return typeOfMovement;
    }

    public List<ItemRow> getGoodsList() {
        return goodsList;
    }

    public List<FreightCharges> getFreightChargesList() {
        return freightChargesList;
    }

    public Price getPrepaid() {
        return prepaid;
    }

    public Price getCollect() {
        return collect;
    }

    public List<Incoterm> getIncotermList() {
        return incotermList;
    }

    public List<ContainerInformation> getContainerInformationList() {
        return containerInformationList;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }
}

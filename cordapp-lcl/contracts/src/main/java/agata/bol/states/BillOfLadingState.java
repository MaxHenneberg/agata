package agata.bol.states;

import agata.bol.contracts.BillOfLadingContract;
import agata.bol.dataholder.*;
import agata.bol.enums.Payable;
import agata.bol.enums.TypeOfMovement;
import agata.bol.schema.BillOfLadingSchemaV1;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@EqualsAndHashCode
@BelongsToContract(BillOfLadingContract.class)
public class BillOfLadingState implements QueryableState, LinearState {

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

    public BillOfLadingState(Party shipper, Party consignee, Party notifyParty, String modeOfInitialCarriage, String placeOfInitialReceipt, String vesselName,
                             String portOfLoading,
                             String portOfDischarge, String placeOfDeliveryByCarrier, String bookingNo, String billOfLadingNo, List<String> exportReference,
                             Party forwardingAgent, String fmcNo,
                             Address pointAndCountry, Party cargoReleaser, String domesticRoutingInstructions, Payable freightPayableAt,
                             TypeOfMovement typeOfMovement, List<ItemRow> goodsList,
                             List<FreightCharges> freightChargesList, Price prepaid, Price collect, List<Incoterm> incotermList,
                             List<ContainerInformation> containerInformationList) {
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

    @ConstructorForDeserialization
    public BillOfLadingState(Party shipper, Party consignee, Party notifyParty, String modeOfInitialCarriage, String placeOfInitialReceipt, String vesselName,
                             String portOfLoading,
                             String portOfDischarge, String placeOfDeliveryByCarrier, String bookingNo, String billOfLadingNo, List<String> exportReference,
                             Party forwardingAgent, String fmcNo,
                             Address pointAndCountry, Party cargoReleaser, String domesticRoutingInstructions, Payable freightPayableAt,
                             TypeOfMovement typeOfMovement, List<ItemRow> goodsList,
                             List<FreightCharges> freightChargesList, Price prepaid, Price collect, List<Incoterm> incotermList,
                             List<ContainerInformation> containerInformationList, UniqueIdentifier linearId) {
        this.linearId = linearId;
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

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }

    @NotNull
    @Override
    public PersistentState generateMappedObject(@NotNull MappedSchema schema) {
        if (schema instanceof BillOfLadingSchemaV1) {
            return new BillOfLadingSchemaV1.PersistentBOL(
                    this.shipper.getName().toString(),
                    this.consignee.getName().toString(),
                    this.notifyParty.getName().toString(),
                    this.modeOfInitialCarriage,
                    this.placeOfInitialReceipt,
                    this.vesselName,
                    this.portOfLoading,
                    this.portOfDischarge,
                    this.placeOfDeliveryByCarrier,
                    this.bookingNo,
                    this.billOfLadingNo,
                    this.exportReference.stream().map(Object::toString).reduce("", (part, ele) -> part + "," + ele),
                    this.forwardingAgent.getName().toString(),
                    this.fmcNo,
                    this.pointAndCountry.toString(),
                    this.cargoReleaser.getName().toString(),
                    this.domesticRoutingInstructions,
                    this.freightPayableAt.toString(),
                    this.typeOfMovement.toString(),
                    this.goodsList.stream().map(Object::toString).reduce("", (part, ele) -> part + "," + ele),
                    this.freightChargesList.stream().map(Object::toString).reduce("", (part, ele) -> part + "," + ele),
                    this.prepaid.toString(),
                    this.collect.toString(),
                    this.incotermList.stream().map(Object::toString).reduce("", (part, ele) -> part + "," + ele),
                    this.containerInformationList.stream().map(Objects::toString).reduce("", (part, ele) -> part + "," + ele),
                    this.getLinearId().getId()
            );
        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }

    @NotNull
    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        return Collections.singletonList(new BillOfLadingSchemaV1());
    }
}

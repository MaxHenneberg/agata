package agata.bol.schema;

import lombok.Getter;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import org.hibernate.annotations.Type;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Collections;
import java.util.UUID;

public class BillOfLadingSchemaV1 extends MappedSchema {
    public BillOfLadingSchemaV1() {
        super(BillOfLadingSchema.class, 1, Collections.singletonList(PersistentBOL.class));
    }

    @Nullable
    @Override
    public String getMigrationResource() {
        return "bol.changelog-master";
    }

    @Entity
    @Getter
    @Table(name = "bol_states")
    public static class PersistentBOL extends PersistentState {

        @Column(name = "shipper")
        private final String shipper;
        @Column(name = "consignee")
        private final String consignee;
        @Column(name = "notifyParty")
        private final String notifyParty;

        @Column(name = "modeOfInitialCarriage")
        private final String modeOfInitialCarriage;
        @Column(name = "placeOfInitialReceipt")
        private final String placeOfInitialReceipt;

        @Column(name = "vesselName")
        private final String vesselName;
        @Column(name = "portOfLoading")
        private final String portOfLoading;
        @Column(name = "portOfDischarge")
        private final String portOfDischarge;
        @Column(name = "placeOfDeliveryByCarrier")
        private final String placeOfDeliveryByCarrier;

        @Column(name = "bookingNo")
        private final String bookingNo;
        @Column(name = "billOfLadingNo")
        private final String billOfLadingNo;

        @Column(name = "exportReference")
        private final String exportReference;

        @Column(name = "forwardingAgent")
        private final String forwardingAgent;
        @Column(name = "fmcNo")
        private final String fmcNo;

        @Column(name = "pointAndCountry")
        private final String pointAndCountry;

        @Column(name = "cargoReleaser")
        private final String cargoReleaser;

        @Column(name = "domesticRoutingInstructions")
        private final String domesticRoutingInstructions;

        @Column(name = "freightPayableAt")
        private final String freightPayableAt;
        @Column(name = "typeOfMovement")
        private final String typeOfMovement;

        @Column(name = "goodsList")
        private final String goodsList;

        @Column(name = "freightChargesList")
        private final String freightChargesList;

        @Column(name = "prepaid")
        private final String prepaid;
        @Column(name = "collect")
        private final String collect;

        @Column(name = "incotermList")
        private final String incotermList;

        @Column(name = "containerInformationList")
        private final String containerInformationList;

        @Column(name = "linear_id")
        @Type(type = "uuid-char")
        private final UUID linearId;

        public PersistentBOL(String shipper, String consignee, String notifyParty, String modeOfInitialCarriage, String placeOfInitialReceipt,
                             String vesselName, String portOfLoading, String portOfDischarge, String placeOfDeliveryByCarrier, String bookingNo,
                             String billOfLadingNo, String exportReference, String forwardingAgent, String fmcNo, String pointAndCountry,
                             String cargoReleaser, String domesticRoutingInstructions, String freightPayableAt, String typeOfMovement, String goodsList,
                             String freightChargesList, String prepaid, String collect, String incotermList, String containerInformationList, UUID linearId) {
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
            this.linearId = linearId;
        }

        public PersistentBOL() {
            this.shipper = "";
            this.consignee = "";
            this.notifyParty = "";
            this.modeOfInitialCarriage = "";
            this.placeOfInitialReceipt = "";
            this.vesselName = "";
            this.portOfLoading = "";
            this.portOfDischarge = "";
            this.placeOfDeliveryByCarrier = "";
            this.bookingNo = "";
            this.billOfLadingNo = "";
            this.exportReference = "";
            this.forwardingAgent = "";
            this.fmcNo = "";
            this.pointAndCountry = "";
            this.cargoReleaser = "";
            this.domesticRoutingInstructions = "";
            this.freightPayableAt = "";
            this.typeOfMovement = "";
            this.goodsList = "";
            this.freightChargesList = "";
            this.prepaid = "";
            this.collect = "";
            this.incotermList = "";
            this.containerInformationList = "";
            this.linearId = null;
        }
    }
}

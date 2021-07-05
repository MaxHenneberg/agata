package agata.lcl.flows.shiploading;

import agata.bol.dataholder.FreightCharges;
import agata.bol.dataholder.ItemRow;
import agata.bol.dataholder.Price;
import agata.bol.enums.BillOfLadingType;
import agata.bol.enums.Payable;
import agata.bol.enums.TypeOfMovement;
import agata.bol.states.BillOfLadingState;
import agata.lcl.flows.ProposalFlow;
import agata.lcl.states.container.ContainerRequestState;
import agata.lcl.states.shiploading.ShiploadingProposal;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ShiploadingProposalFlow {
    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<UniqueIdentifier> {
        private final UniqueIdentifier containerStateId;

        private final Party shippingLine;
        private final List<UniqueIdentifier> houseBolIds;

        private final String modeOfInitialCarriage;
        private final String placeOfInitialReceipt;
        private final String bookingNo;
        private final String billOfLadingNo;
        private final List<String> exportReference;
        private final Payable freightPayableAt;
        private final TypeOfMovement typeOfMovement;
        private final List<FreightCharges> freightChargesList;
        private final Price prepaid;
        private final Price collect;

        public Initiator(UniqueIdentifier containerStateId, Party shippingLine, List<UniqueIdentifier> houseBolIds, String modeOfInitialCarriage,
                         String placeOfInitialReceipt, String bookingNo, String billOfLadingNo, List<String> exportReference, Payable freightPayableAt,
                         TypeOfMovement typeOfMovement, List<FreightCharges> freightChargesList, Price prepaid, Price collect) {
            this.containerStateId = containerStateId;
            this.shippingLine = shippingLine;
            this.houseBolIds = houseBolIds;
            this.modeOfInitialCarriage = modeOfInitialCarriage;
            this.placeOfInitialReceipt = placeOfInitialReceipt;
            this.bookingNo = bookingNo;
            this.billOfLadingNo = billOfLadingNo;
            this.exportReference = exportReference;
            this.freightPayableAt = freightPayableAt;
            this.typeOfMovement = typeOfMovement;
            this.freightChargesList = freightChargesList;
            this.prepaid = prepaid;
            this.collect = collect;
        }

        @Suspendable
        @Override
        public UniqueIdentifier call() throws FlowException {

            if (this.houseBolIds.size() < 1) {
                throw new FlowException("At least one house bill of lading must be passed to create the master bill of lading");
            }

            QueryCriteria.LinearStateQueryCriteria inputCriteria =
                    new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(containerStateId), Vault.StateStatus.UNCONSUMED,
                            null);
            List<StateAndRef<ContainerRequestState>> containerStateList =
                    getServiceHub().getVaultService().queryBy(ContainerRequestState.class, inputCriteria).getStates();
            if (containerStateList.size() > 1) {
                throw new FlowException("Incorrect amount of proposals found. Expected 1 got " + containerStateList.size());
            }
            final ContainerRequestState containerState = containerStateList.get(0).getState().getData();
            if (!getOurIdentity().equals(containerState.getLclCompany())) {
                throw new FlowException("Flow can only be executed by correct LCL Company");
            }
            QueryCriteria.LinearStateQueryCriteria inputCriteriaBol =
                    new QueryCriteria.LinearStateQueryCriteria(null, this.houseBolIds, Vault.StateStatus.UNCONSUMED,
                            null);

            List<StateAndRef<BillOfLadingState>> houseBolList =
                    getServiceHub().getVaultService().queryBy(BillOfLadingState.class, inputCriteriaBol).getStates();

            if (houseBolList.size() != this.houseBolIds.size()) {
                throw new FlowException("Could not find all House BoLs");
            }

            for (StateAndRef<BillOfLadingState> billOfLadingStateStateAndRef : houseBolList) {
                final BillOfLadingState billOfLadingState = billOfLadingStateStateAndRef.getState().getData();
                if (!billOfLadingState.getContainerInformationList().get(0).getContainerNo().equals(containerState.getContainer().getContainerNo())) {
                    throw new FlowException("All House BoLs need to be for the same Container. Wrong House BoL: " + billOfLadingState.getLinearId());
                }
            }

            final BillOfLadingState houseBol = houseBolList.get(0).getState().getData();
            List<ItemRow> packingList = houseBolList.stream().flatMap(bol -> bol.getState().getData().getGoodsList().stream()).collect(Collectors.toList());

            BillOfLadingState billOfLadingState =
                    new BillOfLadingState(BillOfLadingType.Master, shippingLine, houseBol.getShipper(), containerState.getLclDestination(),
                            this.modeOfInitialCarriage,
                            this.placeOfInitialReceipt, houseBol.getVesselName(), houseBol.getPortOfLoading(),
                            houseBol.getPortOfDischarge(),
                            houseBol.getPlaceOfDeliveryByCarrier(), this.bookingNo, this.billOfLadingNo, this.exportReference, getOurIdentity(),
                            containerState.getForwardingAgentNo(), null, containerState.getLclDestination(), null,
                            this.freightPayableAt, this.typeOfMovement, packingList, this.freightChargesList, this.prepaid, this.collect,
                            null,
                            Collections.singletonList(containerState.getContainer()));

            ShiploadingProposal shiploadingProposal =
                    new ShiploadingProposal(getOurIdentity(), shippingLine, billOfLadingState, containerStateId);

            return subFlow(new ProposalFlow.Initiator(shiploadingProposal));
        }
    }
}

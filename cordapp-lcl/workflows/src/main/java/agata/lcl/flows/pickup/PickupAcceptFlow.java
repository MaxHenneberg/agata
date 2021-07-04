package agata.lcl.flows.pickup;

import agata.bol.contracts.BillOfLadingContract;
import agata.bol.dataholder.FreightCharges;
import agata.bol.dataholder.Price;
import agata.bol.enums.BillOfLadingType;
import agata.bol.enums.Payable;
import agata.bol.enums.TypeOfMovement;
import agata.bol.flows.CreateBoLFlow;
import agata.bol.states.BillOfLadingState;
import agata.lcl.flows.AcceptFlow;
import agata.lcl.flows.LclFlowUtils;
import agata.lcl.states.assignment.AssignmentState;
import agata.lcl.states.container.ContainerRequestState;
import agata.lcl.states.pickup.PickupState;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.transactions.SignedTransaction;

import java.util.Collections;
import java.util.List;

public class PickupAcceptFlow {
    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<UniqueIdentifier> {
        private final UniqueIdentifier proposalId;
        private final UniqueIdentifier referenceToContainerRequest;

        private final String modeOfInitialCarriage;
        private final String placeOfInitialReceipt;
        private final String placeOfDeliveryByCarrier;
        private final String bookingNo;
        private final String billOfLadingNo;
        private final List<String> exportReference;
        private final Payable freightPayableAt;
        private final TypeOfMovement typeOfMovement;
        private final List<FreightCharges> freightChargesList;
        private final Price prepaid;
        private final Price collect;

        public Initiator(UniqueIdentifier proposalId, UniqueIdentifier referenceToContainerRequest, String modeOfInitialCarriage, String placeOfInitialReceipt,
                         String placeOfDeliveryByCarrier, String bookingNo, String billOfLadingNo, List<String> exportReference, Payable freightPayableAt,
                         TypeOfMovement typeOfMovement, List<FreightCharges> freightChargesList, Price prepaid, Price collect) {
            this.proposalId = proposalId;
            this.referenceToContainerRequest = referenceToContainerRequest;
            this.modeOfInitialCarriage = modeOfInitialCarriage;
            this.placeOfInitialReceipt = placeOfInitialReceipt;
            this.placeOfDeliveryByCarrier = placeOfDeliveryByCarrier;
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
            final SignedTransaction signedTransaction = subFlow(new AcceptFlow.Initiator(proposalId));
            final PickupState pickupState = (PickupState) signedTransaction.getCoreTransaction().getOutputStates().get(0);

            final StateAndRef<AssignmentState> assignmentStateRef =
                    LclFlowUtils.resolveIdToStateRef(pickupState.getReferenceToAssignmentState().getPointer(), this, AssignmentState.class);
            final AssignmentState assignmentState = assignmentStateRef.getState().getData();

            final StateAndRef<ContainerRequestState> containerRequestStateRef =
                    LclFlowUtils.resolveIdToStateRef(referenceToContainerRequest, this, ContainerRequestState.class);
            final ContainerRequestState containerRequestState = containerRequestStateRef.getState().getData();

            if (!assignmentState.getLclCompany().equals(containerRequestState.getLclCompany()) || !assignmentState.getLclCompany().equals(getOurIdentity())) {
                throw new FlowException("Flow can only be executed by correct LCL Company");
            }

            // Consignee on the house bill of lading is always the buyer
            BillOfLadingState billOfLadingState = new BillOfLadingState(BillOfLadingType.House, getOurIdentity(), assignmentState.getBuyer(), assignmentState.getBuyer(),
                    this.modeOfInitialCarriage,
                    this.placeOfInitialReceipt, containerRequestState.getVesselName(), containerRequestState.getPortOfLoading(),
                    containerRequestState.getPortOfDischarge(),
                    this.placeOfDeliveryByCarrier, this.bookingNo, this.billOfLadingNo, this.exportReference, getOurIdentity(),
                    containerRequestState.getForwardingAgentNo(), null, containerRequestState.getLclDestination(), null,
                    this.freightPayableAt, this.typeOfMovement, pickupState.getPickedUpGoods(), this.freightChargesList, this.prepaid, this.collect, null,
                    Collections.singletonList(containerRequestState.getContainer()));

            final StateAndRef<PickupState> pickupStateStateAndRef = LclFlowUtils.resolveIdToStateRef(pickupState.getLinearId(), this, PickupState.class);

            return subFlow(new CreateBoLFlow.Initiator(billOfLadingState, Collections.singletonList(pickupStateStateAndRef),
                    new BillOfLadingContract.BoLCommands.CreateHouseBoL()));
        }
    }
}

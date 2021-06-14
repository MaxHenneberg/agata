package agata.lcl.flows.pickup;

import agata.bol.contracts.BillOfLadingContract;
import agata.bol.dataholder.ContainerInformation;
import agata.bol.dataholder.FreightCharges;
import agata.bol.dataholder.Price;
import agata.bol.enums.Payable;
import agata.bol.enums.TypeOfMovement;
import agata.bol.flows.CreateBoLFlow;
import agata.bol.states.BillOfLadingState;
import agata.lcl.contracts.GenericProposalContract;
import agata.lcl.flows.AcceptFlow;
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
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
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
        private final List<ContainerInformation> containerInformationList;

        public Initiator(UniqueIdentifier proposalId, UniqueIdentifier referenceToContainerRequest, String modeOfInitialCarriage, String placeOfInitialReceipt,
                         String placeOfDeliveryByCarrier, String bookingNo, String billOfLadingNo, List<String> exportReference, Payable freightPayableAt,
                         TypeOfMovement typeOfMovement, List<FreightCharges> freightChargesList, Price prepaid, Price collect,
                         List<ContainerInformation> containerInformationList) {
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
            this.containerInformationList = containerInformationList;
        }

        @Suspendable
        @Override
        public UniqueIdentifier call() throws FlowException {
            final SignedTransaction signedTransaction = subFlow(new AcceptFlow.Initiator(proposalId, new GenericProposalContract.Commands.Accept()));
            final PickupState pickupState = (PickupState) signedTransaction.getCoreTransaction().getOutputStates().get(0);

            QueryCriteria.LinearStateQueryCriteria inputCriteriaAssignment = new QueryCriteria.LinearStateQueryCriteria(null,
                    Collections.singletonList(pickupState.getReferenceToAssignmentState().getPointer()), Vault.StateStatus.UNCONSUMED, null);
            List<StateAndRef<AssignmentState>> assignmentStateList = getServiceHub().getVaultService().queryBy(AssignmentState.class, inputCriteriaAssignment).getStates();
            if (assignmentStateList.size() > 1) {
                throw new FlowException("Incorrect amount of proposals found. Expected 1 got " + assignmentStateList.size());
            }
            final AssignmentState assignmentState = assignmentStateList.get(0).getState().getData();

            QueryCriteria.LinearStateQueryCriteria inputCriteriaContainer = new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(referenceToContainerRequest),
                    Vault.StateStatus.UNCONSUMED, null);
            List<StateAndRef<ContainerRequestState>> containerRequestStateList = getServiceHub().getVaultService().queryBy(ContainerRequestState.class, inputCriteriaContainer).getStates();
            if (assignmentStateList.size() > 1) {
                throw new FlowException("Incorrect amount of proposals found. Expected 1 got " + assignmentStateList.size());
            }

            final ContainerRequestState containerRequestState = containerRequestStateList.get(0).getState().getData();

            if (!assignmentState.getLclCompany().equals(containerRequestState.getLclCompany()) || assignmentState.getLclCompany().equals(getOurIdentity())) {
                throw new FlowException("Flow can only be executed by correct LCL Company");
            }

            BillOfLadingState billOfLadingState = new BillOfLadingState(getOurIdentity(), assignmentState.getSupplier(), assignmentState.getBuyer(), this.modeOfInitialCarriage,
                    this.placeOfInitialReceipt, containerRequestState.getVesselName(), containerRequestState.getPortOfLoading(), containerRequestState.getPortOfDischarge(),
                    this.placeOfDeliveryByCarrier, this.bookingNo, this.billOfLadingNo, this.exportReference, getOurIdentity(), containerRequestState.getForwardingAgentNo(), null, null, null,
                    this.freightPayableAt, this.typeOfMovement, pickupState.getPickedUpGoods(), this.freightChargesList, this.prepaid, this.collect, null, this.containerInformationList);

            return subFlow(new CreateBoLFlow.Initiator(billOfLadingState, new BillOfLadingContract.BoLCommands.CreateHouseBoL()));
        }
    }
}

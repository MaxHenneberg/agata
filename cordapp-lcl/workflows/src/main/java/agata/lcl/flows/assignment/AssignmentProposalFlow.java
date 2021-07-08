package agata.lcl.flows.assignment;

import agata.bol.dataholder.Address;
import agata.bol.dataholder.ItemRow;
import agata.lcl.flows.ProposalFlow;
import agata.lcl.flows.tracking.CreateTrackingStateFlow;
import agata.lcl.states.assignment.AssignmentProposal;
import agata.lcl.states.assignment.AssignmentState;
import agata.lcl.states.tracking.TrackingState;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;

import java.util.List;

public class AssignmentProposalFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<UniqueIdentifier> {

        private final Party buyer;
        private final Address departureAddress;
        private final Party supplier;
        private final Address arrivalAddress;
        private final Party arrivalParty;
        private final List<ItemRow> expectedGoods;


        public Initiator(Party buyer, Party supplier, Party arrivalParty, Address departureAddress, Address arrivalAddress, List<ItemRow> expectedGoods) {
            this.buyer = buyer;
            this.departureAddress = departureAddress;
            this.supplier = supplier;
            this.arrivalAddress = arrivalAddress;
            this.arrivalParty = arrivalParty;
            this.expectedGoods = expectedGoods;
        }

        @Override
        @Suspendable
        public UniqueIdentifier call() throws FlowException {
            Party lclCompany = getOurIdentity();

            // Create tracking state
            SignedTransaction trackingStateTx = subFlow(new CreateTrackingStateFlow.Initiator(buyer, supplier));
            List<TrackingState> trackingStateList = trackingStateTx.getTx().outputsOfType(TrackingState.class);
            if (trackingStateList.size() != 1) {
                throw new FlowException("Expected exactly 1 tracking state output but got " + trackingStateList.size());
            }
            UniqueIdentifier trackingStateId = trackingStateList.get(0).getLinearId();

            AssignmentState state = new AssignmentState(lclCompany, buyer, supplier, arrivalParty, departureAddress, arrivalAddress, expectedGoods, trackingStateId);
            AssignmentProposal proposal = new AssignmentProposal(lclCompany, buyer, state);
            return subFlow(new ProposalFlow.Initiator(proposal));
        }
    }

}

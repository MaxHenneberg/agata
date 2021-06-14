package agata.lcl.flows.assignment;

import agata.bol.dataholder.Address;
import agata.bol.dataholder.ItemRow;
import agata.lcl.flows.ProposalFlow;
import agata.lcl.states.assignment.AssignmentProposal;
import agata.lcl.states.assignment.AssignmentState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;

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
        private final AssignmentState.Status status;


        public Initiator(Party buyer, Party supplier, Party arrivalParty, Address departureAddress, Address arrivalAddress, List<ItemRow> expectedGoods) {
            this.buyer = buyer;
            this.departureAddress = departureAddress;
            this.supplier = supplier;
            this.arrivalAddress = arrivalAddress;
            this.arrivalParty = arrivalParty;
            this.expectedGoods = expectedGoods;
            this.status = AssignmentState.Status.SlotBooked;
        }

        @Override
        public UniqueIdentifier call() throws FlowException {
            Party lclCompany = getOurIdentity();
            AssignmentState state = new AssignmentState(lclCompany, buyer, supplier, arrivalParty, departureAddress, arrivalAddress, expectedGoods, status);
            AssignmentProposal proposal = new AssignmentProposal(lclCompany, buyer, state);
            return subFlow(new ProposalFlow.Initiator(proposal));
        }
    }

}

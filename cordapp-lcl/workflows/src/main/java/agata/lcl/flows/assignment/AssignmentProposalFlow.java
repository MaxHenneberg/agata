package agata.lcl.flows.assignment;

import agata.bol.dataholder.Address;
import agata.lcl.flows.ProposalFlow;
import agata.lcl.states.assignment.AssignmentProposal;
import agata.lcl.states.assignment.AssignmentState;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
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
        private final List<String> expectedGoods;
        private final AssignmentState.Status status;


        public Initiator(Party buyer, Party supplier, Party arrivalParty, Address departureAddress, Address arrivalAddress, List<String> expectedGoods) {
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
            return subFlow(new ProposalFlow.ProposalFlowInitiator(proposal));
        }
    }

    @InitiatedBy(Initiator.class)
    public static class Responder extends FlowLogic<Void> {

        private final FlowSession counterpartySession;

        public Responder(FlowSession counterpartySession) {
            this.counterpartySession = counterpartySession;
        }

        @Suspendable
        @Override
        public Void call() throws FlowException {
            SignedTransaction signedTransaction = subFlow(new SignTransactionFlow(counterpartySession) {
                @Suspendable
                @Override
                protected void checkTransaction(SignedTransaction stx) throws FlowException {
                    // Since this flow handles just the proposal, there is nothing to check which could reject the proposal.
                    // If the proposal is not fine with the counterparty, it would simply not initiate the accept flow.
                }
            });

            // Receive the final notarized transaction to be stored in the database
            subFlow(new ReceiveFinalityFlow(counterpartySession, signedTransaction.getId()));
            return null;
        }
    }

}

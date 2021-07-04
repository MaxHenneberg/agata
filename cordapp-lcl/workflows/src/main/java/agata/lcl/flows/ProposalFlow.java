package agata.lcl.flows;

import agata.lcl.contracts.GenericProposalContract;
import agata.lcl.states.Proposal;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.ReferencedStateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProposalFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<UniqueIdentifier> {
        private final Proposal proposalState;
        private final CommandData commandType;
        private final List<ReferencedStateAndRef> additionalReferenceStates;

        public Initiator(Proposal proposalState) {
            this(proposalState, new GenericProposalContract.Commands.Propose(), new ArrayList<>());
        }

        public Initiator(Proposal proposalState, List<ReferencedStateAndRef> additionalReferenceStates) {
            this(proposalState, new GenericProposalContract.Commands.Propose(), additionalReferenceStates);
        }

        public Initiator(Proposal proposalState, GenericProposalContract.Commands.Propose commandType, List<ReferencedStateAndRef> additionalReferenceStates) {
            this.proposalState = proposalState;
            this.commandType = commandType;
            this.additionalReferenceStates = additionalReferenceStates;
        }

        @Suspendable
        @Override
        public UniqueIdentifier call() throws FlowException {
            List<PublicKey> requiredSigners = Arrays.asList(proposalState.getProposer().getOwningKey(), proposalState.getProposee().getOwningKey());
            Command command = new Command(this.commandType, requiredSigners);

            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            TransactionBuilder txBuilder = new TransactionBuilder(notary)
                    .addOutputState(this.proposalState)
                    .addCommand(command);
            if (this.additionalReferenceStates != null) {
                this.additionalReferenceStates.forEach(txBuilder::addReferenceState);
            }

            //Signing the transaction ourselves
            SignedTransaction partStx = getServiceHub().signInitialTransaction(txBuilder);

            //Gather counterparty sigs
            FlowSession counterpartySession = initiateFlow(this.proposalState.getProposee());
            SignedTransaction fullyStx = subFlow(new CollectSignaturesFlow(partStx, Collections.singletonList(counterpartySession)));

            //Finalise the transaction
            SignedTransaction finalisedTx = subFlow(new FinalityFlow(fullyStx, Collections.singletonList(counterpartySession)));
            return finalisedTx.getTx().outputsOfType(Proposal.class).get(0).getLinearId();
        }
    }

    @InitiatedBy(Initiator.class)
    public static class Responder extends FlowLogic<SignedTransaction> {
        private FlowSession counterpartySession;

        public Responder(FlowSession counterpartySession) {
            this.counterpartySession = counterpartySession;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {

            SignTransactionFlow signTransactionFlow = new SignTransactionFlow(counterpartySession) {

                @Override
                protected void checkTransaction(@NotNull SignedTransaction stx) {

                }
            };
            SecureHash txId = subFlow(signTransactionFlow).getId();

            return subFlow(new ReceiveFinalityFlow(this.counterpartySession, txId));
        }
    }


}

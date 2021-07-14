package agata.lcl.flows;

import agata.lcl.contracts.GenericProposalContract;
import agata.lcl.states.Proposal;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AcceptFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<SignedTransaction> {
        private final UniqueIdentifier proposalId;
        private final GenericProposalContract.Commands.Accept commandType;
        private final List<StateAndRef> additionalInputs;

        public Initiator(UniqueIdentifier proposalId) {
            this.proposalId = proposalId;
            this.commandType = new GenericProposalContract.Commands.Accept();
            this.additionalInputs = Collections.emptyList();
        }

        public Initiator(UniqueIdentifier proposalId, List<StateAndRef> additionalInputs, GenericProposalContract.Commands.Accept commandType) {
            this.proposalId = proposalId;
            this.commandType = commandType;
            this.additionalInputs = additionalInputs;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            QueryCriteria.LinearStateQueryCriteria inputCriteria = new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(proposalId), Vault.StateStatus.UNCONSUMED, null);

            StateAndRef inputStateAndRef = getServiceHub().getVaultService().queryBy(Proposal.class, inputCriteria).getStates().get(0);

            Proposal input = (Proposal) inputStateAndRef.getState().getData();
            LinearState output = input.getProposedState();

            List<PublicKey> requiredSigners = output.getParticipants().stream().map(x -> x.getOwningKey()).collect(Collectors.toList());
            Command command = new Command(this.commandType, requiredSigners);

            // Build the transaction
            Party notary = inputStateAndRef.getState().getNotary();
            TransactionBuilder txBuilder = new TransactionBuilder(notary)
                    .addInputState(inputStateAndRef)
                    .addOutputState(output)
                    .addCommand(command);
            for (StateAndRef additionalInput : additionalInputs) {
                txBuilder.addInputState(additionalInput);
            }

            // Sign transaction ourselves
            SignedTransaction partStx = getServiceHub().signInitialTransaction(txBuilder);

            // Gather counterparty's signatures
            List<Party> otherParties = output.getParticipants().stream().filter(x -> !x.equals(getOurIdentity())).map(x -> (Party) x).collect(Collectors.toList());
            List<FlowSession> sessions = otherParties.stream().map(this::initiateFlow).collect(Collectors.toList());
            SignedTransaction fullyStx = subFlow(new CollectSignaturesFlow(partStx, sessions));
            return subFlow(new FinalityFlow(fullyStx, sessions));
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
                protected void checkTransaction(@NotNull SignedTransaction stx) throws FlowException {
                    try {
                        LedgerTransaction ledgerTx = stx.toLedgerTransaction(getServiceHub(), false);
                        Party proposee = ledgerTx.inputsOfType(Proposal.class).get(0).getProposee();
                        if (!proposee.equals(counterpartySession.getCounterparty())) {
                            throw new FlowException("Only the proposee can accept a proposal.");
                        }
                    } catch (SignatureException e) {
                        throw new FlowException("Check transaction failed");
                    }


                }
            };
            SecureHash txId = subFlow(signTransactionFlow).getId();
            return subFlow(new ReceiveFinalityFlow(counterpartySession, txId));
        }
    }
}

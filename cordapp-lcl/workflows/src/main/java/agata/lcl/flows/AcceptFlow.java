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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

            //Creating the command
            List<PublicKey> requiredSigners = Arrays.asList(input.getProposee().getOwningKey(), input.getProposer().getOwningKey());
            Command command = new Command(this.commandType, requiredSigners);

            //Building the transaction
            Party notary = inputStateAndRef.getState().getNotary();

            TransactionBuilder txBuilder = new TransactionBuilder(notary)
                    .addInputState(inputStateAndRef)
                    .addOutputState(output)
                    .addCommand(command);

            for (StateAndRef additionalInput : additionalInputs) {
                txBuilder.addInputState(additionalInput);
            }

            //Signing the transaction ourselves
            SignedTransaction partStx = getServiceHub().signInitialTransaction(txBuilder);

            //Gathering the counterparty's signature
            Party counterparty = (getOurIdentity().equals(input.getProposer())) ? input.getProposee() : input.getProposer();
            FlowSession counterpartySession = initiateFlow(counterparty);
            SignedTransaction fullyStx = subFlow(new CollectSignaturesFlow(partStx, Collections.singletonList(counterpartySession)));

            return subFlow(new FinalityFlow(fullyStx, Collections.singletonList(counterpartySession)));
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

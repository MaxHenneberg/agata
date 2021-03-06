package agata.lcl.flows;

import agata.lcl.contracts.GenericProposalContract;
import agata.lcl.states.Proposal;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.ReferencedStateAndRef;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ModifyFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<SignedTransaction> {
        private final UniqueIdentifier proposalId;
        private final Proposal counterProposal;
        private final GenericProposalContract.Commands.Modify commandType;
        private final List<ReferencedStateAndRef> additionalReferenceStates;

        public Initiator(UniqueIdentifier proposalId, Proposal counterProposal) {
            this(proposalId, counterProposal, new GenericProposalContract.Commands.Modify(), new ArrayList<>());
        }

        public Initiator(
                UniqueIdentifier proposalId,
                Proposal counterProposal,
                GenericProposalContract.Commands.Modify commandType,
                List<ReferencedStateAndRef> additionalReferenceStates) {
            this.proposalId = proposalId;
            this.counterProposal = counterProposal;
            this.commandType = commandType;
            this.additionalReferenceStates = additionalReferenceStates;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            QueryCriteria.LinearStateQueryCriteria inputCriteria = new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(proposalId), Vault.StateStatus.UNCONSUMED, null);
            List<StateAndRef<Proposal>> inputStateAndRefList = getServiceHub().getVaultService().queryBy(Proposal.class, inputCriteria).getStates();
            if (inputStateAndRefList.size() != 1) {
                throw new FlowException("Incorrect amount of proposals found. Expected 1 got " + inputStateAndRefList.size());
            }
            StateAndRef inputStateAndRef = inputStateAndRefList.get(0);
            Proposal input = (Proposal) inputStateAndRef.getState().getData();


            Party counterparty = (getOurIdentity().equals(input.getProposer())) ? input.getProposee() : input.getProposer();

            List<PublicKey> requiredSigners = Arrays.asList(input.getProposee().getOwningKey(), input.getProposer().getOwningKey());
            Command command = new Command(this.commandType, requiredSigners);

            Party notary = inputStateAndRef.getState().getNotary();

            TransactionBuilder txBuilder = new TransactionBuilder(notary)
                    .addInputState(inputStateAndRef)
                    .addOutputState(counterProposal)
                    .addCommand(command);
            if (this.additionalReferenceStates != null) {
                this.additionalReferenceStates.forEach(txBuilder::addReferenceState);
            }

            SignedTransaction partStx = getServiceHub().signInitialTransaction(txBuilder);

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
                            throw new FlowException("Only the proposee can modify a proposal.");
                        }
                    } catch (SignatureException e) {
                        throw new FlowException();
                    }
                }
            };
            SecureHash txId = subFlow(signTransactionFlow).getId();

            return subFlow(new ReceiveFinalityFlow(counterpartySession, txId));
        }
    }
}

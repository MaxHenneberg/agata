package agata.lcl.flows;

import agata.lcl.contracts.GenericProposalContract;
import agata.lcl.states.GenericProposalState;
import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.ContractState;
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
import java.util.List;

public class ModifyFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class ModifyFlowInitiator extends FlowLogic<SignedTransaction> {
        private final UniqueIdentifier proposalId;
        private final GenericProposalState counterProposal;

        public ModifyFlowInitiator(UniqueIdentifier proposalId, GenericProposalState counterProposal) {
            this.proposalId = proposalId;
            this.counterProposal = counterProposal;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            QueryCriteria.LinearStateQueryCriteria inputCriteria = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(proposalId), Vault.StateStatus.UNCONSUMED, null);
            StateAndRef inputStateAndRef = getServiceHub().getVaultService().queryBy(GenericProposalState.class, inputCriteria).getStates().get(0);
            GenericProposalState input = (GenericProposalState) inputStateAndRef.getState().getData();

            //Get Counterparty
            Party counterparty = (getOurIdentity().equals(input.getProposer())) ? input.getProposee() : input.getProposer();

            //Creating the command
            List<PublicKey> requiredSigners = ImmutableList.of(input.getProposee().getOwningKey(), input.getProposer().getOwningKey());
            Command command = new Command(new GenericProposalContract.Commands.Modify(), requiredSigners);

            //Building the transaction
            Party notary = inputStateAndRef.getState().getNotary();
            TransactionBuilder txBuilder = new TransactionBuilder(notary)
                    .addInputState(inputStateAndRef)
                    .addOutputState(counterProposal, GenericProposalContract.ID)
                    .addCommand(command);

            //Signing the transaction ourselves
            SignedTransaction partStx = getServiceHub().signInitialTransaction(txBuilder);

            //Gathering the counterparty's signatures
            FlowSession counterpartySession = initiateFlow(counterparty);
            SignedTransaction fullyStx = subFlow(new CollectSignaturesFlow(partStx, ImmutableList.of(counterpartySession)));

            //Finalising the transaction
            return subFlow(new FinalityFlow(fullyStx, ImmutableList.of(counterpartySession)));
        }
    }

    @InitiatedBy(ModifyFlowInitiator.class)
    public static class ModifyFlowResponder extends FlowLogic<SignedTransaction> {
        private FlowSession counterpartySession;

        public ModifyFlowResponder(FlowSession counterpartySession) {
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
                        Party proposee = ledgerTx.inputsOfType(GenericProposalState.class).get(0).getProposee();
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

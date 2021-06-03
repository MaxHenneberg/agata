package agata.lcl.flows;

import agata.lcl.contracts.GenericProposalContract;
import agata.lcl.states.GenericProposalState;
import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;

public class ProposalFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class ProposalFlowInitiator extends FlowLogic<UniqueIdentifier> {
        private final GenericProposalState proposalState;

        public ProposalFlowInitiator(GenericProposalState proposalState) {
            this.proposalState = proposalState;
        }


        @Suspendable
        @Override
        public UniqueIdentifier call() throws FlowException {
            GenericProposalContract.Commands.Propose commandType = new GenericProposalContract.Commands.Propose();
            List<PublicKey> requiredSigners = Arrays.asList(this.proposalState.getProposer().getOwningKey(), this.proposalState.getProposee().getOwningKey());
            Command command = new Command(commandType, requiredSigners);

            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            TransactionBuilder txBuilder = new TransactionBuilder(notary)
                    .addOutputState(proposalState, GenericProposalContract.ID)
                    .addCommand(command);

            //Signing the transaction ourselves
            SignedTransaction partStx = getServiceHub().signInitialTransaction(txBuilder);

            //Gather counterparty sigs
            FlowSession counterpartySession = initiateFlow(proposalState.getProposee());
            SignedTransaction fullyStx = subFlow(new CollectSignaturesFlow(partStx, ImmutableList.of(counterpartySession)));

            //Finalise the transaction
            SignedTransaction finalisedTx = subFlow(new FinalityFlow(fullyStx, ImmutableList.of(counterpartySession)));
            return finalisedTx.getTx().outputsOfType(GenericProposalState.class).get(0).getLinearId();
        }
    }

    @InitiatedBy(ProposalFlowInitiator.class)
    public static class ProposalFlowResponder extends FlowLogic<SignedTransaction> {
        private FlowSession counterpartySession;

        public ProposalFlowResponder(FlowSession counterpartySession) {
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

            return subFlow(new ReceiveFinalityFlow(counterpartySession, txId));
        }
    }


}

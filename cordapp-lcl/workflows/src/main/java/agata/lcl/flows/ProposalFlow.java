package agata.lcl.flows;

import agata.lcl.contracts.pickup.PickupContract;
import agata.lcl.states.Proposal;
import co.paralleluniverse.fibers.Suspendable;
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
import java.util.Collections;
import java.util.List;

public class ProposalFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class ProposalFlowInitiator extends FlowLogic<UniqueIdentifier> {
        private final Proposal proposalState;

        public ProposalFlowInitiator(Proposal proposalState) {
            this.proposalState = proposalState;
        }


        @Suspendable
        @Override
        public UniqueIdentifier call() throws FlowException {
            PickupContract.Commands.Propose commandType = new PickupContract.Commands.Propose();
            List<PublicKey> requiredSigners = Arrays.asList(proposalState.getProposer().getOwningKey(), proposalState.getProposee().getOwningKey());
            Command command = new Command(commandType, requiredSigners);

            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            TransactionBuilder txBuilder = new TransactionBuilder(notary)
                    .addOutputState(proposalState)
                    .addCommand(command);
            txBuilder.verify(getServiceHub());

            //Signing the transaction ourselves
            SignedTransaction partStx = getServiceHub().signInitialTransaction(txBuilder);

            //Gather counterparty sigs
            FlowSession counterpartySession = initiateFlow(proposalState.getProposee());
            SignedTransaction fullyStx = subFlow(new CollectSignaturesFlow(partStx, Arrays.asList(counterpartySession)));

            //Finalise the transaction
            SignedTransaction finalisedTx = subFlow(new FinalityFlow(fullyStx, Arrays.asList(counterpartySession)));
            return finalisedTx.getTx().outputsOfType(Proposal.class).get(0).getLinearId();
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

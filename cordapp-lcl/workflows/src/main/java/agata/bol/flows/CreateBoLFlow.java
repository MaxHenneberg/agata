package agata.bol.flows;

import agata.bol.contracts.BillOfLadingContract;
import agata.bol.states.BillOfLadingState;
import agata.lcl.states.Proposal;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import org.jetbrains.annotations.NotNull;

import java.security.SignatureException;
import java.util.Collections;

public class CreateBoLFlow {
    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<UniqueIdentifier> {
        final BillOfLadingState toBeCreated;
        final BillOfLadingContract.BoLCommands commandData;

        public Initiator(BillOfLadingState toBeCreated, BillOfLadingContract.BoLCommands commandData) {
            this.toBeCreated = toBeCreated;
            this.commandData = commandData;
        }

        @Suspendable
        @Override
        public UniqueIdentifier call() throws FlowException {
            Command command = new Command(this.commandData, toBeCreated.getParticipants());

            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            TransactionBuilder txBuilder = new TransactionBuilder(notary)
                    .addOutputState(this.toBeCreated)
                    .addCommand(command);

            //Signing the transaction ourselves
            SignedTransaction partStx = getServiceHub().signInitialTransaction(txBuilder);

            //Gather counterparty sigs
            FlowSession counterpartySession = initiateFlow(this.toBeCreated.getConsignee());
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

            return subFlow(new ReceiveFinalityFlow(counterpartySession, txId));
        }
    }
}

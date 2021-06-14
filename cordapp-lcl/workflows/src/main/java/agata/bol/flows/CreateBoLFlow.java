package agata.bol.flows;

import agata.bol.contracts.BillOfLadingContract;
import agata.bol.states.BillOfLadingState;
import agata.lcl.states.Proposal;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.core.transactions.SignedTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.SignatureException;

public class CreateBoLFlow {
    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<UniqueIdentifier> {
        final BillOfLadingState toBeCreated;

        public Initiator(BillOfLadingState toBeCreated, BillOfLadingContract.BoLCommands commandData) {
            this.toBeCreated = toBeCreated;
        }

        @Suspendable
        @Override
        public UniqueIdentifier call() throws FlowException {
            return null;
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

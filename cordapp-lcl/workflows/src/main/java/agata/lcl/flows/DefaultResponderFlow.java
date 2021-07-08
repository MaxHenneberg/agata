package agata.lcl.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import org.jetbrains.annotations.NotNull;

public class DefaultResponderFlow extends FlowLogic<SignedTransaction> {

    private final FlowSession counterpartySession;

    public DefaultResponderFlow(FlowSession counterpartySession) {
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



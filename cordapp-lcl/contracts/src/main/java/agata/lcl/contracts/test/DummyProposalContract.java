package agata.lcl.contracts.test;

import agata.lcl.contracts.GenericProposalContract;
import net.corda.core.contracts.Command;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

public class DummyProposalContract extends GenericProposalContract {
    public static String ID = "agata.lcl.contracts.test.DummyProposalContract";

    @Override
    protected void extendedVerifyPropose(@NotNull LedgerTransaction tx, @NotNull Command command) {

    }

    @Override
    protected void extendedVerifyModify(@NotNull LedgerTransaction tx, @NotNull Command command) {

    }

    @Override
    protected void extendedVerifyAccept(@NotNull LedgerTransaction tx, @NotNull Command command) {

    }
}

package agata.sample.container.contracts;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

public class ContainerContract implements Contract {
    public static final String ID = "agata.sample.container.contracts.ContainerContract";
    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {

    }

    public interface Commands extends CommandData{
        class Create implements Commands {}
        class Transfer implements Commands {}
    }
}

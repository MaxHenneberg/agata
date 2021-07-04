package agata.utils;

import agata.bol.states.BillOfLadingState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.transactions.LedgerTransaction;

public class ContractUtils {

    public static BillOfLadingState resolveBillOfLadingReference(LedgerTransaction tx, UniqueIdentifier id) {
        try {
            return tx.findReferenceInputRef(BillOfLadingState.class, x -> x.getLinearId().equals(id)).getState().getData();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not find bill of lading with id " + id.toString() + ": " + e.getMessage());
        }
    }

}

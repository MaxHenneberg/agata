package agata.lcl.contracts.deconsolidation;

import agata.bol.dataholder.Price;
import agata.lcl.states.deconsolidation.DeconsolidationProposal;
import agata.lcl.states.deconsolidation.DeconsolidationState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;

import static net.corda.testing.node.NodeTestUtils.ledger;

public class DeconsolidationContractTest {

    private final MockServices ledgerServices = new MockServices(Arrays.asList("agata.lcl", "agata.bol"));

    private final TestIdentity alice = new TestIdentity(new CordaX500Name("alice", "New York", "US"));
    private final TestIdentity bob = new TestIdentity(new CordaX500Name("bob", "London", "GB"));

    private final Party lclCompany = alice.getParty();
    private final Party shippingLine = bob.getParty();

    @Test
    public void shouldFailForUnsetBoL() {
        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                Price collect = new Price(new BigDecimal("123.45"), Currency.getInstance("EUR"));
                DeconsolidationState state = new DeconsolidationState(new UniqueIdentifier(), lclCompany, shippingLine, collect, "any-container", null); // Leave reference to BoL empty
                DeconsolidationProposal proposal = new DeconsolidationProposal(lclCompany, shippingLine, state);
                tx.output(DeconsolidationContract.ID, proposal);
                tx.command(Arrays.asList(lclCompany.getOwningKey(), shippingLine.getOwningKey()), new DeconsolidationContract.Commands.Propose());
                return tx.failsWith("Following Fields for Input State agata.lcl.states.deconsolidation.DeconsolidationState must not be null: [masterBillOfLadingId]");
            });
            return null;
        });
    }

}

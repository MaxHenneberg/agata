package agata.lcl.contracts.assignment;

import agata.bol.dataholder.Address;
import agata.lcl.states.assignment.AssignmentProposal;
import agata.lcl.states.assignment.AssignmentState;
import net.corda.core.contracts.CommandData;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;
import org.junit.Test;

import java.util.Arrays;

import static net.corda.testing.node.NodeTestUtils.ledger;

public class AssignmentContractTest {
    private final MockServices ledgerServices = new MockServices(Arrays.asList("agata.lcl"));
    TestIdentity alice = new TestIdentity(new CordaX500Name("alice", "New York", "US"));
    TestIdentity bob = new TestIdentity(new CordaX500Name("bob", "London", "GB"));
    TestIdentity charlie = new TestIdentity(new CordaX500Name("charlie", "London", "GB"));
    Address address1 = new Address("Sample street 1", "New York", "", "", "US");
    Address address2 = new Address("Hafenstrasse", "Hamburg", "", "", "DE");


    @Test
    public void shouldVerifyValidProposal() {
        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                Party lclCompany = alice.getParty();
                Party buyer = charlie.getParty();
                Party supplier = bob.getParty();
                AssignmentState state = new AssignmentState(lclCompany, buyer, supplier, supplier, address1, address2, Arrays.asList("foo", "bar", "baz"), AssignmentState.Status.SlotBooked);
                AssignmentProposal proposal = new AssignmentProposal(lclCompany, buyer, state);
                tx.output(AssignmentContract.ID, proposal);
                tx.command(Arrays.asList(lclCompany.getOwningKey(), buyer.getOwningKey()), new AssignmentContract.Commands.Propose());
                return tx.verifies();
            });
            return null;
        });
    }

    @Test
    public void requiredPropertyNotSet() {
        // Exemplary negative test (leave out some required properties)
        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                Party lclCompany = alice.getParty();
                Party buyer = charlie.getParty();
                Party supplier = bob.getParty();
                AssignmentState state = new AssignmentState(lclCompany, null, supplier, supplier, address1, address2, Arrays.asList("foo", "bar", "baz"), AssignmentState.Status.SlotBooked);
                AssignmentProposal proposal = new AssignmentProposal(lclCompany, buyer, state);
                tx.output(AssignmentContract.ID, proposal);
                tx.command(Arrays.asList(lclCompany.getOwningKey(), buyer.getOwningKey()), new AssignmentContract.Commands.Propose());
                return tx.failsWith("The buyer is set");
            });
            return null;
        });
    }
}

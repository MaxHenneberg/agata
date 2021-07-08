package agata.lcl.contracts;

import agata.lcl.contracts.assignment.AssignmentContract;
import agata.lcl.contracts.test.DummyProposalContract;
import agata.lcl.states.test.DummyProposal;
import agata.lcl.states.test.DummyState;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static net.corda.testing.node.NodeTestUtils.ledger;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BaseContractTest {
    private final MockServices ledgerServices = new MockServices(Collections.singletonList("agata.lcl"));
    TestIdentity alice = new TestIdentity(new CordaX500Name("alice", "New York", "US"));
    TestIdentity bob = new TestIdentity(new CordaX500Name("bob", "London", "GB"));


    @Test
    public void testMandatoryCheck() {
        DummyState dummyState = new DummyState(alice.getParty(), bob.getParty(), "", "", null, null, "", null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> BaseContractUtils.checkMandatoryFields(dummyState,
                new GenericProposalContract.Commands.Propose(), true));

        Assert.assertNotNull(exception);
        Assert.assertEquals("Failed requirement: Following Fields for Input State agata.lcl.states.test.DummyState must not be null: [mandatoryObjectField] and following Fields must not be Blank: " +
                "[mandatoryStringField]", exception.getMessage());
    }

    @Test
    public void testMandatoryCheckForCommand() {
        DummyState dummyState = new DummyState(alice.getParty(), bob.getParty(), "", "", null, null, "", null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> BaseContractUtils.checkMandatoryFields(dummyState,
                new GenericProposalContract.Commands.Modify(), true));

        Assert.assertNotNull(exception);
        Assert.assertEquals("Failed requirement: Following Fields for Input State agata.lcl.states.test.DummyState must not be null: [mandatoryObjectField, mandatoryForModify] and following Fields " +
                "must not be Blank: [mandatoryStringField, mandatoryStringForModify]", exception.getMessage());
    }

    @Test
    public void testMandatoryWrappedInProposal() {
        DummyState dummyState = new DummyState(alice.getParty(), bob.getParty(), "", "", null, null, "", null);
        DummyProposal dummyProposal = new DummyProposal(alice.getParty(), bob.getParty(), dummyState);
        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                Party lclCompany = alice.getParty();
                Party supplier = bob.getParty();
                tx.output(DummyProposalContract.ID, dummyProposal);
                tx.command(Arrays.asList(lclCompany.getOwningKey(), supplier.getOwningKey()), new AssignmentContract.Commands.Propose());
                return tx.failsWith(
                        "Failed requirement: Following Fields for Input State agata.lcl.states.test.DummyState must not be null: [mandatoryObjectField] and following Fields must not be Blank: " +
                                "[mandatoryStringField]");
            });
            return null;
        });
    }

}

package agata.lcl.contracts;

import agata.lcl.contracts.test.DummyProposalContract;
import agata.lcl.states.test.DummyState;
import net.corda.core.identity.CordaX500Name;
import net.corda.testing.core.TestIdentity;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static net.corda.testing.node.NodeTestUtils.ledger;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GenericProposalContractTest {
    TestIdentity alice = new TestIdentity(new CordaX500Name("alice", "New York", "US"));
    TestIdentity bob = new TestIdentity(new CordaX500Name("bob", "London", "GB"));


    @Test
    public void testMandatoryCheck() {
        DummyState dummyState = new DummyState(alice.getParty(), bob.getParty(), "", "", null, null, "", null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> GenericProposalContractUtils.checkMandatoryFields(dummyState,
                new GenericProposalContract.Commands.Propose(), true));

        Assert.assertNotNull(exception);
        Assert.assertEquals("Failed requirement: Following Fields for Input State agata.lcl.states.test.DummyState must not be null: [mandatoryObjectField] and following Fields must not be Blank: " +
                "[mandatoryStringField]", exception.getMessage());
    }

    @Test
    public void testMandatoryCheckForCommand() {
        DummyState dummyState = new DummyState(alice.getParty(), bob.getParty(), "", "", null, null, "", null);
        DummyProposalContract dummyProposalContract = new DummyProposalContract();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> GenericProposalContractUtils.checkMandatoryFields(dummyState,
                new GenericProposalContract.Commands.Modify(), true));

        Assert.assertNotNull(exception);
        Assert.assertEquals("Failed requirement: Following Fields for Input State agata.lcl.states.test.DummyState must not be null: [mandatoryObjectField, mandatoryForModify] and following Fields " +
                "must not be Blank: [mandatoryStringField, mandatoryStringForModify]", exception.getMessage());
    }

}

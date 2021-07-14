package agata.lcl.contracts;

import net.corda.core.identity.CordaX500Name;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;

import java.util.Arrays;
import java.util.Collections;

public class BaseTest {

    protected final MockServices ledgerServices = new MockServices(Arrays.asList("agata.lcl", "agata.bol"));

    protected TestIdentity alice = new TestIdentity(new CordaX500Name("alice", "New York", "US"));
    protected TestIdentity bob = new TestIdentity(new CordaX500Name("bob", "London", "GB"));
    protected TestIdentity charlie = new TestIdentity(new CordaX500Name("charlie", "Hamburg", "DE"));

}

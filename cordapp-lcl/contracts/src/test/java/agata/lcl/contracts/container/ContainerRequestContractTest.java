package agata.lcl.contracts.container;

import agata.bol.dataholder.Address;
import agata.bol.dataholder.ContainerInformation;
import agata.bol.enums.ContainerType;
import agata.lcl.states.container.ContainerRequestProposal;
import agata.lcl.states.container.ContainerRequestState;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;
import org.junit.Test;

import java.util.Arrays;

import static net.corda.testing.node.NodeTestUtils.ledger;

public class ContainerRequestContractTest {

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
                Party shippingLine = bob.getParty();
                // Leave fields empty that will be filled by the shipping line
                ContainerRequestState state = new ContainerRequestState(shippingLine, lclCompany, lclCompany, "Port A", "Port B", "Any id", ContainerType.Large, null, null);

                ContainerRequestProposal proposal = new ContainerRequestProposal(lclCompany, shippingLine, state);
                tx.output(ContainerRequestContract.ID, proposal);
                tx.command(Arrays.asList(lclCompany.getOwningKey(), shippingLine.getOwningKey()), new ContainerRequestContract.Commands.Propose());
                return tx.verifies();
            });
            return null;
        });
    }

    @Test
    public void shouldFailForVesselInformationSetOnProposal() {
        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                Party lclCompany = alice.getParty();
                Party shippingLine = bob.getParty();
                // Leave fields empty that will be filled by the shipping line
                ContainerRequestState state = new ContainerRequestState(shippingLine, lclCompany, lclCompany, "Port A", "Port B", "Any id", ContainerType.Large, "my vessel", new ContainerInformation("any number", "123", ContainerType.Small));
                ContainerRequestProposal proposal = new ContainerRequestProposal(lclCompany, shippingLine, state);
                tx.output(ContainerRequestContract.ID, proposal);
                tx.command(Arrays.asList(lclCompany.getOwningKey(), shippingLine.getOwningKey()), new ContainerRequestContract.Commands.Propose());
                return tx.failsWith("Vessel name must not be set initially");
            });
            return null;
        });
    }

}

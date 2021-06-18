package agata.lcl.contracts.container;

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

    @Test
    public void shouldFailForNonMatchingContainerType() {
        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                Party lclCompany = alice.getParty();
                Party shippingLine = bob.getParty();

                tx.command(Arrays.asList(lclCompany.getOwningKey(), shippingLine.getOwningKey()), new ContainerRequestContract.Commands.AssignContainer());

                ContainerRequestState initiallyProposedState = new ContainerRequestState(shippingLine, lclCompany, lclCompany, "Port A", "Port B", "Any id", ContainerType.Large, null, null);
                ContainerRequestProposal requestProposal = new ContainerRequestProposal(initiallyProposedState.getLclCompany(), initiallyProposedState.getShippingLine(), initiallyProposedState);
                tx.input(ContainerRequestContract.ID, requestProposal);

                ContainerRequestState modifiedState = new ContainerRequestState(initiallyProposedState);
                modifiedState.setVesselName("My vessel");
                // Set different container type than expected
                modifiedState.setContainer(new ContainerInformation("123", "456", ContainerType.Small));
                ContainerRequestProposal assignmentProposal = new ContainerRequestProposal(shippingLine, lclCompany, modifiedState);
                tx.output(ContainerRequestContract.ID, assignmentProposal);

                return tx.failsWith("type of the assigned container must match the requested type");
            });
            return null;
        });
    }

    @Test
    public void shouldFailForInvalidAssignContainerCommand() {
        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                Party lclCompany = alice.getParty();
                Party shippingLine = bob.getParty();

                ContainerRequestState initiallyProposedState = new ContainerRequestState(shippingLine, lclCompany, lclCompany, "Port A", "Port B", "Any id", ContainerType.Large, null, null);
                ContainerRequestProposal requestProposal = new ContainerRequestProposal(initiallyProposedState.getLclCompany(), initiallyProposedState.getShippingLine(), initiallyProposedState);
                tx.input(ContainerRequestContract.ID, requestProposal);

                ContainerRequestState modifiedState = new ContainerRequestState(initiallyProposedState);
                modifiedState.setVesselName("My vessel");
                modifiedState.setContainer(new ContainerInformation("123", "456", ContainerType.Large));
                // Use LCL company as proposer (which is invalid)
                ContainerRequestProposal proposal = new ContainerRequestProposal(lclCompany, lclCompany, modifiedState);
                tx.output(ContainerRequestContract.ID, proposal);

                tx.command(Arrays.asList(lclCompany.getOwningKey(), shippingLine.getOwningKey()), new ContainerRequestContract.Commands.AssignContainer());
                return tx.failsWith("Proposer must be the shipping line");
            });
            return null;
        });
    }


}

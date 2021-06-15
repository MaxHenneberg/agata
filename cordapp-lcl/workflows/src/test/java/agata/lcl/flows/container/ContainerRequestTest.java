package agata.lcl.flows.container;

import agata.bol.dataholder.ContainerInformation;
import agata.bol.enums.ContainerType;
import agata.lcl.flows.AcceptFlow;
import agata.lcl.states.container.ContainerRequestState;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.TransactionState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.node.NetworkParameters;
import net.corda.core.transactions.SignedTransaction;
import net.corda.testing.node.MockNetwork;
import net.corda.testing.node.MockNetworkParameters;
import net.corda.testing.node.StartedMockNode;
import net.corda.testing.node.TestCordapp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class ContainerRequestTest {
    private MockNetwork network;
    private StartedMockNode lclCompany;
    private StartedMockNode shippingLine;

    @Before
    public void setup() {
        network = new MockNetwork(new MockNetworkParameters().withCordappsForAllNodes(Arrays.asList(
                TestCordapp.findCordapp("agata.lcl.contracts"),
                TestCordapp.findCordapp("agata.lcl.flows"))).withNetworkParameters(new NetworkParameters(4, Collections.emptyList(),
                10485760, 10485760 * 50, Instant.now(), 1,
                Collections.emptyMap()))
        );
        lclCompany = network.createPartyNode(new CordaX500Name("LCL Company", "New York", "US"));
        shippingLine = network.createPartyNode(new CordaX500Name("Shipping Line A", "New York", "US"));
        network.runNetwork();
    }

    @After
    public void tearDown() {
        network.stopNodes();
    }

    // TODO: These steps of different flows should be separated (since these are unit tests), right?
    @Test
    public void testPropose() throws ExecutionException, InterruptedException {
        Party lclCompanyParty = getParty(this.lclCompany);
        Party shippingLineParty = getParty(this.shippingLine);

        ContainerRequestState expectedState = new ContainerRequestState(shippingLineParty, lclCompanyParty, lclCompanyParty, "Port A", "Port B", "Any id", ContainerType.Large, "My vessel", new ContainerInformation("123", "456", ContainerType.Large));

        // PROPOSE
        ContainerRequestProposalFlow.Initiator proposeFlow = new ContainerRequestProposalFlow.Initiator(
                expectedState.getShippingLine(), expectedState.getLclDestination(), expectedState.getPortOfLoading(), expectedState.getPortOfDischarge(), expectedState.getForwardingAgentNo(), expectedState.getRequestedType());
        CordaFuture<UniqueIdentifier> future1 = this.lclCompany.startFlow(proposeFlow);
        network.runNetwork();
        UniqueIdentifier containerRequestProposalId = future1.get();

        // ASSIGN CONTAINER
        AssignContainerFlow.Initiator assignContainerFlow = new AssignContainerFlow.Initiator(containerRequestProposalId, expectedState.getVesselName(), expectedState.getContainer());
        CordaFuture<SignedTransaction> future2 = this.shippingLine.startFlow(assignContainerFlow);
        network.runNetwork();

        // ACCEPT
        AcceptFlow.Initiator acceptFlow = new AcceptFlow.Initiator(containerRequestProposalId);
        CordaFuture<SignedTransaction> future3 = this.shippingLine.startFlow(acceptFlow);
        network.runNetwork();
        SignedTransaction tx = future3.get();


        // Check that transaction is recorded in both vaults
        for (StartedMockNode node : Arrays.asList(lclCompany, shippingLine)) {
            SignedTransaction recordedTx = node.getServices().getValidatedTransactions().getTransaction(tx.getId());
            List<TransactionState<ContractState>> txOutputs = recordedTx.getTx().getOutputs();
            assert (txOutputs.size() == 1);

            ContainerRequestState recordedState = (ContainerRequestState) txOutputs.get(0).getData();
            assertEquals(recordedState, expectedState);
        }


    }

    protected Party getParty(StartedMockNode mockNode) {
        return mockNode.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();
    }

}

package agata.lcl.flows.tracking;

import agata.lcl.enums.TrackingStatus;
import agata.lcl.states.tracking.ShippingTrackingState;
import agata.lcl.states.tracking.TrackingState;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.TransactionState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.node.NetworkParameters;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
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

public class TrackingFlowTest {

    private MockNetwork network;
    private StartedMockNode lclCompany;
    private StartedMockNode buyer;
    private StartedMockNode supplier;
    private StartedMockNode shippingLine;


    @Before
    public void setup() {
        network = new MockNetwork(new MockNetworkParameters().withCordappsForAllNodes(Arrays.asList(
                TestCordapp.findCordapp("agata.lcl.contracts"),
                TestCordapp.findCordapp("agata.lcl.flows"))).withNetworkParameters(new NetworkParameters(4, Collections.emptyList(),
                10485760, 10485760 * 50, Instant.now(), 1,
                Collections.emptyMap()))
        );
        buyer = network.createPartyNode(new CordaX500Name("Buyer", "London", "GB"));
        supplier = network.createPartyNode(new CordaX500Name("Supplier", "Rotterdam", "NL"));
        lclCompany = network.createPartyNode(new CordaX500Name("LCL Company", "New York", "US"));
        shippingLine = network.createPartyNode(new CordaX500Name("Shipping Line A", "New York", "US"));
        network.runNetwork();
    }

    @After
    public void tearDown() {
        network.stopNodes();
    }

    @Test
    public void testAllFlows() throws ExecutionException, InterruptedException {
        Party buyerParty = getParty(this.buyer);
        Party supplierParty = getParty(this.supplier);
        Party lclCompanyParty = getParty(this.lclCompany);
        Party shippingLineParty = getParty(this.shippingLine);

        // CREATE
        CreateTrackingStateFlow.Initiator flow1 = new CreateTrackingStateFlow.Initiator(buyerParty, supplierParty);
        CordaFuture<SignedTransaction> future1 = this.lclCompany.startFlow(flow1);
        network.runNetwork();
        SignedTransaction tx = future1.get();

        for (StartedMockNode node : Arrays.asList(lclCompany, buyer, supplier)) {
            SignedTransaction recordedTx = node.getServices().getValidatedTransactions().getTransaction(tx.getId());
            List<TransactionState<ContractState>> txOutputs = recordedTx.getTx().getOutputs();
            assert (txOutputs.size() == 1);

            TrackingState trackingState = (TrackingState) txOutputs.get(0).getData();
            assertEquals(buyerParty, trackingState.getBuyer());
            assertEquals(supplierParty, trackingState.getSupplier());
            assertEquals(lclCompanyParty, trackingState.getLclCompany());
            assertEquals(TrackingStatus.SlotBooked, trackingState.getStatus());
        }


        // SET PICKUP COMPLETED
        UniqueIdentifier stateId = ((TrackingState) tx.getTx().getOutput(0)).getLinearId();
        SetPickupCompletedFlow.Initiator flow2 = new SetPickupCompletedFlow.Initiator(stateId);
        CordaFuture<SignedTransaction> future2 = this.lclCompany.startFlow(flow2);
        network.runNetwork();
        tx = future2.get();

        for (StartedMockNode node : Arrays.asList(lclCompany, buyer, supplier)) {
            List<StateAndRef<TrackingState>> states = getTrackingStates(node, stateId, Vault.StateStatus.UNCONSUMED);
            assert (states.size() == 1);
            TrackingState trackingState = states.get(0).getState().getData();

            assertEquals(buyerParty, trackingState.getBuyer());
            assertEquals(supplierParty, trackingState.getSupplier());
            assertEquals(lclCompanyParty, trackingState.getLclCompany());
            assertEquals(TrackingStatus.PickupCompleted, trackingState.getStatus());

            assert (getTrackingStates(node, stateId, Vault.StateStatus.ALL).size() == 2);
        }

        // SET CONTAINER ASSIGNED
        stateId = ((TrackingState) tx.getTx().getOutput(0)).getLinearId();
        String currentPort = "Port A";
        SetContainerAssignedFlow.Initiator flow3 = new SetContainerAssignedFlow.Initiator(stateId, shippingLineParty, currentPort);
        this.lclCompany.startFlow(flow3);
        network.runNetwork();

        for (StartedMockNode node : Arrays.asList(lclCompany, shippingLine, buyer, supplier)) {
            List<StateAndRef<TrackingState>> states = getTrackingStates(node, stateId, Vault.StateStatus.UNCONSUMED);
            assert (states.size() == 1);
            ShippingTrackingState trackingState = (ShippingTrackingState) states.get(0).getState().getData();

            assertEquals(buyerParty, trackingState.getBuyer());
            assertEquals(supplierParty, trackingState.getSupplier());
            assertEquals(lclCompanyParty, trackingState.getLclCompany());
            assertEquals(shippingLineParty, trackingState.getShippingLine());
            assertEquals(currentPort, trackingState.getCurrentPort());
            assertEquals(TrackingStatus.ContainerAssigned, trackingState.getStatus());
        }

        // SET LOADED ON SHIP
        stateId = ((TrackingState) tx.getTx().getOutput(0)).getLinearId();
        SetLoadedOnShipFlow.Initiator flow4 = new SetLoadedOnShipFlow.Initiator(stateId);
        this.shippingLine.startFlow(flow4);
        network.runNetwork();

        for (StartedMockNode node : Arrays.asList(lclCompany, shippingLine, buyer, supplier)) {
            List<StateAndRef<TrackingState>> states = getTrackingStates(node, stateId, Vault.StateStatus.UNCONSUMED);
            assert (states.size() == 1);
            ShippingTrackingState trackingState = (ShippingTrackingState) states.get(0).getState().getData();

            assertEquals(buyerParty, trackingState.getBuyer());
            assertEquals(supplierParty, trackingState.getSupplier());
            assertEquals(lclCompanyParty, trackingState.getLclCompany());
            assertEquals(shippingLineParty, trackingState.getShippingLine());
            assertEquals(currentPort, trackingState.getCurrentPort());
            assertEquals(TrackingStatus.LoadedOnShip, trackingState.getStatus());
        }

    }

    private List<StateAndRef<TrackingState>> getTrackingStates(StartedMockNode node, UniqueIdentifier stateId, Vault.StateStatus status) {
        QueryCriteria.LinearStateQueryCriteria inputCriteriaAll =
                new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(stateId), status, null);
        return node.getServices().getVaultService().queryBy(TrackingState.class, inputCriteriaAll).getStates();
    }

    private Party getParty(StartedMockNode mockNode) {
        return mockNode.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();
    }
}

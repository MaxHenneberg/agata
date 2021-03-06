package agata.lcl.flows;

import agata.bol.dataholder.*;
import agata.bol.enums.ContainerType;
import agata.bol.enums.Payable;
import agata.bol.enums.TypeOfMovement;
import agata.lcl.flows.assignment.AssignmentAcceptFlow;
import agata.lcl.flows.assignment.AssignmentProposalFlow;
import agata.lcl.flows.container.AcceptContainerFlow;
import agata.lcl.flows.container.AssignContainerFlow;
import agata.lcl.flows.container.ContainerRequestProposalFlow;
import agata.lcl.flows.pickup.PickupAcceptFlow;
import agata.lcl.flows.pickup.PickupAddGoodsFlow;
import agata.lcl.flows.pickup.PickupProposalFlow;
import agata.lcl.flows.shiploading.ShiploadingAcceptFlow;
import agata.lcl.flows.shiploading.ShiploadingProposalFlow;
import agata.lcl.states.assignment.AssignmentState;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
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

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class FlowTestBase {
    protected MockNetwork network;

    protected Address arrivalAddress = new Address("Arrival street 1", "Klm", "Def", "45678", "Bar");
    protected Address departureAddress = new Address("Departure street 1", "Abc", "Def", "12345", "Foo");

    @Before
    public void setup() {
        network = new MockNetwork(new MockNetworkParameters().withCordappsForAllNodes(Arrays.asList(
                TestCordapp.findCordapp("agata.lcl.contracts"),
                TestCordapp.findCordapp("agata.lcl.flows"))).withNetworkParameters(new NetworkParameters(4, Collections.emptyList(),
                10485760, 10485760 * 50, Instant.now(), 1,
                Collections.emptyMap()))
        );
    }

    @After
    public void tearDown() {
        network.stopNodes();
    }

    protected Party getParty(StartedMockNode mockNode) {
        return mockNode.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();
    }

    protected UniqueIdentifier createContainerState(StartedMockNode lclCompanyMock,
                                                    StartedMockNode shippingLineMock, List<UniqueIdentifier> trackingStateIds) throws ExecutionException, InterruptedException {
        Party lclCompanyParty = getParty(lclCompanyMock);
        Party shippingLineParty = getParty(shippingLineMock);

        String portOfLoading = "Port A";
        String portOfDischarge = "Port B";
        String forwarderId = "Any id";
        ContainerType requestedType = ContainerType.Large;
        String vesselName = "My vessel";
        ContainerInformation assignedContainer = new ContainerInformation("123", "456", ContainerType.Large);

        // PROPOSE
        ContainerRequestProposalFlow.Initiator proposeFlow = new ContainerRequestProposalFlow.Initiator(
                shippingLineParty, lclCompanyParty, portOfLoading, portOfDischarge, forwarderId, requestedType);
        CordaFuture<UniqueIdentifier> future1 = lclCompanyMock.startFlow(proposeFlow);
        network.runNetwork();
        UniqueIdentifier containerRequestProposalId = future1.get();

        // ASSIGN CONTAINER
        AssignContainerFlow.Initiator assignContainerFlow = new AssignContainerFlow.Initiator(containerRequestProposalId, vesselName,
                assignedContainer);
        CordaFuture<SignedTransaction> future2 = shippingLineMock.startFlow(assignContainerFlow);
        network.runNetwork();

        // ACCEPT
        AcceptContainerFlow.Initiator acceptFlow = new AcceptContainerFlow.Initiator(containerRequestProposalId, trackingStateIds);
        CordaFuture<SignedTransaction> future3 = lclCompanyMock.startFlow(acceptFlow);
        network.runNetwork();
        SignedTransaction tx = future3.get();
        return ((LinearState) tx.getCoreTransaction().getOutputStates().get(0)).getLinearId();
    }

    protected UniqueIdentifier createAssignmentState(StartedMockNode lclCompanyMock, StartedMockNode supplierMock, StartedMockNode buyerMock, Address address1,
                                                     Address address2, String goodsId) throws ExecutionException, InterruptedException {
        Party supplierParty = getParty(supplierMock);
        Party buyerParty = getParty(buyerMock);
        List<ItemRow> goods = Collections
                .singletonList(new ItemRow("abc", goodsId, 3, new DescriptionOfGoods("iPhone", "pallet", 100), 12, 12, 456));
        AssignmentProposalFlow.Initiator proposeFlow = new AssignmentProposalFlow.Initiator(buyerParty, supplierParty, supplierParty, address1, address2, goods);
        Future<UniqueIdentifier> future1 = lclCompanyMock.startFlow(proposeFlow);
        network.runNetwork();

        UniqueIdentifier assignmentProposalUId = future1.get();

        AssignmentAcceptFlow.Initiator acceptFlow = new AssignmentAcceptFlow.Initiator(assignmentProposalUId);
        Future future = buyerMock.startFlow(acceptFlow);
        network.runNetwork();

        SignedTransaction assignmentState = (SignedTransaction) future.get();
        final LinearState state = (LinearState) assignmentState.getCoreTransaction().getOutputStates().get(0);

        return state.getLinearId();
    }

    protected UniqueIdentifier createHouseBol(String id, UniqueIdentifier assignmentStateId, UniqueIdentifier containerStateId, StartedMockNode lclCompany, StartedMockNode supplier) throws ExecutionException, InterruptedException {
        PickupProposalFlow.Initiator pickupProposeFlow = new PickupProposalFlow.Initiator(assignmentStateId);
        Future<UniqueIdentifier> future1 = lclCompany.startFlow(pickupProposeFlow);
        network.runNetwork();

        UniqueIdentifier pickupProposalId = future1.get();

        PickupAddGoodsFlow.Initiator modifyFlow = new PickupAddGoodsFlow.Initiator(pickupProposalId,
                Collections.singletonList(new ItemRow("abc", id, 3, new DescriptionOfGoods("iPhone", "pallet", 100), 12, 12, 456)),
                "NotBlank");
        Future future2 = supplier.startFlow(modifyFlow);
        network.runNetwork();
        future2.get();

        UniqueIdentifier trackingStateId = this.resolveStateId(AssignmentState.class, assignmentStateId, lclCompany, Vault.StateStatus.UNCONSUMED).getTrackingStateId();
        PickupAcceptFlow.Initiator acceptFlow = new PickupAcceptFlow.Initiator(pickupProposalId, containerStateId, trackingStateId, "initCarriage",
                "placeOfReceipt", "deliveryByCarrier", "bookingNo", "boeNo", Collections.singletonList("ref"), Payable.Origin,
                TypeOfMovement.doorToDoor, Collections.singletonList(new FreightCharges("Reason", null)),
                null, null);
        Future<UniqueIdentifier> future3 = lclCompany.startFlow(acceptFlow);
        network.runNetwork();
        return future3.get();
    }

    protected UniqueIdentifier executeShiploading(StartedMockNode shippingLine, StartedMockNode lclCompany, List<UniqueIdentifier> trackingStateIds, UniqueIdentifier containerStateId, List<UniqueIdentifier> houseBolIds) throws ExecutionException, InterruptedException {
        ShiploadingProposalFlow.Initiator shipLoadingProposalFlow =
                new ShiploadingProposalFlow.Initiator(containerStateId, getParty(shippingLine), houseBolIds, "initCarriage",
                        "place", "123", "123", Collections.singletonList("123"), Payable.Origin, TypeOfMovement.doorToDoor,
                        null, null, null);
        Future<UniqueIdentifier> future1 = lclCompany.startFlow(shipLoadingProposalFlow);
        network.runNetwork();
        UniqueIdentifier shipLoadingProposalId = future1.get();

        ShiploadingAcceptFlow.Initiator acceptFlow = new ShiploadingAcceptFlow.Initiator(shipLoadingProposalId, trackingStateIds);
        CordaFuture<SignedTransaction> future2 = shippingLine.startFlow(acceptFlow);
        network.runNetwork();

        // Return id of created master bill of lading
        return ((LinearState) future2.get().getCoreTransaction().getOutputStates().get(0)).getLinearId();
    }

    protected <T extends ContractState> T resolveStateId(Class<T> clazz, UniqueIdentifier id, StartedMockNode node, Vault.StateStatus status) {
        List<StateAndRef<T>> results = node.getServices().getVaultService().queryBy(
                clazz,
                new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(id), status, null)).getStates();
        assert (results.size() == 1);
        return results.get(0).getState().getData();
    }
}

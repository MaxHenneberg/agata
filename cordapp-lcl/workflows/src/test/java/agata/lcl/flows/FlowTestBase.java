package agata.lcl.flows;

import agata.bol.dataholder.*;
import agata.bol.enums.ContainerType;
import agata.bol.enums.Payable;
import agata.bol.enums.TypeOfMovement;
import agata.lcl.flows.container.AssignContainerFlow;
import agata.lcl.flows.container.ContainerRequestProposalFlow;
import agata.lcl.flows.pickup.PickupAcceptFlow;
import agata.lcl.flows.pickup.PickupAddGoodsFlow;
import agata.lcl.flows.pickup.PickupProposalFlow;
import agata.lcl.states.Proposal;
import agata.lcl.states.assignment.AssignmentProposal;
import agata.lcl.states.assignment.AssignmentState;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.core.node.NetworkParameters;
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
                                                    StartedMockNode shippingLineMock) throws ExecutionException, InterruptedException {
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
        AcceptFlow.Initiator acceptFlow = new AcceptFlow.Initiator(containerRequestProposalId);
        CordaFuture<SignedTransaction> future3 = lclCompanyMock.startFlow(acceptFlow);
        network.runNetwork();
        SignedTransaction tx = future3.get();
        return ((LinearState) tx.getCoreTransaction().getOutputStates().get(0)).getLinearId();
    }

    protected UniqueIdentifier createAssignmentState(StartedMockNode lclCompanyMock, StartedMockNode supplierMock, StartedMockNode buyerMock, Address address1,
                                                     Address address2, String goodsId) throws ExecutionException, InterruptedException {
        Party lclCompanyParty = getParty(lclCompanyMock);
        Party supplierParty = getParty(supplierMock);
        Party buyerParty = getParty(buyerMock);
        List<ItemRow> goods = Collections
                .singletonList(new ItemRow("abc", goodsId, 3, new DescriptionOfGoods("iPhone", "pallet", 100), 12, 12, 456));
        AssignmentState assignmentState = new AssignmentState(lclCompanyParty, buyerParty, supplierParty, buyerParty, address1, address2, goods, new UniqueIdentifier());
        Proposal proposal = new AssignmentProposal(lclCompanyParty, buyerParty, assignmentState);
        ProposalFlow.Initiator proposeFlow = new ProposalFlow.Initiator(proposal);
        Future<UniqueIdentifier> future1 = lclCompanyMock.startFlow(proposeFlow);
        network.runNetwork();

        UniqueIdentifier assignmentProposalUId = future1.get();

        AcceptFlow.Initiator acceptFlow = new AcceptFlow.Initiator(assignmentProposalUId);
        Future future = buyerMock.startFlow(acceptFlow);
        network.runNetwork();
        SignedTransaction pickUpStateTx = (SignedTransaction) future.get();

        final LinearState state = (LinearState) pickUpStateTx.getCoreTransaction().getOutputStates().get(0);

        return state.getLinearId();
    }

    protected UniqueIdentifier createHouseBol(String id, StartedMockNode lclCompany, StartedMockNode supplier, StartedMockNode buyer, StartedMockNode shippingLine) throws ExecutionException, InterruptedException {
        Address address1 = new Address("Sample street 1", "New York", "", "", "US");
        Address address2 = new Address("Hafenstrasse", "Hamburg", "", "", "DE");

        final UniqueIdentifier assignmentStateId = createAssignmentState(lclCompany, supplier, buyer, address1, address2, id);
        final UniqueIdentifier containerStateId = createContainerState(lclCompany, shippingLine);

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

        PickupAcceptFlow.Initiator acceptFlow = new PickupAcceptFlow.Initiator(pickupProposalId, containerStateId, "initCarriage",
                "placeOfReceipt", "deliveryByCarrier", "bookingNo", "boeNo", Collections.singletonList("ref"), Payable.Origin,
                TypeOfMovement.doorToDoor, Collections.singletonList(new FreightCharges("Reason", null)),
                null, null);
        Future<UniqueIdentifier> future3 = lclCompany.startFlow(acceptFlow);
        network.runNetwork();
        return future3.get();
    }
}

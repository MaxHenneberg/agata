package agata.lcl.flows.pickup;

import agata.bol.dataholder.Address;
import agata.bol.dataholder.DescriptionOfGoods;
import agata.bol.dataholder.FreightCharges;
import agata.bol.dataholder.ItemRow;
import agata.bol.enums.Payable;
import agata.bol.enums.TypeOfMovement;
import agata.bol.states.BillOfLadingState;
import agata.lcl.enums.TrackingStatus;
import agata.lcl.flows.FlowTestBase;
import agata.lcl.states.assignment.AssignmentState;
import agata.lcl.states.pickup.PickupState;
import agata.lcl.states.tracking.TrackingState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.node.services.Vault;
import net.corda.testing.node.StartedMockNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PickFlowTest extends FlowTestBase {
    private StartedMockNode lclCompany;
    private StartedMockNode supplier;
    private StartedMockNode buyer;
    private StartedMockNode shippingLine;
    Address address1 = new Address("Sample street 1", "New York", "", "", "US");
    Address address2 = new Address("Hafenstrasse", "Hamburg", "", "", "DE");

    @Before
    public void setupPickUpFlow() {
        lclCompany = network.createPartyNode(new CordaX500Name("LCL Company", "New York", "US"));
        supplier = network.createPartyNode(new CordaX500Name("Supplier", "New York", "US"));
        buyer = network.createPartyNode(new CordaX500Name("Buyer", "New York", "US"));
        shippingLine = network.createPartyNode(new CordaX500Name("Shipper", "New York", "US"));
        network.runNetwork();
    }

    @Test
    public void testAddGoodsWithException() throws ExecutionException, InterruptedException {
        final UniqueIdentifier assignmentStateId = createAssignmentState(lclCompany, supplier, buyer, address1, address2, "123");
        PickupProposalFlow.Initiator pickupProposalFlow = new PickupProposalFlow.Initiator(assignmentStateId);
        Future<UniqueIdentifier> future1 = this.lclCompany.startFlow(pickupProposalFlow);
        network.runNetwork();

        UniqueIdentifier pickupProposalId = future1.get();

        PickupAddGoodsFlow.Initiator modifyFlow = new PickupAddGoodsFlow.Initiator(pickupProposalId,
                Collections
                        .singletonList(new ItemRow("abc", "123", 3, new DescriptionOfGoods("Not an Iphone", "pallet", 100), 12, 12, 456)),
                "test");
        Future future2 = this.supplier.startFlow(modifyFlow);
        network.runNetwork();
        ExecutionException exception = assertThrows(ExecutionException.class, future2::get);
        Assert.assertNotNull(exception);
        Assert.assertTrue(exception.getMessage().startsWith(
                "net.corda.core.contracts.TransactionVerificationException$ContractRejection: Contract verification failed: Failed " +
                        "requirement: Picked-up Goods must be Equals to Expected Goods, " +
                        "contract: agata.lcl.contracts.pickup.PickupContract"));
    }

    @Test
    public void testAccept() throws ExecutionException, InterruptedException {
        final UniqueIdentifier assignmentStateId = createAssignmentState(lclCompany, supplier, buyer, address1, address2, "123");
        UniqueIdentifier trackingStateId = this.resolveStateId(AssignmentState.class, assignmentStateId, this.lclCompany, Vault.StateStatus.UNCONSUMED).getTrackingStateId();
        final UniqueIdentifier containerStateId = createContainerState(lclCompany, shippingLine, Collections.singletonList(trackingStateId));

        PickupProposalFlow.Initiator pickupProposeFlow = new PickupProposalFlow.Initiator(assignmentStateId);
        Future<UniqueIdentifier> future1 = this.lclCompany.startFlow(pickupProposeFlow);
        network.runNetwork();

        UniqueIdentifier pickupProposalId = future1.get();

        PickupAddGoodsFlow.Initiator modifyFlow = new PickupAddGoodsFlow.Initiator(pickupProposalId,
                Collections.singletonList(new ItemRow("abc", "123", 3, new DescriptionOfGoods("iPhone", "pallet", 100), 12, 12, 456)),
                "NotBlank");
        Future future2 = this.supplier.startFlow(modifyFlow);
        network.runNetwork();
        future2.get();

        PickupAcceptFlow.Initiator acceptFlow = new PickupAcceptFlow.Initiator(pickupProposalId, containerStateId, trackingStateId, "initCarriage",
                "placeOfReceipt", "deliveryByCarrier", "bookingNo", "boeNo", Collections.singletonList("ref"), Payable.Origin,
                TypeOfMovement.doorToDoor, Collections.singletonList(new FreightCharges("Reason", null)),
                null, null);
        Future future3 = this.lclCompany.startFlow(acceptFlow);
        network.runNetwork();
        future3.get();

        Arrays.asList(lclCompany, buyer).forEach(node -> node.transaction(() -> {
            List<StateAndRef<PickupState>> pickUpStateList = node.getServices().getVaultService().queryBy(PickupState.class).getStates();
            Assert.assertEquals(0, pickUpStateList.size());

            List<StateAndRef<BillOfLadingState>> billOfLadingStateList = node.getServices().getVaultService().queryBy(BillOfLadingState.class).getStates();
            Assert.assertEquals(1, billOfLadingStateList.size());

            TrackingState trackingState = this.resolveStateId(TrackingState.class, trackingStateId, node, Vault.StateStatus.UNCONSUMED);
            assertEquals(trackingState.getStatus(), TrackingStatus.PickupCompleted);
            return null;
        }));
    }
}

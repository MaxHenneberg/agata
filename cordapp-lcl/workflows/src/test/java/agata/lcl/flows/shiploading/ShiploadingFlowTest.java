package agata.lcl.flows.shiploading;

import agata.bol.dataholder.Address;
import agata.bol.dataholder.DescriptionOfGoods;
import agata.bol.dataholder.FreightCharges;
import agata.bol.dataholder.ItemRow;
import agata.bol.enums.Payable;
import agata.bol.enums.TypeOfMovement;
import agata.bol.states.BillOfLadingState;
import agata.lcl.flows.FlowTestBase;
import agata.lcl.flows.pickup.PickupAcceptFlow;
import agata.lcl.flows.pickup.PickupAddGoodsFlow;
import agata.lcl.flows.pickup.PickupProposalFlow;
import agata.lcl.states.shiploading.ShiploadingProposal;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.testing.node.StartedMockNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ShiploadingFlowTest extends FlowTestBase {
    private StartedMockNode lclCompany;
    private StartedMockNode supplier;
    private StartedMockNode buyer;
    private StartedMockNode shippingLine;
    Address address1 = new Address("Sample street 1", "New York", "", "", "US");
    Address address2 = new Address("Hafenstrasse", "Hamburg", "", "", "DE");

    @Before
    public void setupShiploadingTest() {
        lclCompany = network.createPartyNode(new CordaX500Name("LCL Company", "New York", "US"));
        shippingLine = network.createPartyNode(new CordaX500Name("Shipper", "New York", "US"));
        supplier = network.createPartyNode(new CordaX500Name("Supplier", "New York", "US"));
        buyer = network.createPartyNode(new CordaX500Name("Buyer", "New York", "US"));
        network.runNetwork();
    }

    @Test
    public void shipLoadingTest() throws ExecutionException, InterruptedException {
        final UniqueIdentifier containerStateId = createContainerState(lclCompany, shippingLine);
        List<UniqueIdentifier> houseBolList = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            houseBolList.add(createHouseBol(i + ""));
        }

        ShiploadingProposalFlow.Initiator shipLoadingProposalFlow =
                new ShiploadingProposalFlow.Initiator(containerStateId, getParty(shippingLine), houseBolList, "initCarriage",
                        "place", "123", "123", Collections.singletonList("123"), Payable.Origin, TypeOfMovement.doorToDoor,
                        null, null, null);
        Future<UniqueIdentifier> future1 = this.lclCompany.startFlow(shipLoadingProposalFlow);
        network.runNetwork();
        UniqueIdentifier shipLoadingProposalId = future1.get();

        ShiploadingAcceptFlow.Initiator acceptFlow = new ShiploadingAcceptFlow.Initiator(shipLoadingProposalId);

        Future future2 = this.shippingLine.startFlow(acceptFlow);
        network.runNetwork();
        future2.get();

        Arrays.asList(lclCompany, shippingLine).forEach(node -> node.transaction(() -> {
            List<StateAndRef<ShiploadingProposal>> pickUpStateList = node.getServices().getVaultService().queryBy(ShiploadingProposal.class).getStates();
            Assert.assertEquals(0, pickUpStateList.size());
            return null;
        }));

        List<StateAndRef<BillOfLadingState>> lclCompanyBillOfLadingStateList =
                lclCompany.getServices().getVaultService().queryBy(BillOfLadingState.class).getStates();
        Assert.assertEquals(11, lclCompanyBillOfLadingStateList.size());
        final List<ItemRow> packingList =
                lclCompanyBillOfLadingStateList.stream().map(e -> e.getState().getData()).filter(bol -> bol.getGoodsList().size() == 1)
                        .flatMap(bol -> bol.getGoodsList().stream()).collect(
                        Collectors.toList());

        List<StateAndRef<BillOfLadingState>> shippingLineBillOfLadingStateList =
                shippingLine.getServices().getVaultService().queryBy(BillOfLadingState.class).getStates();
        Assert.assertEquals(1, shippingLineBillOfLadingStateList.size());

        for (ItemRow itemRow : packingList) {
            Assert.assertTrue(shippingLineBillOfLadingStateList.get(0).getState().getData().getGoodsList().contains(itemRow));
        }
    }

    private UniqueIdentifier createHouseBol(String id) throws ExecutionException, InterruptedException {
        final UniqueIdentifier assignmentStateId = createAssignmentState(lclCompany, supplier, buyer, address1, address2, id);
        final UniqueIdentifier containerStateId = createContainerState(lclCompany, shippingLine);

        PickupProposalFlow.Initiator pickupProposeFlow = new PickupProposalFlow.Initiator(assignmentStateId);
        Future<UniqueIdentifier> future1 = this.lclCompany.startFlow(pickupProposeFlow);
        network.runNetwork();

        UniqueIdentifier pickupProposalId = future1.get();

        PickupAddGoodsFlow.Initiator modifyFlow = new PickupAddGoodsFlow.Initiator(pickupProposalId,
                Collections.singletonList(new ItemRow("abc", id, 3, new DescriptionOfGoods("iPhone", "pallet", 100), 12, 12, 456)),
                "NotBlank");
        Future future2 = this.supplier.startFlow(modifyFlow);
        network.runNetwork();
        future2.get();

        PickupAcceptFlow.Initiator acceptFlow = new PickupAcceptFlow.Initiator(pickupProposalId, containerStateId, "initCarriage",
                "placeOfReceipt", "deliveryByCarrier", "bookingNo", "boeNo", Collections.singletonList("ref"), Payable.Origin,
                TypeOfMovement.doorToDoor, Collections.singletonList(new FreightCharges("Reason", null)),
                null, null);
        Future<UniqueIdentifier> future3 = this.lclCompany.startFlow(acceptFlow);
        network.runNetwork();
        return future3.get();
    }
}

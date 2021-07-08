package agata.lcl.flows.shiploading;

import agata.bol.dataholder.ItemRow;
import agata.bol.enums.Payable;
import agata.bol.enums.TypeOfMovement;
import agata.bol.schema.BillOfLadingSchemaV1;
import agata.bol.states.BillOfLadingState;
import agata.lcl.enums.TrackingStatus;
import agata.lcl.flows.FlowTestBase;
import agata.lcl.states.assignment.AssignmentState;
import agata.lcl.states.shiploading.ShiploadingProposal;
import agata.lcl.states.tracking.ShippingTrackingState;
import agata.lcl.states.tracking.TrackingState;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.Builder;
import net.corda.core.node.services.vault.CriteriaExpression;
import net.corda.core.node.services.vault.FieldInfo;
import net.corda.core.node.services.vault.QueryCriteria;
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

import static net.corda.core.node.services.vault.QueryCriteriaUtils.getField;

public class ShiploadingFlowTest extends FlowTestBase {
    private StartedMockNode lclCompany;
    private StartedMockNode supplier;
    private StartedMockNode buyer;
    private StartedMockNode shippingLine;

    @Before
    public void setupShiploadingTest() {
        lclCompany = network.createPartyNode(new CordaX500Name("LCL Company", "New York", "US"));
        shippingLine = network.createPartyNode(new CordaX500Name("Shipper", "New York", "US"));
        supplier = network.createPartyNode(new CordaX500Name("Supplier", "New York", "US"));
        buyer = network.createPartyNode(new CordaX500Name("Buyer", "New York", "US"));
        network.runNetwork();
    }

    @Test
    public void shipLoadingTest() throws ExecutionException, InterruptedException, NoSuchFieldException {

        List<UniqueIdentifier> assignmentIds = new ArrayList<>(3);
        List<UniqueIdentifier> trackingStateIds = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            // Create fake LCL contract and retrieve associated tracking state id
            final UniqueIdentifier assignmentStateId = createAssignmentState(lclCompany, supplier, buyer, this.departureAddress, this.arrivalAddress, i + "");
            assignmentIds.add(assignmentStateId);
            UniqueIdentifier trackingStateId = this.resolveStateId(AssignmentState.class, assignmentStateId, this.lclCompany, Vault.StateStatus.UNCONSUMED).getTrackingStateId();
            trackingStateIds.add(trackingStateId);
        }

        // Request container for all these packages
        final UniqueIdentifier containerStateId = createContainerState(lclCompany, shippingLine, trackingStateIds);

        // Pickup goods of each LCL assignment
        List<UniqueIdentifier> houseBolIds = new ArrayList<>(assignmentIds.size());
        for (int i = 0; i < assignmentIds.size(); i++) {
            houseBolIds.add(this.createHouseBol(i + "", assignmentIds.get(i), containerStateId, this.lclCompany, this.supplier));
        }

        ShiploadingProposalFlow.Initiator shipLoadingProposalFlow =
                new ShiploadingProposalFlow.Initiator(containerStateId, getParty(shippingLine), houseBolIds, "initCarriage",
                        "place", "123", "123", Collections.singletonList("123"), Payable.Origin, TypeOfMovement.doorToDoor,
                        null, null, null);
        Future<UniqueIdentifier> future1 = this.lclCompany.startFlow(shipLoadingProposalFlow);
        network.runNetwork();
        UniqueIdentifier shipLoadingProposalId = future1.get();

        ShiploadingAcceptFlow.Initiator acceptFlow = new ShiploadingAcceptFlow.Initiator(shipLoadingProposalId, trackingStateIds);
        Future future2 = this.shippingLine.startFlow(acceptFlow);
        network.runNetwork();
        future2.get();

        Arrays.asList(lclCompany, shippingLine).forEach(node -> node.transaction(() -> {
            List<StateAndRef<ShiploadingProposal>> pickUpStateList = node.getServices().getVaultService().queryBy(ShiploadingProposal.class).getStates();
            Assert.assertEquals(0, pickUpStateList.size());

            // Check if all tracking states are set as expected
            for (UniqueIdentifier trackingStateId : trackingStateIds) {
                TrackingState state = this.resolveStateId(ShippingTrackingState.class, trackingStateId, node, Vault.StateStatus.UNCONSUMED);
                Assert.assertEquals(TrackingStatus.LoadedOnShip, state.getStatus());
            }

            return null;
        }));

        List<StateAndRef<BillOfLadingState>> lclCompanyBillOfLadingStateList =
                lclCompany.getServices().getVaultService().queryBy(BillOfLadingState.class).getStates();
        Assert.assertEquals(4, lclCompanyBillOfLadingStateList.size());

        QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.ALL);
        FieldInfo attributeShipper = getField("shipper", BillOfLadingSchemaV1.PersistentBOL.class);
        CriteriaExpression shipperIndex = Builder.equal(attributeShipper, getParty(shippingLine).getName().toString());
        QueryCriteria customCriteria1 = new QueryCriteria.VaultCustomQueryCriteria(shipperIndex);
        QueryCriteria criteria = generalCriteria.and(customCriteria1);
        Vault.Page<ContractState> results = lclCompany.getServices().getVaultService().queryBy(BillOfLadingState.class, criteria);
        results.getStates().forEach((e) -> System.out.println(e.getState().getData().toString()));

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
}

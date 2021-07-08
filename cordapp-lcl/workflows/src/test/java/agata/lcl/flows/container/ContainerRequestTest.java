package agata.lcl.flows.container;

import agata.bol.dataholder.ContainerInformation;
import agata.bol.enums.ContainerType;
import agata.lcl.enums.TrackingStatus;
import agata.lcl.flows.FlowTestBase;
import agata.lcl.states.assignment.AssignmentState;
import agata.lcl.states.container.ContainerRequestState;
import agata.lcl.states.tracking.TrackingState;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.TransactionState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.transactions.SignedTransaction;
import net.corda.testing.node.StartedMockNode;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class ContainerRequestTest extends FlowTestBase {
    private StartedMockNode lclCompany;
    private StartedMockNode shippingLine;
    private StartedMockNode supplier;
    private StartedMockNode buyer;

    @Before
    public void setupTest() {
        lclCompany = network.createPartyNode(new CordaX500Name("LCL Company", "New York", "US"));
        shippingLine = network.createPartyNode(new CordaX500Name("Shipping Line A", "New York", "US"));
        supplier = network.createPartyNode(new CordaX500Name("Supplier", "New York", "US"));
        buyer = network.createPartyNode(new CordaX500Name("Buyer", "New York", "US"));
        network.runNetwork();
    }

    @Test
    public void testEntireContainerRequestFlow() throws ExecutionException, InterruptedException {

        UniqueIdentifier assignmentId = this.createAssignmentState(this.lclCompany, this.supplier, this.buyer, this.departureAddress, this.arrivalAddress, "xyz");
        UniqueIdentifier trackingStateId = this.resolveStateId(AssignmentState.class, assignmentId, this.lclCompany, Vault.StateStatus.UNCONSUMED).getTrackingStateId();

        Party lclCompanyParty = getParty(this.lclCompany);
        Party shippingLineParty = getParty(this.shippingLine);

        String portOfLoading = "Port A";
        String portOfDischarge = "Port B";
        String forwarderId = "Any id";
        ContainerType requestedType = ContainerType.Large;
        String vesselName = "My vessel";
        ContainerInformation assignedContainer = new ContainerInformation("123", "456", ContainerType.Large);

        // PROPOSE
        ContainerRequestProposalFlow.Initiator proposeFlow = new ContainerRequestProposalFlow.Initiator(
                shippingLineParty, lclCompanyParty, portOfLoading, portOfDischarge, forwarderId, requestedType);
        CordaFuture<UniqueIdentifier> future1 = this.lclCompany.startFlow(proposeFlow);
        network.runNetwork();
        UniqueIdentifier containerRequestProposalId = future1.get();

        // ASSIGN CONTAINER
        AssignContainerFlow.Initiator assignContainerFlow = new AssignContainerFlow.Initiator(containerRequestProposalId, vesselName, assignedContainer);
        CordaFuture<SignedTransaction> future2 = this.shippingLine.startFlow(assignContainerFlow);
        network.runNetwork();

        // ACCEPT
        AcceptContainerFlow.Initiator acceptFlow = new AcceptContainerFlow.Initiator(containerRequestProposalId, trackingStateId);
        CordaFuture<SignedTransaction> future3 = this.lclCompany.startFlow(acceptFlow);
        network.runNetwork();
        SignedTransaction tx = future3.get();


        // Check that transaction is recorded in both vaults
        for (StartedMockNode node : Arrays.asList(lclCompany, shippingLine)) {
            SignedTransaction recordedTx = node.getServices().getValidatedTransactions().getTransaction(tx.getId());
            List<TransactionState<ContractState>> txOutputs = recordedTx.getTx().getOutputs();
            assert (txOutputs.size() == 1);

            ContainerRequestState recordedState = (ContainerRequestState) txOutputs.get(0).getData();
            assertEquals(lclCompanyParty, recordedState.getLclCompany());
            assertEquals(lclCompanyParty, recordedState.getLclDestination());
            assertEquals(shippingLineParty, recordedState.getShippingLine());
            assertEquals(portOfLoading, recordedState.getPortOfLoading());
            assertEquals(portOfDischarge, recordedState.getPortOfDischarge());
            assertEquals(forwarderId, recordedState.getForwardingAgentNo());
            assertEquals(requestedType, recordedState.getRequestedType());
            assertEquals(vesselName, recordedState.getVesselName());
            assertEquals(assignedContainer, recordedState.getContainer());

            TrackingState trackingState = this.resolveStateId(TrackingState.class, trackingStateId, node, Vault.StateStatus.UNCONSUMED);
            assertEquals(trackingState.getStatus(), TrackingStatus.ContainerAssigned);
        }

    }
}

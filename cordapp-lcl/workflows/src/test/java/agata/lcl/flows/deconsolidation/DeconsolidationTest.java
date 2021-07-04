package agata.lcl.flows.deconsolidation;

import agata.bol.contracts.BillOfLadingContract;
import agata.bol.dataholder.Address;
import agata.bol.dataholder.ContainerInformation;
import agata.bol.dataholder.Price;
import agata.bol.enums.ContainerType;
import agata.bol.enums.Payable;
import agata.bol.enums.TypeOfMovement;
import agata.bol.flows.CreateBoLFlow;
import agata.bol.states.BillOfLadingState;
import agata.lcl.flows.FlowTestBase;
import agata.lcl.states.deconsolidation.DeconsolidationState;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.TransactionState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.testing.node.StartedMockNode;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class DeconsolidationTest extends FlowTestBase {

    private StartedMockNode lclCompany;
    private StartedMockNode shippingLine;

    @Before
    public void setupDeconsolidationTest() {
        lclCompany = network.createPartyNode(new CordaX500Name("LCL Company", "New York", "US"));
        shippingLine = network.createPartyNode(new CordaX500Name("Shipping Line", "New York", "US"));
        network.runNetwork();
    }

    @Test
    public void testContainerDeconsolidation() throws ExecutionException, InterruptedException {
        Party lclCompanyParty = getParty(this.lclCompany);
        Party shippingLineParty = getParty(this.shippingLine);

        // Create fake master bill of lading
        Address fakeAddress = new Address("", "", "", "", "");
        String containerNo = "123abc";
        List<ContainerInformation> containerList = Arrays.asList(new ContainerInformation(containerNo, "", ContainerType.Large));
        BillOfLadingState masterBillOfLading = new BillOfLadingState(shippingLineParty, lclCompanyParty, lclCompanyParty, "", "", "", "", "", "", "", "", Lists.emptyList(), null, "", fakeAddress, null, "", Payable.Origin, TypeOfMovement.doorToDoor, Lists.emptyList(), Lists.emptyList(), null, null, Lists.emptyList(), containerList);
        CreateBoLFlow.Initiator flow = new CreateBoLFlow.Initiator(masterBillOfLading, Lists.emptyList(), new BillOfLadingContract.BoLCommands.CreateMasterBoL());
        CordaFuture<UniqueIdentifier> future0 = this.shippingLine.startFlow(flow);
        network.runNetwork();
        UniqueIdentifier masterBolId = future0.get();

        // PROPOSE
        ProposeDeconsolidationFlow.Initiator proposeFlow = new ProposeDeconsolidationFlow.Initiator(shippingLineParty, masterBolId, containerNo);
        CordaFuture<UniqueIdentifier> future1 = this.lclCompany.startFlow(proposeFlow);
        network.runNetwork();
        UniqueIdentifier deconsolidationProposalId = future1.get();

        // RELEASE CONTAINER
        Price collect = new Price(new BigDecimal("123"), Currency.getInstance("EUR"));
        ReleaseContainerFlow.Initiator releaseFlow = new ReleaseContainerFlow.Initiator(deconsolidationProposalId, collect);
        this.shippingLine.startFlow(releaseFlow);
        network.runNetwork();

        // ACCEPT
        AcceptDeconsolidationFlow.Initiator acceptFlow = new AcceptDeconsolidationFlow.Initiator(deconsolidationProposalId);
        CordaFuture<SignedTransaction> future3 = this.lclCompany.startFlow(acceptFlow);
        network.runNetwork();
        SignedTransaction tx = future3.get();

        // Check that transaction is recorded in both vaults
        for (StartedMockNode node : Arrays.asList(this.lclCompany, this.shippingLine)) {
            SignedTransaction recordedTx = node.getServices().getValidatedTransactions().getTransaction(tx.getId());
            List<TransactionState<ContractState>> txOutputs = recordedTx.getTx().getOutputs();
            assert (txOutputs.size() == 1);
            assert (txOutputs.get(0).getData() instanceof DeconsolidationState);

            DeconsolidationState recordedState = (DeconsolidationState) txOutputs.get(0).getData();
            assertEquals(lclCompanyParty, recordedState.getLclCompany());
            assertEquals(shippingLineParty, recordedState.getShippingLine());
            assertEquals(masterBillOfLading.getLinearId(), recordedState.getMasterBillOfLadingId());
            assertEquals(collect, recordedState.getCollect());
            assertEquals(containerNo, recordedState.getContainerNo());
        }
    }

}

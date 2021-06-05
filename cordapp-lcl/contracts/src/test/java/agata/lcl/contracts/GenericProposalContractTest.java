package agata.lcl.contracts;

import agata.lcl.contracts.pickup.PickupContract;
import agata.lcl.states.pickup.PickupProposal;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static net.corda.testing.node.NodeTestUtils.ledger;

public class GenericProposalContractTest {
//    private MockServices ledgerServices = new MockServices(Arrays.asList("agata.lcl.contracts"));
//    private TestIdentity proposer = new TestIdentity(new net.corda.core.identity.CordaX500Name("proposer", "New York", "US"));
//    private TestIdentity propose = new TestIdentity(new net.corda.core.identity.CordaX500Name("propose", "Tokyo", "JP"));
//    private TestIdentity other = new TestIdentity(new net.corda.core.identity.CordaX500Name("other", "London", "GB"));
//
//    @Test
//    public void testProposal() {
//        final PickupProposal pickupState = new PickupProposal(other.getParty(), this.propose.getParty(), this.proposer.getParty(), Collections.emptyList(), new UniqueIdentifier());
//        ledger(ledgerServices, l -> {
//            l.transaction(tx -> {
//                tx.command(Arrays.asList(proposer.getPublicKey(), propose.getPublicKey()), new GenericProposalContract.GenericProposalCommands.Propose());
//                tx.output(GenericProposalContract.ID, new GenericProposalState<>(proposer.getParty(), propose.getParty(), pickupState, new PickupContract.PickupCommands.Propose()));
//                return tx.verifies();
//            });
//            return null;
//        });
//    }
//
//    @Test
//    public void testAccept() {
//        final PickupProposal pickupState = new PickupProposal(other.getParty(), this.propose.getParty(), this.proposer.getParty(), Collections.emptyList(), new UniqueIdentifier());
//        ledger(ledgerServices, l -> {
//            l.transaction(tx -> {
//                tx.command(Arrays.asList(proposer.getPublicKey(), propose.getPublicKey()), new GenericProposalContract.GenericProposalCommands.Accept());
//                tx.input(GenericProposalContract.ID, new GenericProposalState<>(proposer.getParty(), propose.getParty(),
//                        pickupState, new PickupContract.PickupCommands.Accept()));
//                tx.output(PickupContract.ID, pickupState.generateState());
//                return tx.verifies();
//            });
//            return null;
//        });
//    }
//
//    @Test
//    public void testModify() {
//        final PickupProposal pickupState = new PickupProposal(other.getParty(), this.propose.getParty(), this.proposer.getParty(), Collections.emptyList(), new UniqueIdentifier());
//        final PickupProposal modifiedPickupState = new PickupProposal(other.getParty(), this.propose.getParty(), this.proposer.getParty(), Collections.singletonList("Test"), new UniqueIdentifier());
//        ledger(ledgerServices, l -> {
//            l.transaction(tx -> {
//                tx.command(Arrays.asList(proposer.getPublicKey(), propose.getPublicKey()), new GenericProposalContract.GenericProposalCommands.Modify());
//                tx.input(GenericProposalContract.ID, new GenericProposalState<PickupProposal>(proposer.getParty(), propose.getParty(),
//                        pickupState, new PickupContract.PickupCommands.AddGoods()));
//
//                tx.output(GenericProposalContract.ID, new GenericProposalState<PickupProposal>(proposer.getParty(), propose.getParty(),
//                        modifiedPickupState, new PickupContract.PickupCommands.AddGoods()));
//                return tx.verifies();
//            });
//            return null;
//        });
//    }
}

package agata.lcl.contracts;

import agata.lcl.contracts.pickup.PickupContract;
import agata.lcl.states.GenericProposalState;
import agata.lcl.states.pickup.PickupState;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static net.corda.testing.node.NodeTestUtils.ledger;

public class GenericProposalContractTest {
    private MockServices ledgerServices = new MockServices(ImmutableList.of("agata.lcl.contracts"));
    private TestIdentity proposer = new TestIdentity(new net.corda.core.identity.CordaX500Name("proposer", "New York", "US"));
    private TestIdentity propose = new TestIdentity(new net.corda.core.identity.CordaX500Name("propose", "Tokyo", "JP"));
    private TestIdentity other = new TestIdentity(new net.corda.core.identity.CordaX500Name("other", "London", "GB"));

    @Test
    public void testProposal() {
        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                tx.command(Arrays.asList(proposer.getPublicKey(), propose.getPublicKey()), new GenericProposalContract.Commands.Propose());
                tx.output(GenericProposalContract.ID, new GenericProposalState<>(proposer.getParty(), propose.getParty(),
                        new PickupState(other.getParty(), this.propose.getParty(), this.proposer.getParty(), Collections.emptyList(), new UniqueIdentifier())));
                return tx.verifies();
            });
            return null;
        });
    }

    @Test
    public void testAccept(){
        final PickupState pickupState = new PickupState(other.getParty(), this.propose.getParty(), this.proposer.getParty(), Collections.emptyList(), new UniqueIdentifier());
        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                tx.command(Arrays.asList(proposer.getPublicKey(), propose.getPublicKey()), new GenericProposalContract.Commands.Accept());
                tx.input(GenericProposalContract.ID, new GenericProposalState<>(proposer.getParty(), propose.getParty(),
                        pickupState));
                tx.output(PickupContract.ID, pickupState
                );
                return tx.verifies();
            });
            return null;
        });
    }

    @Test
    public void testModify() {
        final PickupState pickupState = new PickupState(other.getParty(), this.propose.getParty(), this.proposer.getParty(), Collections.emptyList(), new UniqueIdentifier());
        final PickupState modifiedPickupState = new PickupState(other.getParty(), this.propose.getParty(), this.proposer.getParty(), Collections.singletonList("Test"), new UniqueIdentifier());
        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                tx.command(ImmutableList.of(proposer.getPublicKey(), propose.getPublicKey()), new GenericProposalContract.Commands.Modify());
                tx.input(GenericProposalContract.ID, new GenericProposalState<>(proposer.getParty(), propose.getParty(),
                        pickupState));

                tx.output(GenericProposalContract.ID, new GenericProposalState<>(proposer.getParty(), propose.getParty(),
                        modifiedPickupState));
                return tx.verifies();
            });
            return null;
        });
    }
}

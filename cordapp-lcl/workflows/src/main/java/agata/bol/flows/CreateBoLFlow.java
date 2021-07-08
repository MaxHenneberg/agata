package agata.bol.flows;

import agata.bol.contracts.BillOfLadingContract;
import agata.bol.states.BillOfLadingState;
import agata.lcl.flows.DefaultResponderFlow;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CreateBoLFlow {

    @InitiatingFlow
    public static class Initiator extends FlowLogic<UniqueIdentifier> {
        final BillOfLadingState toBeCreated;
        final List<StateAndRef> inputStateRefs;
        final BillOfLadingContract.BoLCommands commandData;

        public Initiator(BillOfLadingState toBeCreated, List<StateAndRef> inputStateRefs, BillOfLadingContract.BoLCommands commandData) {
            this.toBeCreated = toBeCreated;
            this.commandData = commandData;
            this.inputStateRefs = inputStateRefs;
        }

        @Suspendable
        @Override
        public UniqueIdentifier call() throws FlowException {
            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
            TransactionBuilder txBuilder = new TransactionBuilder(notary);
            txBuilder.addOutputState(this.toBeCreated);

            List<AbstractParty> otherParties = new ArrayList<>();
            otherParties.addAll(toBeCreated.getParticipants());
            for (StateAndRef input : inputStateRefs) {
                txBuilder.addInputState(input);
                otherParties.addAll(input.getState().getData().getParticipants());
            }
            otherParties = otherParties.stream().distinct().filter(x -> !x.equals(getOurIdentity())).collect(Collectors.toList());

            List<PublicKey> requiredSigners = otherParties.stream().map(AbstractParty::getOwningKey).collect(Collectors.toList());
            Command command = new Command(this.commandData, requiredSigners);
            txBuilder.addCommand(command);

            //Signing the transaction ourselves
            SignedTransaction partStx = getServiceHub().signInitialTransaction(txBuilder);

            //Gather counterparty sigs
            List<FlowSession> sessions = otherParties.stream().map(this::initiateFlow).collect(Collectors.toList());
            SignedTransaction fullyStx = subFlow(new CollectSignaturesFlow(partStx, sessions));

            //Finalise the transaction
            SignedTransaction finalisedTx = subFlow(new FinalityFlow(fullyStx, sessions));
            return finalisedTx.getTx().outputsOfType(BillOfLadingState.class).get(0).getLinearId();
        }
    }

    @InitiatedBy(Initiator.class)
    public static class Responder extends DefaultResponderFlow {
        public Responder(FlowSession counterpartySession) {
            super(counterpartySession);
        }
    }
}

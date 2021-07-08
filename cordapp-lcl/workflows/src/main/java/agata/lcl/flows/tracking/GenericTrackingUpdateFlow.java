package agata.lcl.flows.tracking;

import agata.lcl.flows.DefaultResponderFlow;
import agata.lcl.states.tracking.TrackingState;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.security.PublicKey;
import java.util.List;
import java.util.stream.Collectors;

public class GenericTrackingUpdateFlow {

    @InitiatingFlow
    public static class Initiator extends FlowLogic<SignedTransaction> {

        private final StateAndRef<TrackingState> input;
        private final TrackingState output;
        private final CommandData commandData;

        public Initiator(StateAndRef input, TrackingState output, CommandData commandData) {
            this.input = input;
            this.output = output;
            this.commandData = commandData;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {
            List<PublicKey> requiredSigners = this.output.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList());
            Command command = new Command(this.commandData, requiredSigners);

            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
            final TransactionBuilder txBuilder = new TransactionBuilder(notary);
            if (input != null) {
                txBuilder.addInputState(input);
            }
            txBuilder.addOutputState(output);
            txBuilder.addCommand(command);

            final SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(txBuilder);

            List<FlowSession> sessions = this.output.getParticipants().stream().distinct().filter(p -> !p.equals(getOurIdentity())).map(this::initiateFlow).collect(Collectors.toList());
            SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(partSignedTx, sessions));
            return subFlow(new FinalityFlow(fullySignedTx, sessions));
        }
    }

    @InitiatedBy(Initiator.class)
    public static class Responder extends DefaultResponderFlow {

        public Responder(FlowSession counterpartySession) {
            super(counterpartySession);
        }
    }
}

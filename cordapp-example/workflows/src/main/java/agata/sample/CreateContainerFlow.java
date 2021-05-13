package agata.sample;

import agata.sample.container.contracts.ContainerContract;
import agata.sample.container.dataholder.ReservedSlot;
import agata.sample.container.states.ContainerState;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CreateContainerFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class CreateContainerInitiator extends FlowLogic<SignedTransaction> {

        private final String containerId;
        private final int freeSlots;
        private final double pricePerSlot;
        private final LocalDateTime endAuctionDate;
        private final LocalDateTime shippingDate;

        public CreateContainerInitiator(String containerId, int freeSlots, double pricePerSlot, LocalDateTime endAuctionDate, LocalDateTime shippingDate) {
            this.containerId = containerId;
            this.freeSlots = freeSlots;
            this.pricePerSlot = pricePerSlot;
            this.endAuctionDate = endAuctionDate;
            this.shippingDate = shippingDate;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            final Party notary = getServiceHub().getNetworkMapCache().getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB"));
            final Party me = getOurIdentity();
            ContainerState containerState = new ContainerState(this.containerId, me, this.freeSlots, this.pricePerSlot, this.endAuctionDate, this.shippingDate, new UniqueIdentifier());

            TransactionBuilder builder = new TransactionBuilder(notary)
                    .addOutputState(containerState)
                    .addCommand(new ContainerContract.Commands.Create(), Collections.singletonList(me.getOwningKey()));

            builder.verify(getServiceHub());
            SignedTransaction selfSignedTransaction = getServiceHub().signInitialTransaction(builder);

            List<FlowSession> containerSession = new ArrayList<>();

            return subFlow(new FinalityFlow(selfSignedTransaction, containerSession));
        }
    }
}

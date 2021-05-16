package agata.sample.container.states;

import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class GoodsState implements LinearState {

    private final AbstractParty containerOwner;
    private final AbstractParty supplierIdentity;
    private final String goodsDescription;
    private final int boughedSlots;

    private final UniqueIdentifier linearId;

    public GoodsState(AbstractParty containerOwner, AbstractParty supplierIdentity, String goodsDescription, int boughedSlots, UniqueIdentifier linearId) {
        this.containerOwner = containerOwner;
        this.supplierIdentity = supplierIdentity;
        this.goodsDescription = goodsDescription;
        this.boughedSlots = boughedSlots;
        this.linearId = linearId;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(containerOwner, supplierIdentity);
    }
}

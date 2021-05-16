package agata.sample.container.states;

import agata.sample.container.contracts.ContainerContract;
import net.corda.core.contracts.CommandAndState;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.OwnableState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ContainerAssetState implements OwnableState, LinearState {

    private final String containerId;
    private final AbstractParty owner;
    private final int size;

    private final UniqueIdentifier linearId;

    public ContainerAssetState(String containerId, AbstractParty owner, int size, UniqueIdentifier linearId) {
        this.containerId = containerId;
        this.owner = owner;
        this.size = size;
        this.linearId = linearId;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }

    @NotNull
    @Override
    public AbstractParty getOwner() {
        return this.owner;
    }

    @NotNull
    @Override
    public CommandAndState withNewOwner(@NotNull AbstractParty newOwner) {
        return new CommandAndState(new ContainerContract.Commands.Transfer(), new ContainerAssetState(this.containerId, newOwner, this.size, this.linearId));
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Collections.singletonList(owner);
    }
}

package agata.sample.container.states;

import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConsolidationState implements LinearState, QueryableState {

    private final ContainerAssetState containerAsset;

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return null;
    }

    @NotNull
    @Override
    public PersistentState generateMappedObject(@NotNull MappedSchema schema) {
        return null;
    }

    @NotNull
    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        return null;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return null;
    }
}

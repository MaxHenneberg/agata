package agata.lcl.enums;

import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
public enum TrackingStatus {
    SlotBooked,
    PickupCompleted,
    ContainerAssigned,
    LoadedOnShip,
    Deconsolidated,
    GoodsDelivered
}

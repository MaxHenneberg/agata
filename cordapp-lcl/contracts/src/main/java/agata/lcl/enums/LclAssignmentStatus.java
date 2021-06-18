package agata.lcl.enums;

import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
public enum LclAssignmentStatus {
    SlotBooked,
    GoodsPickedUp,
    ContainerAssigned
}

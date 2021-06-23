package agata.bol.enums;

import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
public enum TypeOfMovement {
    doorToDoor,
    portToPort,
    cfsToCfs
}

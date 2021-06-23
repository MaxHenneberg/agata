package agata.bol.enums;

import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
public enum CompanyType {
    Customer,
    Supplier,
    ForwardingAgent,
    Other
}

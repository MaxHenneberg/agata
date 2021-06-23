package agata.bol.enums;

import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
public enum IncotermCode {
    EXW,
    FCA,
    FAS,
    FOB,
    CFR,
    CIF,
    DAP,
    DPU,
    CPT,
    CIP,
    DDP
}

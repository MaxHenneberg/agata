package agata.bol.dataholder;

import lombok.*;
import net.corda.core.serialization.CordaSerializable;

import java.math.BigDecimal;
import java.util.Currency;

@CordaSerializable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Price {

    private BigDecimal amount;
    private Currency currency;

    @Override
    public String toString() {
        return amount + " " + currency.getSymbol();
    }
}

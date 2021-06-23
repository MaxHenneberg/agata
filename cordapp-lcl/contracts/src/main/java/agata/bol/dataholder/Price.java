package agata.bol.dataholder;

import net.corda.core.serialization.CordaSerializable;

import java.math.BigDecimal;
import java.util.Currency;

@CordaSerializable
public class Price {

    private BigDecimal amount;
    private Currency currency;

    public Price(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return amount + " " + currency.getSymbol();
    }
}

package agata.lcl.contracts.annotations;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static agata.lcl.contracts.GenericProposalContract.*;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(value=RUNTIME)
@Target(value= FIELD)
public @interface MandatoryForContract {
    Class[] value() default {Commands.All.class};
}

package agata.lcl.contracts.annotations;

import agata.lcl.contracts.GenericProposalContract;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(value=RUNTIME)
@Target(value= FIELD)
public @interface NotBlankForContract {
    Class[] value() default {GenericProposalContract.Commands.All.class};
}

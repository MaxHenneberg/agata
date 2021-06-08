package agata.lcl.contracts;

import agata.lcl.contracts.annotations.MandatoryForContract;
import agata.lcl.contracts.annotations.NotBlankForContract;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.ContractState;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public class GenericProposalContractUtils {
    public static void checkMandatoryFields(ContractState toBeChecked, CommandData command, boolean isInput) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        List<String> missingMandatoryFields = new LinkedList<>();
        List<Field> fieldsForSecondCheck = new LinkedList<>();
        List<String> missingNotBlankFields = new LinkedList<>();
        Field[] fieldList = Stream.concat(Arrays.stream(toBeChecked.getClass().getDeclaredFields()), Arrays.stream(toBeChecked.getClass().getSuperclass().getDeclaredFields())).toArray(Field[]::new);
        for (Field field : fieldList) {
            if (field.isAnnotationPresent(MandatoryForContract.class)) {
                List<String> forCommands = Arrays.stream(field.getAnnotation(MandatoryForContract.class).value()).map(Class::getName).collect(Collectors.toList());
                if (forCommands.contains(GenericProposalContract.Commands.All.class.getName()) || forCommands.contains(command.getClass().getName())) {
                    if (Objects.isNull(getGetterMethodName(field, field.getDeclaringClass()).invoke(toBeChecked))) {
                        missingMandatoryFields.add(field.getName());
                    }
                }
            } else {
                fieldsForSecondCheck.add(field);
            }
        }

        for (Field field : fieldsForSecondCheck) {
            if (field.isAnnotationPresent(NotBlankForContract.class)) {
                if (field.getGenericType().getTypeName().equals(String.class.getTypeName())) {
                    List<String> forCommands = Arrays.stream(field.getAnnotation(NotBlankForContract.class).value()).map(Class::getName).collect(Collectors.toList());
                    if (forCommands.contains(GenericProposalContract.Commands.All.class.getName()) || forCommands.contains(command.getClass().getName())) {
                        final String stringToBeChecked = (String) getGetterMethodName(field, field.getDeclaringClass()).invoke(toBeChecked);
                        if (stringToBeChecked == null || stringToBeChecked.isEmpty()) {
                            missingNotBlankFields.add(field.getName());
                        }
                    }
                }
            }
        }
        requireThat(require -> {
            require.using(buildErrorMessage(missingMandatoryFields, missingNotBlankFields, toBeChecked.getClass().getName(), isInput),
                    missingMandatoryFields.isEmpty() && missingNotBlankFields.isEmpty());
            return null;
        });

    }

    private static Method getGetterMethodName(Field field, Class clazz) throws NoSuchMethodException {
        String fieldName = field.getName();
        fieldName = fieldName.substring(0, 1).toUpperCase().concat(fieldName.substring(1));
        String getterName = "get" + fieldName;
        return clazz.getDeclaredMethod(getterName);
    }

    private static String buildErrorMessage(List<String> missingMandatoryFields, List<String> missingNotBlankFields, String stateName, boolean isInput) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Following Fields for ");
        if (isInput) {
            stringBuilder.append("Input ");
        } else {
            stringBuilder.append("Output ");
        }
        stringBuilder.append("State ");
        stringBuilder.append(stateName);
        stringBuilder.append(" must not be null: [");
        if (!missingMandatoryFields.isEmpty()) {
            stringBuilder.append(missingMandatoryFields.stream().reduce("", (subtotal, element) -> subtotal + ", " + element).substring(2));
        }
        stringBuilder.append("] and following Fields must not be Blank: [");
        if (!missingNotBlankFields.isEmpty()) {
            stringBuilder.append(missingNotBlankFields.stream().reduce("", (subtotal, element) -> subtotal + ", " + element).substring(2));
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}

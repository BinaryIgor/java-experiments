package com.igor101.records;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;

public class Records {

    public static <T extends Record> T copy(T source, Map<String, Object> toReplaceFields) {
        var recordType = source.getClass();
        try {
            var newRecordFields = newRecordFields(source, toReplaceFields);
            validateIfToReplaceFieldsExist(recordType, newRecordFields, toReplaceFields);

            var primaryConstructor = (Constructor<T>) primaryConstructorOfRecord(recordType);

            var constructorArgs = newRecordFields.values().toArray();

            return primaryConstructor.newInstance(constructorArgs);
        } catch (Exception e) {
            throw new CopyException("Can't create an instance of %s record".formatted(recordType), e);
        }
    }

    private static <T extends Record> Map<String, Object> newRecordFields(T source,
                                                                          Map<String, Object> toReplaceFields) throws Exception {
        var recordType = source.getClass();
        var recordFields = new LinkedHashMap<String, Object>();

        for (var f : recordType.getRecordComponents()) {
            var fieldName = f.getName();
            var fieldType = f.getType();

            var toReplaceFieldValue = toReplaceFields.get(fieldName);
            if (toReplaceFieldValue == null) {
                recordFields.put(fieldName, f.getAccessor().invoke(source));
            } else {
                var givenType = toReplaceFieldValue.getClass();
                if (!fieldType.isAssignableFrom(givenType)) {
                    throw new RuntimeException("%s field of %s record requires %s type, but %s was given"
                            .formatted(fieldName, recordType, fieldType, givenType));
                }

                recordFields.put(fieldName, toReplaceFieldValue);
            }
        }
        return recordFields;
    }

    private static <T extends Record> void validateIfToReplaceFieldsExist(Class<T> recordType,
                                                                          Map<String, Object> recordFields,
                                                                          Map<String, Object> toReplaceFields) {
        toReplaceFields.forEach((k, v) -> {
            if (!recordFields.containsKey(k)) {
                throw new RuntimeException("%s record doesn't have a %s field".formatted(recordType, k));
            }
        });
    }

    private static Constructor<?> primaryConstructorOfRecord(Class<?> recordType) throws Exception {
        var recordComponents = recordType.getRecordComponents();
        var recordComponentsTypes = new Class<?>[recordComponents.length];
        for (int i = 0; i < recordComponents.length; i++) {
            recordComponentsTypes[i] = recordComponents[i].getType();
        }
        return recordType.getDeclaredConstructor(recordComponentsTypes);
    }

    public static class CopyException extends RuntimeException {
        public CopyException(String message, Exception root) {
            super(message, root);
        }
    }
}

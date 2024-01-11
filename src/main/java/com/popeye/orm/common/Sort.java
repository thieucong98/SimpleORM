package com.popeye.orm.common;

public class Sort {
    private String fieldName;
    private Type type;

    private Sort(String fieldName, Type type) {
        this.fieldName = fieldName;
        this.type = type;
    }

    public static Sort of(String name, Type type) {
        return new Sort(name, type);
    }

    public static Sort of(String name) {
        return new Sort(name, Type.ASC);
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        ASC("ASC"),
        DESC("DESC");

        public final String value;

        Type(String value) {
            this.value = value;
        }
    }
}

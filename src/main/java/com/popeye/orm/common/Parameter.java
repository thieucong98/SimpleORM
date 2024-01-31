package com.popeye.orm.common;

public class Parameter {
    private String fieldName;
    private Operator operator;

    private Object value;

    private Parameter(String fieldName, Operator operator, Object value) {
        this.fieldName = fieldName;
        this.operator = operator;
        this.value = value;
    }

    public static Parameter of(String fieldName, Object value) {
        return new Parameter(fieldName, Operator.EQ, value);
    }

    public static Parameter of(String fieldName, Operator operator, Object value) {
        return new Parameter(fieldName, operator, value);
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }
}

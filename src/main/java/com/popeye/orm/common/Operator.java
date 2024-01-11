package com.popeye.orm.common;

import java.util.Arrays;

public enum Operator {
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/"),
    MT(">"),
    MT_EQ(">="),
    LT("<"),
    LT_EQ("<="),
    EQ("="),
    NOT_EQ("!="),
    MOD("%"),
    IN("IN");

    public final String value;

    Operator(String value) {
        this.value = value;
    }

    public static boolean validateOperator(String operator) {
        return Arrays.stream(Operator.values()).anyMatch(i -> i.value.equals(operator));
    }
}

package org.practice.jwtauth.util;

public enum Params {

    BEARER_PREFIX("Bearer "),
    USER_NAME("username");

    public final String value;

    private Params(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


}

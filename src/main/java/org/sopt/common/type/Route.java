package org.sopt.common.type;


public enum Route {
    NONE(""),
    LIMIT("LIMIT"),
    FUND("FUND"),
    ALERT("ALERT");;

    private final String value;

    Route(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}

package fr.ufrst.m1info.comp4.typeChecker;

enum Mode {
    FIRST_PASS,
    SECOND_PASS
}

public class TypeCheckerData {
    private String scope;
    private Mode mode;

    public TypeCheckerData(Mode mode, String scope) {
        this.mode = mode;
        this.scope = scope;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }
}

package fr.ufrst.m1info.comp4.memory;

public enum OBJ {
    VAR,
    CST,
    VCST,
    METH,
    TAB;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}

package fr.ufrst.m1info.comp4.memory;

public enum SORTE {
    INT,
    BOOL,
    VOID,
    ANY;


    @Override
    public String toString() {
        return switch (this) {
            case INT -> "entier";
            case BOOL -> "booleen";
            case VOID -> "rien";
            case ANY -> "undefined";
        };
    }
}

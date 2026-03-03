package fr.ufrst.m1info.comp4.typeChecker;

public class ErrorType implements Comparable<ErrorType> {
    String err;
    public ErrorType(String err, int line, int column) {
        this.err = line + ":" + column + " Error: " + err;
    }

    @Override
    public String toString() {
        return this.err;
    }

    @Override
    public int compareTo(ErrorType o) {
        return this.err.compareTo(o.err);
    }
}

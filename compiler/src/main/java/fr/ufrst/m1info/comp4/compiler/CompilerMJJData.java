package fr.ufrst.m1info.comp4.compiler;

enum Mode {
    ADD,
    REMOVE
}

public class CompilerMJJData {
    private Mode mode;
    private int n;

    public CompilerMJJData(int n, Mode mode) {
        this.n = n;
        this.mode = mode;
    }

    public int getAddress() {
        return this.n;
    }

    public Mode getMode() {
        return this.mode;
    }
}

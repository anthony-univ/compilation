package fr.ufrst.m1info.comp4.interpreter.debugger;

import fr.ufrst.m1info.comp4.interpreter.jajacode.InterpreterJJC;
import fr.ufrst.m1info.comp4.interpreter.minijaja.InterpreterMJJ;
import fr.ufrst.m1info.comp4.parser.jajacode.VisitorJJCException;
import fr.ufrst.m1info.comp4.parser.minijaja.Node;
import fr.ufrst.m1info.comp4.parser.minijaja.VisitorMJJException;

import java.util.*;

public class Debugger {
    private InterpreterMJJ interpreterMJJ;
    private InterpreterJJC interpreterJJC;
    private Thread thread;
    private int currentLine;
    private boolean continu = false;
    private Set<Integer> breakPoints;
    private String error = "";
    private boolean paused = false;

    public Debugger() {
        this.breakPoints = new TreeSet<>();
    }

    public Thread getThread() {
        return this.thread;
    }

    public void setInterpreterMJJ(InterpreterMJJ interpreterMJJ) {
        this.interpreterMJJ = interpreterMJJ;
        this.interpreterMJJ.setDebugger(this);
        this.interpreterJJC = null;
    }

    public void setInterpreterJJC(InterpreterJJC interpreterJJC) {
        this.interpreterJJC = interpreterJJC;
        this.interpreterJJC.setDebugger(this);
        this.interpreterMJJ = null;
    }

    public void setBreakPoints(Set<Integer> breakPoints) {
        this.breakPoints = breakPoints;
    }

    public void startDebugMJJ() {
        this.currentLine = 1;
        this.error = "";
        continu = false;
        paused = false;
        this.thread = new Thread(() -> {
            try {
                interpreterMJJ.interpret();
            } catch (VisitorMJJException e) {
                this.error = e.getMessage();
                stop();
            }
        });
        this.thread.start();
    }

    public void startDebugJJC() {
        this.currentLine = 1;
        this.error = "";
        continu = false;
        paused = false;
        this.thread = new Thread(() -> {
            try {
                interpreterJJC.interpret();
            } catch (VisitorJJCException e) {
                this.error = e.getMessage();
                stop();

            }
        });
        this.thread.start();
    }

    public void stop() {
        if (this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
            if (this.interpreterMJJ != null) {
                this.interpreterMJJ.setDebugger(null);
            } else {
                this.interpreterJJC.setDebugger(null);
            }
        }
    }

    public synchronized void step() {
        paused = false;
        continu = false;
        notifyAll();
    }

    public synchronized void continu() {
        paused = false;
        continu = true;
        notifyAll();
    }

    public boolean isBreakPoint() {
        return this.breakPoints.contains(this.currentLine);
    }

    private synchronized void visiteur() throws InterruptedException {
        if (!continu) {
            paused = true;
            wait();
        }
        if (continu && isBreakPoint()) {
            paused = true;
            wait();
        }
        if (continu && breakPoints.isEmpty()) {
            paused = true;
            wait();
        }
    }

    public synchronized void visit(Node node) throws InterruptedException {
        this.currentLine = node.getLine();
        visiteur();
    }

    public synchronized void visit(fr.ufrst.m1info.comp4.parser.jajacode.Node node) throws InterruptedException {
        this.currentLine = node.getLine();
        visiteur();
    }

    public boolean debuggingMJJ() {
        return this.interpreterMJJ != null;
    }

    public boolean isFinished() {
        return this.thread == null;
    }

    public String getError()  {
        return this.error;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public Object[] getPauseResult() {
        if (this.debuggingMJJ()) {
            return new Object[]{this.currentLine, this.interpreterMJJ.getMemory().toStringTopStack(), this.interpreterMJJ.getMemory().toStringHeap()};
        } else {
            return new Object[]{this.currentLine, this.interpreterJJC.getMemory().toStringTopStack(), this.interpreterJJC.getMemory().toStringHeap()};
        }
    }
}

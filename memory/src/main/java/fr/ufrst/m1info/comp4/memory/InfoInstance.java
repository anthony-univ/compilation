package fr.ufrst.m1info.comp4.memory;

public class InfoInstance {

    private Object value;

    private InfoInstance nextInstance;

    private ElementTable elementTable;
    private InfoInstance nextStack;
    private InfoInstance prevStack;

    public InfoInstance(Object value) {
        this.value = value;
        this.nextInstance = null;
        this.elementTable = null;
        this.nextStack = null;
        this.prevStack = null;
    }

    public void setNextStack(InfoInstance nextStack) {
        this.nextStack = nextStack;
    }

    public void setPrevStack(InfoInstance prevStack) {
        this.prevStack = prevStack;
    }

    public void setIdent(ElementTable e) {
        this.elementTable = e;
    }

    public void setNextInstance(InfoInstance nextInstance) {
        this.nextInstance = nextInstance;
    }

    public InfoInstance getNextInstance() {
        return this.nextInstance;
    }

    public InfoInstance getNextStack() {
        return nextStack;
    }

    public InfoInstance getPrevStack() {
        return prevStack;
    }

    public Object getVal() {
        return this.value;
    }

    public void setVal(Object val) {this.value = val;}

    public ElementTable getElementTable() {
        return elementTable;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        InfoInstance curr = this;
        while (curr != null) {
            s.append("(").append(curr.value).append(")-");
            curr = curr.nextInstance;
        }
        return s.toString();
    }
}

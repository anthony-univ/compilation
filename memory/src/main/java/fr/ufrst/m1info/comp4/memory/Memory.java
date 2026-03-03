package fr.ufrst.m1info.comp4.memory;

import java.util.Objects;

public class Memory  {
    private SymbolTable table;
    private InfoInstance stackTop;
    private InfoInstance stackBottom;
    private Heap heap;

    public Memory() {
        this.table = new SymbolTable();
        this.stackTop = null;
        this.stackBottom = null;
        this.heap = new Heap();
    }

    public SymbolTable getSymbolTable() {
        return this.table;
    }
    public void setSymbolTable(SymbolTable table) {
        this.table = table;
    }

    public Heap getHeap() {
        return this.heap;
    }

    private void decl(String id) throws SymbolException {
        ElementTable q = this.table.getElement(id);
        this.stackTop.setIdent(q);
        q.setInstance(this.stackTop);
    }

    public void declVar(String id) throws SymbolException {
        decl(id);
    }

    public void declCst(String id) throws SymbolException {
        decl(id);
    }

    public void declTab(String id) throws SymbolException, HeapException {
        ElementTable q = this.table.getElement(id);
        Object size = this.stackTop.getVal();
        if(size==null) {
            throw new HeapException(String.format("Cannot allocate array '%s' : size is null", id));
        }
        if((int)size<=0) {
            throw new HeapException(String.format("Cannot allocate array '%s' : size is negative or equal to 0", id));
        }
        int ref = heap.allocate((int)size);
        this.stackTop.setIdent(q);
        this.stackTop.setVal(ref);
        q.setInstance(this.stackTop);
    }

    public void declMeth(String id) throws SymbolException {
        decl(id);
    }

    public void identVal(String id, int pos) throws StackException, SymbolException {
        if (this.stackTop == null) {
            throw new StackException(String.format("Stack is empty : cannot identVal '%s'", id));
        }
        InfoInstance infoInstance = this.stackTop;
        while (pos > 0) {
            infoInstance = infoInstance.getNextStack();
            pos--;
        }
        ElementTable q = this.table.getElement(id);
        infoInstance.setIdent(q);
        q.setInstance(infoInstance);
    }

    public void removeDecl(String id) throws SymbolException, StackException, HeapException {
        if (this.stackTop == null) {
            throw new StackException(String.format("Stack is empty : cannot removeDecl of '%s'", id));
        }

        ElementTable q = this.table.getElement(id);


        InfoInstance info = q.removeInstance();
        if (q.getInfo().getObj() == OBJ.TAB) {
            heap.deallocate((Integer) info.getVal());
        }
        if (info == this.stackBottom) {
            this.stackTop = null;
            this.stackBottom = null;
        } else if (info == this.stackTop) {
            this.stackTop = info.getNextStack();
            this.stackTop.setPrevStack(null);
        } else {
            info.getPrevStack().setNextStack(info.getNextStack());
            info.getNextStack().setPrevStack(info.getPrevStack());
        }
    }

    public void affectVal(String id, Object val) throws StackException, SymbolException {
        if (this.stackTop == null) {
            throw new StackException(String.format("Stack is empty : cannot affect '%s'", id));
        }

        ElementTable q = this.table.getElement(id);
        InfoIdent infoIdent = q.getInfo();
        if (infoIdent.getObj() == OBJ.VCST){
            infoIdent.setObj(OBJ.CST);
        } else if (infoIdent.getObj() == OBJ.CST) {
            throw new SymbolException(String.format("'%s' is a constant : cannot affect", id));
        }

        InfoInstance info = q.getFirstInstance();
        info.setVal(val);
    }

    public void affectValT(String id, Object val, int pos) throws StackException, SymbolException, HeapException {
        if (this.stackTop == null) {
            throw new StackException(String.format("Stack is empty : cannot affect '%s'", id));
        }

        ElementTable q = this.table.getElement(id);
        InfoIdent infoIdent = q.getInfo();
        if (infoIdent.getObj() != OBJ.TAB){
            throw new SymbolException(String.format("'%s' is not an array : cannot affect", id));
        }

        int ref = (int) q.getFirstInstance().getVal();
        heap.setValue(ref, pos, val);
    }

    public OBJ getObj(String id) throws SymbolException {
        return this.table.getObj(id);
    }

    public Object getVal(String s) throws SymbolException{
        Object val = this.table.getElement(s).getFirstInstance().getVal();
        if (val==null) {
            throw new SymbolException(String.format("'%s' is not initialized", s));
        }
        return val;
    }

    public Object getValT(String id, int pos) throws SymbolException, HeapException {
        int ref = (int) this.table.getElement(id).getFirstInstance().getVal();
        Object val = heap.getValue(ref, pos);
        if (val==null) {
            throw new SymbolException(String.format("'%s' is not initialized", id));
        }
        return val;
    }

    public String getNameClass() throws StackException {
        if (this.stackBottom==null) {
            throw new StackException("Stack is empty : cannot getNameClass");
        } else {
            return this.stackBottom.getElementTable().getIdent();
        }
    }

    public Object getValClass() throws StackException {
        if (this.stackBottom==null) {
            throw new StackException("Stack is empty : cannot getValClass");
        } else {
            return this.stackBottom.getVal();
        }
    }

    public int getArraySize(int id) throws HeapException {
        return heap.getSizeSymbol(id);
    }

    public void enqueue(Object value) {
        if (this.stackTop==null) {
            this.stackTop = new InfoInstance(value);
        } else {
            InfoInstance q = new InfoInstance(value);
            q.setNextStack(this.stackTop);
            this.stackTop.setPrevStack(q);
            this.stackTop = q;
        }

        if (this.stackBottom==null) {
            this.stackBottom = this.stackTop;
        }
    }

    public Object dequeue() throws StackException, HeapException, SymbolException {
        if (this.stackTop == null) {
            throw new StackException("Stack is empty : cannot dequeue");
        } else {
            Object val = this.stackTop.getVal();
            ElementTable q = this.stackTop.getElementTable();
            if (q!=null) {
                removeDecl(q.getIdent());
                return val;
            }

            if (this.stackTop == this.stackBottom) {
                this.stackTop = null;
                this.stackBottom = null;
            } else {
                this.stackTop = this.stackTop.getNextStack();
                this.stackTop.setPrevStack(null);
            }
            return val;
        }
    }

    public void swap() throws StackException {
        if (this.stackTop==null) {
            throw new StackException("Stack is empty : cannot swap");
        } else {
            if (this.stackTop == this.stackBottom) {
                throw new StackException("Stack has only one element : cannot swap");
            } else {
                InfoInstance q1 = this.stackTop;
                InfoInstance q2 = this.stackTop.getNextStack();

                q1.setPrevStack(q2);
                q1.setNextStack(q2.getNextStack());
                q2.setPrevStack(null);
                q2.setNextStack(q1);

                this.stackTop = q2;
                if (q2 == this.stackBottom) {
                    this.stackBottom = q1;
                }
            }
        }
    }

    public boolean stackIsEmpty() {
        return this.stackTop == null && this.stackBottom == null;
    }

    public int getStackLength() {
        int length = 0;
        InfoInstance curr = this.stackTop;
        while (curr != null) {
            length++;
            curr = curr.getNextStack();
        }
        return length;
    }

    @Override
    public String toString() {
        return this.table.toString();
    }

    private String toStringStack(InfoInstance info) {
        StringBuilder sb = new StringBuilder();
        InfoInstance curr = info;
        while (curr != null) {
            Object val = curr.getVal();
            if (val==null || Objects.equals(val.toString(), "methode")) {
                val = "omega";
            }

            Object ident = "omega";
            Object obj = OBJ.CST;
            Object sorte = "omega";
            if (curr.getElementTable() != null) {
                ident = curr.getElementTable().getIdent();
                obj = curr.getElementTable().getInfo().getObj();
                sorte = curr.getElementTable().getInfo().getSorte();
                if (sorte == null) {
                    sorte = "omega";
                }
            }

            sb.append("<").append(ident).append(",").append(val).append(",").append(obj).append(",").append(sorte).append(">\n");
            curr = curr.getNextStack();
        }
        return sb.toString();
    }

    public String toStringTopStack() {
        return this.toStringStack(this.stackTop);
    }

    public void deallocateHeap(int id) throws HeapException {
        heap.deallocate(id);
    }

    public void incrementReferenceHeap(int id) throws HeapException {
        heap.incrementReference(id);
    }

    public String toStringHeap() {
        return this.heap.toString();
    }

    public void clear(SymbolTable table) {
        this.heap = new Heap();
        this.table = table;
        this.stackTop = null;
        this.stackBottom = null;
    }
}

package fr.ufrst.m1info.comp4.interpreter.jajacode;

import fr.ufrst.m1info.comp4.interpreter.debugger.Debugger;
import fr.ufrst.m1info.comp4.memory.*;
import fr.ufrst.m1info.comp4.parser.jajacode.*;

public class InterpreterJJCVisitor implements JajacodeVisitor {
    private Memory memory;
    private int adr = 1;
    private Debugger debugger;
    private String output = "";

    private static final String TOP_OF_STACK_IS_NULL = "value at top of stack is null.";
    private static final String BEFORE_THE_TOP_OF_THE_STACK_IS_NULL = "value before the top of the stack is null.";

    public void setDebugger(Debugger debugger) { this.debugger = debugger; }
    public void setMemory(Memory memory) {
        this.memory = memory;
    }
    public Memory getMemory() { return memory; }
    public int getAdr() {
        return adr;
    }

    public void debug(Node node) {
        if (debugger != null) {
            try {
                debugger.visit(node);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public String getOutput() {
        return output;
    }

    @Override
    public Object visit(SimpleNode node, Object data) throws VisitorJJCException {
        return null;
    }

    @Override
    public Object visit(ASTJJCJajaCode node, Object data) throws VisitorJJCException {
        throw new InterpreterException("ASTJJCJajaCode should not be visited.", node.getLine(), node.getColumn());
    }

    @Override
    public Object visit(ASTJJCjcnil node, Object data) throws VisitorJJCException {
        return null;
    }

    @Override
    public Object visit(ASTJJCinit node, Object data) throws VisitorJJCException {
        debug(node);
        adr++;
        return null;
    }

    @Override
    public Object visit(ASTJJCswap node, Object data) throws VisitorJJCException {
        debug(node);
        try {
            memory.swap();
        } catch (StackException e) {
            throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
        }
        adr++;
        return null;
    }

    @Override
    public Object visit(ASTJJCneww node, Object data) throws VisitorJJCException {
        debug(node);
        String id = (String) ((ASTJJCjcident) node.jjtGetChild(0)).jjtGetValue();
        OBJ obj = (OBJ) node.jjtGetChild(2).jjtAccept(this, data);
        int pos = (int) node.jjtGetChild(3).jjtAccept(this, data);
        switch (obj) {
            case VAR:
                try {
                    memory.identVal(id, pos);
                } catch (StackException | SymbolException e) {
                    throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
                }
                break;

            case CST:
                try {
                    memory.declCst(id);
                } catch (SymbolException e) {
                    throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
                }
				break;

            case METH:
                try {
                    memory.declMeth(id);
                } catch (SymbolException e) {
                    throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
                }
                break;
            default:
        }
        adr++;
        return null;
    }

    @Override
    public Object visit(ASTJJCnewarray node, Object data) throws VisitorJJCException  {
        debug(node);
        String id = (String) ((ASTJJCjcident) node.jjtGetChild(0)).jjtGetValue();

        try {
            memory.declTab(id);
        } catch (SymbolException | HeapException e) {
            throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
        }
        adr++;
        return null;
    }

    @Override
    public Object visit(ASTJJCinvoke node, Object data) throws VisitorJJCException  {
        debug(node);
        memory.enqueue(adr + 1);

        String id = (String) ((ASTJJCjcident) node.jjtGetChild(0)).jjtGetValue();
        try {
            adr = (int) memory.getVal(id);
        } catch (SymbolException e) {
            throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
        }
        return null;
    }

    @Override
    public Object visit(ASTJJCreturnn node, Object data) throws VisitorJJCException {
        debug(node);
        try {
            adr = (int) memory.dequeue();
        } catch (StackException | HeapException | SymbolException e) {
            throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
        }
        return null;
    }

    private String write(Node node) throws VisitorJJCException {
        String value;
        try {
            Object val = memory.dequeue();
            if (val == null) {
                throw new InterpreterException(TOP_OF_STACK_IS_NULL, node.getLine(), node.getColumn());
            }
            value = val.toString();
        } catch (StackException | HeapException | SymbolException e) {
            throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
        }
        return value;
    }

    @Override
    public Object visit(ASTJJCwrite node, Object data) throws VisitorJJCException  {
        debug(node);
        output += write(node);
        adr++;
        return null;
    }

    @Override
    public Object visit(ASTJJCwriteln node, Object data) throws VisitorJJCException {
        debug(node);
        output += write(node) + "\n";
        adr++;
        return null;
    }

    @Override
    public Object visit(ASTJJCpush node, Object data) throws VisitorJJCException {
        debug(node);
        Object value = node.jjtGetChild(0).jjtAccept(this, data);
        memory.enqueue(value);
        adr++;
        return null;
    }

    @Override
    public Object visit(ASTJJCpop node, Object data) throws VisitorJJCException {
        debug(node);
        try {
            memory.dequeue();
        } catch (StackException | HeapException | SymbolException e) {
            throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
        }
        adr++;
        return null;
    }

    @Override
    public Object visit(ASTJJCload node, Object data) throws VisitorJJCException {
        debug(node);
        String id = (String) ((ASTJJCjcident) node.jjtGetChild(0)).jjtGetValue();
        Object value;
        try {
            value = memory.getVal(id);
        } catch (SymbolException e) {
            throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
        }
        memory.enqueue(value);
        adr++;
        return null;
    }

    @Override
    public Object visit(ASTJJCaload node, Object data) throws VisitorJJCException {
        debug(node);
        String id = (String) ((ASTJJCjcident) node.jjtGetChild(0)).jjtGetValue();
        Object value;
        try {
            Object val = memory.dequeue();
            if (val == null) {
                throw new InterpreterException(TOP_OF_STACK_IS_NULL, node.getLine(), node.getColumn());
            }
            int ind = (int) val;
            value = memory.getValT(id, ind);
        } catch (StackException | HeapException | SymbolException e) {
            throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
        }
        memory.enqueue(value);
        adr++;
        return null;
    }

    @Override
    public Object visit(ASTJJCstore node, Object data) throws VisitorJJCException {
        debug(node);
        String id = (String) ((ASTJJCjcident) node.jjtGetChild(0)).jjtGetValue();
        try {
            Object value = memory.dequeue();
            if (memory.getObj(id) == OBJ.TAB) {
                memory.deallocateHeap((Integer) memory.getVal(id));
                memory.incrementReferenceHeap((Integer) value);
            }
            memory.affectVal(id, value);
        } catch (StackException | SymbolException | HeapException e) {
            throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
        }
        adr++;
        return null;
    }

    @Override
    public Object visit(ASTJJCastore node, Object data) throws VisitorJJCException {
        debug(node);
        String id = (String) ((ASTJJCjcident) node.jjtGetChild(0)).jjtGetValue();
        try {
            Object value = memory.dequeue();
            Object val = memory.dequeue();
            if (val == null) {
                throw new InterpreterException(BEFORE_THE_TOP_OF_THE_STACK_IS_NULL, node.getLine(), node.getColumn());
            }
            int ind = (int) val;
            memory.affectValT(id, value, ind);
        } catch (StackException | HeapException | SymbolException e) {
            throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
        }
        adr++;
        return null;
    }

    @Override
    public Object visit(ASTJJCiff node, Object data) throws VisitorJJCException {
        debug(node);
        boolean exp;
        try {
            Object val = memory.dequeue();
            if (val == null) {
                throw new InterpreterException(TOP_OF_STACK_IS_NULL, node.getLine(), node.getColumn());
            }
            exp = (boolean) val;
        } catch (StackException | HeapException | SymbolException e) {
            throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
        }
        if (exp) {
			adr = (int) node.jjtGetChild(0).jjtAccept(this, data);
        } else {
            adr++;
        }
        return null;
    }

    @Override
    public Object visit(ASTJJCgotoo node, Object data) throws VisitorJJCException {
        debug(node);
        adr = (int) node.jjtGetChild(0).jjtAccept(this, data);
        return null;
    }

    @Override
    public Object visit(ASTJJCinc node, Object data) throws VisitorJJCException {
        debug(node);
        String id = (String) ((ASTJJCjcident) node.jjtGetChild(0)).jjtGetValue();
        try {
            Object val = memory.dequeue();
            if (val == null) {
                throw new InterpreterException(TOP_OF_STACK_IS_NULL, node.getLine(), node.getColumn());
            }
            int value = (int) val;
            val = memory.getVal(id);
            if (val == null) {
                throw new InterpreterException(BEFORE_THE_TOP_OF_THE_STACK_IS_NULL, node.getLine(), node.getColumn());
            }
            value += (int) val;
            memory.affectVal(id, value);
        } catch (StackException | SymbolException | HeapException e) {
            throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
        }
        adr++;
        return null;
    }

    @Override
    public Object visit(ASTJJCainc node, Object data) throws VisitorJJCException {
        debug(node);
        String id = (String) ((ASTJJCjcident) node.jjtGetChild(0)).jjtGetValue();
        try {
            Integer[] values = opBinaire(node);
            Object val = memory.getValT(id, values[0]);
            if (val == null) {
                throw new InterpreterException("value in the array is null.", node.getLine(), node.getColumn());
            }
            values[1] += (int) val;
            memory.affectValT(id, values[1], values[0]);
        } catch (StackException | SymbolException | HeapException e) {
            throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
        }
        adr++;
        return null;
    }

    @Override
    public Object visit(ASTJJCnop node, Object data) throws VisitorJJCException {
        debug(node);
        adr++;
        return null;
    }

    @Override
    public Object visit(ASTJJCjcstop node, Object data) throws VisitorJJCException {
        debug(node);
        adr = -1;
        if (debugger != null) {
            debugger.stop();
        }
        return null;
    }

    @Override
    public Object visit(ASTJJCjcident node, Object data) throws VisitorJJCException {
        return null;
    }

    @Override
    public Object visit(ASTJJCjcvrai node, Object data) throws VisitorJJCException {
        return true;
    }

    @Override
    public Object visit(ASTJJCjcfaux node, Object data) throws VisitorJJCException {
        return false;
    }

    @Override
    public Object visit(ASTJJCjcnbre node, Object data)throws VisitorJJCException {
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTJJCneg node, Object data) throws VisitorJJCException {
        debug(node);
        try {
            Object val = memory.dequeue();
            if (val == null) {
                throw new InterpreterException(TOP_OF_STACK_IS_NULL, node.getLine(), node.getColumn());
            }
            int value = (int) val;
            memory.enqueue(-value);
        } catch (StackException | HeapException | SymbolException e) {
            throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
        }
        adr++;
        return null;
    }

    @Override
    public Object visit(ASTJJCnot node, Object data) throws VisitorJJCException {
        debug(node);
        try {
            Object val = memory.dequeue();
            if (val == null) {
                throw new InterpreterException(TOP_OF_STACK_IS_NULL, node.getLine(), node.getColumn());
            }
            boolean value = (boolean) val;
            memory.enqueue(!value);
        } catch (StackException | HeapException | SymbolException e) {
            throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
        }
        adr++;
        return null;
    }

    @Override
    public Object visit(ASTJJCadd node, Object data) throws VisitorJJCException {
        debug(node);
        Integer[] values = opBinaire(node);
        memory.enqueue(values[0] + values[1]);
        adr++;
        return null;
    }

    @Override
    public Object visit(ASTJJCsub node, Object data) throws VisitorJJCException {
        debug(node);
        Integer[] values = opBinaire(node);
        memory.enqueue(values[0] - values[1]);
        adr++;
        return null;
    }

    private Integer[] opBinaire(Node node) throws VisitorJJCException {
        try {
            Object val = memory.dequeue();
            if (val == null) {
                throw new InterpreterException(TOP_OF_STACK_IS_NULL, node.getLine(), node.getColumn());
            }
            int value2 = (int) val;
            val = memory.dequeue();
            if (val == null) {
                throw new InterpreterException(BEFORE_THE_TOP_OF_THE_STACK_IS_NULL, node.getLine(), node.getColumn());
            }
            int value1 = (int) val;
            return new Integer[] {value1, value2};
        } catch (StackException | HeapException | SymbolException e) {
            throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
        }
    }

    @Override
    public Object visit(ASTJJCmul node, Object data) throws VisitorJJCException {
        debug(node);
        Integer[] values = opBinaire(node);
        memory.enqueue(values[0] * values[1]);
        adr++;
        return null;
    }

    @Override
    public Object visit(ASTJJCdiv node, Object data) throws VisitorJJCException {
        debug(node);
        Integer[] values = opBinaire(node);
        if (values[1] == 0) {
            throw new InterpreterException("division by zero.", node.getLine(), node.getColumn());
        }
        memory.enqueue(values[0] / values[1]);
        adr++;
        return null;
    }

    @Override
    public Object visit(ASTJJCcmp node, Object data) throws VisitorJJCException {
        debug(node);
        try {
            Object value2 = memory.dequeue();
            if (value2 == null) {
                throw new InterpreterException(TOP_OF_STACK_IS_NULL, node.getLine(), node.getColumn());
            }
            Object value1 = memory.dequeue();
            if (value1 == null) {
                throw new InterpreterException(BEFORE_THE_TOP_OF_THE_STACK_IS_NULL, node.getLine(), node.getColumn());
            }
            memory.enqueue(value1 == value2);
        } catch (StackException | HeapException | SymbolException e) {
            throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
        }
        adr++;
        return null;
    }

    @Override
    public Object visit(ASTJJCsup node, Object data) throws VisitorJJCException {
        debug(node);
        Integer[] values = opBinaire(node);
        memory.enqueue(values[0] > values[1]);
        adr++;
        return null;
    }

    private void enqueueVal2(Object val1, SimpleNode node) throws InterpreterException {
        if (val1 == null) {
            throw new InterpreterException(TOP_OF_STACK_IS_NULL, node.getLine(), node.getColumn());
        }
        boolean value2 = (boolean) val1;
        memory.enqueue(value2);
    }

    @Override
    public Object visit(ASTJJCor node, Object data) throws VisitorJJCException {
        debug(node);
        try {
            Object val1 = memory.dequeue();
            Object val2 = memory.dequeue();
            if (val2 == null) {
                throw new InterpreterException(BEFORE_THE_TOP_OF_THE_STACK_IS_NULL, node.getLine(), node.getColumn());
            }
            boolean value1 = (boolean) val2;
            if (value1) {
                memory.enqueue(true);
            } else {
                enqueueVal2(val1, node);
            }
        } catch (StackException | HeapException | SymbolException e) {
            throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
        }
        adr++;
        return null;
    }

    @Override
    public Object visit(ASTJJCand node, Object data) throws VisitorJJCException {
        debug(node);
        try {
            Object val1 = memory.dequeue();
            Object val2 = memory.dequeue();
            if (val2 == null) {
                throw new InterpreterException(BEFORE_THE_TOP_OF_THE_STACK_IS_NULL, node.getLine(), node.getColumn());
            }
            boolean value1 = (boolean) val2;
            if (!value1) {
                memory.enqueue(false);
            } else {
                enqueueVal2(val1, node);
            }
        } catch (StackException | HeapException | SymbolException e) {
            throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
        }
        adr++;
        return null;
    }

    @Override
    public Object visit(ASTJJCentier node, Object data) throws VisitorJJCException {
        return SORTE.INT;
    }

    @Override
    public Object visit(ASTJJCbooleen node, Object data)throws VisitorJJCException  {
        return SORTE.BOOL;
    }

    @Override
    public Object visit(ASTJJCvoidd node, Object data) throws VisitorJJCException {
        return SORTE.VOID;
    }

    @Override
    public Object visit(ASTJJCvar node, Object data) throws VisitorJJCException {
        return OBJ.VAR;
    }

    @Override
    public Object visit(ASTJJCmeth node, Object data) throws VisitorJJCException {
        return OBJ.METH;
    }

    @Override
    public Object visit(ASTJJCtab node, Object data) throws VisitorJJCException {
        return OBJ.TAB;
    }

    @Override
    public Object visit(ASTJJCcst node, Object data) throws VisitorJJCException {
        return OBJ.CST;
    }

    @Override
    public Object visit(ASTJJCvcst node, Object data) throws VisitorJJCException {
        return OBJ.VCST;
    }

    @Override
    public Object visit(ASTJJCjcchaine node, Object data)throws VisitorJJCException {
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTJJClength node, Object data) throws VisitorJJCException {
        debug(node);
        String ident = (String) ((ASTJJCjcident) node.jjtGetChild(0)).jjtGetValue();
		try {
			if (memory.getObj(ident) != OBJ.TAB) {
				throw new InterpreterException("cannot get size of a non array object.", node.getLine(), node.getColumn());
			}
			int id = (int) memory.getVal(ident);
            this.memory.enqueue(memory.getArraySize(id));
		} catch (SymbolException | HeapException e) {
			throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
		}
        adr++;
        return null;
    }
}

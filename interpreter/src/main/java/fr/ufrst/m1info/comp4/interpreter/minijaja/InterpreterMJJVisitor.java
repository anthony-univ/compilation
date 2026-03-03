package fr.ufrst.m1info.comp4.interpreter.minijaja;

import fr.ufrst.m1info.comp4.interpreter.debugger.Debugger;
import fr.ufrst.m1info.comp4.memory.*;
import fr.ufrst.m1info.comp4.parser.minijaja.*;

public class InterpreterMJJVisitor implements MinijajaVisitor {
	private Memory memory;
	private boolean hasReturned = false;
	private Debugger debugger;
	private String output = "";

	public void setDebugger(Debugger debugger) {
		this.debugger = debugger;
	}

	public void setMemory(Memory memory) {
		this.memory = memory;
	}

	public Memory getMemory() {
		return memory;
	}

	public boolean getHasReturned() {
		return hasReturned;
	}

	public void setHasReturned(boolean b) {
		hasReturned = b;
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
	public Object visit(SimpleNode node, Object data) throws VisitorMJJException {
		node.childrenAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTMJJclasse node, Object data) throws VisitorMJJException {
		debug(node);
		InterpreterMode mode = ((InterpreterMJJData) data).getMode();
		if (mode == InterpreterMode.DEFAULT) {
			String id = (String) ((ASTMJJident) node.jjtGetChild(0)).jjtGetValue();
			try {
				memory.enqueue(null);
				memory.declVar(id);
			} catch (SymbolException e) {
				throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
			}

			node.jjtGetChild(1).jjtAccept(this, data);
			node.jjtGetChild(2).jjtAccept(this, data);

			((InterpreterMJJData) data).setMode(InterpreterMode.DELETE);
			node.jjtGetChild(1).jjtAccept(this, data);

			try {
				debug(node);
				memory.removeDecl(id);
				debug(node);
				if (debugger != null) {
					debugger.stop();
				}
			} catch (SymbolException | StackException | HeapException e) {
				throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
			}
		}
		return null;
	}

	@Override
	public Object visit(ASTMJJident node, Object data) throws InterpreterException {
		try {
			Object value = memory.getVal((String) node.jjtGetValue());
			if (value == null) {
				throw new InterpreterException("variable is not initialized.", node.getLine(), node.getColumn());
			}
			return value;
		} catch (SymbolException e) {
			throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
		}
	}

	@Override
	public Object visit(ASTMJJdecls node, Object data) throws VisitorMJJException {
		return visitDeclsVars(node, data);
	}

	@Override
	public Object visit(ASTMJJvnil node, Object data) {
		return null;
	}

	private void remove(SimpleNode node) throws InterpreterException {
		String id = (String) ((ASTMJJident) node.jjtGetChild(1)).jjtGetValue();
		try {
			memory.removeDecl(id);
		} catch (StackException  | SymbolException | HeapException e) {
			throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
		}
	}

	@Override
	public Object visit(ASTMJJcst node, Object data) throws VisitorMJJException {
		debug(node);
		InterpreterMode mode = ((InterpreterMJJData) data).getMode();
		if (mode == InterpreterMode.DEFAULT) {
			((InterpreterMJJData) data).setMode(InterpreterMode.EVAL);
			Object value = node.jjtGetChild(2).jjtAccept(this, data);
			((InterpreterMJJData) data).setMode(InterpreterMode.DEFAULT);

			String id = (String) ((ASTMJJident) node.jjtGetChild(1)).jjtGetValue();

			try {
				memory.enqueue(value);
				memory.declCst(id);
			} catch (SymbolException e) {
				throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
			}
		} else if (mode == InterpreterMode.DELETE) {
			remove(node);
		}
		return null;
	}

	@Override
	public Object visit(ASTMJJtableau node, Object data) throws VisitorMJJException {
		debug(node);
		InterpreterMode mode = ((InterpreterMJJData) data).getMode();
		if (mode == InterpreterMode.DEFAULT) {
			String id = (String) ((ASTMJJident) node.jjtGetChild(1)).jjtGetValue();
			((InterpreterMJJData) data).setMode(InterpreterMode.EVAL);
			Object value = node.jjtGetChild(2).jjtAccept(this, data);
			((InterpreterMJJData) data).setMode(InterpreterMode.DEFAULT);

			try {
				memory.enqueue(value);
				memory.declTab(id);
			} catch (SymbolException | HeapException e) {
				throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
			}
		} else if (mode == InterpreterMode.DELETE) {
			remove(node);
		}
		return null;
	}

	@Override
	public Object visit(ASTMJJmethode node, Object data) throws VisitorMJJException {
		debug(node);
		InterpreterMode mode = ((InterpreterMJJData) data).getMode();
		if (mode == InterpreterMode.DEFAULT) {
			String id = (String) ((ASTMJJident) node.jjtGetChild(1)).jjtGetValue();

			try {
				memory.enqueue(node);
				memory.declMeth(id);
			} catch (SymbolException e) {
				throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
			}
		} else if (mode == InterpreterMode.DELETE) {
			remove(node);
		}
		return null;
	}

	@Override
	public Object visit(ASTMJJvar node, Object data) throws VisitorMJJException {
		debug(node);
		InterpreterMode mode = ((InterpreterMJJData) data).getMode();
		if (mode == InterpreterMode.DEFAULT) {
			((InterpreterMJJData) data).setMode(InterpreterMode.EVAL);
			Object value = node.jjtGetChild(2).jjtAccept(this, data);
			((InterpreterMJJData) data).setMode(InterpreterMode.DEFAULT);

			String id = (String) ((ASTMJJident) node.jjtGetChild(1)).jjtGetValue();

			try {
				memory.enqueue(value);
				memory.declVar(id);
			} catch (SymbolException e) {
				throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
			}
		} else if (mode == InterpreterMode.DELETE) {
			remove(node);
		}
		return null;
	}

	@Override
	public Object visit(ASTMJJvars node, Object data) throws VisitorMJJException {
		return visitDeclsVars(node, data);
	}

	@Override
	public Object visit(ASTMJJomega node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTMJJmain node, Object data) throws VisitorMJJException {
		debug(node);
		InterpreterMode mode = ((InterpreterMJJData) data).getMode();
		if (mode == InterpreterMode.DEFAULT) {
			node.jjtGetChild(0).jjtAccept(this, data);
			node.jjtGetChild(1).jjtAccept(this, data);

			((InterpreterMJJData) data).setMode(InterpreterMode.DELETE);
			node.jjtGetChild(0).jjtAccept(this, data);
			((InterpreterMJJData) data).setMode(InterpreterMode.DEFAULT);
		}
		return null;
	}

	@Override
	public Object visit(ASTMJJentetes node, Object data) throws VisitorMJJException {
		node.jjtGetChild(1).jjtAccept(this, data);
		node.jjtGetChild(0).jjtAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTMJJenil node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTMJJentete node, Object data) throws InterpreterException {
		String id = (String) ((ASTMJJident) node.jjtGetChild(1)).jjtGetValue();
		try {
			memory.removeDecl(id);
		} catch (Exception e) {
			throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
		}
		return null;
	}

	@Override
	public Object visit(ASTMJJinstrs node, Object data) throws VisitorMJJException {
		InterpreterMode mode = ((InterpreterMJJData) data).getMode();
		if (mode == InterpreterMode.DEFAULT) {
			node.jjtGetChild(0).jjtAccept(this, data);
			if (!hasReturned) {
				node.jjtGetChild(1).jjtAccept(this, data);
			}
		}
		return null;
	}

	@Override
	public Object visit(ASTMJJinil node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTMJJret node, Object data) throws VisitorMJJException {
		debug(node);
		InterpreterMode mode = ((InterpreterMJJData) data).getMode();
		if (mode == InterpreterMode.DEFAULT) {
			((InterpreterMJJData) data).setMode(InterpreterMode.EVAL);
			Object value = node.jjtGetChild(0).jjtAccept(this, data);
			try {
				memory.affectVal(memory.getNameClass(), value);
			} catch (StackException | SymbolException e) {
				throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
			}
			((InterpreterMJJData) data).setMode(InterpreterMode.DEFAULT);
			hasReturned = true;
		}
		return null;
	}

	@Override
	public Object visit(ASTMJJecrire node, Object data) throws VisitorMJJException {
		debug(node);
		InterpreterMode mode = ((InterpreterMJJData) data).getMode();
		if (mode == InterpreterMode.DEFAULT) {
			((InterpreterMJJData) data).setMode(InterpreterMode.EVAL);
			Object value = node.jjtGetChild(0).jjtAccept(this, data);
			output += value;
			((InterpreterMJJData) data).setMode(InterpreterMode.DEFAULT);
		}
		return null;
	}

	@Override
	public Object visit(ASTMJJecrireln node, Object data) throws VisitorMJJException {
		debug(node);
		InterpreterMode mode = ((InterpreterMJJData) data).getMode();
		if (mode == InterpreterMode.DEFAULT) {
			((InterpreterMJJData) data).setMode(InterpreterMode.EVAL);
			Object value = node.jjtGetChild(0).jjtAccept(this, data);
			output += value + "\n";
			((InterpreterMJJData) data).setMode(InterpreterMode.DEFAULT);
		}
		return null;
	}

	@Override
	public Object visit(ASTMJJsi node, Object data) throws VisitorMJJException {
		debug(node);
		InterpreterMode mode = ((InterpreterMJJData) data).getMode();
		if (mode == InterpreterMode.DEFAULT) {
			((InterpreterMJJData) data).setMode(InterpreterMode.EVAL);
			Object value = node.jjtGetChild(0).jjtAccept(this, data);

			((InterpreterMJJData) data).setMode(InterpreterMode.DEFAULT);
			if ((boolean) value) {
				node.jjtGetChild(1).jjtAccept(this, data);
			} else {
				node.jjtGetChild(2).jjtAccept(this, data);
			}
		}
		return null;
	}

	@Override
	public Object visit(ASTMJJtantque node, Object data) throws VisitorMJJException {
		debug(node);
		InterpreterMode mode = ((InterpreterMJJData) data).getMode();
		if (mode == InterpreterMode.DEFAULT) {
			((InterpreterMJJData) data).setMode(InterpreterMode.EVAL);
			Object value = node.jjtGetChild(0).jjtAccept(this, data);

			((InterpreterMJJData) data).setMode(InterpreterMode.DEFAULT);
			if ((boolean) value) {
				node.jjtGetChild(1).jjtAccept(this, data);
				visit(node, data);
			}
		}
		return null;
	}

	@Override
	public Object visit(ASTMJJchaine node, Object data) {
		return node.jjtGetValue();
	}

	@Override
	public Object visit(ASTMJJappelI node, Object data) throws VisitorMJJException {
		debug(node);
		InterpreterMode mode = ((InterpreterMJJData) data).getMode();
		if (mode == InterpreterMode.DEFAULT) {
			try {
				Object val = memory.getVal((String) ((ASTMJJident) node.jjtGetChild(0)).jjtGetValue());
				if (val == null) {
					throw new InterpreterException("function is not initialized.", node.getLine(), node.getColumn());
				}
				ASTMJJmethode meth = (ASTMJJmethode) val;
				// Parameters
				Node entetes = meth.jjtGetChild(2);
				Node listexp = node.jjtGetChild(1);
				while (!(entetes instanceof ASTMJJenil)) {
					Node entete = entetes.jjtGetChild(0);
					String id = (String) ((ASTMJJident) entete.jjtGetChild(1)).jjtGetValue();
					((InterpreterMJJData) data).setMode(InterpreterMode.EVAL);
					Object value = listexp.jjtGetChild(0).jjtAccept(this, data);
					((InterpreterMJJData) data).setMode(InterpreterMode.DEFAULT);

					memory.enqueue(value);
					memory.declVar(id);

					entetes = entetes.jjtGetChild(1);
					listexp = listexp.jjtGetChild(1);
				}
				// Declaration variables
				meth.jjtGetChild(3).jjtAccept(this, data);
				// Body
				meth.jjtGetChild(4).jjtAccept(this, data);
				// Remove decls
				((InterpreterMJJData) data).setMode(InterpreterMode.DELETE);
				meth.jjtGetChild(3).jjtAccept(this, data);
				meth.jjtGetChild(2).jjtAccept(this, data);
				((InterpreterMJJData) data).setMode(InterpreterMode.DEFAULT);
			} catch (SymbolException e) {
				throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
			}
			hasReturned = false;
		}
		return null;
	}

	@Override
	public Object visit(ASTMJJaffectation node, Object data) throws VisitorMJJException {
		debug(node);
		InterpreterMode mode = ((InterpreterMJJData) data).getMode();
		if (mode == InterpreterMode.DEFAULT) {
			((InterpreterMJJData) data).setMode(InterpreterMode.EVAL);
			Object value = node.jjtGetChild(1).jjtAccept(this, data);

			if (node.jjtGetChild(0) instanceof ASTMJJtab) {
				String id = (String) ((ASTMJJident) node.jjtGetChild(0).jjtGetChild(0)).jjtGetValue();
				Object ind = node.jjtGetChild(0).jjtGetChild(1).jjtAccept(this, data);
				try {
					memory.affectValT(id, value, (Integer) ind);
				} catch (StackException | SymbolException | HeapException e) {
					throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
				}
			} else {
				String id = (String) ((ASTMJJident) node.jjtGetChild(0)).jjtGetValue();
				try {
					if (memory.getObj(id) == OBJ.TAB) {
						memory.deallocateHeap((Integer) memory.getVal(id));
						memory.incrementReferenceHeap((Integer) value);
					}
					memory.affectVal(id, value);
				} catch (StackException | SymbolException | HeapException e) {
					throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
				}
			}
			((InterpreterMJJData) data).setMode(InterpreterMode.DEFAULT);
		}
		return null;
	}

	@Override
	public Object visit(ASTMJJsomme node, Object data) throws VisitorMJJException {
		debug(node);
		InterpreterMode mode = ((InterpreterMJJData) data).getMode();
		if (mode == InterpreterMode.DEFAULT) {
			((InterpreterMJJData) data).setMode(InterpreterMode.EVAL);
			int value = (int) node.jjtGetChild(1).jjtAccept(this, data);
			if (node.jjtGetChild(0) instanceof ASTMJJtab) {
				String id = (String) ((ASTMJJident) node.jjtGetChild(0).jjtGetChild(0)).jjtGetValue();
				Object ind = node.jjtGetChild(0).jjtGetChild(1).jjtAccept(this, data);
				try {
					value += (int) memory.getValT(id, (Integer) ind);
					memory.affectValT(id, value, (Integer) ind);
				} catch (StackException | SymbolException | HeapException e) {
					throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
				}
			} else {
				value += (int) node.jjtGetChild(0).jjtAccept(this, data);
				((InterpreterMJJData) data).setMode(InterpreterMode.DEFAULT);

				String id = (String) ((ASTMJJident) node.jjtGetChild(0)).jjtGetValue();
				try {
					memory.affectVal(id, value);
				} catch (StackException | SymbolException e) {
					throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
				}
			}
			((InterpreterMJJData) data).setMode(InterpreterMode.DEFAULT);
		}
		return null;
	}

	@Override
	public Object visit(ASTMJJincrement node, Object data) throws VisitorMJJException {
		debug(node);
		InterpreterMode mode = ((InterpreterMJJData) data).getMode();
		if (mode == InterpreterMode.DEFAULT) {
			if (node.jjtGetChild(0) instanceof  ASTMJJtab) {
				String id = (String) ((ASTMJJident) node.jjtGetChild(0).jjtGetChild(0)).jjtGetValue();
				Object ind = node.jjtGetChild(0).jjtGetChild(1).jjtAccept(this, data);
				try {
					int value = (int) memory.getValT(id, (Integer) ind) + 1;
					memory.affectValT(id, value, (Integer) ind);
				} catch (StackException | SymbolException | HeapException e) {
					throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
				}
			} else {
				((InterpreterMJJData) data).setMode(InterpreterMode.EVAL);
				int value = (int) node.jjtGetChild(0).jjtAccept(this, data) + 1;

				String id = (String) ((ASTMJJident) node.jjtGetChild(0)).jjtGetValue();
				try {
					memory.affectVal(id, value);
				} catch (StackException | SymbolException e) {
					throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
				}
			}
			((InterpreterMJJData) data).setMode(InterpreterMode.DEFAULT);
		}
		return null;
	}

	@Override
	public Object visit(ASTMJJtab node, Object data) throws VisitorMJJException {
		String id = (String) ((ASTMJJident) node.jjtGetChild(0)).jjtGetValue();
		Object ind = node.jjtGetChild(1).jjtAccept(this, data);
		try {
			return memory.getValT(id, (Integer) ind);
		} catch (SymbolException | HeapException e) {
			throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
		}
	}

	@Override
	public Object visit(ASTMJJlistexp node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTMJJexnil node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTMJJnon node, Object data) throws VisitorMJJException {
		return !(boolean) node.jjtGetChild(0).jjtAccept(this, data);
	}

	@Override
	public Object visit(ASTMJJet node, Object data) throws VisitorMJJException {
		boolean v1 = (boolean) node.jjtGetChild(0).jjtAccept(this, data);
		if (!v1) {
			return false;
		}
		return node.jjtGetChild(1).jjtAccept(this, data);
	}

	@Override
	public Object visit(ASTMJJou node, Object data) throws VisitorMJJException {
		boolean v1 = (boolean) node.jjtGetChild(0).jjtAccept(this, data);
		if (v1) {
			return true;
		}
		return node.jjtGetChild(1).jjtAccept(this, data);
	}

	@Override
	public Object visit(ASTMJJequal node, Object data) throws VisitorMJJException {
		Object v1 = node.jjtGetChild(0).jjtAccept(this, data);
		Object v2 = node.jjtGetChild(1).jjtAccept(this, data);
		return v1 == v2;
	}

	@Override
	public Object visit(ASTMJJsup node, Object data) throws VisitorMJJException {
		int v1 = (int) node.jjtGetChild(0).jjtAccept(this, data);
		int v2 = (int) node.jjtGetChild(1).jjtAccept(this, data);
		return v1 > v2;
	}

	@Override
	public Object visit(ASTMJJmoinsUnaire node, Object data) throws VisitorMJJException {
		return -(int) node.jjtGetChild(0).jjtAccept(this, data);

	}

	@Override
	public Object visit(ASTMJJplus node, Object data) throws VisitorMJJException {
		int v1 = (int) node.jjtGetChild(0).jjtAccept(this, data);
		int v2 = (int) node.jjtGetChild(1).jjtAccept(this, data);
		return v1 + v2;
	}

	@Override
	public Object visit(ASTMJJmoins node, Object data) throws VisitorMJJException {
		int v1 = (int) node.jjtGetChild(0).jjtAccept(this, data);
		int v2 = (int) node.jjtGetChild(1).jjtAccept(this, data);
		return v1 - v2;
	}

	@Override
	public Object visit(ASTMJJmult node, Object data) throws VisitorMJJException {
		int v1 = (int) node.jjtGetChild(0).jjtAccept(this, data);
		int v2 = (int) node.jjtGetChild(1).jjtAccept(this, data);
		return v1 * v2;
	}

	@Override
	public Object visit(ASTMJJdiv node, Object data) throws VisitorMJJException {
		int v1 = (int) node.jjtGetChild(0).jjtAccept(this, data);
		int v2 = (int) node.jjtGetChild(1).jjtAccept(this, data);
		if (v2 == 0) {
			throw new InterpreterException("division by zero.", node.getLine(), node.getColumn());
		}
		return v1 / v2;
	}

	@Override
	public Object visit(ASTMJJlongueur node, Object data) throws VisitorMJJException {
		String ident = (String) ((ASTMJJident) node.jjtGetChild(0)).jjtGetValue();
		try {
			if (memory.getObj(ident) != OBJ.TAB) {
				throw new InterpreterException("cannot get size of a non array object.", node.getLine(), node.getColumn());
			}
			int id = (int) memory.getVal(ident);
			return memory.getArraySize(id);
		} catch (SymbolException | HeapException e) {
			throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
		}
	}

	@Override
	public Object visit(ASTMJJvrai node, Object data) {
		return true;
	}

	@Override
	public Object visit(ASTMJJfaux node, Object data) {
		return false;
	}

	@Override
	public Object visit(ASTMJJnbre node, Object data) {
		return node.jjtGetValue();
	}

	@Override
	public Object visit(ASTMJJappelE node, Object data) throws VisitorMJJException {
		ASTMJJappelI appelI = new ASTMJJappelI(MinijajaTreeConstants.JJTAPPELI);
		appelI.setLine(node.getLine());
		appelI.setColumn(node.getColumn());
		appelI.jjtAddChild(node.jjtGetChild(0), 0);
		appelI.jjtAddChild(node.jjtGetChild(1), 1);

		((InterpreterMJJData) data).setMode(InterpreterMode.DEFAULT);
		appelI.jjtAccept(this, data);
		((InterpreterMJJData) data).setMode(InterpreterMode.EVAL);
		try {
			Object value = memory.getValClass();
			if (value == null) {
				throw new InterpreterException("return value is null.", node.getLine(), node.getColumn());
			}
			return value;
		} catch (StackException e) {
			throw new InterpreterException(e.getMessage(), node.getLine(), node.getColumn());
		}
	}

	@Override
	public Object visit(ASTMJJrien node, Object data) {
		return SORTE.VOID;
	}

	@Override
	public Object visit(ASTMJJentier node, Object data) {
		return SORTE.INT;
	}

	@Override
	public Object visit(ASTMJJbooleen node, Object data) {
		return SORTE.BOOL;
	}

	private Object visitDeclsVars(Node node, Object data) throws VisitorMJJException {
		InterpreterMode mode = ((InterpreterMJJData) data).getMode();
		if (mode == InterpreterMode.DEFAULT) {
			node.jjtGetChild(0).jjtAccept(this, data);
			node.jjtGetChild(1).jjtAccept(this, data);
		} else if (mode == InterpreterMode.DELETE) {
			node.jjtGetChild(1).jjtAccept(this, data);
			node.jjtGetChild(0).jjtAccept(this, data);
		}
		return null;
	}
}

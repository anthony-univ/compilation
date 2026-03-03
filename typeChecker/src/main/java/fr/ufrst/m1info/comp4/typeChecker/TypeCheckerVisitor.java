package fr.ufrst.m1info.comp4.typeChecker;

import fr.ufrst.m1info.comp4.memory.*;
import fr.ufrst.m1info.comp4.parser.minijaja.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TypeCheckerVisitor implements MinijajaVisitor {
	private SymbolTable table;
	private Set<ErrorType> errors;
	private final static String msgPrevDecl = "previous declaration of '%s'";
	private final static String scopeClass = "@class";
	private final static String msgInit = "initialization of '%s' with an expression of type '%s' is denied";

	public void setTable(SymbolTable table) {
		this.table = table;
	}
	public SymbolTable getTable() {
		return this.table;
	}
	public void setErrors(TreeSet<ErrorType> errors) {this.errors = errors;}
	public Set<ErrorType> getErrors() {return this.errors;}

	@Override
	public Object visit(SimpleNode node, Object data) throws VisitorMJJException {
		node.childrenAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTMJJclasse node, Object data) throws VisitorMJJException {
		Mode mode = ((TypeCheckerData) data).getMode();
		String scope = ((TypeCheckerData) data).getScope();
		if (mode == Mode.FIRST_PASS) {
			String newName =  ((ASTMJJident)node.jjtGetChild(0)).jjtGetValue() + "@" + scope;
			try {
				((ASTMJJident) node.jjtGetChild(0)).jjtSetValue(newName);
				this.table.addIdent(newName, null, OBJ.VAR);
			} catch (SymbolException e) {
				//never happen
				this.errors.add(new ErrorType(String.format(msgPrevDecl, deleteScope(newName)), node.getLine(), node.getColumn()));
			}
		}
		((TypeCheckerData) data).setScope("class");
		node.jjtGetChild(1).jjtAccept(this, data);
		node.jjtGetChild(2).jjtAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTMJJident node, Object data) throws VisitorMJJException {
		String scope = 	((TypeCheckerData) data).getScope();
		String name = (String) node.jjtGetValue();

        InfoIdent q;
        if(!name.contains("@")) {
            q = getQ(name + "@" + scope);
			if (q != null) {
				node.jjtSetValue(name + "@" + scope);
				return q.getSorte();
			}
			q = getQ(name + scopeClass);
			if (q != null) {
				node.jjtSetValue(name + scopeClass);
				return q.getSorte();
			}
        }else {
            q = getQ(name);
			if (q != null) {
				return q.getSorte();
			}
        }
        this.errors.add(new ErrorType(String.format("'%s' undeclared (first use in this function)", name), node.getLine(), node.getColumn()));
        return SORTE.ANY;
	}

	@Override
	public Object visit(ASTMJJdecls node, Object data) throws VisitorMJJException {
		node.jjtGetChild(0).jjtAccept(this, data);
		node.jjtGetChild(1).jjtAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTMJJvnil node, Object data) throws VisitorMJJException {return null;}

	@Override
	public Object visit(ASTMJJcst node, Object data) throws VisitorMJJException {
		Mode mode = ((TypeCheckerData) data).getMode();
		String scope = 	((TypeCheckerData) data).getScope();
		String name = (String) ((ASTMJJident)node.jjtGetChild(1)).jjtGetValue();
		SORTE type = (SORTE) node.jjtGetChild(0).jjtAccept(this, data);
		SORTE typeVexp = (SORTE) node.jjtGetChild(2).jjtAccept(this, data);

		OBJ obj = OBJ.CST;
		if (node.jjtGetChild(2) instanceof ASTMJJomega) {
			obj = OBJ.VCST;
		}

		if (mode == Mode.FIRST_PASS) {
			String newName = name + "@" + scope;
			try {
				((ASTMJJident)node.jjtGetChild(1)).jjtSetValue(newName);
				this.table.addIdent(newName, type, obj);
			}catch (SymbolException e) {
				this.errors.add(new ErrorType(String.format(msgPrevDecl, deleteScope(newName)), node.getLine(), node.getColumn()));
			}

		}

		if (mode == Mode.SECOND_PASS) {
			if (typeVexp==SORTE.ANY) { return null; }

			if (type != typeVexp && !(node.jjtGetChild(2) instanceof ASTMJJomega)) {
				this.errors.add(new ErrorType(String.format(msgInit, deleteScope(name), typeVexp), node.getLine(), node.getColumn()));
				return null;
			}

			if (node.jjtGetChild(2) instanceof ASTMJJident) {
				tabAuthorized((String) ((ASTMJJident)node.jjtGetChild(2)).jjtGetValue(), node, false);
			}
		}

		return null;
	}

	@Override
	public Object visit(ASTMJJtableau node, Object data) throws VisitorMJJException {
		Mode mode = ((TypeCheckerData) data).getMode();
		String scope = 	((TypeCheckerData) data).getScope();
		String name = (String) ((ASTMJJident)node.jjtGetChild(1)).jjtGetValue();
		SORTE type = (SORTE) node.jjtGetChild(0).jjtAccept(this, data);
		SORTE typeExp = (SORTE) node.jjtGetChild(2).jjtAccept(this, data);

		if (mode == Mode.FIRST_PASS) {
			String newName = name + "@" + scope;
			try {
				((ASTMJJident)node.jjtGetChild(1)).jjtSetValue(newName);
				this.table.addIdent(newName, type, OBJ.TAB);
			}catch (SymbolException e) {
				this.errors.add(new ErrorType(String.format(msgPrevDecl, deleteScope(newName)), node.getLine(), node.getColumn()));
			}
		}

		if (mode == Mode.SECOND_PASS) {
			if (typeExp==SORTE.ANY) { return null; }

			if (type == SORTE.VOID) {
				this.errors.add(new ErrorType(String.format("variable '%s' cannot be of type 'void'", deleteScope(name)), node.getLine(), node.getColumn()));
			}

			if (typeExp != SORTE.INT) {
				this.errors.add(new ErrorType(String.format(msgInit, deleteScope(name), typeExp), node.getLine(), node.getColumn()));
			}

			if (node.jjtGetChild(2) instanceof ASTMJJident) {
				tabAuthorized((String) ((ASTMJJident)node.jjtGetChild(2)).jjtGetValue(), node, false);
			}
		}

		return null;
	}

	@Override
	public Object visit(ASTMJJmethode node, Object data) throws VisitorMJJException {
		Mode mode = ((TypeCheckerData) data).getMode();
		SORTE type = (SORTE) node.jjtGetChild(0).jjtAccept(this, data);
		String name = (String) ((ASTMJJident)node.jjtGetChild(1)).jjtGetValue();
		String typeParams = getTypeParams(node.jjtGetChild(2), data);

		if (mode == Mode.FIRST_PASS) {
			String newScope = name + "->" + typeParams;
			((TypeCheckerData) data).setScope(newScope);
			String newName = newScope + scopeClass;
			try {
				((ASTMJJident)node.jjtGetChild(1)).jjtSetValue(newName);
				this.table.addIdent(newName, type, OBJ.METH);
			}catch (SymbolException e) {
				if (typeParams.equals("none")) {
					this.errors.add(new ErrorType(String.format("previous declaration of function '%s' without parameters", name), node.getLine(), node.getColumn()));
				}else {
					this.errors.add(new ErrorType(String.format("previous declaration of function '%s' with parameters (%s)", name, typeParams.replace("&", ",")), node.getLine(), node.getColumn()));
				}
				return null;
			}

			node.jjtGetChild(2).jjtAccept(this, data); // node entetes
			node.jjtGetChild(3).jjtAccept(this, data); // node vars
		}

		if(mode == Mode.SECOND_PASS) {
			((TypeCheckerData) data).setScope(deleteScope(name));
			node.jjtGetChild(3).jjtAccept(this, data); // node vars
			node.jjtGetChild(4).jjtAccept(this, data); // node instrs

			boolean missingReturn = getMissingReturn(node.jjtGetChild(4));

			if (type != SORTE.VOID && !missingReturn) {
				this.errors.add(new ErrorType(String.format("missing return '%s' in the function", type), node.getLine(), node.getColumn()));
			}
		}

		((TypeCheckerData) data).setScope("class");
		return null;
	}

	@Override
	public Object visit(ASTMJJvar node, Object data) throws VisitorMJJException {
		Mode mode = ((TypeCheckerData) data).getMode();
		String name = (String) ((ASTMJJident)node.jjtGetChild(1)).jjtGetValue();
		String scope = 	((TypeCheckerData) data).getScope();
		SORTE type = (SORTE) node.jjtGetChild(0).jjtAccept(this, data);
		SORTE typeVexp = (SORTE) node.jjtGetChild(2).jjtAccept(this, data);

		if (mode == Mode.FIRST_PASS) {
			String newName = name + "@" + scope;
			try {
				((ASTMJJident)node.jjtGetChild(1)).jjtSetValue(newName);
				this.table.addIdent(newName, type, OBJ.VAR);
			}catch (SymbolException e) {
				this.errors.add(new ErrorType(String.format(msgPrevDecl, deleteScope(newName)), node.getLine(), node.getColumn()));
			}
		}

		if(mode == Mode.SECOND_PASS) {
			if (typeVexp==SORTE.ANY) { return null; }

			if (type == SORTE.VOID) {
				this.errors.add(new ErrorType(String.format("variable '%s' cannot be of type 'void'", deleteScope(name)), node.getLine(), node.getColumn()));
			}

			if (typeVexp != type && !(node.jjtGetChild(2) instanceof ASTMJJomega)) {
				this.errors.add(new ErrorType(String.format(msgInit, deleteScope(name), typeVexp), node.getLine(), node.getColumn()));
				return null;
			}

			if (node.jjtGetChild(2) instanceof ASTMJJident) {
				tabAuthorized((String) ((ASTMJJident)node.jjtGetChild(2)).jjtGetValue(), node, false);
			}
		}

		return null;
	}

	@Override
	public Object visit(ASTMJJvars node, Object data) throws VisitorMJJException {
		node.jjtGetChild(0).jjtAccept(this, data);
		node.jjtGetChild(1).jjtAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTMJJomega node, Object data) {return null;}

	@Override
	public Object visit(ASTMJJmain node, Object data) throws VisitorMJJException {
		((TypeCheckerData) data).setScope("main");
		node.jjtGetChild(0).jjtAccept(this, data);
		node.jjtGetChild(1).jjtAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTMJJentetes node, Object data) throws VisitorMJJException {
		node.jjtGetChild(0).jjtAccept(this, data);
		node.jjtGetChild(1).jjtAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTMJJenil node, Object data) throws VisitorMJJException{return null;}

	@Override
	public Object visit(ASTMJJentete node, Object data) throws VisitorMJJException {
		Mode mode = ((TypeCheckerData) data).getMode();
		String name = (String) ((ASTMJJident)node.jjtGetChild(1)).jjtGetValue();
		String scope = 	((TypeCheckerData) data).getScope();

		SORTE type = (SORTE) node.jjtGetChild(0).jjtAccept(this, data);

		if (mode == Mode.FIRST_PASS) {
			String newName = name + "@" + scope;
			try {
				((ASTMJJident)node.jjtGetChild(1)).jjtSetValue(newName);
				this.table.addIdent(newName, type, OBJ.VAR);
			}catch (SymbolException e) {
				this.errors.add(new ErrorType(String.format(msgPrevDecl, deleteScope(newName)), node.getLine(), node.getColumn()));
			}
		}

		return type;
	}

	@Override
	public Object visit(ASTMJJinstrs node, Object data) throws VisitorMJJException {
		Mode mode = ((TypeCheckerData) data).getMode();

		if (mode == Mode.SECOND_PASS) {
			node.jjtGetChild(0).jjtAccept(this, data);
			node.jjtGetChild(1).jjtAccept(this, data);
		}

		return null;
	}

	@Override
	public Object visit(ASTMJJinil node, Object data) throws VisitorMJJException {return null;}

	@Override
	public Object visit(ASTMJJret node, Object data) throws VisitorMJJException {
		String scope = 	((TypeCheckerData) data).getScope();
		SORTE typeExp = (SORTE) node.jjtGetChild(0).jjtAccept(this, data);
		if (scope.equals("main")) {
			this.errors.add(new ErrorType("return in main is denied", node.getLine(), node.getColumn()));
		}

		InfoIdent q = getQ(scope + scopeClass);
		if (q == null) {
			return SORTE.ANY;
		}

		SORTE type = q.getSorte();

		if (type == SORTE.VOID) {
			this.errors.add(new ErrorType("return in a function returning 'void' is denied", node.getLine(), node.getColumn()));
		}

		if (type != typeExp) {
			this.errors.add(new ErrorType(String.format("return '%s' in a function which return '%s'", typeExp, type), node.getLine(), node.getColumn()));
		}


		if (node.jjtGetChild(0) instanceof ASTMJJident) {
			tabAuthorized((String) ((ASTMJJident)node.jjtGetChild(0)).jjtGetValue(), node, false);
		}

		return typeExp;
	}

	@Override
	public Object visit(ASTMJJecrire node, Object data) throws VisitorMJJException {
		node.jjtGetChild(0).jjtAccept(this, data);

		if (node.jjtGetChild(0) instanceof ASTMJJident) {
			tabAuthorized((String) ((ASTMJJident)node.jjtGetChild(0)).jjtGetValue(), node, false);
		}

		return null;
	}

	@Override
	public Object visit(ASTMJJecrireln node, Object data) throws VisitorMJJException {
		node.jjtGetChild(0).jjtAccept(this, data);

		if (node.jjtGetChild(0) instanceof ASTMJJident) {
			tabAuthorized((String) ((ASTMJJident)node.jjtGetChild(0)).jjtGetValue(), node, false);
		}

		return null;
	}

	@Override
	public Object visit(ASTMJJsi node, Object data) throws VisitorMJJException {
		SORTE type = (SORTE) node.jjtGetChild(0).jjtAccept(this, data);

		if(type==SORTE.ANY) { return null; }

		if (node.jjtGetChild(0) instanceof ASTMJJident) {
			tabAuthorized((String) ((ASTMJJident)node.jjtGetChild(0)).jjtGetValue(), node, false);
		}

		if (type != SORTE.BOOL) {
			this.errors.add(new ErrorType("condition of if must be a type 'bool'", node.getLine(), node.getColumn()));
		}

		node.jjtGetChild(1).jjtAccept(this, data);

		if (node.jjtGetChild(2) instanceof ASTMJJinstrs) { // else
			node.jjtGetChild(2).jjtAccept(this, data);
		}

		return null;
	}

	@Override
	public Object visit(ASTMJJtantque node, Object data) throws VisitorMJJException {
		SORTE type = (SORTE) node.jjtGetChild(0).jjtAccept(this, data);

		if(type==SORTE.ANY) { return null; }

		if (node.jjtGetChild(0) instanceof ASTMJJident) {
			tabAuthorized((String) ((ASTMJJident)node.jjtGetChild(0)).jjtGetValue(), node, false);
		}

		if (type != SORTE.BOOL) {
			this.errors.add(new ErrorType("condition of while must be a type 'bool'", node.getLine(), node.getColumn()));
		}

		node.jjtGetChild(1).jjtAccept(	this, data);
		return null;
	}

	@Override
	public Object visit(ASTMJJchaine node, Object data) throws VisitorMJJException {return null;}

	@Override
	public Object visit(ASTMJJappelI node, Object data) throws VisitorMJJException {
		return appelFct(node, data);
	}

	private Object returnType(SimpleNode node, SORTE typeLeft, SORTE typeRight, String s) {
		if(typeLeft==SORTE.ANY || typeRight==SORTE.ANY) {
			return SORTE.ANY;
		}

		if (node.jjtGetChild(0) instanceof ASTMJJident) {
			InfoIdent qLeft = getQ((String) ((ASTMJJident) node.jjtGetChild(0)).jjtGetValue());
			if (qLeft != null && qLeft.getObj() == OBJ.TAB) {
				if (node.jjtGetChild(1) instanceof ASTMJJident) {
					InfoIdent qRight = getQ((String) ((ASTMJJident) node.jjtGetChild(1)).jjtGetValue());
					if (qRight != null && qRight.getObj() != OBJ.TAB) {
						this.errors.add(new ErrorType(String.format(s + " between an array and '%s'", qRight.getSorte()), node.getLine(), node.getColumn()));
						return typeLeft;
					}
				} else {
					this.errors.add(new ErrorType(s + " between an non-array and a array is denied", node.getLine(), node.getColumn()));
					return typeLeft;
				}
			}
		}
		return null;
	}

	@Override
	public Object visit(ASTMJJaffectation node, Object data) throws VisitorMJJException {
		SORTE typeLeft = (SORTE) node.jjtGetChild(0).jjtAccept(this, data);
		SORTE typeExp = (SORTE) node.jjtGetChild(1).jjtAccept(this, data);

		Object ret = returnType(node, typeLeft, typeExp, "affectation");
		if (ret != null) {
			return ret;
		}

		if (node.jjtGetChild(1) instanceof ASTMJJident) {
			InfoIdent qRight = getQ((String) ((ASTMJJident)node.jjtGetChild(1)).jjtGetValue());
			if (qRight != null && qRight.getObj()==OBJ.TAB) {
				if (node.jjtGetChild(0) instanceof ASTMJJident) {
					InfoIdent qLeft = getQ((String) ((ASTMJJident)node.jjtGetChild(0)).jjtGetValue());
					if (qLeft != null && qLeft.getObj()!=OBJ.TAB) {
						this.errors.add(new ErrorType(String.format("affectation between an array and '%s'", qLeft.getSorte()), node.getLine(), node.getColumn()));
						return typeLeft;
					}
				}else {
					this.errors.add(new ErrorType("affectation between an array and a non-array is denied", node.getLine(), node.getColumn()));
					return typeLeft;
				}
			}
		}

		if (typeLeft != typeExp) {
			this.errors.add(new ErrorType(String.format("affectation of '%s' in '%s' is denied", typeExp, typeLeft), node.getLine(), node.getColumn()));
		}

		return typeLeft;
	}

	@Override
	public Object visit(ASTMJJsomme node, Object data) throws VisitorMJJException {
		opBinaire(node, data);
		return SORTE.INT;
	}

	@Override
	public Object visit(ASTMJJincrement node, Object data) throws VisitorMJJException {
		SORTE type = (SORTE) node.jjtGetChild(0).jjtAccept(this, data);

		if(type==SORTE.ANY) { return SORTE.ANY; }

		if (node.jjtGetChild(0) instanceof ASTMJJident) {
			tabAuthorized((String) ((ASTMJJident)node.jjtGetChild(0)).jjtGetValue(), node, false);
		}

		if (type != SORTE.INT) {
			this.errors.add(new ErrorType(String.format("incrementation of type '%s' is denied", type), node.getLine(), node.getColumn()));
		}

		return SORTE.INT;
	}

	@Override
	public Object visit(ASTMJJtab node, Object data) throws VisitorMJJException {
		SORTE typeTab = (SORTE) node.jjtGetChild(0).jjtAccept(this, data);
		SORTE typeExp = (SORTE) node.jjtGetChild(1).jjtAccept(this, data);

		if(typeTab==SORTE.ANY || typeExp==SORTE.ANY) { return SORTE.ANY; }

		tabAuthorized((String) ((ASTMJJident)node.jjtGetChild(0)).jjtGetValue(), node, true);

		if (typeExp != SORTE.INT) {
			this.errors.add(new ErrorType("the array index must be a type 'int'", node.getLine(), node.getColumn()));
		}

		return typeTab;
	}

	@Override
	public Object visit(ASTMJJlistexp node, Object data) throws VisitorMJJException {
		node.jjtGetChild(0).jjtAccept(this, data);
		node.jjtGetChild(1).jjtAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTMJJexnil node, Object data) throws VisitorMJJException {return null;}

	@Override
	public Object visit(ASTMJJnon node, Object data) throws VisitorMJJException {
		SORTE type = (SORTE) node.jjtGetChild(0).jjtAccept(this, data);

		if(type==SORTE.ANY) { return SORTE.ANY; }

		if (node.jjtGetChild(0) instanceof ASTMJJident) {
			tabAuthorized((String) ((ASTMJJident)node.jjtGetChild(0)).jjtGetValue(), node, false);
		}

		if (type != SORTE.BOOL) {
			this.errors.add(new ErrorType("the expression must be a type 'bool'", node.getLine(), node.getColumn()));
		}

		return SORTE.BOOL;
	}

	@Override
	public Object visit(ASTMJJet node, Object data) throws VisitorMJJException {
		etOu(node, data);
		return SORTE.BOOL;
	}

	@Override
	public Object visit(ASTMJJou node, Object data) throws VisitorMJJException {
		etOu(node, data);
		return SORTE.BOOL;
	}

	@Override
	public Object visit(ASTMJJequal node, Object data) throws VisitorMJJException {
		SORTE typeLeft = (SORTE) node.jjtGetChild(0).jjtAccept(this, data);
		SORTE typeRight = (SORTE) node.jjtGetChild(1).jjtAccept(this, data);

		Object ret = returnType(node, typeLeft, typeRight, "equal");
		if (ret != null) {
			return ret;
		}

		if (typeLeft != typeRight) {
			this.errors.add(new ErrorType("the expression must be the same type", node.getLine(), node.getColumn()));
		}

		return SORTE.BOOL;
	}

	@Override
	public Object visit(ASTMJJsup node, Object data) throws VisitorMJJException {
		opBinaire(node, data);
		return SORTE.BOOL;
	}

	@Override
	public Object visit(ASTMJJmoinsUnaire node, Object data) throws VisitorMJJException {
		SORTE type = (SORTE) node.jjtGetChild(0).jjtAccept(this, data);

		if(type==SORTE.ANY) { return SORTE.ANY; }

		if (node.jjtGetChild(0) instanceof ASTMJJident) {
			tabAuthorized((String) ((ASTMJJident)node.jjtGetChild(0)).jjtGetValue(), node, false);
		}

		if (type != SORTE.INT) {
			this.errors.add(new ErrorType("the expression must be a type 'int'", node.getLine(), node.getColumn()));
		}

		return SORTE.INT;
	}

	@Override
	public Object visit(ASTMJJplus node, Object data) throws VisitorMJJException {
		opBinaire(node, data);
		return SORTE.INT;
	}

	@Override
	public Object visit(ASTMJJmoins node, Object data) throws VisitorMJJException {
		opBinaire(node, data);
		return SORTE.INT;
	}

	@Override
	public Object visit(ASTMJJmult node, Object data) throws VisitorMJJException {
		opBinaire(node, data);
		return SORTE.INT;
	}

	@Override
	public Object visit(ASTMJJdiv node, Object data) throws VisitorMJJException {
		opBinaire(node, data);
		return SORTE.INT;
	}

	@Override
	public Object visit(ASTMJJlongueur node, Object data) throws VisitorMJJException {
		SORTE type = (SORTE) node.jjtGetChild(0).jjtAccept(this, data);

		if(type==SORTE.ANY) { return SORTE.ANY; }
		tabAuthorized((String) ((ASTMJJident)node.jjtGetChild(0)).jjtGetValue(), node, true);

		return SORTE.INT;
	}

	@Override
	public Object visit(ASTMJJvrai node, Object data) throws VisitorMJJException {return SORTE.BOOL;}

	@Override
	public Object visit(ASTMJJfaux node, Object data) throws VisitorMJJException {return SORTE.BOOL;}

	@Override
	public Object visit(ASTMJJnbre node, Object data) {return SORTE.INT;}

	@Override
	public Object visit(ASTMJJappelE node, Object data) throws VisitorMJJException {
		return appelFct(node, data);
	}

	@Override
	public Object visit(ASTMJJrien node, Object data) throws VisitorMJJException{return SORTE.VOID;}

	@Override
	public Object visit(ASTMJJentier node, Object data) throws VisitorMJJException{return SORTE.INT;}

	@Override
	public Object visit(ASTMJJbooleen node, Object data) throws VisitorMJJException{return SORTE.BOOL;}

	private String getTypeParams(Node node, Object data) throws VisitorMJJException {
		List<String> typesParam = new ArrayList<>();
		while (!(node instanceof ASTMJJenil)) {
			SORTE type = (SORTE) node.jjtGetChild(0).jjtGetChild(0).jjtAccept(this, data);
			typesParam.add(String.valueOf(type).toLowerCase());
			node = node.jjtGetChild(1);
		}
		String typeParams = String.join("&", typesParam);
		if (typeParams.isEmpty()) {
			typeParams = "none";
		}
		return typeParams;
	}

	private String getTypeParamsAppel(Node node, Object data) throws VisitorMJJException {
		List<String> typesParam = new ArrayList<>();
		while (!(node instanceof ASTMJJexnil)) {
			SORTE type = (SORTE) node.jjtGetChild(0).jjtAccept(this, data);
			if (node.jjtGetChild(0) instanceof ASTMJJident && !tabAuthorized((String) ((ASTMJJident)node.jjtGetChild(0)).jjtGetValue(), node, false)) {
				// has a array in params
				return null;
			}
			typesParam.add(String.valueOf(type).toLowerCase());
			node = node.jjtGetChild(1);
		}
		String typeParams = String.join("&", typesParam);
		if (typeParams.isEmpty()) {
			typeParams = "none";
		}
		return typeParams;
	}

	private String deleteScope(String scope) {
		int index = scope.lastIndexOf("@");
		if (index == -1) {
			return scope;
		}
		return scope.substring(0, scope.lastIndexOf("@"));
	}

	public String deleteParamScope(String scope) {
	int index = scope.lastIndexOf("->");
	if (index == -1) {
		return scope;
	}
		return scope.substring(0, scope.lastIndexOf("->"));
	}

	private boolean getMissingReturn(Node node) {
		boolean hasReturn = false;
		while (node instanceof ASTMJJinstrs) {
			if (node.jjtGetChild(0) instanceof ASTMJJret) {
				hasReturn = true;
			}

			if (node.jjtGetChild(0) instanceof ASTMJJsi) {
				hasReturn = getMissingReturn(node.jjtGetChild(0).jjtGetChild(1)) && getMissingReturn(node.jjtGetChild(0).jjtGetChild(2));
			}

			node = node.jjtGetChild(1);
		}

		return hasReturn;
	}

	private Boolean tabAuthorized(String name ,Node node, boolean authorized) {
		InfoIdent qVexp = getQ(name);
		if (qVexp == null) {
			return false;
		}

		if(!authorized && qVexp.getObj() == OBJ.TAB) {
			this.errors.add(new ErrorType("use a array here is denied", node.getLine(), node.getColumn()));
			return false;
		}

		if(authorized && qVexp.getObj() != OBJ.TAB) {
			this.errors.add(new ErrorType("use a variable  here is denied", node.getLine(), node.getColumn()));
			return false;
		}
		return true;
	}

	private Object appelFct(Node node, Object data) throws VisitorMJJException {
		String name = (String) ((ASTMJJident)node.jjtGetChild(0)).jjtGetValue();

		// search type params and set value of ident
		String typeParams = getTypeParamsAppel(node.jjtGetChild(1), data);

		if (typeParams == null) {
			return SORTE.ANY;
		}

		String newName =  name;
		if(!name.contains("@")) {
			newName =  name + "->" + typeParams + scopeClass;
			((ASTMJJident) node.jjtGetChild(0)).jjtSetValue(newName);
		}

		InfoIdent q = getQ(newName);
		if (q == null) {
			if (typeParams.equals("none")) {
				this.errors.add(new ErrorType(String.format("undeclared declaration of function '%s' without parameters", deleteParamScope(newName)), node.getLine(), node.getColumn()));
			}else {
				this.errors.add(new ErrorType(String.format("undeclared declaration of function '%s' with parameters (%s)", deleteParamScope(newName), typeParams.replace("&", ",")), node.getLine(), node.getColumn()));
			}
			return SORTE.ANY;
		}

		return q.getSorte();
	}

	public void etOu(Node node, Object data) throws VisitorMJJException {
		SORTE typeLeft = (SORTE) node.jjtGetChild(0).jjtAccept(this, data);
		SORTE typeRight = (SORTE) node.jjtGetChild(1).jjtAccept(this, data);

		if(typeLeft==SORTE.ANY || typeRight==SORTE.ANY) { return; }

		if (node.jjtGetChild(0) instanceof ASTMJJident) {
			tabAuthorized((String) ((ASTMJJident)node.jjtGetChild(0)).jjtGetValue(), node, false);
		}
		if (node.jjtGetChild(1) instanceof ASTMJJident) {
			tabAuthorized((String) ((ASTMJJident)node.jjtGetChild(1)).jjtGetValue(), node, false);
		}

		if (typeLeft != SORTE.BOOL || typeRight != SORTE.BOOL) {
			this.errors.add(new ErrorType("the expression must be a type 'bool'", node.getLine(), node.getColumn()));
		}
	}

	public void opBinaire(Node node, Object data) throws VisitorMJJException {
		SORTE typeLeft = (SORTE) node.jjtGetChild(0).jjtAccept(this, data);
		SORTE typeRight = (SORTE) node.jjtGetChild(1).jjtAccept(this, data);

		if(typeLeft==SORTE.ANY || typeRight==SORTE.ANY) { return; }

		if (node.jjtGetChild(0) instanceof ASTMJJident) {
			tabAuthorized((String) ((ASTMJJident)node.jjtGetChild(0)).jjtGetValue(), node, false);
		}
		if (node.jjtGetChild(1) instanceof ASTMJJident) {
			tabAuthorized((String) ((ASTMJJident)node.jjtGetChild(1)).jjtGetValue(), node, false);
		}

		if (typeLeft != SORTE.INT || typeRight != SORTE.INT) {
			this.errors.add(new ErrorType("the expression must be a type 'int'", node.getLine(), node.getColumn()));
		}
	}

	public InfoIdent getQ(String name) {
		try {
			return this.table.getInfoIdent(name);
		} catch (SymbolException e) {
            return null;
        }
	}
}
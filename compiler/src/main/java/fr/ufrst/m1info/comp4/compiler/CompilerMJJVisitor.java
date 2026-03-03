package fr.ufrst.m1info.comp4.compiler;

import fr.ufrst.m1info.comp4.parser.minijaja.*;

import java.util.ArrayList;
import java.util.List;


public class CompilerMJJVisitor implements MinijajaVisitor {

    private int nbEntete = 0;
    List<Integer> nops = new ArrayList<>();

    private final ArrayList<String> instrs = new ArrayList<>();

    private static final String STRPOP = " pop;\n";
    private static final String STRNEW = " new(";
    private static final String STRSWAP = " swap;\n";
    private static final String STRGOTO = " goto(";

    public String getInstrs() {
        return String.join("", this.instrs);
    }

    public ArrayList<String> getInstrsArray() {
        return this.instrs;
    }

    @Override
    public Object visit(SimpleNode node, Object data) throws VisitorMJJException {
        return 0;
    }

    @Override
    public Object visit(ASTMJJclasse node, Object data) throws VisitorMJJException {
        int n = ((CompilerMJJData) data).getAddress();

        this.instrs.add(n + " init;\n");

        int ndss = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerMJJData(n+1, Mode.ADD));
        int nmma = (int) node.jjtGetChild(2).jjtAccept(this, new CompilerMJJData(n+ndss+1, Mode.ADD));
        int nrdss = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerMJJData(n+ndss+nmma+1, Mode.REMOVE));

        this.instrs.add((n+ndss+nmma+nrdss+1) + STRPOP);
        this.instrs.add((n+ndss+nmma+nrdss+2) + " jcstop;\n");

        return ndss+nmma+nrdss+3;
    }

    @Override
    public Object visit(ASTMJJident node, Object data) throws VisitorMJJException {
        int n = ((CompilerMJJData) data).getAddress();

        this.instrs.add(n + " load(" + node.jjtGetValue() + ");\n");
        return 1;
    }

    @Override
    public Object visit(ASTMJJdecls node, Object data) throws VisitorMJJException {
        return visitDeclsVars(node, data);
    }

    @Override
    public Object visit(ASTMJJvnil node, Object data) throws VisitorMJJException {
        return 0;
    }

    private Object newFact(SimpleNode node, Object data, String left, String right) throws VisitorMJJException {
        Mode mode = ((CompilerMJJData) data).getMode();
        int n = ((CompilerMJJData) data).getAddress();

        if (mode == Mode.ADD) {
            int ne = (int) node.jjtGetChild(2).jjtAccept(this, new CompilerMJJData(n, Mode.ADD));
            this.instrs.add((n + ne) + left + (((ASTMJJident) node.jjtGetChild(1)).jjtGetValue()) + ',' + node.jjtGetChild(0).jjtAccept(this, data) + right);
            return ne + 1;
        }

        this.instrs.add(n + STRSWAP);
        this.instrs.add((n+1) + STRPOP);
        return 2;
    }

    @Override
    public Object visit(ASTMJJcst node, Object data) throws VisitorMJJException {
        return newFact(node, data, STRNEW, ",cst,0);\n");
    }

    @Override
    public Object visit(ASTMJJtableau node, Object data) throws VisitorMJJException {
        return newFact(node, data, " newarray(", ");\n");
    }

    @Override
    public Object visit(ASTMJJmethode node, Object data) throws VisitorMJJException {
        Mode mode = ((CompilerMJJData) data).getMode();
        int n = ((CompilerMJJData) data).getAddress();
        this.nbEntete = 0;
        this.nops.clear();

        if (mode == Mode.ADD) {
            this.instrs.add(n + " push(" + (n + 3) + ");\n");
            this.instrs.add((n + 1) + STRNEW + (((ASTMJJident) node.jjtGetChild(1)).jjtGetValue()) + "," + node.jjtGetChild(0).jjtAccept(this, data) + ",meth,0);\n");
            this.instrs.add((n + 2) + " goto();\n");

            int nens = (int) node.jjtGetChild(2).jjtAccept(this, new CompilerMJJData(n + 3, Mode.ADD));
            int ndvs = (int) node.jjtGetChild(3).jjtAccept(this, new CompilerMJJData(n + 3 + nens, Mode.ADD));
            int niss = (int) node.jjtGetChild(4).jjtAccept(this, new CompilerMJJData(n + 3 + nens + ndvs, Mode.ADD));

            int index = 5;
            int index2 = 3;
            if (node.jjtGetChild(0) instanceof ASTMJJrien) {
                index = 6;
                index2 = 4;
                this.instrs.add((n + 3 + nens + ndvs + niss) + " push(0);\n");
            }

            int nrdvs = (int) node.jjtGetChild(3).jjtAccept(this, new CompilerMJJData(n + index2 + nens + ndvs + niss, Mode.REMOVE));

            this.instrs.set(n + 1, (n + 2) + STRGOTO + (n + nens + ndvs + niss + nrdvs + index) + ");\n");

            this.instrs.add((n + index2 + nens + ndvs + niss + nrdvs) + STRSWAP);
            this.instrs.add((n + index2 + nens + ndvs + niss + nrdvs + 1) + " return;\n");

            for (Integer nop : this.nops) {
                this.instrs.set(nop, (nop + 1) + STRGOTO + (n + index2 + nens + ndvs + niss) + ");\n");
            }

            return nens + ndvs + niss + nrdvs + index;
        }

        this.instrs.add(n + STRSWAP);
        this.instrs.add((n+1) + STRPOP);
        return 2;
    }

    @Override
    public Object visit(ASTMJJvar node, Object data) throws VisitorMJJException {
        return newFact(node, data, STRNEW, ",var,0);\n");
    }

    @Override
    public Object visit(ASTMJJvars node, Object data) throws VisitorMJJException {
        return visitDeclsVars(node, data);
    }

    @Override
    public Object visit(ASTMJJomega node, Object data) throws VisitorMJJException {
        int n = ((CompilerMJJData) data).getAddress();
        this.instrs.add((n) + " push();\n");
        return 1;
    }

    @Override
    public Object visit(ASTMJJmain node, Object data) throws VisitorMJJException {
        int n = ((CompilerMJJData) data).getAddress();
        int ndvs = (int) node.jjtGetChild(0).jjtAccept(this, new CompilerMJJData(n, Mode.ADD));
        int niss = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerMJJData(n+ndvs, Mode.ADD));
        this.instrs.add((n+ndvs+niss) + " push(0);\n");
        int nrdvs = (int) node.jjtGetChild(0).jjtAccept(this, new CompilerMJJData(n+ndvs+niss+1, Mode.REMOVE));
        return ndvs+niss+nrdvs+1;
    }

    @Override
    public Object visit(ASTMJJentetes node, Object data) throws VisitorMJJException {
        int n = ((CompilerMJJData) data).getAddress();

        int nens = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerMJJData(n, Mode.ADD));
        int nen = (int) node.jjtGetChild(0).jjtAccept(this, new CompilerMJJData(n+nens, Mode.ADD));
        return nens+nen;
    }

    @Override
    public Object visit(ASTMJJenil node, Object data) throws VisitorMJJException {
        return 0;
    }

    @Override
    public Object visit(ASTMJJentete node, Object data) throws VisitorMJJException {
        int n = ((CompilerMJJData) data).getAddress();
        this.instrs.add((n) + STRNEW + (((ASTMJJident)node.jjtGetChild(1)).jjtGetValue()) + ',' + node.jjtGetChild(0).jjtAccept(this, data) +  ",var," + (++this.nbEntete) + ");\n");
        return 1;
    }

    @Override
    public Object visit(ASTMJJinstrs node, Object data) throws VisitorMJJException {
        int n = ((CompilerMJJData) data).getAddress();
        int nis = (int) node.jjtGetChild(0).jjtAccept(this, new CompilerMJJData(n, Mode.ADD));
        int niss = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerMJJData(n+nis, Mode.ADD));
        return nis+niss;
    }

    @Override
    public Object visit(ASTMJJinil node, Object data) throws VisitorMJJException {
        return 0;
    }

    @Override
    public Object visit(ASTMJJret node, Object data) throws VisitorMJJException {
        int n = ((CompilerMJJData) data).getAddress();
        int ne = (int) node.jjtGetChild(0).jjtAccept(this, data);
        this.instrs.add((n+ne) + " nop;\n");
        this.nops.add(n+ne-1);
        return ne+1;
    }

    @Override
    public Object visit(ASTMJJecrire node, Object data) throws VisitorMJJException {
        int n = ((CompilerMJJData) data).getAddress();
        int ne = (int) node.jjtGetChild(0).jjtAccept(this, new CompilerMJJData(n, Mode.ADD));
        this.instrs.add((n+ne) + " write;\n");
        return ne+1;
    }

    @Override
    public Object visit(ASTMJJecrireln node, Object data) throws VisitorMJJException {
        int n = ((CompilerMJJData) data).getAddress();
        int ne = (int) node.jjtGetChild(0).jjtAccept(this, new CompilerMJJData(n, Mode.ADD));
        this.instrs.add((n+ne) + " writeln;\n");
        return ne+1;
    }

    @Override
    public Object visit(ASTMJJsi node, Object data) throws VisitorMJJException {
        int n = ((CompilerMJJData) data).getAddress();

        int ne = (int) node.jjtGetChild(0).jjtAccept(this, new CompilerMJJData(n, Mode.ADD));
        this.instrs.add((n+ne) + "if()\n");
        //if
        int ns1 = (int) node.jjtGetChild(2).jjtAccept(this, new CompilerMJJData(n+ne+1, Mode.ADD));
        this.instrs.add((n+ne+ns1) + " goto();\n");
        // goto
        int ns = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerMJJData(n+ne+ns1+2, Mode.ADD));

        this.instrs.set(n+ne-1, (n+ne) + " if(" + (n+ne+ns1+2) + ");\n");
        this.instrs.set(n+ne+ns1, (n+ne+ns1+1) + STRGOTO + (n+ne+ns1+ns+2) + ");\n");

        return ne+ns1+ns+2;
    }

    @Override
    public Object visit(ASTMJJtantque node, Object data) throws VisitorMJJException {
        int n = ((CompilerMJJData) data).getAddress();

        int ne = (int) node.jjtGetChild(0).jjtAccept(this, new CompilerMJJData(n, Mode.ADD));
        this.instrs.add((n+ne) + " not;\n");
        this.instrs.add((n+ne+1) + " if();\n");
        //if
        int niss = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerMJJData(n+ne+2, Mode.ADD));

        this.instrs.set(n+ne, (n+ne+1) + " if(" + (n+ne+niss+3) + ");\n");
        this.instrs.add((n+ne+niss+2) + STRGOTO + (n) + ");\n");

        return ne+niss+3;
    }

    @Override
    public Object visit(ASTMJJchaine node, Object data) throws VisitorMJJException {
        int n = ((CompilerMJJData) data).getAddress();
        this.instrs.add((n) + " push(\""+ node.jjtGetValue() + "\");\n");
        return 1;
    }

    @Override
    public Object visit(ASTMJJappelI node, Object data) throws VisitorMJJException { // sans retour
        int n = ((CompilerMJJData) data).getAddress();

        int nlexp = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerMJJData(n, Mode.ADD));
        this.instrs.add((n+nlexp) + " invoke(" + ((ASTMJJident)node.jjtGetChild(0)).jjtGetValue() + ");\n");
        int prlexp = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerMJJData(n+nlexp+1, Mode.REMOVE));
        this.instrs.add((n+nlexp+prlexp+1) + STRPOP);
        return nlexp+prlexp+2;
    }

    private Object storeInc(SimpleNode node, Object data, String str, String astr) throws VisitorMJJException {
        int n = ((CompilerMJJData) data).getAddress();

        if(node.jjtGetChild(0) instanceof ASTMJJident ident) {
            int ne = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerMJJData(n, Mode.ADD));
            this.instrs.add((n+ne) + str + ident.jjtGetValue() + ");\n");
            return ne+1;
        }

        int ne1 = (int) node.jjtGetChild(0).jjtGetChild(1).jjtAccept(this, new CompilerMJJData(n, Mode.ADD));
        int ne = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerMJJData(n+ne1, Mode.ADD));
        this.instrs.add((n+ne1+ne) + astr + ((ASTMJJident)node.jjtGetChild(0).jjtGetChild(0)).jjtGetValue() + ");\n");
        return ne1+ne+1;
    }

    @Override
    public Object visit(ASTMJJaffectation node, Object data) throws VisitorMJJException {
        return storeInc(node, data, " store(", " astore(");
    }

    @Override
    public Object visit(ASTMJJsomme node, Object data) throws VisitorMJJException {
        return storeInc(node, data, " inc(", " ainc(");
    }

    @Override
    public Object visit(ASTMJJincrement node, Object data) throws VisitorMJJException {
        int n = ((CompilerMJJData) data).getAddress();

        if (node.jjtGetChild(0) instanceof ASTMJJident ident) {
            this.instrs.add((n) + " push(1);\n");
            this.instrs.add((n+1) + " inc(" + ident.jjtGetValue() + ");\n");
            return 2;
        }else if (node.jjtGetChild(0) instanceof ASTMJJtab) {
            int ne = (int) node.jjtGetChild(0).jjtGetChild(1).jjtAccept(this, new CompilerMJJData(n, Mode.ADD));
            this.instrs.add((n+ne) + " push(1);\n");
            this.instrs.add((n+ne+1) + " ainc(" + ((ASTMJJident) node.jjtGetChild(0).jjtGetChild(0)).jjtGetValue() + ");\n");
            return ne+2;
        }

        return 0;
    }

    @Override
    public Object visit(ASTMJJtab node, Object data) throws VisitorMJJException {
        int n = ((CompilerMJJData) data).getAddress();

        int ne = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerMJJData(n, Mode.ADD));
        this.instrs.add((n+ne) + " aload(" + ((ASTMJJident)node.jjtGetChild(0)).jjtGetValue() + ");\n");
        return ne+1;
    }

    @Override
    public Object visit(ASTMJJlistexp node, Object data) throws VisitorMJJException {
        Mode mode = ((CompilerMJJData) data).getMode();
        int n = ((CompilerMJJData) data).getAddress();

        if (mode == Mode.ADD) {
            int nexp = (int) node.jjtGetChild(0).jjtAccept(this, new CompilerMJJData(n, Mode.ADD));
            int nlexp = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerMJJData(n + nexp, Mode.ADD));
            return nexp + nlexp;
        }

        this.instrs.add(n + STRSWAP);
        this.instrs.add((n+1) + STRPOP);
        int nrlexp = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerMJJData(n+2, Mode.REMOVE));
        return 2+nrlexp;
    }

    @Override
    public Object visit(ASTMJJexnil node, Object data) throws VisitorMJJException {
        return 0;
    }

    @Override
    public Object visit(ASTMJJnon node, Object data) throws VisitorMJJException { //!
        int n = ((CompilerMJJData) data).getAddress();

        int ne = (int) node.jjtGetChild(0).jjtAccept(this, new CompilerMJJData(n, Mode.ADD));
        this.instrs.add((n+ne) + " not;\n");
        return ne+1;
    }

    @Override
    public Object visit(ASTMJJet node, Object data) throws VisitorMJJException {
        return opBinaire(node, data, "and");
    }

    @Override
    public Object visit(ASTMJJou node, Object data) throws VisitorMJJException {
        return opBinaire(node, data, "or");
    }

    @Override
    public Object visit(ASTMJJequal node, Object data) throws VisitorMJJException {
        return opBinaire(node, data, "cmp");
    }

    @Override
    public Object visit(ASTMJJsup node, Object data) throws VisitorMJJException {
        return opBinaire(node, data, "sup");
    }

    @Override
    public Object visit(ASTMJJmoinsUnaire node, Object data) throws VisitorMJJException { // -
        int n = ((CompilerMJJData) data).getAddress();

        int ne = (int) node.jjtGetChild(0).jjtAccept(this, new CompilerMJJData(n, Mode.ADD));
        this.instrs.add((n+ne) + " neg;\n");
        return ne+1;
    }

    @Override
    public Object visit(ASTMJJplus node, Object data) throws VisitorMJJException {
        return opBinaire(node, data, "add");
    }

    @Override
    public Object visit(ASTMJJmoins node, Object data) throws VisitorMJJException {
        return opBinaire(node, data, "sub");
    }

    @Override
    public Object visit(ASTMJJmult node, Object data) throws VisitorMJJException {
        return opBinaire(node, data, "mul");
    }

    @Override
    public Object visit(ASTMJJdiv node, Object data) throws VisitorMJJException {
        return opBinaire(node, data, "div");
    }

    @Override
    public Object visit(ASTMJJlongueur node, Object data) throws VisitorMJJException {
        int n = ((CompilerMJJData) data).getAddress();
        this.instrs.add((n) + " length(" + ((ASTMJJident)node.jjtGetChild(0)).jjtGetValue() + ");\n");
        return 1;
    }

    @Override
    public Object visit(ASTMJJvrai node, Object data) throws VisitorMJJException {
        int n = ((CompilerMJJData) data).getAddress();
        this.instrs.add((n) + " push(vrai);\n");
        return 1;
    }

    @Override
    public Object visit(ASTMJJfaux node, Object data) throws VisitorMJJException {
        int n = ((CompilerMJJData) data).getAddress();
        this.instrs.add((n) + " push(faux);\n");
        return 1;
    }

    @Override
    public Object visit(ASTMJJnbre node, Object data) throws VisitorMJJException {
        int n = ((CompilerMJJData) data).getAddress();

        this.instrs.add((n) + " push(" + node.jjtGetValue() + ");\n");
        return 1;
    }

    @Override
    public Object visit(ASTMJJappelE node, Object data) throws VisitorMJJException { //avec retour
        int n = ((CompilerMJJData) data).getAddress();

        int nlexp = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerMJJData(n, Mode.ADD));
        this.instrs.add((n+nlexp) + " invoke(" + ((ASTMJJident)node.jjtGetChild(0)).jjtGetValue() + ");\n");
        int prlexp = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerMJJData(n+nlexp+1, Mode.REMOVE));
        return nlexp+prlexp+1;
    }

    @Override
    public Object visit(ASTMJJrien node, Object data) throws VisitorMJJException {
        return "rien";
    }

    @Override
    public Object visit(ASTMJJentier node, Object data) throws VisitorMJJException {
        return "entier";
    }

    @Override
    public Object visit(ASTMJJbooleen node, Object data) throws VisitorMJJException {
        return "booleen";
    }

    public int opBinaire(Node node, Object data, String op) throws VisitorMJJException {
        int n = ((CompilerMJJData) data).getAddress();

        int ne1 = (int) node.jjtGetChild(0).jjtAccept(this, new CompilerMJJData(n, Mode.ADD));
        int ne2 = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerMJJData(n+ne1, Mode.ADD));
        this.instrs.add((n+ne1+ne2) + " " + op + ";\n");
        return ne1+ne2+1;
    }

    public Object visitDeclsVars(Node node, Object data) throws VisitorMJJException {
        Mode mode = ((CompilerMJJData) data).getMode();
        int n = ((CompilerMJJData) data).getAddress();

        if (mode == Mode.ADD) {
            int nds = (int) node.jjtGetChild(0).jjtAccept(this, new CompilerMJJData(n, Mode.ADD));
            int ndss = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerMJJData(n + nds, Mode.ADD));
            return nds + ndss;
        }

        int nrdss = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerMJJData(n, Mode.REMOVE));
        int nrds = (int) node.jjtGetChild(0).jjtAccept(this, new CompilerMJJData(n+nrdss, Mode.REMOVE));
        return nrdss+nrds;
    }
}
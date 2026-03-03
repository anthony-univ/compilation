package fr.ufrst.m1info.comp4.compiler;

import fr.ufrst.m1info.comp4.parser.minijaja.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static fr.ufrst.m1info.comp4.parser.minijaja.MinijajaTreeConstants.*;


public class CompilerMJJUnitTest {

    private CompilerMJJVisitor compilerMJJVisitor;
    private ArrayList<String> instrsExpected;

    @Before
    public void init() throws VisitorMJJException {
        compilerMJJVisitor = new CompilerMJJVisitor();
        instrsExpected = new ArrayList<>();
    }

    public ASTMJJident createNodeIdent(String name) {
        ASTMJJident astident = new ASTMJJident(JJTIDENT);
        astident.jjtSetValue(name);
        return astident;
    }

    public ASTMJJnbre createNodeNbre(int value) {
        ASTMJJnbre astnbre = new ASTMJJnbre(JJTNBRE);
        astnbre.jjtSetValue(value);
        return astnbre;
    }

    public ASTMJJinstrs createNodeInstrs(Node node) {
        ASTMJJinstrs astinstrs = new ASTMJJinstrs(JJTINSTRS);
        astinstrs.jjtAddChild(node, 0);
        astinstrs.jjtAddChild(new ASTMJJinil(JJTINIL), 1);
        return astinstrs;
    }

    public ASTMJJdecls createNodeDecls(Node node) {
        ASTMJJdecls astdecls = new ASTMJJdecls(JJTDECLS);
        astdecls.jjtAddChild(node, 0);
        astdecls.jjtAddChild(new ASTMJJvnil(JJTVNIL), 1);
        return astdecls;
    }

    public ASTMJJvars createNodeVars(Node node) {
        ASTMJJvars astvars = new ASTMJJvars(JJTVARS);
        astvars.jjtAddChild(node, 0);
        astvars.jjtAddChild(new ASTMJJvnil(JJTVNIL), 1);
        return astvars;
    }

    public ASTMJJaffectation createNodeAffectation(Node node1, Node node2) {
        ASTMJJaffectation astaffectation = new ASTMJJaffectation(JJTAFFECTATION);
        astaffectation.jjtAddChild(node1, 0);
        astaffectation.jjtAddChild(node2, 1);
        return astaffectation;
    }

    public ASTMJJincrement createNodeIncrement(Node node1) {
        ASTMJJincrement astincrement = new ASTMJJincrement(JJTINCREMENT);
        astincrement.jjtAddChild(node1, 0);
        ASTMJJnbre astnbre = createNodeNbre(1);
        astincrement.jjtAddChild(astnbre, 1);
        return astincrement;
    }

    public ASTMJJsi createNodeSi(Node node1, Node node2, Node node3) {
        ASTMJJsi astsi = new ASTMJJsi(JJTSI);
        astsi.jjtAddChild(node1, 0);
        astsi.jjtAddChild(node2, 1);
        astsi.jjtAddChild(node3, 2);
        return astsi;
    }

    public ASTMJJsup createNodeSup(Node node1, Node node2) {
        ASTMJJsup astsup = new ASTMJJsup(JJTSUP);
        astsup.jjtAddChild(node1, 0);
        astsup.jjtAddChild(node2, 1);
        return astsup;
    }

    public ASTMJJmethode createNodeMeth(String name) {
        ASTMJJmethode astmethode = new ASTMJJmethode(JJTMETHODE);
        astmethode.jjtAddChild(new ASTMJJentier(JJTENTIER), 0);
        astmethode.jjtAddChild(createNodeIdent(name), 1);
        astmethode.jjtAddChild(new ASTMJJexnil(JJTEXNIL), 2);
        astmethode.jjtAddChild(new ASTMJJvnil(JJTVNIL), 3);
        astmethode.jjtAddChild(new ASTMJJinil(JJTINIL), 4);
        return astmethode;
    }

    public ASTMJJmethode createNodeMethR(String name) {
        ASTMJJmethode astmethode = new ASTMJJmethode(JJTMETHODE);
        astmethode.jjtAddChild(new ASTMJJrien(JJTRIEN), 0);
        astmethode.jjtAddChild(createNodeIdent(name), 1);
        astmethode.jjtAddChild(new ASTMJJexnil(JJTEXNIL), 2);
        astmethode.jjtAddChild(new ASTMJJvnil(JJTVNIL), 3);
        astmethode.jjtAddChild(new ASTMJJinil(JJTINIL), 4);
        return astmethode;
    }

    private ASTMJJvar createNodeVarOmega(String name) {
        ASTMJJvar astvar = new ASTMJJvar(JJTVAR);
        ASTMJJident astident = createNodeIdent(name);
        astvar.jjtAddChild(new ASTMJJentier(JJTENTIER), 0);
        astvar.jjtAddChild(astident, 1);
        astvar.jjtAddChild(new ASTMJJomega(JJTOMEGA), 2);
        return astvar;
    }

    /********************************************/
    /*              TESTS UNITAIRES             */
    /********************************************/

    @Test
    public void testNodeClasse() throws VisitorMJJException {
        ASTMJJclasse asTclasse = new ASTMJJclasse(JJTCLASSE);
        asTclasse.jjtAddChild(createNodeIdent("C"), 0);
        asTclasse.jjtAddChild(new ASTMJJvnil(JJTVNIL), 1);
        ASTMJJmain astmain = new ASTMJJmain(JJTMAIN);
        astmain.jjtAddChild(new ASTMJJvnil(JJTVNIL), 0);
        astmain.jjtAddChild(new ASTMJJinil(JJTINIL), 1);
        asTclasse.jjtAddChild(astmain, 2);
        instrsExpected.add("1 init;\n");
        instrsExpected.add("2 push(0);\n");
        instrsExpected.add("3 pop;\n");
        instrsExpected.add("4 jcstop;\n");
        int n = (int)compilerMJJVisitor.visit(asTclasse, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(4, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeDecls() throws VisitorMJJException {
        ASTMJJdecls astdecls = createNodeDecls(createNodeMeth("f"));
        ASTMJJdecls astdecls2 = createNodeDecls(createNodeMeth("g"));
        astdecls.jjtAddChild(astdecls2, 1);
        instrsExpected.add("1 push(4);\n");
        instrsExpected.add("2 new(f,entier,meth,0);\n");
        instrsExpected.add("3 goto(6);\n");
        instrsExpected.add("4 swap;\n");
        instrsExpected.add("5 return;\n");
        instrsExpected.add("6 push(9);\n");
        instrsExpected.add("7 new(g,entier,meth,0);\n");
        instrsExpected.add("8 goto(11);\n");
        instrsExpected.add("9 swap;\n");
        instrsExpected.add("10 return;\n");
        int n = (int)compilerMJJVisitor.visit(astdecls, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(10, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeVars() throws VisitorMJJException {
        ASTMJJvars astvars = createNodeVars(createNodeVarOmega("a"));
        ASTMJJvars astvars2 = createNodeVars(createNodeVarOmega("b"));
        astvars.jjtAddChild(astvars2, 1);
        instrsExpected.add("1 push();\n");
        instrsExpected.add("2 new(a,entier,var,0);\n");
        instrsExpected.add("3 push();\n");
        instrsExpected.add("4 new(b,entier,var,0);\n");
        int n = (int)compilerMJJVisitor.visit(astvars, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(4, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeVar() throws VisitorMJJException {
        ASTMJJvar astvar = createNodeVarOmega("a");
        instrsExpected.add("1 push();\n");
        instrsExpected.add("2 new(a,entier,var,0);\n");
        int n = (int)compilerMJJVisitor.visit(astvar, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(2, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeCst() throws VisitorMJJException {
        ASTMJJcst astcst = new ASTMJJcst(JJTCST);
        ASTMJJident astident = createNodeIdent("i");
        ASTMJJnbre astnbre = createNodeNbre(42);
        astcst.jjtAddChild(new ASTMJJbooleen(JJTBOOLEEN), 0);
        astcst.jjtAddChild(astident, 1);
        astcst.jjtAddChild(astnbre, 2);
        instrsExpected.add("1 push(42);\n");
        instrsExpected.add("2 new(i,booleen,cst,0);\n");
        int n = (int)compilerMJJVisitor.visit(astcst, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(2, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeTableau() throws VisitorMJJException {
        ASTMJJtableau asttableau = new ASTMJJtableau(JJTTABLEAU);
        ASTMJJident astident = createNodeIdent("ta");
        ASTMJJnbre astnbre = createNodeNbre(42);
        asttableau.jjtAddChild(new ASTMJJbooleen(JJTBOOLEEN), 0);
        asttableau.jjtAddChild(astident, 1);
        asttableau.jjtAddChild(astnbre, 2);
        instrsExpected.add("1 push(42);\n");
        instrsExpected.add("2 newarray(ta,booleen);\n");
        int n = (int)compilerMJJVisitor.visit(asttableau, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(2, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeEntetes() throws VisitorMJJException {
        ASTMJJentetes astentetes = new ASTMJJentetes(JJTENTETES);
        ASTMJJentete astentete = new ASTMJJentete(JJTENTETE);
        ASTMJJentetes astentetes2 = new ASTMJJentetes(JJTENTETES);
        ASTMJJentete astentete2 = new ASTMJJentete(JJTENTETE);
        astentete.jjtAddChild(new ASTMJJentier(JJTENTIER), 0);
        astentete.jjtAddChild(createNodeIdent("i"), 1);
        astentete2.jjtAddChild(new ASTMJJbooleen(JJTBOOLEEN), 0);
        astentete2.jjtAddChild(createNodeIdent("b"), 1);
        astentetes2.jjtAddChild(astentete2, 0);
        astentetes2.jjtAddChild( new ASTMJJenil(JJTENIL), 1);
        astentetes.jjtAddChild(astentete, 0);
        astentetes.jjtAddChild(astentetes2, 1);
        instrsExpected.add("1 new(b,booleen,var,1);\n");
        instrsExpected.add("2 new(i,entier,var,2);\n");
        int n = (int)compilerMJJVisitor.visit(astentetes, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(2, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeEntete() throws VisitorMJJException {
        ASTMJJentete astentete = new ASTMJJentete(JJTENTETE);
        astentete.jjtAddChild(new ASTMJJentier(JJTENTIER), 0);
        astentete.jjtAddChild(createNodeIdent("i"), 1);
        instrsExpected.add("1 new(i,entier,var,1);\n");
        int n = (int)compilerMJJVisitor.visit(astentete, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(1, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeMain() throws VisitorMJJException {
        ASTMJJmain astmain = new ASTMJJmain(JJTMAIN);
        ASTMJJvars astvars = createNodeVars(createNodeVarOmega("i"));
        ASTMJJinstrs astinstrs = createNodeInstrs(createNodeAffectation(createNodeIdent("i"), createNodeNbre(42)));
        astmain.jjtAddChild(astvars, 0);
        astmain.jjtAddChild(astinstrs, 1);
        instrsExpected.add("1 push();\n");
        instrsExpected.add("2 new(i,entier,var,0);\n");
        instrsExpected.add("3 push(42);\n");
        instrsExpected.add("4 store(i);\n");
        instrsExpected.add("5 push(0);\n");
        instrsExpected.add("6 swap;\n");
        instrsExpected.add("7 pop;\n");
        int n = (int)compilerMJJVisitor.visit(astmain, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(7, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeMethode() throws VisitorMJJException {
        ASTMJJmethode astmethode = createNodeMeth("f");
        ASTMJJvars astvars = createNodeVars(createNodeVarOmega("i"));
        ASTMJJinstrs astinstrs = createNodeInstrs(createNodeAffectation(createNodeIdent("i"), createNodeNbre(42)));
        astmethode.jjtAddChild(astvars, 3);
        astmethode.jjtAddChild(astinstrs, 4);
        instrsExpected.add("1 push(4);\n");
        instrsExpected.add("2 new(f,entier,meth,0);\n");
        instrsExpected.add("3 goto(12);\n");
        instrsExpected.add("4 push();\n");
        instrsExpected.add("5 new(i,entier,var,0);\n");
        instrsExpected.add("6 push(42);\n");
        instrsExpected.add("7 store(i);\n");
        instrsExpected.add("8 swap;\n");
        instrsExpected.add("9 pop;\n");
        instrsExpected.add("10 swap;\n");
        instrsExpected.add("11 return;\n");
        int n = (int)compilerMJJVisitor.visit(astmethode, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(11, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeMethodeR() throws VisitorMJJException {
        ASTMJJmethode astmethode = createNodeMethR("f");
        ASTMJJvars astvars = createNodeVars(createNodeVarOmega("i"));
        ASTMJJret astretour = new ASTMJJret(JJTRET);
        astretour.jjtAddChild(createNodeIdent("i"), 0);
        ASTMJJinstrs astinstrs = createNodeInstrs(astretour);
        astmethode.jjtAddChild(astvars, 3);
        astmethode.jjtAddChild(astinstrs, 4);
        instrsExpected.add("1 push(4);\n");
        instrsExpected.add("2 new(f,rien,meth,0);\n");
        instrsExpected.add("3 goto(13);\n");
        instrsExpected.add("4 push();\n");
        instrsExpected.add("5 new(i,entier,var,0);\n");
        instrsExpected.add("6 load(i);\n");
        instrsExpected.add("7 goto(9);\n");
        instrsExpected.add("8 push(0);\n");
        instrsExpected.add("9 swap;\n");
        instrsExpected.add("10 pop;\n");
        instrsExpected.add("11 swap;\n");
        instrsExpected.add("12 return;\n");
        int n = (int)compilerMJJVisitor.visit(astmethode, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(12, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeInstrs() throws VisitorMJJException {
        ASTMJJinstrs astinstrs = new ASTMJJinstrs(JJTINSTRS);
        ASTMJJinstrs astinstrs2 = new ASTMJJinstrs(JJTINSTRS);
        ASTMJJaffectation astaffectation = createNodeAffectation(createNodeIdent("i"), createNodeNbre(4));
        ASTMJJincrement astincrement = createNodeIncrement(createNodeIdent("j"));
        astinstrs2.jjtAddChild(astincrement, 0);
        astinstrs2.jjtAddChild(new ASTMJJinil(JJTINIL), 1);
        astinstrs.jjtAddChild(astaffectation, 0);
        astinstrs.jjtAddChild(astinstrs2, 1);
        instrsExpected.add("1 push(4);\n");
        instrsExpected.add("2 store(i);\n");
        instrsExpected.add("3 push(1);\n");
        instrsExpected.add("4 inc(j);\n");
        int n = (int)compilerMJJVisitor.visit(astinstrs, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(4, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeSomme() throws VisitorMJJException {
        ASTMJJsomme astsomme = new ASTMJJsomme(JJTSOMME);
        ASTMJJident astident = createNodeIdent("i");
        ASTMJJnbre astnbre = createNodeNbre(42);
        astsomme.jjtAddChild(astident, 0);
        astsomme.jjtAddChild(astnbre, 1);
        instrsExpected.add("1 push(42);\n");
        instrsExpected.add("2 inc(i);\n");
        int n = (int)compilerMJJVisitor.visit(astsomme, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(2, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeSommeT() throws VisitorMJJException {
        ASTMJJsomme astsomme = new ASTMJJsomme(JJTSOMME);
        ASTMJJtab asttab = new ASTMJJtab(JJTTAB);
        ASTMJJident astident = createNodeIdent("ta");
        ASTMJJnbre astnbre = createNodeNbre(0);
        asttab.jjtAddChild(astident, 0);
        asttab.jjtAddChild(astnbre, 1);
        ASTMJJnbre astnbre2 = createNodeNbre(42);
        astsomme.jjtAddChild(asttab, 0);
        astsomme.jjtAddChild(astnbre2, 1);
        instrsExpected.add("1 push(0);\n");
        instrsExpected.add("2 push(42);\n");
        instrsExpected.add("3 ainc(ta);\n");
        int n = (int)compilerMJJVisitor.visit(astsomme, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(3, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());

    }

    @Test
    public void testNodeInc() throws VisitorMJJException {
        ASTMJJincrement astincrement = new ASTMJJincrement(JJTINCREMENT);
        ASTMJJident astident = createNodeIdent("i");
        astincrement.jjtAddChild(astident, 0);
        instrsExpected.add("1 push(1);\n");
        instrsExpected.add("2 inc(i);\n");
        int n = (int)compilerMJJVisitor.visit(astincrement, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(2, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeIncT() throws VisitorMJJException {
        ASTMJJincrement astincrement = new ASTMJJincrement(JJTINCREMENT);
        ASTMJJtab asttab = new ASTMJJtab(JJTTAB);
        ASTMJJident astident = createNodeIdent("ta");
        ASTMJJnbre astnbre = createNodeNbre(0);
        asttab.jjtAddChild(astident, 0);
        asttab.jjtAddChild(astnbre, 1);
        astincrement.jjtAddChild(asttab, 0);
        instrsExpected.add("1 push(0);\n");
        instrsExpected.add("2 push(1);\n");
        instrsExpected.add("3 ainc(ta);\n");
        int n = (int)compilerMJJVisitor.visit(astincrement, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(3, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeAffect() throws VisitorMJJException {
        ASTMJJnbre astnbre = createNodeNbre(42);
        ASTMJJaffectation astaffectation = createNodeAffectation(createNodeIdent("i"), astnbre);
        instrsExpected.add("1 push(42);\n");
        instrsExpected.add("2 store(i);\n");
        int n = (int)compilerMJJVisitor.visit(astaffectation, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(2, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeAffectT() throws VisitorMJJException {
        ASTMJJident astident = createNodeIdent("ta");
        ASTMJJnbre astnbre = createNodeNbre(0);
        ASTMJJtab asttab = new ASTMJJtab(JJTTAB);
        asttab.jjtAddChild(astident, 0);
        asttab.jjtAddChild(astnbre, 1);
        ASTMJJnbre astnbre2 = createNodeNbre(42);
        ASTMJJaffectation astaffectation = createNodeAffectation(asttab, astnbre2);
        instrsExpected.add("1 push(0);\n");
        instrsExpected.add("2 push(42);\n");
        instrsExpected.add("3 astore(ta);\n");
        int n = (int)compilerMJJVisitor.visit(astaffectation, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(3, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeAppelE() throws VisitorMJJException {  //avec retour
        ASTMJJappelE astappelE = new ASTMJJappelE(JJTAPPELE);
        ASTMJJident astident = createNodeIdent("f");
        ASTMJJnbre astnbre = createNodeNbre(42);
        ASTMJJlistexp astlistexp = new ASTMJJlistexp(JJTLISTEXP);
        astlistexp.jjtAddChild(astnbre, 0);
        astlistexp.jjtAddChild(new ASTMJJexnil(JJTEXNIL), 1);
        astappelE.jjtAddChild(astident, 0);
        astappelE.jjtAddChild(astlistexp, 1);
        instrsExpected.add("1 push(42);\n");
        instrsExpected.add("2 invoke(f);\n");
        instrsExpected.add("3 swap;\n");
        instrsExpected.add("4 pop;\n");
        int n = (int)compilerMJJVisitor.visit(astappelE, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(4, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeAppelI() throws VisitorMJJException { // sans retour
        ASTMJJappelI astappelI = new ASTMJJappelI(JJTAPPELI);
        ASTMJJident astident = createNodeIdent("f");
        ASTMJJnbre astnbre = createNodeNbre(42);
        ASTMJJlistexp astlistexp = new ASTMJJlistexp(JJTLISTEXP);
        astlistexp.jjtAddChild(astnbre, 0);
        astlistexp.jjtAddChild(new ASTMJJexnil(JJTEXNIL), 1);
        astappelI.jjtAddChild(astident, 0);
        astappelI.jjtAddChild(astlistexp, 1);
        instrsExpected.add("1 push(42);\n");
        instrsExpected.add("2 invoke(f);\n");
        instrsExpected.add("3 swap;\n");
        instrsExpected.add("4 pop;\n");
        instrsExpected.add("5 pop;\n");
        int n = (int)compilerMJJVisitor.visit(astappelI, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(5, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testRetour() throws VisitorMJJException {
        ASTMJJret astretour = new ASTMJJret(JJTRET);
        ASTMJJident asTident = createNodeIdent("i");
        astretour.jjtAddChild(asTident, 0);
        instrsExpected.add("1 load(i);\n");
        instrsExpected.add("2 nop;\n");
        int n = (int)compilerMJJVisitor.visit(astretour, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(2, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testEcrire() throws VisitorMJJException {
        ASTMJJecrire astecrire = new ASTMJJecrire(JJTECRIRE);
        ASTMJJchaine astchaine = new ASTMJJchaine(JJTCHAINE);
        astchaine.jjtSetValue("Hello World!");
        astecrire.jjtAddChild(astchaine, 0);
        instrsExpected.add("1 push(\"Hello World!\");\n");
        instrsExpected.add("2 write;\n");
        int n = (int)compilerMJJVisitor.visit(astecrire, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(2, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testEcrireLn() throws VisitorMJJException {
        ASTMJJecrireln astecrireln = new ASTMJJecrireln(JJTECRIRELN);
        ASTMJJident astident = createNodeIdent("i");
        astecrireln.jjtAddChild(astident, 0);
        instrsExpected.add("1 load(i);\n");
        instrsExpected.add("2 writeln;\n");
        int n = (int)compilerMJJVisitor.visit(astecrireln, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(2, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testSi() throws VisitorMJJException {
        //if (3 > i){ i = 1; }
        ASTMJJnbre astnbre = createNodeNbre(3);
        ASTMJJnbre astnbre2 = createNodeNbre(1);
        ASTMJJident astident = createNodeIdent("i");
        ASTMJJsup astsup = createNodeSup(astnbre, astident);
        ASTMJJaffectation astaffectation = createNodeAffectation(astident, astnbre2);
        ASTMJJinstrs astinstrs = createNodeInstrs(astaffectation);
        ASTMJJsi astsi = createNodeSi(astsup, astinstrs, new ASTMJJinil(JJTINIL));
        instrsExpected.add("1 push(3);\n");
        instrsExpected.add("2 load(i);\n");
        instrsExpected.add("3 sup;\n");
        instrsExpected.add("4 if(6);\n");
        instrsExpected.add("5 goto(8);\n");
        instrsExpected.add("6 push(1);\n");
        instrsExpected.add("7 store(i);\n");
        int n = (int)compilerMJJVisitor.visit(astsi, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(7, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testSiSinon() throws VisitorMJJException {
        //if (3 > i){i = 1}else{i = 0}; }
        ASTMJJnbre astnbre = createNodeNbre(3);
        ASTMJJnbre astnbre2 = createNodeNbre(1);
        ASTMJJident astident = createNodeIdent("i");
        ASTMJJsup astsup = createNodeSup(astnbre, astident);
        ASTMJJaffectation astaffectation = createNodeAffectation(astident, astnbre2);
        ASTMJJinstrs astinstrs = createNodeInstrs(astaffectation);

        ASTMJJnbre astnbre3 = createNodeNbre(0);
        ASTMJJaffectation astaffectation2 = createNodeAffectation(astident, astnbre3);
        ASTMJJinstrs astinstrs2 = createNodeInstrs(astaffectation2);

        ASTMJJsi astsi = createNodeSi(astsup, astinstrs, astinstrs2);
        instrsExpected.add("1 push(3);\n");
        instrsExpected.add("2 load(i);\n");
        instrsExpected.add("3 sup;\n");
        instrsExpected.add("4 if(8);\n");
        instrsExpected.add("5 push(0);\n");
        instrsExpected.add("6 store(i);\n");
        instrsExpected.add("7 goto(10);\n");
        instrsExpected.add("8 push(1);\n");
        instrsExpected.add("9 store(i);\n");
        int n = (int)compilerMJJVisitor.visit(astsi, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(9, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testTantQue() throws VisitorMJJException {
        //while(i) { write(i)} ;
        ASTMJJident astident = createNodeIdent("i");
        ASTMJJecrireln astecrireln = new ASTMJJecrireln(JJTECRIRELN);
        astecrireln.jjtAddChild(astident, 0);
        ASTMJJinstrs astinstrs = createNodeInstrs(astecrireln);
        ASTMJJtantque asttantque = new ASTMJJtantque(JJTTANTQUE);
        asttantque.jjtAddChild(astident, 0);
        asttantque.jjtAddChild(astinstrs, 1);
        instrsExpected.add("1 load(i);\n");
        instrsExpected.add("2 not;\n");
        instrsExpected.add("3 if(7);\n");
        instrsExpected.add("4 load(i);\n");
        instrsExpected.add("5 writeln;\n");
        instrsExpected.add("6 goto(1);\n");
        int n = (int)compilerMJJVisitor.visit(asttantque, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(6, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testTab() throws VisitorMJJException {
        ASTMJJtab asttab = new ASTMJJtab(JJTTAB);
        ASTMJJident astident = createNodeIdent("ta");
        ASTMJJnbre astnbre = createNodeNbre(42);
        asttab.jjtAddChild(astident, 0);
        asttab.jjtAddChild(astnbre, 1);
        instrsExpected.add("1 push(42);\n");
        instrsExpected.add("2 aload(ta);\n");
        int n = (int)compilerMJJVisitor.visit(asttab, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(2, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testIdent() throws VisitorMJJException {
        ASTMJJident astident = new ASTMJJident(JJTIDENT);
        astident.jjtSetValue("i");
        instrsExpected.add("1 load(i);\n");
        int n = (int)compilerMJJVisitor.visit(astident, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(1, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNbre() throws VisitorMJJException {
        ASTMJJnbre astnbre = new ASTMJJnbre(JJTNBRE);
        astnbre.jjtSetValue(42);
        instrsExpected.add("1 push(42);\n");
        int n = (int)compilerMJJVisitor.visit(astnbre, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(1, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testChaine() throws VisitorMJJException {
        ASTMJJchaine astchaine = new ASTMJJchaine(JJTCHAINE);
        astchaine.jjtSetValue("Hello World!");
        instrsExpected.add("1 push(\"Hello World!\");\n");
        int n = (int)compilerMJJVisitor.visit(astchaine, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(1, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeVrai() throws VisitorMJJException {
        ASTMJJvrai astvrai = new ASTMJJvrai(JJTVRAI);
        instrsExpected.add("1 push(vrai);\n");
        int n = (int)compilerMJJVisitor.visit(astvrai, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(1, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeFaux() throws VisitorMJJException {
        ASTMJJfaux astfaux = new ASTMJJfaux(JJTFAUX);
        instrsExpected.add("1 push(faux);\n");
        int n = (int)compilerMJJVisitor.visit(astfaux, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(1, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeMoinsUnaire() throws VisitorMJJException {
        ASTMJJmoinsUnaire astmoinsUnaire = new ASTMJJmoinsUnaire(JJTMOINSUNAIRE);
        ASTMJJnbre astnbre = createNodeNbre(42);
        astmoinsUnaire.jjtAddChild(astnbre, 0);
        instrsExpected.add("1 push(42);\n");
        instrsExpected.add("2 neg;\n");
        int n = (int)compilerMJJVisitor.visit(astmoinsUnaire, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(2, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeNon() throws VisitorMJJException {
        ASTMJJnon astnon = new ASTMJJnon(JJTNON);
        ASTMJJvrai astvrai = new ASTMJJvrai(JJTVRAI);
        astnon.jjtAddChild(astvrai, 0);
        instrsExpected.add("1 push(vrai);\n");
        instrsExpected.add("2 not;\n");
        int n = (int)compilerMJJVisitor.visit(astnon, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(2, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void nodeET() throws VisitorMJJException {
        ASTMJJet astet = new ASTMJJet(JJTET);
        ASTMJJvrai astvrai = new ASTMJJvrai(JJTVRAI);
        ASTMJJfaux astfaux = new ASTMJJfaux(JJTFAUX);
        astet.jjtAddChild(astvrai, 0);
        astet.jjtAddChild(astfaux, 1);
        instrsExpected.add("1 push(vrai);\n");
        instrsExpected.add("2 push(faux);\n");
        instrsExpected.add("3 and;\n");
        int n = (int)compilerMJJVisitor.visit(astet, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(3, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void nodeOU() throws VisitorMJJException {
        ASTMJJou astou = new ASTMJJou(JJTOU);
        ASTMJJvrai astvrai = new ASTMJJvrai(JJTVRAI);
        ASTMJJfaux astfaux = new ASTMJJfaux(JJTFAUX);
        astou.jjtAddChild(astvrai, 0);
        astou.jjtAddChild(astfaux, 1);
        instrsExpected.add("1 push(vrai);\n");
        instrsExpected.add("2 push(faux);\n");
        instrsExpected.add("3 or;\n");
        int n = (int)compilerMJJVisitor.visit(astou, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(3, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void nodeEqual() throws VisitorMJJException {
        ASTMJJequal astequal = new ASTMJJequal(JJTEQUAL);
        ASTMJJnbre astnbre1 = createNodeNbre(42);
        ASTMJJnbre astnbre2 = createNodeNbre(42);
        astequal.jjtAddChild(astnbre1, 0);
        astequal.jjtAddChild(astnbre2, 1);
        instrsExpected.add("1 push(42);\n");
        instrsExpected.add("2 push(42);\n");
        instrsExpected.add("3 cmp;\n");
        int n = (int)compilerMJJVisitor.visit(astequal, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(3, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void nodeSup() throws VisitorMJJException {
        ASTMJJsup astsup = new ASTMJJsup(JJTSUP);
        ASTMJJnbre astnbre1 = createNodeNbre(42);
        ASTMJJnbre astnbre2 = createNodeNbre(42);
        astsup.jjtAddChild(astnbre1, 0);
        astsup.jjtAddChild(astnbre2, 1);
        instrsExpected.add("1 push(42);\n");
        instrsExpected.add("2 push(42);\n");
        instrsExpected.add("3 sup;\n");
        int n = (int)compilerMJJVisitor.visit(astsup, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(3, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testPlus() throws VisitorMJJException {
        ASTMJJplus astplus = new ASTMJJplus(JJTPLUS);
        ASTMJJnbre astnbre1 = createNodeNbre(42);
        ASTMJJnbre astnbre2 = createNodeNbre(41);
        astplus.jjtAddChild(astnbre1, 0);
        astplus.jjtAddChild(astnbre2, 1);
        instrsExpected.add("1 push(42);\n");
        instrsExpected.add("2 push(41);\n");
        instrsExpected.add("3 add;\n");
        int n = (int)compilerMJJVisitor.visit(astplus, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(3, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public  void testNodeMoins() throws VisitorMJJException {
        ASTMJJmoins astmoins = new ASTMJJmoins(JJTMOINS);
        ASTMJJnbre astnbre1 = createNodeNbre(42);
        ASTMJJnbre astnbre2 = createNodeNbre(41);
        astmoins.jjtAddChild(astnbre1, 0);
        astmoins.jjtAddChild(astnbre2, 1);
        instrsExpected.add("1 push(42);\n");
        instrsExpected.add("2 push(41);\n");
        instrsExpected.add("3 sub;\n");
        int n = (int)compilerMJJVisitor.visit(astmoins, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(3, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testMult() throws VisitorMJJException {
        ASTMJJmult astmult = new ASTMJJmult(JJTMULT);
        ASTMJJnbre astnbre1 = createNodeNbre(42);
        ASTMJJnbre astnbre2 = createNodeNbre(41);
        astmult.jjtAddChild(astnbre1, 0);
        astmult.jjtAddChild(astnbre2, 1);
        instrsExpected.add("1 push(42);\n");
        instrsExpected.add("2 push(41);\n");
        instrsExpected.add("3 mul;\n");
        int n = (int)compilerMJJVisitor.visit(astmult, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(3, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testDiv() throws VisitorMJJException {
        ASTMJJdiv astdiv = new ASTMJJdiv(JJTDIV);
        ASTMJJnbre astnbre1 = createNodeNbre(42);
        ASTMJJnbre astnbre2 = createNodeNbre(41);
        astdiv.jjtAddChild(astnbre1, 0);
        astdiv.jjtAddChild(astnbre2, 1);
        instrsExpected.add("1 push(42);\n");
        instrsExpected.add("2 push(41);\n");
        instrsExpected.add("3 div;\n");
        int n = (int)compilerMJJVisitor.visit(astdiv, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(3, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testListExp() throws VisitorMJJException {
        ASTMJJlistexp astlistExp = new ASTMJJlistexp(JJTLISTEXP);
        ASTMJJnbre astnbre1 = createNodeNbre(42);
        ASTMJJnbre astnbre2 = createNodeNbre(41);
        astlistExp.jjtAddChild(astnbre1, 0);
        astlistExp.jjtAddChild(astnbre2, 1);
        instrsExpected.add("1 push(42);\n");
        instrsExpected.add("2 push(41);\n");
        int n = (int)compilerMJJVisitor.visit(astlistExp, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(2, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeVnil() throws VisitorMJJException {
        ASTMJJvnil astvnil = new ASTMJJvnil(JJTVNIL);
        int n = (int)compilerMJJVisitor.visit(astvnil, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(0, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeInil() throws VisitorMJJException {
        ASTMJJinil astinil = new ASTMJJinil(JJTINIL);
        int n = (int)compilerMJJVisitor.visit(astinil, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(0, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeEnil() throws VisitorMJJException {
        ASTMJJenil astenil = new ASTMJJenil(JJTENIL);
        int n = (int)compilerMJJVisitor.visit(astenil, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(0, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeExnil() throws VisitorMJJException {
        ASTMJJexnil astexnil = new ASTMJJexnil(JJTEXNIL);
        int n = (int)compilerMJJVisitor.visit(astexnil, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(0, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testVarRetrait() throws VisitorMJJException {
        ASTMJJvar astvar = createNodeVarOmega("i");
        instrsExpected.add("1 swap;\n");
        instrsExpected.add("2 pop;\n");
        int n = (int)compilerMJJVisitor.visit(astvar, new CompilerMJJData(1, Mode.REMOVE));
        Assert.assertEquals(2, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testCstRetrait() throws VisitorMJJException {
        ASTMJJcst astcst = new ASTMJJcst(JJTCST);
        ASTMJJident astident = createNodeIdent("i");
        astcst.jjtAddChild(new ASTMJJentier(JJTENTIER), 0);
        astcst.jjtAddChild(astident, 1);
        astcst.jjtAddChild(new ASTMJJomega(JJTOMEGA), 2);
        instrsExpected.add("1 swap;\n");
        instrsExpected.add("2 pop;\n");
        int n = (int)compilerMJJVisitor.visit(astcst, new CompilerMJJData(1, Mode.REMOVE));
        Assert.assertEquals(2, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testMethodeRetrait() throws VisitorMJJException {
        ASTMJJmethode astmethode = createNodeMeth("f");
        instrsExpected.add("1 swap;\n");
        instrsExpected.add("2 pop;\n");
        int n = (int)compilerMJJVisitor.visit(astmethode, new CompilerMJJData(1, Mode.REMOVE));
        Assert.assertEquals(2, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testDeclsRetrait() throws VisitorMJJException {
        ASTMJJdecls astdecls = new ASTMJJdecls(JJTDECLS);
        ASTMJJdecls astdecls2 = new ASTMJJdecls(JJTDECLS);
        astdecls.jjtAddChild(createNodeVarOmega("i"), 0);
        astdecls2.jjtAddChild(createNodeVarOmega("j"), 0);
        astdecls2.jjtAddChild(new ASTMJJvnil(JJTVNIL), 1);
        astdecls.jjtAddChild(astdecls2, 1);
        instrsExpected.add("1 swap;\n");
        instrsExpected.add("2 pop;\n");
        instrsExpected.add("3 swap;\n");
        instrsExpected.add("4 pop;\n");
        int n = (int)compilerMJJVisitor.visit(astdecls, new CompilerMJJData(1, Mode.REMOVE));
        Assert.assertEquals(4, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testVarsRetrait() throws VisitorMJJException {
        ASTMJJvars astvars = new ASTMJJvars(JJTVARS);
        ASTMJJvars astvars2 = new ASTMJJvars(JJTVARS);
        astvars.jjtAddChild(createNodeVarOmega("i"), 0);
        astvars2.jjtAddChild(createNodeVarOmega("j"), 0);
        astvars2.jjtAddChild(new ASTMJJvnil(JJTVNIL), 1);
        astvars.jjtAddChild(astvars2, 1);
        instrsExpected.add("1 swap;\n");
        instrsExpected.add("2 pop;\n");
        instrsExpected.add("3 swap;\n");
        instrsExpected.add("4 pop;\n");
        int n = (int)compilerMJJVisitor.visit(astvars, new CompilerMJJData(1, Mode.REMOVE));
        Assert.assertEquals(4, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testListExpRetrait() throws VisitorMJJException {
        ASTMJJlistexp astlistexp = new ASTMJJlistexp(JJTLISTEXP);
        ASTMJJlistexp astlistexp2 = new ASTMJJlistexp(JJTLISTEXP);
        ASTMJJnbre astnbre1 = createNodeNbre(42);
        ASTMJJnbre astnbre2 = createNodeNbre(41);
        astlistexp.jjtAddChild(astnbre1, 0);
        astlistexp2.jjtAddChild(astnbre2, 0);
        astlistexp2.jjtAddChild(new ASTMJJexnil(JJTEXNIL), 1);
        astlistexp.jjtAddChild(astlistexp2, 1);
        instrsExpected.add("1 swap;\n");
        instrsExpected.add("2 pop;\n");
        instrsExpected.add("3 swap;\n");
        instrsExpected.add("4 pop;\n");
        int n = (int)compilerMJJVisitor.visit(astlistexp, new CompilerMJJData(1, Mode.REMOVE));
        Assert.assertEquals(4, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testTableauRetrait() throws VisitorMJJException {
        ASTMJJtableau asttableau = new ASTMJJtableau(JJTTABLEAU);
        ASTMJJident astident = createNodeIdent("ta");
        ASTMJJnbre astnbre = createNodeNbre(0);
        asttableau.jjtAddChild(astident, 0);
        asttableau.jjtAddChild(astnbre, 1);
        instrsExpected.add("1 swap;\n");
        instrsExpected.add("2 pop;\n");
        int n = (int)compilerMJJVisitor.visit(asttableau, new CompilerMJJData(1, Mode.REMOVE));
        Assert.assertEquals(2, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }

    @Test
    public void testNodeLongeur() throws VisitorMJJException {
        ASTMJJlongueur astlongueur = new ASTMJJlongueur(JJTLONGUEUR);
        ASTMJJident astident = createNodeIdent("ta");
        astlongueur.jjtAddChild(astident, 0);
        instrsExpected.add("1 length(ta);\n");
        int n = (int)compilerMJJVisitor.visit(astlongueur, new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(1, n);
        Assert.assertEquals(instrsExpected, compilerMJJVisitor.getInstrsArray());
    }
}
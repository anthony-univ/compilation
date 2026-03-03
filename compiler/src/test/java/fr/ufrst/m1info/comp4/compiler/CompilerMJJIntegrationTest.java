package fr.ufrst.m1info.comp4.compiler;

import fr.ufrst.m1info.comp4.parser.minijaja.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CompilerMJJIntegrationTest {
    final private String path = "src/test/resources/expected/";
    private CompilerMJJVisitor compilerMJJVisitor;

    @Before
    public void init() {
        compilerMJJVisitor = new CompilerMJJVisitor();
    }

    private String readFile(String name) throws IOException {
        String line = "";
        StringBuilder res = new StringBuilder();
        BufferedReader lecteur = new BufferedReader(new FileReader(path + name));
        while ((line = lecteur.readLine()) != null) {
            res.append(line+"\n");
        }
        lecteur.close();
        return res.toString();
    }

    @Test
    public void testClasseEmpty() throws ParseException, IOException, VisitorMJJException {
        String code = "class C { main { } }";
        InputStream stream = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
        Minijaja parser = new Minijaja(stream);
        SimpleNode root = parser.classe();
        int n = (int)compilerMJJVisitor.visit((ASTMJJclasse) root,  new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(4, n);
        Assert.assertEquals(readFile("minimumCode.txt"), compilerMJJVisitor.getInstrs());
    }

    @Test
    public void testDeclsVars() throws ParseException, IOException, VisitorMJJException{
        String code = "class C { int x=0; final int y;  main { boolean b = true;} }";
        InputStream stream = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
        Minijaja parser = new Minijaja(stream);
        SimpleNode root = parser.classe();
        CompilerMJJ compiler = new CompilerMJJ(root);
        compiler.compile();
        int n = (int)compilerMJJVisitor.visit((ASTMJJclasse) root,  new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(16, n);
        Assert.assertEquals(readFile("declsVars.txt"), compilerMJJVisitor.getInstrs());
    }

    @Test
    public void testCours() throws ParseException, IOException, VisitorMJJException{
        String code = "class C {  int x=0; int f(int p) { return p; }; main { x=3; x+= f(x); } }";
        InputStream stream = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
        Minijaja parser = new Minijaja(stream);
        SimpleNode root = parser.classe();
        CompilerMJJ compiler = new CompilerMJJ(root);
        compiler.compile();
        int n = (int)compilerMJJVisitor.visit((ASTMJJclasse) root,  new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(25, n);
        Assert.assertEquals(readFile("cours.txt"), compilerMJJVisitor.getInstrs());
    }

    @Test
    public void testIf() throws ParseException, IOException, VisitorMJJException{
        String code = "class C {int a = 4; main { if(a>6) { a++; }; }}";
        InputStream stream = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
        Minijaja parser = new Minijaja(stream);
        SimpleNode root = parser.classe();
        int n = (int)compilerMJJVisitor.visit((ASTMJJclasse) root,  new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(15, n);
        Assert.assertEquals(readFile("if.txt"), compilerMJJVisitor.getInstrs());
    }

    @Test
    public void testIfElse() throws ParseException, IOException, VisitorMJJException{
        String code = "class C { int a = 4; int b = 0; main { if(6>a) { a++; b+=6; }else{ a=4; b = 4*5; }; }}";
        InputStream stream = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
        Minijaja parser = new Minijaja(stream);
        SimpleNode root = parser.classe();
        int n = (int)compilerMJJVisitor.visit((ASTMJJclasse) root,  new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(27, n);
        Assert.assertEquals(readFile("ifelse.txt"), compilerMJJVisitor.getInstrs());
    }

    @Test
    public void testWhile() throws ParseException, IOException, VisitorMJJException {
        String code = "class C { main { boolean b = false; while(!b) { write(b); writeln(\"in while\"); }; }}";
        InputStream stream = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
        Minijaja parser = new Minijaja(stream);
        SimpleNode root = parser.classe();
        int n = (int)compilerMJJVisitor.visit((ASTMJJclasse) root,  new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(17, n);
        Assert.assertEquals(readFile("while.txt"), compilerMJJVisitor.getInstrs());
    }

    @Test
    public void testDeclMeths() throws ParseException, IOException, VisitorMJJException {
        String code = "class C { int f(int i, boolean b) { return i; }; void g(){}; boolean h(){}; main { }}";
        InputStream stream = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
        Minijaja parser = new Minijaja(stream);
        SimpleNode root = parser.classe();
        int n = (int)compilerMJJVisitor.visit((ASTMJJclasse) root,  new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(30, n);
        Assert.assertEquals(readFile("declsMeths.txt"), compilerMJJVisitor.getInstrs());
    }

    @Test
    public void testOperatorMath() throws ParseException, IOException, VisitorMJJException {
        String code = "class C { main { int a = (1 + (-2)) - (-3) * (4 / (-5)); }}";
        InputStream stream = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
        Minijaja parser = new Minijaja(stream);
        SimpleNode root = parser.classe();
        int n = (int)compilerMJJVisitor.visit((ASTMJJclasse) root,  new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(19, n);
        Assert.assertEquals(readFile("operatorMath.txt"), compilerMJJVisitor.getInstrs());
    }

    @Test
    public void testOperatorComp() throws ParseException, IOException, VisitorMJJException {
        String code = "class C { main { if (3 > a){ a = 1; }; if(5==b){ b=2; }; }}";
        InputStream stream = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
        Minijaja parser = new Minijaja(stream);
        SimpleNode root = parser.classe();
        int n = (int)compilerMJJVisitor.visit((ASTMJJclasse) root,  new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(18, n);
        Assert.assertEquals(readFile("operatorComp.txt"), compilerMJJVisitor.getInstrs());
    }

    @Test
    public void testOperatorET_OR_INV() throws ParseException, IOException, VisitorMJJException {
        String code = "class C { main { if ((!a[1]) && b){ }; if((a || b) && c){ }; }}";
        InputStream stream = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
        Minijaja parser = new Minijaja(stream);
        SimpleNode root = parser.classe();
        int n = (int)compilerMJJVisitor.visit((ASTMJJclasse) root,  new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(18, n);
        Assert.assertEquals(readFile("operatorET_OR_INV.txt"), compilerMJJVisitor.getInstrs());
    }

    @Test
    public void testInstrs() throws ParseException, IOException, VisitorMJJException {
        String code = "class C { main { int a = 1; a =6; a++; a+=2; }}";
        InputStream stream = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
        Minijaja parser = new Minijaja(stream);
        SimpleNode root = parser.classe();
        int n = (int)compilerMJJVisitor.visit((ASTMJJclasse) root,  new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(14, n);
        Assert.assertEquals(readFile("instrs.txt"), compilerMJJVisitor.getInstrs());
    }

    @Test
    public void testArray() throws ParseException, IOException, VisitorMJJException {
        String code = "class C { int a[6]; main { a[1] = 4; a[2] ++; a[3]+=a[2]; a[4] = length(a);} }}";
        InputStream stream = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
        Minijaja parser = new Minijaja(stream);
        SimpleNode root = parser.classe();
        int n = (int)compilerMJJVisitor.visit((ASTMJJclasse) root,  new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(21, n);
        Assert.assertEquals(readFile("array.txt"), compilerMJJVisitor.getInstrs());
    }

    @Test
    public void testAppelIAppelE() throws ParseException, IOException, VisitorMJJException {
        String code = "class C { main { int a = f(1, true); g(); }}";
        InputStream stream = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
        Minijaja parser = new Minijaja(stream);
        SimpleNode root = (SimpleNode) parser.classe();
        int n = (int)compilerMJJVisitor.visit((ASTMJJclasse) root,  new CompilerMJJData(1, Mode.ADD));
        Assert.assertEquals(16, n);
        Assert.assertEquals(readFile("appelIAppelE.txt"), compilerMJJVisitor.getInstrs());
    }

}
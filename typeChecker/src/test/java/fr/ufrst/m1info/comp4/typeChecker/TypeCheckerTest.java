package fr.ufrst.m1info.comp4.typeChecker;

import fr.ufrst.m1info.comp4.memory.SymbolTable;
import fr.ufrst.m1info.comp4.parser.minijaja.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class TypeCheckerTest {
	final private String path = "src/test/resources/expected/";
	final private String pathSrcExample = "../src/main/resources/";
	
	private TypeChecker typeChecker;
	private SimpleNode root;

	public void launch(String code) throws ParseException, VisitorMJJException {
		typeChecker = new TypeChecker(null);
		InputStream stream = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
		Minijaja parser = new Minijaja(stream);
		root = parser.classe();
		this.typeChecker = new TypeChecker(root);

		SymbolTable table = new SymbolTable();
		typeChecker.setTable(table);
		typeChecker.check();
		//System.out.println(typeChecker.getErrors());
		//System.out.println(ReprAst.getSimpleRepr(root));
	}

	private String readFile(String pathTofile, String name) throws IOException {
		String line = "";
		StringBuilder res = new StringBuilder();
		BufferedReader lecteur = new BufferedReader(new FileReader(pathTofile + name));
		while ((line = lecteur.readLine()) != null) {
			res.append(line+"\n");
		}
		lecteur.close();
		return res.toString().substring(0, res.length()-1);
	}

	@Test
	public void testVoidRetValide() throws ParseException, VisitorMJJException, IOException {
		String code = "class C { void f(){}; main { } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
		Assert.assertEquals(readFile(path, "testVoidRetValide.txt"), ReprAst.getSimpleRepr(root));
	}

	@Test
	public void testVoidRet() throws ParseException, VisitorMJJException {
		String code = "class C { void f(){return 4;}; main { } }";
		launch(code);
		Assert.assertEquals(2, typeChecker.getErrors().size());
	}

	@Test
	public void testRetMain() throws ParseException, VisitorMJJException {
		String code = "class C {  main { int c = 4 ; return c; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void testIntRetValide() throws ParseException, VisitorMJJException, IOException {
		String code = "class C { int f(){return 4;}; main { } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
		Assert.assertEquals(readFile(path, "testIntRetValide.txt"), ReprAst.getSimpleRepr(root));
	}

	@Test
	public void testMissingReturn() throws ParseException, VisitorMJJException {
		String code = "class C { int f(){}; main { } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void testFctIntRetBool() throws ParseException, VisitorMJJException {
		String code = "class C { int f(){if(true){return 1;}else{return true;};}; main { } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void testBoolRetIfElse() throws ParseException, VisitorMJJException, IOException {
		String code = "class C { boolean a = true; boolean f(){if(true){return false;}else{return a;};}; main { } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
		Assert.assertEquals(readFile(path, "testBoolRetIfElse.txt"), ReprAst.getSimpleRepr(root));
	}

	@Test
	public void testBoolRetIfElseMissingReturnElse() throws ParseException, VisitorMJJException {
		String code = "class C { boolean f(){if(true){return false;}else{};}; main { } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void testBoolRetIfElseMissingReturnIf() throws ParseException, VisitorMJJException {
		String code = "class C { boolean f(){if(true){}else{return true;};}; main { } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void testBoolRetIfElseMiisingRetElse() throws ParseException, VisitorMJJException {
		String code = "class C { boolean f(){if(true){return true;}else{};}; main { } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void testIfVarNotDeclared() throws ParseException, VisitorMJJException {
		String code = "class C { void f(){if(a){if(b){return true;}else{};};}; main { } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void testMissingIntRetWhile() throws ParseException, VisitorMJJException {
		String code = "class C { int f(){while(true){return 1;};}; main { } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void testIntRetWhileValide() throws ParseException, VisitorMJJException, IOException {
		String code = "class C { int i; int f(){int i; while(true){return i+1;}; return i;}; main { } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
		Assert.assertEquals(ReprAst.getSimpleRepr(root), readFile(path, "testIntRetWhileValide.txt"));
	}

	@Test
	public void testVoidRetWhile() throws ParseException, VisitorMJJException {
		String code = "class C { void f(){while(true){if(false){return 1;};};}; main { } }";
		launch(code);
		Assert.assertEquals(2, typeChecker.getErrors().size());
	}

	@Test
	public void testMissingIntRetIf() throws ParseException, VisitorMJJException {
		String code = "class C { int f(){if(true){return 2;};}; main { } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void testIntRet() throws ParseException, VisitorMJJException, IOException {
		String code = "class C { int a=4; int f(){int c = 6; if(true){return c;};return a;}; main { } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
		Assert.assertEquals(ReprAst.getSimpleRepr(root), readFile(path, "testIntRet.txt"));
	}

	//HERE
	@Test
	public void testIntReNoMissingIfElse() throws ParseException, VisitorMJJException {
		String code = "class C { int a =0; int f(){if(true){return 2; }else{return 4;};}; main { } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void testIntReMissingIfElse() throws ParseException, VisitorMJJException {
		String code = "class C { int a =0; int f(){if(true){if(true){return 3;}else{}; a=0;}else{return 4;};}; main { } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void synonymieSameType() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; int tab2[2]; main { tab2 = tab; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void synonymieNotSameType() throws ParseException, VisitorMJJException {
		String code = "class C { boolean tab[4]; int tab2[2]; main { tab2 = tab; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void initArrayWithSizeArray() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; int tab2[tab]; main { } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void initArrayWithNumber() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; int tab2[2]; main { } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void incArray() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { tab++; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void incArrayAt() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { tab[0]++; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void affectSameType() throws ParseException, VisitorMJJException {
		String code = "class C {  main { int i = 4; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void affectNotSameType() throws ParseException, VisitorMJJException {
		String code = "class C {  main { int i = true; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void affectArraySameType() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; int tab2[4]; main { tab = tab2; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void affectArrayNotSameType() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; boolean tab2[4]; main { tab = tab2; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void affectArrayAt() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; int tab2[4]; main { tab[0] = tab2[1]; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void affectArrayAt2() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; int tab2[4]; main { tab[0] = tab2; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void affectArrayAt3() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; int tab2[4]; main { tab = tab2[0]; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void affectIntToArray() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; int tab2[4]; main { tab = 4; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void affectVarToArray() throws ParseException, VisitorMJJException {
		String code = "class C { int a; int tab[4]; int tab2[4]; main { tab = a; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void affectIntToArrayAt() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; int tab2[4]; main { int a; tab[0] = a; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void affectArrayToInt() throws ParseException, VisitorMJJException {
		String code = "class C { int b; int tab[4]; int tab2[4]; main { b = tab; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void sommeArray() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; int tab2[4]; main { tab += tab2; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void sommeArrayAt() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; int tab2[4]; main { tab[0] += tab2[1]; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void returnArray() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; int f(){return tab;}; main { } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void writeArray() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { write(tab); } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void writelnArray() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { writeln(tab); } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void whileArray() throws ParseException, VisitorMJJException {
		String code = "class C { boolean tab[4]; main { while(tab){}; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void ifArray() throws ParseException, VisitorMJJException {
		String code = "class C { boolean tab[4]; main { if(tab){}; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}
	@Test
	public void ifArrayAt() throws ParseException, VisitorMJJException {
		String code = "class C { boolean tab[4]; main { if(tab[0]){}; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}


	@Test
	public void appelITab() throws ParseException, VisitorMJJException {
		String code = "class C { boolean tab[4]; void f(boolean a){}; main { f(tab); } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void appelETab() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; int f(int a){return 1;}; main { int i = f(tab); } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void negArray() throws ParseException, VisitorMJJException {
		String code = "class C { boolean tab[4]; main { if(!tab){}; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void negArrayAt() throws ParseException, VisitorMJJException {
		String code = "class C { boolean tab[4]; main { if(!tab[0]){}; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void minusUnaryArray() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { int i = -tab; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void minusUnaryArrayAt() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { int i = -tab[0]; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void ANDArray() throws ParseException, VisitorMJJException {
		String code = "class C { boolean tab[4]; main { if(true&&tab){}; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void ANDArrayAt() throws ParseException, VisitorMJJException {
		String code = "class C { boolean tab[4]; main { if(true&&tab[0]){}; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void ORArray() throws ParseException, VisitorMJJException {
		String code = "class C { boolean tab[4]; main { if(tab){}; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void ORArrayAt() throws ParseException, VisitorMJJException {
		String code = "class C { boolean tab[4]; main { if(tab[0]){}; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void EQUALArraySameType() throws ParseException, VisitorMJJException {
		String code = "class C { boolean tab[4]; main { if(tab==tab){}; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void EQUALArrayNotSameType() throws ParseException, VisitorMJJException {
		String code = "class C { boolean tab[4]; int tab2[4]; main { if(tab==tab2){}; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void EQUALArrayNbre1() throws ParseException, VisitorMJJException {
		String code = "class C { boolean tab[4]; main { if(tab==4){}; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void EQUALArrayNbre2() throws ParseException, VisitorMJJException {
		String code = "class C { boolean tab[4]; main { if(4==tab){}; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void EQUALArrayVar1() throws ParseException, VisitorMJJException {
		String code = "class C { boolean tab[4]; main { int i; if(tab==i){}; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void EQUALArrayVar2() throws ParseException, VisitorMJJException {
		String code = "class C { boolean tab[4]; main { int i; if(i==tab){}; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}
	@Test
	public void EQUALArrayANDArrayAt() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; int tab2[4]; main { if(tab==tab2[0]){}; } }";
		launch(code);
		Assert.assertEquals(2, typeChecker.getErrors().size());
	}

	@Test
	public void EQUALArrayAt() throws ParseException, VisitorMJJException {
		String code = "class C { boolean tab[4]; main { if(tab[0]==tab[1]){}; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void SupArray() throws ParseException, VisitorMJJException {
		String code = "class C { boolean tab[4]; main { if(tab>tab){}; } }";
		launch(code);
		Assert.assertEquals(2, typeChecker.getErrors().size());
	}

	@Test
	public void SupArrayAt() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { if(tab[0]>tab[1]){}; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void PlusArray() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { int i = 4 + tab; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void PlusArrayAt() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { int i = 4 + tab[0]; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void MinusArray() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { int i = 4 - tab; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void MinusArrayAt() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { int i = 4 - tab[0]; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}


	@Test
	public void MultArray() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { int i = 4 * tab; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void MultArrayAt() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { int i = 4 * tab[0]; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void DivArray() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { int i = 4 / tab; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void DivArrayAt() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { int i = 4 / tab[0]; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void WhileVarNotInit() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { while(i){writeln(\"YES\");}; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void LengthIdent() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { int a; int i = length(a); } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void LengthIdentNoInit() throws ParseException, VisitorMJJException {
		String code = "class C { main { int i = length(tab2); } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void LengthTab() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { int a; int i = length(tab); } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void RedeclarationVar() throws ParseException, VisitorMJJException {
		String code = "class C { boolean a; main { int a; boolean a; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void declarationVarVoid() throws ParseException, VisitorMJJException {
		String code = "class C { main { void a; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void RedeclarationVcst() throws ParseException, VisitorMJJException {
		String code = "class C { boolean a; main { final int a; final boolean a; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void RedeclarationMeth() throws ParseException, VisitorMJJException {
		String code = "class C { void a(){}; void a(){}; main { } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void declarationTableauVoid() throws ParseException, VisitorMJJException {
		String code = "class C { boolean a; main { void a[4]; void g[6]; } }";
		launch(code);
		Assert.assertEquals(2, typeChecker.getErrors().size());
	}

	@Test
	public void RedeclarationTableau() throws ParseException, VisitorMJJException {
		String code = "class C { int a[4]; boolean a[6]; main { } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void declarationTableauVarSizeDeclared() throws ParseException, VisitorMJJException {
		String code = "class C { int l; boolean a[l]; main { } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void declarationTableauLength() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; boolean a[length(tab)]; main { } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void declarationTableauVarSizeUndeclared() throws ParseException, VisitorMJJException {
		String code = "class C {boolean a[l]; main { } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void declarationTableauLengthNoInt() throws ParseException, VisitorMJJException {
		String code = "class C { boolean l; int a[l]; main { } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void SurchargeFct() throws ParseException, VisitorMJJException {
		String code = "class C { void a(){}; void a(int i){}; boolean a(int i, int j){return true;}; main { } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void SurchargeFctNotSameType() throws ParseException, VisitorMJJException {
		String code = "class C { void a(){}; int a(int i){return 1;}; boolean a(int i)\n{return false;}; main { } }";
		launch(code);
		Assert.assertEquals(2, typeChecker.getErrors().size());
	}


	@Test
	public void AppelCrossFct() throws ParseException, VisitorMJJException {
		String code = "class C { void a(){a(4);}; int a(int i){return 1;};  main { } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void AppelCrossVar() throws ParseException, VisitorMJJException {
		String code = "class C { int a=b; int b; main { } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void initIntCstArray() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { final int i = tab;} }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void initIntCstArrayAt() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { final int i = tab[0];} }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void initIntVarArray() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { int i = tab;} }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void initIntVarArrayAt() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { int i = tab[0];} }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void initBoolCstArray() throws ParseException, VisitorMJJException {
		String code = "class C { boolean tab[4]; main { final boolean i = true && tab;} }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void initBoolCstArrayAt() throws ParseException, VisitorMJJException {
		String code = "class C { boolean tab[4]; main { final boolean i = false || tab[0];} }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void initBoolVarArray() throws ParseException, VisitorMJJException {
		String code = "class C { boolean tab[4]; main { boolean i = true && tab;} }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void initBoolVarArrayAt() throws ParseException, VisitorMJJException {
		String code = "class C { boolean tab[4]; main { boolean i = false || tab[0];} }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void initBoolVarBadType() throws ParseException, VisitorMJJException {
		String code = "class C { int b; main { boolean i = true && b;} }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void initBoolVarGoodType() throws ParseException, VisitorMJJException {
		String code = "class C { boolean b; main { boolean i = false || b;} }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void initVoidVar() throws ParseException, VisitorMJJException {
		String code = "class C { void b; main {} }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void initVarGoodType() throws ParseException, VisitorMJJException {
		String code = "class C { int b = 5; boolean c = true; main {} }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void appelEBadType() throws ParseException, VisitorMJJException {
		String code = "class C { void b(){}; int c(){return 1;}; main {boolean i = c(); i = b();} }";
		launch(code);
		Assert.assertEquals(2, typeChecker.getErrors().size());
	}

	@Test
	public void appelEGoodType() throws ParseException, VisitorMJJException {
		String code = "class C { int c(){return 1;}; main {int i = c();} }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void appelEBadTypeEntete() throws ParseException, VisitorMJJException {
		String code = "class C { int b(){return 0;}; int c(boolean a){return 1;}; main { int i = c(4); i = b(true);} }";
		launch(code);
		Assert.assertEquals(2, typeChecker.getErrors().size());
	}

	@Test
	public void appelEBadTypeEnteteArray() throws ParseException, VisitorMJJException {
		String code = "class C { int c(boolean a){return 1;}; main {boolean tab[4]; int i = c(tab); } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void appelEGoodTypeEntete() throws ParseException, VisitorMJJException {
		String code = "class C { int b(){return 0;}; int c(boolean a, int j){return 1;}; main {int i = c(true, 4); i = b();} }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void appelArrayWithNoArray() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { int i = 0; int j = i[0]; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void appelArrayWithArray() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { int i = 0; int j = tab[0]; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void whileConditionBadType() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { while(tab[0]){}; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void InitCst() throws ParseException, VisitorMJJException {
		String code = "class C { boolean a; main { final int a = c; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void InitCstBadType() throws ParseException, VisitorMJJException {
		String code = "class C { boolean a; main { final int a = true; final boolean c = a; } }";
		launch(code);
		Assert.assertEquals(2, typeChecker.getErrors().size());
	}

	@Test
	public void enteteSameName() throws ParseException, VisitorMJJException {
		String code = "class C { void a(int i, boolean i){}; main { } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void enteteNotSameName() throws ParseException, VisitorMJJException {
		String code = "class C { void a(int i, boolean j){}; main { } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void writeChaine() throws ParseException, VisitorMJJException {
		String code = "class C { main { write(\"SALUT\"); } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void writeLnChaine() throws ParseException, VisitorMJJException {
		String code = "class C { main { writeln(\"SALUT\"); } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void undeclaredFctVoid() throws ParseException, VisitorMJJException {
		String code = "class C { main { f(); g(4); } }";
		launch(code);
		Assert.assertEquals(2, typeChecker.getErrors().size());
	}

	@Test
	public void undeclaredFctInt() throws ParseException, VisitorMJJException {
		String code = "class C { main { int i; i = f(); i = g(4); } }";
		launch(code);
		Assert.assertEquals(2, typeChecker.getErrors().size());
	}

	@Test
	public void affectationTwoUndeclared() throws ParseException, VisitorMJJException {
		String code = "class C { main { a = b; } }";
		launch(code);
		Assert.assertEquals(2, typeChecker.getErrors().size());
	}

	@Test
	public void incrementVarNotInitialized() throws ParseException, VisitorMJJException {
		String code = "class C { main {b++; tab[0]++;} }";
		launch(code);
		Assert.assertEquals(2, typeChecker.getErrors().size());
	}

	@Test
	public void incrementNoInt() throws ParseException, VisitorMJJException {
		String code = "class C { main { boolean b; boolean tab[4]; b++; tab[0]++;} }";
		launch(code);
		Assert.assertEquals(2, typeChecker.getErrors().size());
	}

	@Test
	public void incrementInt() throws ParseException, VisitorMJJException {
		String code = "class C { main { int b; int tab[4]; b++; tab[0]++;} }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void retNotInit() throws ParseException, VisitorMJJException {
		String code = "class C { int f(){return a;}; main {} }";
		launch(code);
		Assert.assertEquals(2,typeChecker.getErrors().size());
	}

	@Test
	public void tabNoInit() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[2]; main {int i; i = tab[l]; tab2[l] = 4;} }";
		launch(code);
		Assert.assertEquals(3,typeChecker.getErrors().size());
	}

	@Test
	public void tabNoIntPos() throws ParseException, VisitorMJJException {
		String code = "class C { main { int tab[4]; boolean b; int i; i = tab[b];} }";
		launch(code);
		Assert.assertEquals(1,typeChecker.getErrors().size());
	}

	@Test
	public void nonNoInit() throws ParseException, VisitorMJJException {
		String code = "class C { main { if(!b){}; } }";
		launch(code);
		Assert.assertEquals(1,typeChecker.getErrors().size());
	}

	@Test
	public void nonNoBoolean() throws ParseException, VisitorMJJException {
		String code = "class C { main { int b = 4; if(!b){}; } }";
		launch(code);
		Assert.assertEquals(1,typeChecker.getErrors().size());
	}

	@Test
	public void EQUALTwoNoInits() throws ParseException, VisitorMJJException {
		String code = "class C { main { if(a==b){}; } }";
		launch(code);
		Assert.assertEquals(2,typeChecker.getErrors().size());
	}

	@Test
	public void EQUALFirtNoInit() throws ParseException, VisitorMJJException {
		String code = "class C { main { boolean b= false; if(b==a){};} }";
		launch(code);
		Assert.assertEquals(1,typeChecker.getErrors().size());
	}

	@Test
	public void EQUALSecondNoInit() throws ParseException, VisitorMJJException {
		String code = "class C { main { boolean b= false; if(a==b){};} }";
		launch(code);
		Assert.assertEquals(1,typeChecker.getErrors().size());
	}

	@Test
	public void minusUnaryNotInit() throws ParseException, VisitorMJJException {
		String code = "class C { main { int i= -a; } }";
		launch(code);
		Assert.assertEquals(1,typeChecker.getErrors().size());
	}

	@Test
	public void minusUnaryNoInt() throws ParseException, VisitorMJJException {
		String code = "class C { main { boolean a; int i= -a; } }";
		launch(code);
		Assert.assertEquals(1,typeChecker.getErrors().size());
	}

	@Test
	public void initVarWithSelfInClass() throws ParseException, VisitorMJJException {
		String code = "class C { boolean a = a; main { } }";
		launch(code);
		Assert.assertEquals(1,typeChecker.getErrors().size());
	}

	@Test
	public void initVarWithSelfInMain() throws ParseException, VisitorMJJException {
		String code = "class C { main { boolean a = a; } }";
		launch(code);
		Assert.assertEquals(1,typeChecker.getErrors().size());
	}

	@Test
	public void initVcstWithSelfInClass() throws ParseException, VisitorMJJException {
		String code = "class C { final boolean a = a;  main { } }";
		launch(code);
		Assert.assertEquals(1,typeChecker.getErrors().size());
	}

	@Test
	public void initVcstWithSelfInMain() throws ParseException, VisitorMJJException {
		String code = "class C { main { final boolean a = a; } }";
		launch(code);
		Assert.assertEquals(1,typeChecker.getErrors().size());
	}

	@Test
	public void initVarWithVarSameNameClasse() throws ParseException, VisitorMJJException {
		String code = "class C { boolean a=true; main { boolean a = a; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void initVcstWithVarSameNameClasse() throws ParseException, VisitorMJJException {
		String code = "class C { final boolean a=true; main { final boolean a = a; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}
	@Test
	public void initVarWithSelf() throws ParseException, VisitorMJJException {
		String code = "class C { boolean a = a; main { } }";
		launch(code);
		Assert.assertEquals(1,typeChecker.getErrors().size());
	}

	@Test
	public void initVcstWithSelf() throws ParseException, VisitorMJJException {
		String code = "class C { final boolean a = a; main { } }";
		launch(code);
		Assert.assertEquals(1,typeChecker.getErrors().size());
	}

	@Test
	public void affectSynnoymieNoInit() throws ParseException, VisitorMJJException {
		String code = "class C { main {  boolean a[4]; a=b; } }";
		launch(code);
		Assert.assertEquals(1,typeChecker.getErrors().size());
	}

	@Test
	public void etTwoNoInits() throws ParseException, VisitorMJJException {
		String code = "class C { main {  if(a&&b){}; } }";
		launch(code);
		Assert.assertEquals(2,typeChecker.getErrors().size());
	}

	@Test
	public void etFirstNoInit() throws ParseException, VisitorMJJException {
		String code = "class C { main { boolean b = true; if(a&&b){}; } }";
		launch(code);
		Assert.assertEquals(1,typeChecker.getErrors().size());
	}

	@Test
	public void etSecondNoInit() throws ParseException, VisitorMJJException {
		String code = "class C { main { boolean b = true; if(b&&a){}; } }";
		launch(code);
		Assert.assertEquals(1,typeChecker.getErrors().size());
	}

	@Test
	public void ORnotBools() throws ParseException, VisitorMJJException {
		String code = "class C { main { int a=4; int b=2; if(b||a){}; } }";
		launch(code);
		Assert.assertEquals(1,typeChecker.getErrors().size());
	}



	@Test
	public void SupTwoNoInits() throws ParseException, VisitorMJJException {
		String code = "class C { main {  if(a>b){}; } }";
		launch(code);
		Assert.assertEquals(2,typeChecker.getErrors().size());
	}

	@Test
	public void SupFirstNoInit() throws ParseException, VisitorMJJException {
		String code = "class C { main { int b = 6; if(a>b){}; } }";
		launch(code);
		Assert.assertEquals(1,typeChecker.getErrors().size());
	}

	@Test
	public void SupSecondNoInit() throws ParseException, VisitorMJJException {
		String code = "class C { main { int b = 6; if(b>a){}; } }";
		launch(code);
		Assert.assertEquals(1,typeChecker.getErrors().size());
	}

	@Test
	public void SupFirstNotInt() throws ParseException, VisitorMJJException {
		String code = "class C { main { boolean a=true; int b=6; if(b>a){}; } }";
		launch(code);
		Assert.assertEquals(1,typeChecker.getErrors().size());
	}

	@Test
	public void SupSecondNotInt() throws ParseException, VisitorMJJException {
		String code = "class C { main { int a=6; boolean b=true; if(b>a){}; } }";
		launch(code);
		Assert.assertEquals(1,typeChecker.getErrors().size());
	}

	@Test
	public void SupValid() throws ParseException, VisitorMJJException {
		String code = "class C { main { int a=4; int b=6; if(b>a){}; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void SupnotInts() throws ParseException, VisitorMJJException {
		String code = "class C { main { boolean a=true; boolean b=false; if(b>a){}; } }";
		launch(code);
		Assert.assertEquals(1,typeChecker.getErrors().size());
	}

	@Test
	public void appelFctValid() throws ParseException, VisitorMJJException {
		String code = "class C { int f(int i, boolean j){return 1;}; main { int a=f(6, true); f(4, true); } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void initVarWithTab() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { int a=tab; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void initVcstWithTab() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[4]; main { final int a=tab; } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void initTableauSizeSelf() throws ParseException, VisitorMJJException {
		String code = "class C { int tab[length(tab)]; main { } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void initTableauSizeSelfInMain() throws ParseException, VisitorMJJException {
		String code = "class C {  int tab[4]; main { int tab[length(tab)]; } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void initVarInFctWithSelfClass() throws ParseException, VisitorMJJException, IOException {
		String code = "class C { int i; void f(int j){int i=i;}; main{ } }";
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
		Assert.assertEquals(ReprAst.getSimpleRepr(root), readFile(path, "initVarInFctWithSelfClass.txt"));
	}

	@Test
	public void initVarInFctWithSelf() throws ParseException, VisitorMJJException, IOException {
		String code = "class C { void f(int j){int i=i;}; main{ } }";
		launch(code);
		Assert.assertEquals(1, typeChecker.getErrors().size());
	}

	@Test
	public void Test1() throws ParseException, VisitorMJJException, IOException {
		String code = readFile(pathSrcExample, "1.mjj");
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void TestFact() throws ParseException, VisitorMJJException, IOException {
		String code = readFile(pathSrcExample, "fact.mjj");
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void TestQuickSort() throws ParseException, VisitorMJJException, IOException {
		String code = readFile(pathSrcExample, "quick_sort.mjj");
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void TestSynonymie() throws ParseException, VisitorMJJException, IOException {
		String code = readFile(pathSrcExample, "synonymie.mjj");
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}

	@Test
	public void TestTas() throws ParseException, VisitorMJJException, IOException {
		String code = readFile(pathSrcExample, "tas.mjj");
		launch(code);
		Assert.assertTrue(typeChecker.getErrors().isEmpty());
	}
}
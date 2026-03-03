package fr.ufrst.m1info.comp4.GUI;

import fr.ufrst.m1info.comp4.compiler.CompilerMJJ;
import fr.ufrst.m1info.comp4.interpreter.jajacode.InterpreterJJC;
import fr.ufrst.m1info.comp4.interpreter.minijaja.InterpreterException;
import fr.ufrst.m1info.comp4.interpreter.minijaja.InterpreterMJJ;
import fr.ufrst.m1info.comp4.memory.*;
import fr.ufrst.m1info.comp4.parser.minijaja.*;
import fr.ufrst.m1info.comp4.parser.jajacode.*;
import fr.ufrst.m1info.comp4.parser.minijaja.ParseException;
import fr.ufrst.m1info.comp4.typeChecker.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class AcceptanceTest {
	final private String path = "../src/main/resources/";
	private TypeChecker typeChecker;
	private InterpreterMJJ interpreterMJJ;
	private InterpreterJJC interpreterJJC;
	private Memory memory;

	public fr.ufrst.m1info.comp4.parser.minijaja.SimpleNode check(String code) throws fr.ufrst.m1info.comp4.parser.minijaja.ParseException, VisitorMJJException {
		typeChecker = new TypeChecker(null);
		InputStream stream = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
		Minijaja parser = new Minijaja(stream);
		fr.ufrst.m1info.comp4.parser.minijaja.SimpleNode root = parser.classe();
		this.typeChecker = new TypeChecker(root);

		memory = new Memory();
		typeChecker.setTable(memory.getSymbolTable());
		typeChecker.check();
		return root;
	}

	public void interpretMJJ(fr.ufrst.m1info.comp4.parser.minijaja.SimpleNode node) throws VisitorMJJException {
		interpreterMJJ = new InterpreterMJJ(node);
		interpreterMJJ.setMemory(memory);
		interpreterMJJ.interpret();
	}

	public String compile(fr.ufrst.m1info.comp4.parser.minijaja.SimpleNode node) throws VisitorMJJException {
		CompilerMJJ compiler = new CompilerMJJ(node);
		compiler.compile();
		return compiler.getInstrs();
	}

	public void interpretJJC(String code) throws VisitorJJCException, fr.ufrst.m1info.comp4.parser.jajacode.ParseException {
		InputStream stream = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
		Jajacode parser = new Jajacode(stream);
		fr.ufrst.m1info.comp4.parser.jajacode.SimpleNode node = parser.classe();

		interpreterJJC = new InterpreterJJC(node);
		interpreterJJC.setMemory(memory);
		interpreterJJC.interpret();
	}

	private String readFile(String pathTofile, String name) throws IOException {
		String line = "";
		StringBuilder res = new StringBuilder();
		BufferedReader lecteur = new BufferedReader(new FileReader(pathTofile + name));
		while ((line = lecteur.readLine()) != null) {
			res.append(line).append("\n");
		}
		lecteur.close();
		return res.substring(0, res.length()-1);
	}

	@Test
	public void tasTest() throws IOException, ParseException, VisitorMJJException, fr.ufrst.m1info.comp4.parser.jajacode.ParseException, VisitorJJCException {
		String code = readFile(path, "tas.mjj");
		fr.ufrst.m1info.comp4.parser.minijaja.SimpleNode node = check(code);
		interpretMJJ(node);
		Assert.assertTrue(memory.stackIsEmpty());

		String jjc = compile(node);
		interpretJJC(jjc);
		Assert.assertTrue(memory.stackIsEmpty());
	}

	@Test(expected = InterpreterException.class)
	public void unMJJTest() throws IOException, ParseException, VisitorMJJException, fr.ufrst.m1info.comp4.parser.jajacode.ParseException, VisitorJJCException {
		String code = readFile(path, "1.mjj");
		fr.ufrst.m1info.comp4.parser.minijaja.SimpleNode node = check(code);
		interpretMJJ(node);
	}

	@Test(expected = fr.ufrst.m1info.comp4.interpreter.jajacode.InterpreterException.class)
	public void unJJCTest() throws IOException, ParseException, VisitorMJJException, fr.ufrst.m1info.comp4.parser.jajacode.ParseException, VisitorJJCException {
		String code = readFile(path, "1.mjj");
		fr.ufrst.m1info.comp4.parser.minijaja.SimpleNode node = check(code);

		String jjc = compile(node);
		interpretJJC(jjc);
	}

	@Test
	public void factTest() throws IOException, ParseException, VisitorMJJException, fr.ufrst.m1info.comp4.parser.jajacode.ParseException, VisitorJJCException {
		String code = readFile(path, "fact.mjj");
		fr.ufrst.m1info.comp4.parser.minijaja.SimpleNode node = check(code);
		interpretMJJ(node);
		Assert.assertTrue(memory.stackIsEmpty());
		Assert.assertEquals("5040\n", interpreterMJJ.getOutput());

		String jjc = compile(node);
		interpretJJC(jjc);
		Assert.assertTrue(memory.stackIsEmpty());
		Assert.assertEquals("5040\n", interpreterJJC.getOutput());
	}

	@Test
	public void quickSortTest() throws IOException, ParseException, VisitorMJJException, fr.ufrst.m1info.comp4.parser.jajacode.ParseException, VisitorJJCException {
		String code = readFile(path, "quick_sort.mjj");
		fr.ufrst.m1info.comp4.parser.minijaja.SimpleNode node = check(code);
		interpretMJJ(node);
		Assert.assertTrue(memory.stackIsEmpty());

		String jjc = compile(node);
		interpretJJC(jjc);
		Assert.assertTrue(memory.stackIsEmpty());
	}

	@Test
	public void synonymieTest() throws IOException, ParseException, VisitorMJJException, fr.ufrst.m1info.comp4.parser.jajacode.ParseException, VisitorJJCException {
		String code = readFile(path, "synonymie.mjj");
		fr.ufrst.m1info.comp4.parser.minijaja.SimpleNode node = check(code);
		interpretMJJ(node);
		Assert.assertTrue(memory.stackIsEmpty());

		String jjc = compile(node);
		interpretJJC(jjc);
		Assert.assertTrue(memory.stackIsEmpty());
	}
}
package fr.ufrst.m1info.comp4.compiler;


import fr.ufrst.m1info.comp4.parser.minijaja.Node;
import fr.ufrst.m1info.comp4.parser.minijaja.VisitorMJJException;

import java.io.*;
import java.util.ArrayList;

public class CompilerMJJ {
    private CompilerMJJVisitor visitor;
    private Node root;

    public void setRoot(Node root) {
        this.root = root;
    }

    public CompilerMJJ(Node root) {
        this.visitor = new CompilerMJJVisitor();
        this.root = root;
    }

    public void compile() throws VisitorMJJException {
        this.visitor = new CompilerMJJVisitor();
        if(this.root == null) throw new VisitorMJJException("Root is null", 0, 0);
        this.root.jjtAccept(this.visitor, new CompilerMJJData(1, Mode.ADD));
    }

    public String getInstrs() {
        String instrs = this.visitor.getInstrs();
        return instrs.substring(0, instrs.length() - 1);
    }

    public String getInstrsWithoutAddress() {
        ArrayList<String> instrs = this.visitor.getInstrsArray();
        ArrayList<String> instrs2 = new ArrayList<>();
        for (String instr : instrs) {
            int spaceIndex = instr.indexOf(" ");
            if (spaceIndex != -1) {
                // Extraire la sous-chaîne après le premier espace
                instrs2.add(instr.substring(spaceIndex + 1));
            }
        }

        String instrsString = String.join("", instrs2);
        return instrsString.substring(0, instrsString.length() - 1);
    }

    public String getInstrsWithoutAddressAndScope() {
        ArrayList<String> instrs = this.visitor.getInstrsArray();
        ArrayList<String> instrs2 = new ArrayList<>();
        for (String instr : instrs) {
            int spaceIndex = instr.indexOf(" ");
            if (spaceIndex != -1) {
                // Enlever les paramètres de la fonction et le scope
                // Rechercher le premier "->" et le premier "@"
                int arrowIndex = instr.indexOf("->", spaceIndex);
                int atIndex = instr.indexOf("@", spaceIndex);
                int commaIndex = instr.indexOf(",", spaceIndex);
                int parenthesisIndex = instr.indexOf(")", spaceIndex);

                if (arrowIndex == -1 && atIndex == -1) {
                    instrs2.add(instr.substring(spaceIndex + 1));
                    continue;
                }

                if (commaIndex == -1) commaIndex = parenthesisIndex + 1;
                if (parenthesisIndex == -1) parenthesisIndex = commaIndex + 1;
                if (arrowIndex == -1) arrowIndex = commaIndex + 1;
                if (atIndex == -1) atIndex = parenthesisIndex + 1;

                if (arrowIndex < atIndex) {
                    if (commaIndex < parenthesisIndex) {
                        instrs2.add(instr.substring(spaceIndex + 1, arrowIndex) + instr.substring(commaIndex));
                    } else {
                        instrs2.add(instr.substring(spaceIndex + 1, arrowIndex) + instr.substring(parenthesisIndex));
                    }
                } else {
                    if (commaIndex < parenthesisIndex) {
                        instrs2.add(instr.substring(spaceIndex + 1, atIndex) + instr.substring(commaIndex));
                    } else {
                        instrs2.add(instr.substring(spaceIndex + 1, atIndex) + instr.substring(parenthesisIndex));
                    }
                }
            }
        }

        String instrsString = String.join("", instrs2);
        return instrsString.substring(0, instrsString.length() - 1);
    }

    public void instrsToFile(String fileName) throws IOException {
        BufferedWriter writer = null;
        try {
            // write instructions
            String instrs = this.visitor.getInstrs();

            writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(instrs.substring(0, instrs.length() - 1));
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}

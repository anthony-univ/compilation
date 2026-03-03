package fr.ufrst.m1info.comp4.interpreter.jajacode;

import fr.ufrst.m1info.comp4.interpreter.debugger.Debugger;
import fr.ufrst.m1info.comp4.memory.Memory;
import fr.ufrst.m1info.comp4.parser.jajacode.*;

import java.util.ArrayList;
import java.util.List;

public class InterpreterJJC {
    private final InterpreterJJCVisitor visitor;
    private final SimpleNode root;

    public InterpreterJJC(SimpleNode root) {
        this.visitor = new InterpreterJJCVisitor();
        this.root = root;
    }

    public void setMemory(Memory memory) {
        visitor.setMemory(memory);
    }
    public Memory getMemory() { return this.visitor.getMemory(); }
    public void setDebugger(Debugger debugger) { this.visitor.setDebugger(debugger); }

    public void interpret() throws VisitorJJCException {
        List<Node> instructions = instrsToNodes(this.root);
        while (this.visitor.getAdr() != -1) {
            instructions.get(this.visitor.getAdr()-1).jjtAccept(this.visitor, null);
        }
    }

    public String getOutput() {
        return visitor.getOutput();
    }

    private List<Node> instrsToNodes(Node node){
        return getNodeInstr(node);
    }

    private List<Node> getNodeInstr(Node node){
        List<Node> instrs = new ArrayList<>();
        if (node instanceof ASTJJCjcnil) {
            return instrs;
        }
        instrs.add(node.jjtGetChild(1));
        instrs.addAll(getNodeInstr(node.jjtGetChild(2)));
        return instrs;
    }
}

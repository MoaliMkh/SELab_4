package semantic.Symbol;

import CodeGenerator.Address.Address;
import CodeGenerator.Memory;

public class SymbolFacade {
    private final SymbolTable symbolTable;

    public SymbolFacade(Memory memory) {
        this.symbolTable = new SymbolTable(memory);
    }

    public void addMethodToTable(String className, String methodName, int currentCodeBlockAddress) {
        this.symbolTable.addMethod(className, methodName, currentCodeBlockAddress);
    }

    public void startCall(String className, String methodName) {
        this.symbolTable.startCall(className, methodName);
    }

    public Symbol getNextParam(String className, String methodName) {
        return this.symbolTable.getNextParam(className, methodName);
    }

    public void setLastTypeBool() {
        this.symbolTable.setLastType(SymbolType.Bool);
    }

    public void setLastTypeInt() {
        this.symbolTable.setLastType(SymbolType.Int);
    }

    public void addMethodParameter(String className, String methodName, String param) {
        this.symbolTable.addMethodParameter(className, methodName, param);
    }

    public int getMethodReturnAddress(String peek, String methodName) {
        return this.symbolTable.getMethodReturnAddress(peek, methodName);
    }

    public int getMethodCallerAddress(String peek, String methodName) {
        return this.symbolTable.getMethodCallerAddress(peek, methodName);
    }

    public void addMethodLocalVariable(String className, String methodName, String var) {
        this.symbolTable.addMethodLocalVariable(className, methodName, var);
    }

    public void setSuperClass(String pop, String peek) {
        this.symbolTable.setSuperClass(pop, peek);
    }

    public void addFieldToTable(String pop, String peek) {
        this.symbolTable.addField(pop, peek);
    }

    public void addClassToTable(String peek) {
        this.symbolTable.addClass(peek);
    }

    public Address get(String value) {
        return this.symbolTable.get(value);
    }

    public Symbol get(String pop, String pop1) {
        return this.symbolTable.get(pop, pop1);
    }

    public Symbol get(String className, String methodName, String value) {
        return this.symbolTable.get(className, methodName, value);
    }

    public int getMethodAddress(String className, String methodName) {
        return this.symbolTable.getMethodAddress(className, methodName);
    }

    public String getMethodReturnType(String peek, String methodName) {
        return String.valueOf(this.symbolTable.getMethodReturnType(peek, methodName));
    }

    public String[] getSymbolInfo(Symbol symbol) {
        return new String[]{String.valueOf(symbol.type), String.valueOf(symbol.address)};
    }
}

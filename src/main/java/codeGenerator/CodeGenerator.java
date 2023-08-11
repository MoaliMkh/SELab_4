package codeGenerator;

import Log.Log;
import errorHandler.ErrorHandler;
import scanner.token.Token;
import semantic.symbol.Symbol;
import semantic.symbol.SymbolTable;
import semantic.symbol.SymbolType;

import java.util.Stack;

/**
 * Created by Alireza on 6/27/2015.
 */
public class CodeGenerator {
    private Memory memory = new Memory();
    private Stack<Address> ss = new Stack<Address>();
    private Stack<String> symbolStack = new Stack<>();
    private Stack<String> callStack = new Stack<>();
    private SymbolTable symbolTable;

    public CodeGenerator() {
        this.setSymbolTable(new SymbolTable(this.getMemory()));
    }

    public Memory getMemory() {
        return memory;
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    public Stack<Address> getSs() {
        return this.ss;
    }

    public void setSs(Stack<Address> ss) {
        this.ss = ss;
    }

    public Stack<String> getSymbolStack() {
        return this.symbolStack;
    }

    public void setSymbolStack(Stack<String> symbolStack) {
        this.symbolStack = symbolStack;
    }

    public Stack<String> getCallStack() {
        return this.callStack;
    }

    public void setCallStack(Stack<String> callStack) {
        this.callStack = callStack;
    }

    public SymbolTable getSymbolTable() {
        return this.symbolTable;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public void printMemory() {
        this.getMemory().pintCodeBlock();
    }

    public void semanticFunction(int func, Token next) {
        Log.print("codegenerator : " + func);
        switch (func) {
            case 0:
                return;
            case 1:
                checkID();
                break;
            case 2:
                pid(next);
                break;
            case 3:
                fpid();
                break;
            case 4:
                kpid(next);
                break;
            case 5:
                intpid(next);
                break;
            case 6:
                startCall();
                break;
            case 7:
                call();
                break;
            case 8:
                arg();
                break;
            case 9:
                assign();
                break;
            case 10:
                add();
                break;
            case 11:
                sub();
                break;
            case 12:
                mult();
                break;
            case 13:
                label();
                break;
            case 14:
                save();
                break;
            case 15:
                _while();
                break;
            case 16:
                jpf_save();
                break;
            case 17:
                jpHere();
                break;
            case 18:
                print();
                break;
            case 19:
                equal();
                break;
            case 20:
                less_than();
                break;
            case 21:
                and();
                break;
            case 22:
                not();
                break;
            case 23:
                defClass();
                break;
            case 24:
                defMethod();
                break;
            case 25:
                popClass();
                break;
            case 26:
                extend();
                break;
            case 27:
                defField();
                break;
            case 28:
                defVar();
                break;
            case 29:
                methodReturn();
                break;
            case 30:
                defParam();
                break;
            case 31:
                lastTypeBool();
                break;
            case 32:
                lastTypeInt();
                break;
            case 33:
                defMain();
                break;
        }
    }

    private void defMain() {
        this.getMemory().add3AddressCode(this.getSs().pop().num, Operation.JP, new Address(this.getMemory().getCurrentCodeBlockAddress(), varType.Address), null, null);
        String methodName = "main";
        String className = this.getSymbolStack().pop();

        this.getSymbolTable().addMethod(className, methodName, this.getMemory().getCurrentCodeBlockAddress());

        this.getSymbolStack().push(className);
        this.getSymbolStack().push(methodName);
    }


    public void checkID() {
        this.getSymbolStack().pop();
        if (this.getSs().peek().varType == varType.Non) {
            //TODO : error
        }
    }

    public void pid(Token next) {
        if (this.getSymbolStack().size() > 1) {
            String methodName = this.getSymbolStack().pop();
            String className = this.getSymbolStack().pop();
            try {

                Symbol s = this.getSymbolTable().get(className, methodName, next.value);
                varType t = varType.Int;
                switch (s.type) {
                    case Bool:
                        t = varType.Bool;
                        break;
                    case Int:
                        t = varType.Int;
                        break;
                }
                this.getSs().push(new Address(s.address, t));


            } catch (Exception e) {
                this.getSs().push(new Address(0, varType.Non));
            }
            this.getSymbolStack().push(className);
            this.getSymbolStack().push(methodName);
        } else {
            this.getSs().push(new Address(0, varType.Non));
        }
        this.getSymbolStack().push(next.value);
    }

    public void fpid() {
        this.getSs().pop();
        this.getSs().pop();

        Symbol s = this.getSymbolTable().get(this.getSymbolStack().pop(), this.getSymbolStack().pop());
        varType t = varType.Int;
        switch (s.type) {
            case Bool:
                t = varType.Bool;
                break;
            case Int:
                t = varType.Int;
                break;
        }
        this.getSs().push(new Address(s.address, t));
    }

    public void kpid(Token next) {
        this.getSs().push(this.getSymbolTable().get(next.value));
    }

    public void intpid(Token next) {
        this.getSs().push(new Address(Integer.parseInt(next.value), varType.Int, TypeAddress.Imidiate));
    }

    public void startCall() {
        this.getSs().pop();
        this.getSs().pop();
        String methodName = this.getSymbolStack().pop();
        String className = this.getSymbolStack().pop();
        this.getSymbolTable().startCall(className, methodName);
        this.getCallStack().push(className);
        this.getCallStack().push(methodName);
    }

    public void call() {
        String methodName = this.getCallStack().pop();
        String className = this.getCallStack().pop();
        try {
            this.getSymbolTable().getNextParam(className, methodName);
            ErrorHandler.printError("The few argument pass for method");
        } catch (IndexOutOfBoundsException e) {
        }
        varType t = varType.Int;
        switch (this.getSymbolTable().getMethodReturnType(className, methodName)) {
            case Int:
                t = varType.Int;
                break;
            case Bool:
                t = varType.Bool;
                break;
        }
        Address temp = new Address(this.getMemory().getTemp(), t);
        this.getSs().push(temp);
        this.getMemory().add3AddressCode(Operation.ASSIGN, new Address(temp.num, varType.Address, TypeAddress.Imidiate), new Address(this.getSymbolTable().getMethodReturnAddress(className, methodName), varType.Address), null);
        this.getMemory().add3AddressCode(Operation.ASSIGN, new Address(this.getMemory().getCurrentCodeBlockAddress() + 2, varType.Address, TypeAddress.Imidiate), new Address(this.getSymbolTable().getMethodCallerAddress(className, methodName), varType.Address), null);
        this.getMemory().add3AddressCode(Operation.JP, new Address(this.getSymbolTable().getMethodAddress(className, methodName), varType.Address), null, null);
    }

    public void arg() {
        String methodName = this.getCallStack().pop();
        try {
            Symbol s = this.getSymbolTable().getNextParam(this.getCallStack().peek(), methodName);
            varType t = varType.Int;
            switch (s.type) {
                case Bool:
                    t = varType.Bool;
                    break;
                case Int:
                    t = varType.Int;
                    break;
            }
            Address param = this.getSs().pop();
            if (param.varType != t) {
                ErrorHandler.printError("The argument type isn't match");
            }
            this.getMemory().add3AddressCode(Operation.ASSIGN, param, new Address(s.address, t), null);

        } catch (IndexOutOfBoundsException e) {
            ErrorHandler.printError("Too many arguments pass for method");
        }
        this.getCallStack().push(methodName);
    }

    public void assign() {
        Address s1 = this.getSs().pop();
        Address s2 = this.getSs().pop();
        if (s1.varType != s2.varType) {
            ErrorHandler.printError("The type of operands in assign is different ");
        }
        this.getMemory().add3AddressCode(Operation.ASSIGN, s1, s2, null);
    }

    public void add() {
        Address temp = new Address(this.getMemory().getTemp(), varType.Int);
        Address s2 = this.getSs().pop();
        Address s1 = this.getSs().pop();

        if (s1.varType != varType.Int || s2.varType != varType.Int) {
            ErrorHandler.printError("In add two operands must be integer");
        }
        this.getMemory().add3AddressCode(Operation.ADD, s1, s2, temp);
        this.getSs().push(temp);
    }

    public void sub() {
        Address temp = new Address(this.getMemory().getTemp(), varType.Int);
        Address s2 = this.getSs().pop();
        Address s1 = this.getSs().pop();
        if (s1.varType != varType.Int || s2.varType != varType.Int) {
            ErrorHandler.printError("In sub two operands must be integer");
        }
        this.getMemory().add3AddressCode(Operation.SUB, s1, s2, temp);
        this.getSs().push(temp);
    }

    public void mult() {
        Address temp = new Address(this.getMemory().getTemp(), varType.Int);
        Address s2 = this.getSs().pop();
        Address s1 = this.getSs().pop();
        if (s1.varType != varType.Int || s2.varType != varType.Int) {
            ErrorHandler.printError("In mult two operands must be integer");
        }
        this.getMemory().add3AddressCode(Operation.MULT, s1, s2, temp);
        this.getSs().push(temp);
    }

    public void label() {
        this.getSs().push(new Address(this.getMemory().getCurrentCodeBlockAddress(), varType.Address));
    }

    public void save() {
        this.getSs().push(new Address(this.getMemory().saveMemory(), varType.Address));
    }

    public void _while() {
        this.getMemory().add3AddressCode(this.getSs().pop().num, Operation.JPF, this.getSs().pop(), new Address(this.getMemory().getCurrentCodeBlockAddress() + 1, varType.Address), null);
        this.getMemory().add3AddressCode(Operation.JP, this.getSs().pop(), null, null);
    }

    public void jpf_save() {
        Address save = new Address(this.getMemory().saveMemory(), varType.Address);
        this.getMemory().add3AddressCode(this.getSs().pop().num, Operation.JPF, this.getSs().pop(), new Address(this.getMemory().getCurrentCodeBlockAddress(), varType.Address), null);
        this.getSs().push(save);
    }

    public void jpHere() {
        this.getMemory().add3AddressCode(this.getSs().pop().num, Operation.JP, new Address(this.getMemory().getCurrentCodeBlockAddress(), varType.Address), null, null);
    }

    public void print() {
        this.getMemory().add3AddressCode(Operation.PRINT, this.getSs().pop(), null, null);
    }

    public void equal() {
        Address temp = new Address(this.getMemory().getTemp(), varType.Bool);
        Address s2 = this.getSs().pop();
        Address s1 = this.getSs().pop();
        if (s1.varType != s2.varType) {
            ErrorHandler.printError("The type of operands in equal operator is different");
        }
        this.getMemory().add3AddressCode(Operation.EQ, s1, s2, temp);
        this.getSs().push(temp);
    }

    public void less_than() {
        Address temp = new Address(this.getMemory().getTemp(), varType.Bool);
        Address s2 = this.getSs().pop();
        Address s1 = this.getSs().pop();
        if (s1.varType != varType.Int || s2.varType != varType.Int) {
            ErrorHandler.printError("The type of operands in less than operator is different");
        }
        this.getMemory().add3AddressCode(Operation.LT, s1, s2, temp);
        this.getSs().push(temp);
    }

    public void and() {
        Address temp = new Address(this.getMemory().getTemp(), varType.Bool);
        Address s2 = this.getSs().pop();
        Address s1 = this.getSs().pop();
        if (s1.varType != varType.Bool || s2.varType != varType.Bool) {
            ErrorHandler.printError("In and operator the operands must be boolean");
        }
        this.getMemory().add3AddressCode(Operation.AND, s1, s2, temp);
        this.getSs().push(temp);
    }

    public void not() {
        Address temp = new Address(this.getMemory().getTemp(), varType.Bool);
        Address s2 = this.getSs().pop();
        Address s1 = this.getSs().pop();
        if (s1.varType != varType.Bool) {
            ErrorHandler.printError("In not operator the operand must be boolean");
        }
        this.getMemory().add3AddressCode(Operation.NOT, s1, s2, temp);
        this.getSs().push(temp);
    }

    public void defClass() {
        this.getSs().pop();
        this.getSymbolTable().addClass(this.getSymbolStack().peek());
    }

    public void defMethod() {
        this.getSs().pop();
        String methodName = this.getSymbolStack().pop();
        String className = this.getSymbolStack().pop();

        this.getSymbolTable().addMethod(className, methodName, this.getMemory().getCurrentCodeBlockAddress());

        this.getSymbolStack().push(className);
        this.getSymbolStack().push(methodName);
    }

    public void popClass() {
        this.getSymbolStack().pop();
    }

    public void extend() {
        this.getSs().pop();
        this.getSymbolTable().setSuperClass(this.getSymbolStack().pop(), this.getSymbolStack().peek());
    }

    public void defField() {
        this.getSs().pop();
        this.getSymbolTable().addField(this.getSymbolStack().pop(), this.getSymbolStack().peek());
    }

    public void defVar() {
        this.getSs().pop();

        String var = this.getSymbolStack().pop();
        String methodName = this.getSymbolStack().pop();
        String className = this.getSymbolStack().pop();

        this.getSymbolTable().addMethodLocalVariable(className, methodName, var);

        this.getSymbolStack().push(className);
        this.getSymbolStack().push(methodName);
    }

    public void methodReturn() {
        String methodName = this.getSymbolStack().pop();
        Address s = this.getSs().pop();
        SymbolType t = this.getSymbolTable().getMethodReturnType(this.getSymbolStack().peek(), methodName);
        varType temp = varType.Int;
        switch (t) {
            case Int:
                break;
            case Bool:
                temp = varType.Bool;
        }
        if (s.varType != temp) {
            ErrorHandler.printError("The type of method and return address was not match");
        }
        this.getMemory().add3AddressCode(Operation.ASSIGN, s, new Address(this.getSymbolTable().getMethodReturnAddress(this.getSymbolStack().peek(), methodName), varType.Address, TypeAddress.Indirect), null);
        this.getMemory().add3AddressCode(Operation.JP, new Address(this.getSymbolTable().getMethodCallerAddress(this.getSymbolStack().peek(), methodName), varType.Address), null, null);
    }

    public void defParam() {
        this.getSs().pop();
        String param = this.getSymbolStack().pop();
        String methodName = this.getSymbolStack().pop();
        String className = this.getSymbolStack().pop();

        this.getSymbolTable().addMethodParameter(className, methodName, param);

        this.getSymbolStack().push(className);
        this.getSymbolStack().push(methodName);
    }

    public void lastTypeBool() {
        this.getSymbolTable().setLastType(SymbolType.Bool);
    }

    public void lastTypeInt() {
        this.getSymbolTable().setLastType(SymbolType.Int);
    }

    public void main() {

    }
}

package CodeGenerator;

import CodeGenerator.Address.Address;
import CodeGenerator.Address.DirectAddress;
import CodeGenerator.Address.ImmediateAddress;
import CodeGenerator.Address.IndirectAddress;
import ErrorHandler.ErrorHandler;
import Log.Log;
import Scanner.token.Token;
import semantic.Symbol.SymbolFacade;

import java.util.Stack;

/**
 * Created by Alireza on 6/27/2015.
 */
public class CodeGenerator {
    private Memory memory = new Memory();
    private Stack<Address> ss = new Stack<>();
    private Stack<String> symbolStack = new Stack<>();
    private Stack<String> callStack = new Stack<>();
    private SymbolFacade symbolFacade;

    public CodeGenerator() {
        this.setSymbolFacade(new SymbolFacade(this.getMemory()));
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

    public SymbolFacade getSymbolFacade() {
        return symbolFacade;
    }

    public void setSymbolFacade(SymbolFacade symbolFacade) {
        this.symbolFacade = symbolFacade;
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
                jpfSave();
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
                lessThan();
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
        Address address = new DirectAddress(this.getMemory().getCurrentCodeBlockAddress(), varType.Address);
        this.getMemory().add3AddressCode(this.getSs().pop().num, Operation.JP, address, null, null);
        String methodName = "main";
        String className = this.getSymbolStack().pop();

        this.getSymbolFacade().addMethod(className, methodName, this.getMemory().getCurrentCodeBlockAddress());

        this.getSymbolStack().push(className);
        this.getSymbolStack().push(methodName);
    }

    private void checkID() {
        this.getSymbolStack().pop();
        if (this.getSs().peek().varType == varType.Non) {
            //TODO : error
        }
    }

    private void pid(Token next) {
        if (this.getSymbolStack().size() > 1) {
            String methodName = this.getSymbolStack().pop();
            String className = this.getSymbolStack().pop();
            try {
                String[] info = this.getSymbolFacade().getSymbolInfo(this.getSymbolFacade().get(className, methodName, next.value));
                String type = info[0];
                int symbolAddress = Integer.parseInt(info[1]);
                pushAddress(type, symbolAddress);
            } catch (Exception e) {
                Address address = new DirectAddress(0, varType.Non);
                this.getSs().push(address);
            }
            this.getSymbolStack().push(className);
            this.getSymbolStack().push(methodName);
        } else {
            Address address = new DirectAddress(0, varType.Non);
            this.getSs().push(address);
        }
        this.getSymbolStack().push(next.value);
    }

    private void pushAddress(String type, int symbolAddress) {
        varType variableType = varType.Int;
        if (type.equals("Bool")) {
            variableType = varType.Bool;
        }

        Address address = new DirectAddress(symbolAddress, variableType);
        this.getSs().push(address);
    }

    private void fpid() {
        this.getSs().pop();
        this.getSs().pop();

        String first = this.getSymbolStack().pop();
        String second = this.getSymbolStack().pop();
        String[] info = this.getSymbolFacade().getSymbolInfo(this.getSymbolFacade().get(first, second));
        String type = info[0];
        int symbolAddress = Integer.parseInt(info[1]);

        pushAddress(type, symbolAddress);
    }

    private void kpid(Token next) {
        this.getSs().push(this.getSymbolFacade().get(next.value));
    }

    private void intpid(Token next) {
        Address address = new ImmediateAddress(Integer.parseInt(next.value), varType.Int);
        this.getSs().push(address);
    }

    private void startCall() {
        this.getSs().pop();
        this.getSs().pop();
        String methodName = this.getSymbolStack().pop();
        String className = this.getSymbolStack().pop();
        this.getSymbolFacade().startCall(className, methodName);
        this.getCallStack().push(className);
        this.getCallStack().push(methodName);
    }

    private void call() {
        String methodName = this.getCallStack().pop();
        String className = this.getCallStack().pop();
        try {
            this.getSymbolFacade().getNextParam(className, methodName);
            ErrorHandler.printError("The few argument pass for method");
        } catch (IndexOutOfBoundsException ignored) {
        }
        varType variableType = varType.Int;
        if (this.getSymbolFacade().getMethodReturnType(className, methodName).equals("Bool")) {
            variableType = varType.Bool;
        }

        Address temp = new DirectAddress(this.getMemory().getTemp(), variableType);
        this.getSs().push(temp);
        Address address1 = new ImmediateAddress(temp.num, varType.Address);
        Address address2 = new DirectAddress(this.getSymbolFacade().getMethodReturnAddress(className, methodName), varType.Address);
        this.getMemory().add3AddressCode(Operation.ASSIGN, address1, address2, null);
        Address address3 = new ImmediateAddress(this.getMemory().getCurrentCodeBlockAddress() + 2, varType.Address);
        Address address4 = new DirectAddress(this.getSymbolFacade().getMethodCallerAddress(className, methodName), varType.Address);
        this.getMemory().add3AddressCode(Operation.ASSIGN, address3, address4, null);
        Address address5 = new DirectAddress(this.getSymbolFacade().getMethodAddress(className, methodName), varType.Address);
        this.getMemory().add3AddressCode(Operation.JP, address5, null, null);
    }

    private void arg() {
        String methodName = this.getCallStack().pop();
        try {
            String first = this.getCallStack().peek();
            String[] info = this.getSymbolFacade().getSymbolInfo(this.getSymbolFacade().getNextParam(first, methodName));
            String type = info[0];
            int symbolAddress = Integer.parseInt(info[1]);
            varType variableType = varType.Int;
            if (type.equals("Bool")) {
                variableType = varType.Bool;
            }

            Address param = this.getSs().pop();
            if (param.varType != variableType) {
                ErrorHandler.printError("The argument type isn't match");
            }
            Address address = new DirectAddress(symbolAddress, variableType);
            this.getMemory().add3AddressCode(Operation.ASSIGN, param, address, null);

        } catch (IndexOutOfBoundsException e) {
            ErrorHandler.printError("Too many arguments pass for method");
        }
        this.getCallStack().push(methodName);
    }

    private void assign() {
        Address s1 = this.getSs().pop();
        Address s2 = this.getSs().pop();
        if (s1.varType != s2.varType) {
            ErrorHandler.printError("The type of operands in assign is different ");
        }
        this.getMemory().add3AddressCode(Operation.ASSIGN, s1, s2, null);
    }

    private void add() {
        Address temp = new DirectAddress(this.getMemory().getTemp(), varType.Int);
        Address s2 = this.getSs().pop();
        Address s1 = this.getSs().pop();

        if (s1.varType != varType.Int || s2.varType != varType.Int) {
            ErrorHandler.printError("In add two operands must be integer");
        }
        this.getMemory().add3AddressCode(Operation.ADD, s1, s2, temp);
        this.getSs().push(temp);
    }

    private void sub() {
        Address temp = new DirectAddress(this.getMemory().getTemp(), varType.Int);
        Address s2 = this.getSs().pop();
        Address s1 = this.getSs().pop();
        if (s1.varType != varType.Int || s2.varType != varType.Int) {
            ErrorHandler.printError("In sub two operands must be integer");
        }
        this.getMemory().add3AddressCode(Operation.SUB, s1, s2, temp);
        this.getSs().push(temp);
    }

    private void mult() {
        Address temp = new DirectAddress(this.getMemory().getTemp(), varType.Int);
        Address s2 = this.getSs().pop();
        Address s1 = this.getSs().pop();
        if (s1.varType != varType.Int || s2.varType != varType.Int) {
            ErrorHandler.printError("In mult two operands must be integer");
        }
        this.getMemory().add3AddressCode(Operation.MULT, s1, s2, temp);
        this.getSs().push(temp);
    }

    private void label() {
        Address address = new DirectAddress(this.getMemory().getCurrentCodeBlockAddress(), varType.Address);
        this.getSs().push(address);
    }

    private void save() {
        Address address = new DirectAddress(this.getMemory().saveMemory(), varType.Address);
        this.getSs().push(address);
    }

    private void _while() {
        Address address = new DirectAddress(this.getMemory().getCurrentCodeBlockAddress() + 1, varType.Address);
        this.getMemory().add3AddressCode(this.getSs().pop().num, Operation.JPF, this.getSs().pop(), address, null);
        this.getMemory().add3AddressCode(Operation.JP, this.getSs().pop(), null, null);
    }

    private void jpfSave() {
        Address save = new DirectAddress(this.getMemory().saveMemory(), varType.Address);
        Address address = new DirectAddress(this.getMemory().getCurrentCodeBlockAddress(), varType.Address);
        this.getMemory().add3AddressCode(this.getSs().pop().num, Operation.JPF, this.getSs().pop(), address, null);
        this.getSs().push(save);
    }

    private void jpHere() {
        Address address = new DirectAddress(this.getMemory().getCurrentCodeBlockAddress(), varType.Address);
        this.getMemory().add3AddressCode(this.getSs().pop().num, Operation.JP, address, null, null);
    }

    private void print() {
        this.getMemory().add3AddressCode(Operation.PRINT, this.getSs().pop(), null, null);
    }

    private void equal() {
        Address temp = new DirectAddress(this.getMemory().getTemp(), varType.Bool);
        Address s2 = this.getSs().pop();
        Address s1 = this.getSs().pop();
        if (s1.varType != s2.varType) {
            ErrorHandler.printError("The type of operands in equal operator is different");
        }
        this.getMemory().add3AddressCode(Operation.EQ, s1, s2, temp);
        this.getSs().push(temp);
    }

    private void lessThan() {
        Address temp = new DirectAddress(this.getMemory().getTemp(), varType.Bool);
        Address s2 = this.getSs().pop();
        Address s1 = this.getSs().pop();
        if (s1.varType != varType.Int || s2.varType != varType.Int) {
            ErrorHandler.printError("The type of operands in less than operator is different");
        }
        this.getMemory().add3AddressCode(Operation.LT, s1, s2, temp);
        this.getSs().push(temp);
    }

    private void and() {
        Address temp = new DirectAddress(this.getMemory().getTemp(), varType.Bool);
        Address s2 = this.getSs().pop();
        Address s1 = this.getSs().pop();
        if (s1.varType != varType.Bool || s2.varType != varType.Bool) {
            ErrorHandler.printError("In and operator the operands must be boolean");
        }
        this.getMemory().add3AddressCode(Operation.AND, s1, s2, temp);
        this.getSs().push(temp);
    }

    private void not() {
        Address temp = new DirectAddress(this.getMemory().getTemp(), varType.Bool);
        Address s2 = this.getSs().pop();
        Address s1 = this.getSs().pop();
        if (s1.varType != varType.Bool) {
            ErrorHandler.printError("In not operator the operand must be boolean");
        }
        this.getMemory().add3AddressCode(Operation.NOT, s1, s2, temp);
        this.getSs().push(temp);
    }

    private void defClass() {
        this.getSs().pop();
        this.getSymbolFacade().addClass(this.getSymbolStack().peek());
    }

    private void defMethod() {
        this.getSs().pop();
        String methodName = this.getSymbolStack().pop();
        String className = this.getSymbolStack().pop();

        this.getSymbolFacade().addMethod(className, methodName, this.getMemory().getCurrentCodeBlockAddress());

        this.getSymbolStack().push(className);
        this.getSymbolStack().push(methodName);
    }

    private void popClass() {
        this.getSymbolStack().pop();
    }

    private void extend() {
        this.getSs().pop();
        this.getSymbolFacade().setSuperClass(this.getSymbolStack().pop(), this.getSymbolStack().peek());
    }

    private void defField() {
        this.getSs().pop();
        this.getSymbolFacade().addField(this.getSymbolStack().pop(), this.getSymbolStack().peek());
    }

    private void defVar() {
        this.getSs().pop();

        String var = this.getSymbolStack().pop();
        String methodName = this.getSymbolStack().pop();
        String className = this.getSymbolStack().pop();

        this.getSymbolFacade().addMethodLocalVariable(className, methodName, var);

        this.getSymbolStack().push(className);
        this.getSymbolStack().push(methodName);
    }

    private void methodReturn() {
        String methodName = this.getSymbolStack().pop();
        Address s = this.getSs().pop();
        String type = this.getSymbolFacade().getMethodReturnType(this.getSymbolStack().peek(), methodName);
        varType variableType = varType.Int;
        if (type.equals("Bool")) {
            variableType = varType.Bool;
        }

        if (s.varType != variableType) {
            ErrorHandler.printError("The type of method and return address was not match");
        }
        Address address1 = new IndirectAddress(this.getSymbolFacade().getMethodReturnAddress(this.getSymbolStack().peek(), methodName), varType.Address);
        this.getMemory().add3AddressCode(Operation.ASSIGN, s, address1, null);
        Address address2 = new DirectAddress(this.getSymbolFacade().getMethodCallerAddress(this.getSymbolStack().peek(), methodName), varType.Address);
        this.getMemory().add3AddressCode(Operation.JP, address2, null, null);
    }

    private void defParam() {
        this.getSs().pop();
        String param = this.getSymbolStack().pop();
        String methodName = this.getSymbolStack().pop();
        String className = this.getSymbolStack().pop();

        this.getSymbolFacade().addMethodParameter(className, methodName, param);

        this.getSymbolStack().push(className);
        this.getSymbolStack().push(methodName);
    }

    private void lastTypeBool() {
        this.getSymbolFacade().setLastTypeBool();
    }

    private void lastTypeInt() {
        this.getSymbolFacade().setLastTypeInt();
    }

    private void main() {
    }
}

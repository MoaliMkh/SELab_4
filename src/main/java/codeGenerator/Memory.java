package codeGenerator;

import codeGenerator.Address.Address;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohammad hosein on 6/27/2015.
 */
public class Memory {
    private final List<_3AddressCode> codeBlock;
    private int lastTempIndex;
    private int lastDataAddress;

    public Memory() {
        codeBlock = new ArrayList<>();
        lastTempIndex = 500;
        lastDataAddress = 200;
    }

    public int getTemp() {
        int tempSize = 4;
        lastTempIndex += tempSize;
        return lastTempIndex - tempSize;
    }

    public int getDateAddress() {
        int dataSize = 4;
        lastDataAddress += dataSize;
        return lastDataAddress - dataSize;
    }

    public int saveMemory() {
        codeBlock.add(new _3AddressCode());
        return codeBlock.size() - 1;
    }

    public void add3AddressCode(Operation op, Address opr1, Address opr2, Address opr3) {
        codeBlock.add(new _3AddressCode(op, opr1, opr2, opr3));
    }

    public void add3AddressCode(int i, Operation op, Address opr1, Address opr2, Address opr3) {
        codeBlock.remove(i);
        codeBlock.add(i, new _3AddressCode(op, opr1, opr2, opr3));
    }

    public int getCurrentCodeBlockAddress() {
        return codeBlock.size();
    }

    public void pintCodeBlock() {
        System.out.println("Code Block");
        for (int i = 0; i < codeBlock.size(); i++) {
            System.out.println(i + " : " + codeBlock.get(i).toString());
        }
    }
}

class _3AddressCode {
    public Operation operation;
    public Address Operand1;
    public Address Operand2;
    public Address Operand3;

    public _3AddressCode() {

    }

    public _3AddressCode(Operation op, Address opr1, Address opr2, Address opr3) {
        operation = op;
        Operand1 = opr1;
        Operand2 = opr2;
        Operand3 = opr3;
    }

    public String toString() {
        if (operation == null) return "";
        StringBuilder res = new StringBuilder("(");
        res.append(operation.toString()).append(",");
        if (Operand1 != null) res.append(Operand1);
        res.append(",");
        if (Operand2 != null) res.append(Operand2);
        res.append(",");
        if (Operand3 != null) res.append(Operand3);
        res.append(")");

        return res.toString();
    }
}

package codeGenerator.Address;

import codeGenerator.Address.Address;

public class IndirectAddress extends Address {
    public IndirectAddress(int num, codeGenerator.varType varType) {
        super(num, varType);
    }

    @Override
    public String toString() {
        return "@" + num;
    }
}

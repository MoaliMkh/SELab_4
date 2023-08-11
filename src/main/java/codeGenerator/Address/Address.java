package codeGenerator.Address;

import codeGenerator.varType;

/**
 * Created by mohammad hosein on 6/28/2015.
 */

public abstract class Address {
    public int num;
    public codeGenerator.varType varType;

    public Address(int num, varType varType) {
        this.num = num;
        this.varType = varType;
    }

    public abstract String toString();
}
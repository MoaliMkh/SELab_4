package codeGenerator.Address;

public class DirectAddress extends Address {
    public DirectAddress(int num, codeGenerator.varType varType) {
        super(num, varType);
    }

    @Override
    public String toString() {
        return num + "";
    }
}

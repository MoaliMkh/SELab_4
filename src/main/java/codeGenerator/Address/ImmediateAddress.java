package codeGenerator.Address;

public class ImmediateAddress extends Address {
    public ImmediateAddress(int num, codeGenerator.varType varType) {
        super(num, varType);
    }

    @Override
    public String toString() {
        return "#" + num;
    }
}

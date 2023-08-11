package CodeGenerator.Address;

public class IndirectAddress extends Address {
    public IndirectAddress(int num, CodeGenerator.varType varType) {
        super(num, varType);
    }

    @Override
    public String toString() {
        return "@" + num;
    }
}

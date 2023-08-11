package CodeGenerator.Address;

public class ImmediateAddress extends Address {
    public ImmediateAddress(int num, CodeGenerator.varType varType) {
        super(num, varType);
    }

    @Override
    public String toString() {
        return "#" + num;
    }
}

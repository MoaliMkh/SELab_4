package CodeGenerator.Address;

public class DirectAddress extends Address {
    public DirectAddress(int num, CodeGenerator.varType varType) {
        super(num, varType);
    }

    @Override
    public String toString() {
        return num + "";
    }
}

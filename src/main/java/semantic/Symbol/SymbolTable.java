package semantic.Symbol;

import CodeGenerator.Address.Address;
import CodeGenerator.Address.ImmediateAddress;
import CodeGenerator.Memory;
import CodeGenerator.varType;
import ErrorHandler.ErrorHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {
    private final Map<String, Klass> klasses;
    private final Map<String, Address> keyWords;
    private final Memory mem;
    private SymbolType lastType;

    public SymbolTable(Memory memory) {
        mem = memory;
        klasses = new HashMap<>();
        keyWords = new HashMap<>();
        Address address1 = new ImmediateAddress(1, varType.Bool);
        keyWords.put("true", address1);
        Address address2 = new ImmediateAddress(0, varType.Bool);
        keyWords.put("false", address2);
    }

    public void setLastType(SymbolType type) {
        lastType = type;
    }

    public void addClass(String className) {
        try {
            if (klasses.containsKey(className)) {
                throw new Exception();
            }
        } catch (Exception e) {
            ErrorHandler.printError("This class already defined");
        }
        klasses.put(className, new Klass());
    }

    public void addField(String fieldName, String className) {
        klasses.get(className).Fields.put(fieldName, new Symbol(lastType, mem.getDateAddress()));
    }

    public void addMethod(String className, String methodName, int address) {
        try {
            if (klasses.get(className).Methods.containsKey(methodName)) {
                throw new Exception();
            }
        } catch (Exception e) {
            ErrorHandler.printError("This method already defined");
        }
        klasses.get(className).Methods.put(methodName, new Method(address, lastType));
    }

    public void addMethodParameter(String className, String methodName, String parameterName) {
        klasses.get(className).Methods.get(methodName).addParameter(parameterName);
    }

    public void addMethodLocalVariable(String className, String methodName, String localVariableName) {
        try {
            if (klasses.get(className).Methods.get(methodName).localVariable.containsKey(localVariableName)) {
                throw new Exception();
            }
        } catch (Exception e) {
            ErrorHandler.printError("This variable already defined");
        }
        klasses.get(className).Methods.get(methodName).localVariable.put(localVariableName, new Symbol(lastType, mem.getDateAddress()));
    }

    public void setSuperClass(String superClass, String className) {
        klasses.get(className).superClass = klasses.get(superClass);
    }

    public Address get(String keywordName) {
        return keyWords.get(keywordName);
    }

    public Symbol get(String fieldName, String className) {
        return klasses.get(className).getField(fieldName);
    }

    public Symbol get(String className, String methodName, String variable) {
        Symbol res = klasses.get(className).Methods.get(methodName).getVariable(variable);
        if (res == null) res = get(variable, className);
        return res;
    }

    public Symbol getNextParam(String className, String methodName) {
        return klasses.get(className).Methods.get(methodName).getNextParameter();
    }

    public void startCall(String className, String methodName) {
        klasses.get(className).Methods.get(methodName).reset();
    }

    public int getMethodCallerAddress(String className, String methodName) {
        return klasses.get(className).Methods.get(methodName).callerAddress;
    }

    public int getMethodReturnAddress(String className, String methodName) {
        return klasses.get(className).Methods.get(methodName).returnAddress;
    }

    public SymbolType getMethodReturnType(String className, String methodName) {
        return klasses.get(className).Methods.get(methodName).returnType;
    }

    public int getMethodAddress(String className, String methodName) {
        return klasses.get(className).Methods.get(methodName).codeAddress;
    }


    class Klass {
        public Map<String, Symbol> Fields;
        public Map<String, Method> Methods;
        public Klass superClass;

        public Klass() {
            Fields = new HashMap<>();
            Methods = new HashMap<>();
        }

        public Symbol getField(String fieldName) {
            if (Fields.containsKey(fieldName)) {
                return Fields.get(fieldName);
            }
            return superClass.getField(fieldName);
        }
    }

    class Method {
        private final List<String> orderedParameters;
        public int codeAddress;
        public Map<String, Symbol> parameters;
        public Map<String, Symbol> localVariable;
        public int callerAddress;
        public int returnAddress;
        public SymbolType returnType;
        private int index;

        public Method(int codeAddress, SymbolType returnType) {
            this.codeAddress = codeAddress;
            this.returnType = returnType;
            this.orderedParameters = new ArrayList<>();
            this.returnAddress = mem.getDateAddress();
            this.callerAddress = mem.getDateAddress();
            this.parameters = new HashMap<>();
            this.localVariable = new HashMap<>();
        }

        public Symbol getVariable(String variableName) {
            if (parameters.containsKey(variableName)) return parameters.get(variableName);
            if (localVariable.containsKey(variableName)) return localVariable.get(variableName);
            return null;
        }

        public void addParameter(String parameterName) {
            parameters.put(parameterName, new Symbol(lastType, mem.getDateAddress()));
            orderedParameters.add(parameterName);
        }

        private void reset() {
            index = 0;
        }

        private Symbol getNextParameter() {
            return parameters.get(orderedParameters.get(index++));
        }
    }
}
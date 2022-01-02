package top.kwseeker.rpc.thrift.calculator.handler;

import top.kwseeker.rpc.thrift.calculator.api.Calculator;

public class CalculatorHandler implements Calculator.Iface {

    @Override
    public int add(int num1, int num2) {
        return num1 + num2;
    }

}

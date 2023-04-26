package top.kwseeker.rpc.service.calculator;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service("calculatorService")
public class CalculatorServiceImpl implements CalculatorService.Iface {

    @Override
    public int add(int arg1, int arg2) {
        BigDecimal arg1Decimal = new BigDecimal(arg1);
        BigDecimal arg2Decimal = new BigDecimal(arg2);
        return arg1Decimal.add(arg2Decimal).intValue();
    }

    @Override
    public int subtract(int arg1, int arg2) {
        BigDecimal arg1Decimal = new BigDecimal(arg1);
        BigDecimal arg2Decimal = new BigDecimal(arg2);
        return arg1Decimal.subtract(arg2Decimal).intValue();
    }

    @Override
    public int multiply(int arg1, int arg2) {
        BigDecimal arg1Decimal = new BigDecimal(arg1);
        BigDecimal arg2Decimal = new BigDecimal(arg2);
        return arg1Decimal.multiply(arg2Decimal).intValue();
    }

    @Override
    public int division(int arg1, int arg2) {
        BigDecimal arg1Decimal = new BigDecimal(arg1);
        BigDecimal arg2Decimal = new BigDecimal(arg2);
        return arg1Decimal.divide(arg2Decimal).intValue();
    }
}

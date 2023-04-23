package top.kwseeker.rpc.client.constants;

public class Constant {

    public enum LoadBalanceStrategy {
        RANDOM,
        ROUND_ROBIN,
        WEIGHT,
        HASH;
    }
}

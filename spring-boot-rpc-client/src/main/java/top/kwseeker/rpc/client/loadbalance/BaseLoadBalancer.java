package top.kwseeker.rpc.client.loadbalance;

import top.kwseeker.rpc.client.exception.ClientException;
import top.kwseeker.rpc.client.loadbalance.strategy.RoundRobinRule;

import java.util.List;

/**
 * Ribbon 的 BaseLoadBalancer 同时通过观察者模式负责维护更新服务列表
 */
public class BaseLoadBalancer implements ILoadBalancer {

    private static final IRule DEFAULT_RULE = new RoundRobinRule();

    protected IRule rule = DEFAULT_RULE;

    @Override
    public Address chooseAddress() {
        //TODO: 添加观察者模式监听注册中心服务列表变化信息
        return null;
    }

    @Override
    public Address chooseAddress(List<Address> addresses) throws ClientException {
        if (rule == null) {
            return null;
        } else {
            return rule.choose(addresses);
        }
    }
}

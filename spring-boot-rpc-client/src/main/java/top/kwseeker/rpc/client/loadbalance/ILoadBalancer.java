package top.kwseeker.rpc.client.loadbalance;

import top.kwseeker.rpc.client.exception.ClientException;

import java.util.List;

public interface ILoadBalancer {

    //TODO 由 LoadBalancer 监听服务器节点变动并维护最新节点列表
    Address chooseAddress();

    Address chooseAddress(List<Address> addresses) throws ClientException;
}

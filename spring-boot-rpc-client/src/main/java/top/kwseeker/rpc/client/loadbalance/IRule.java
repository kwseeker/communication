package top.kwseeker.rpc.client.loadbalance;

import top.kwseeker.rpc.client.exception.ClientException;

import java.util.List;

public interface IRule {

    Address choose(List<Address> addresses) throws ClientException;
}

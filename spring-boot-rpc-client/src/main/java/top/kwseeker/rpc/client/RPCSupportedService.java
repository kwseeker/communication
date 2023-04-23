package top.kwseeker.rpc.client;

import top.kwseeker.rpc.client.loadbalance.Address;

import java.util.List;

public class RPCSupportedService {

    //服务名
    private String name;
    //服务的实例地址列表
    private List<Address> addresses;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
}

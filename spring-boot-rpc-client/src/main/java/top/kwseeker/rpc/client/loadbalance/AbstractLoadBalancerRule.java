package top.kwseeker.rpc.client.loadbalance;

public abstract class AbstractLoadBalancerRule implements IRule {

    // LoadBalancer 和 Rule 互相引用，基本相当于是同一个类了，TODO
    //private ILoadBalancer lb;
    //
    //@Override
    //public void setLoadBalancer(ILoadBalancer lb){
    //    this.lb = lb;
    //}
    //
    //@Override
    //public ILoadBalancer getLoadBalancer(){
    //    return lb;
    //}
}

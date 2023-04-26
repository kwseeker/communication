package top.kwseeker.rpc.server;

import org.apache.thrift.server.TServer;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * RPC Server 集群
 * TODO 适配 GRPC
 */
public class RPCServerGroup {

    private Queue<TServer> servers = new LinkedBlockingQueue<>();

    public RPCServerGroup(TServer... servers) {
        if (Objects.isNull(servers) || servers.length == 0) {
            return;
        }

        this.servers.addAll(Arrays.asList(servers));
    }

    public RPCServerGroup(List<TServer> servers) {
        if (CollectionUtils.isEmpty(servers)) {
            return;
        }

        this.servers.clear();
        this.servers.addAll(servers);
    }

    public void startAllServers() {
        //TODO
    }

    public void stopAllServers() {
        //TODO
    }

    public boolean hasRunningServers() {
        //TODO
        return false;
    }

    // getters and setters

    public Queue<TServer> getServers() {
        return servers;
    }

    public void setServers(Queue<TServer> servers) {
        this.servers = servers;
    }
}

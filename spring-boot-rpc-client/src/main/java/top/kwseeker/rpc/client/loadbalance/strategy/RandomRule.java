package top.kwseeker.rpc.client.loadbalance.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.rpc.client.exception.ClientException;
import top.kwseeker.rpc.client.loadbalance.AbstractLoadBalancerRule;
import top.kwseeker.rpc.client.loadbalance.Address;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomRule extends AbstractLoadBalancerRule {

    private static final Logger log = LoggerFactory.getLogger(RandomRule.class);

    @Override
    public Address choose(List<Address> addresses) throws ClientException {
        int addressCount = addresses.size();
        if (addressCount == 0) {
            throw new ClientException(ClientException.ErrorType.NO_VALID_SERVER_EXCEPTION);
        }

        int nextIndex = chooseRandomInt(addressCount);
        Address choseAddress = addresses.get(nextIndex);
        log.debug("RandomRule chose address: {}", choseAddress);
        return choseAddress;
    }

    protected int chooseRandomInt(int serverCount) {
        return ThreadLocalRandom.current().nextInt(serverCount);
    }
}

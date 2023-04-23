package top.kwseeker.rpc.client.loadbalance.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.rpc.client.exception.ClientException;
import top.kwseeker.rpc.client.loadbalance.AbstractLoadBalancerRule;
import top.kwseeker.rpc.client.loadbalance.Address;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinRule extends AbstractLoadBalancerRule {

    private static final Logger log = LoggerFactory.getLogger(RoundRobinRule.class);

    private final AtomicInteger nextServerCyclicCounter;

    public RoundRobinRule() {
        nextServerCyclicCounter = new AtomicInteger(0);
    }

    @Override
    public Address choose(List<Address> addresses) throws ClientException {
        int addressCount = addresses.size();
        if (addressCount == 0) {
           throw new ClientException(ClientException.ErrorType.NO_VALID_SERVER_EXCEPTION);
        }

        int nextIndex = incrementAndGetModulo(addressCount);
        Address choseAddress = addresses.get(nextIndex);
        log.debug("RoundRobinRule chose address: {}", choseAddress);
        return choseAddress;
    }

    /**
     * 加1并取模
     */
    private int incrementAndGetModulo(int modulo) {
        for (;;) {
            int current = nextServerCyclicCounter.get();
            int next = (current + 1) % modulo;
            if (nextServerCyclicCounter.compareAndSet(current, next))
                return next;
        }
    }
}

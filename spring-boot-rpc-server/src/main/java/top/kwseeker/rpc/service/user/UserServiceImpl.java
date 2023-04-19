package top.kwseeker.rpc.service.user;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import top.kwseeker.rpc.processor.thrift.user.User;
import top.kwseeker.rpc.processor.thrift.user.UserService;

@Service("userService")
public class UserServiceImpl implements UserService.Iface {

    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User getUser(String name) throws TException {
        log.info(">>>>>>> getUser called, name: " + name);
        return new User(name, 18);
    }

    @Override
    public void setUser(User user) throws TException {
        log.info(">>>>>>> setUser called, user: " + user.toString());
    }
}

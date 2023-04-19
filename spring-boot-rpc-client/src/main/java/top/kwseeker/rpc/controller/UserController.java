package top.kwseeker.rpc.controller;

import org.apache.thrift.TException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.kwseeker.rpc.processor.thrift.user.User;
import top.kwseeker.rpc.processor.thrift.user.UserService;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService.Iface userServiceProxy;

    @GetMapping("/get")
    public User getUser() throws TException {
        return userServiceProxy.getUser("Arvin");
    }

    @PutMapping("/set")
    public void setUser() throws TException {
        User user = new User("Nancy", 18);
        userServiceProxy.setUser(user);
    }
}

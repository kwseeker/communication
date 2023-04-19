// thrift --gen java -out ../../java/ User.thrift

namespace java top.kwseeker.rpc.processor.thrift.user

struct User {
    1: string name,
    2: i32 age
}

service UserService {
    User getUser(1: string name),
    void setUser(1: User user)
}
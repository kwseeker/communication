package top.kwseeker.communication.grpc.boot;

public interface BootService {

    void prepare() throws Throwable;

    void boot() throws Throwable;

    void onComplete() throws Throwable;

    void shutdown() throws Throwable;

    //default int priority() {
    //    return 0;
    //}
}

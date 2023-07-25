package top.kwseeker.communication.grpc.boot;

import top.kwseeker.communication.grpc.remote.TraceSegmentServiceClient;
import top.kwseeker.communication.grpc.remote.GRPCChannelManager;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

// BootService 管理器: 加载、初始化、启动BootService
public enum ServiceManager {
    INSTANCE;

    private Map<Class<? extends BootService>, BootService> bootedServices = Collections.emptyMap();

    public void boot() {
        bootedServices = loadAllServices();

        prepare();
        startup();
        onComplete();
    }

    public void shutdown() {
        bootedServices.values().forEach(service -> {
            try {
                service.shutdown();
            } catch (Throwable e) {
                System.err.println("ServiceManager try to shutdown " + service.getClass().getName() + "fail.");
                e.printStackTrace();
            }
        });
    }

    private void prepare() {
        bootedServices.values().forEach(service -> {
            try {
                service.prepare();
            } catch (Throwable e) {
                System.err.println("ServiceManager try to pre-start " + service.getClass().getName() + " fail.");
            }
        });
    }

    private void startup() {
        bootedServices.values().forEach(service -> {
            try {
                service.boot();
            } catch (Throwable e) {
                System.err.println("ServiceManager try to start " + service.getClass().getName() + " fail.");
                e.printStackTrace();
            }
        });
    }

    private void onComplete() {
        for (BootService service : bootedServices.values()) {
            try {
                service.onComplete();
            } catch (Throwable e) {
                System.err.println("Service " + service.getClass().getName() + " AfterBoot process fails.");
            }
        }
    }

    // skywalking 中加载 BootService 是通过 SPI 机制, 这里简写
    private Map<Class<? extends BootService>, BootService> loadAllServices() {
        Map<Class<? extends BootService>, BootService> bootServices = new LinkedHashMap<>();
        bootServices.put(GRPCChannelManager.class, new GRPCChannelManager());
        bootServices.put(TraceSegmentServiceClient.class, new TraceSegmentServiceClient());
        return bootServices;
    }

    public <T extends BootService> T findService(Class<T> serviceClass) {
        return (T) bootedServices.get(serviceClass);
    }
}

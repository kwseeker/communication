package top.kwseeker.rpc.server.thrift;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.layered.TFramedTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.rpc.processor.thrift.user.UserService;
import top.kwseeker.rpc.server.IRPCServer;
import top.kwseeker.rpc.server.RPCManagerProperties;

import javax.annotation.Resource;

public class ThriftServer implements IRPCServer {

    private final Logger log = LoggerFactory.getLogger(ThriftServer.class);

    private RPCManagerProperties.ThriftProperties properties;

    @Resource
    private UserService.Iface userService;

    public ThriftServer() {
    }

    public ThriftServer(RPCManagerProperties.ThriftProperties properties) {
        this.properties = properties;
    }

    @Override
    //public void start(CountDownLatch latch) {
    public void start() {
        try {
            //1 Socket: thrift支持的socket有很多种
            //非阻塞的socket
            TNonblockingServerSocket socket = new TNonblockingServerSocket(properties.getPort());
            //2 Server: THsHaServer 一个高可用的server
            //minWorkerThreads 最小的工作线程
            //maxWorkerThreads 最大的工作线程
            //如果这里Args不使用executorService指定线程池的话，创建THsHaServer会创建一个默认的LinkedBlockingQueue
            THsHaServer.Args arg = new THsHaServer.Args(socket)
                    .minWorkerThreads(properties.getMinThreadPool())
                    .maxWorkerThreads(properties.getMaxThreadPool());
            //可以自定义指定线程池
            //ExecutorService pool = Executors.newFixedThreadPool(minThreadPool);
            //arg.executorService(pool);

            //3 TProcessor
            //TProcessor tProcessor = new StudentService.Processor<>(myServerService);
            //Processor处理区  用于处理业务逻辑
            //泛型就是实现的业务
            UserService.Processor<UserService.Iface> userProcessor = new UserService.Processor<>(userService);
            //汇总，注册多个服务处理器
            TMultiplexedProcessor multiplexedProcessor = new TMultiplexedProcessor();
            multiplexedProcessor.registerProcessor(UserService.class.getSimpleName(), userProcessor);

            //---------------thrift传输协议------------------------------
            //1. TBinaryProtocol      二进制传输协议
            //2. TCompactProtocol     压缩协议 他是基于TBinaryProtocol二进制协议在进一步的压缩，使得体积更小
            //3. TJSONProtocol        Json格式传输协议
            //4. TSimpleJSONProtocol  简单JSON只写协议，生成的文件很容易通过脚本语言解析，实际开发中很少使用
            //5. TDebugProtocol       简单易懂的可读协议，调试的时候用于方便追踪传输过程中的数据
            //-----------------------------------------------------------
            //设置工厂
            //协议工厂 TCompactProtocol压缩工厂  二进制压缩协议
            arg.protocolFactory(new TCompactProtocol.Factory());
            //---------------thrift传输格式------------------------------

            //---------------thrift数据传输方式------------------------------
            //1. TSocket            阻塞式 Socket 相当于Java中的ServerSocket
            //2. TFrameTransport    以frame为单位进行数据传输，非阻塞式服务中使用
            //3. TFileTransport     以文件的形式进行传输
            //4. TMemoryTransport   将内存用于IO,Java实现的时候内部实际上是使用了简单的ByteArrayOutputStream
            //5. TZlibTransport     使用zlib进行压缩，与其他传世方式联合使用；java当前无实现所以无法使用
            //传输工厂 更加底层的概念
            arg.transportFactory(new TFramedTransport.Factory());
            //arg.transportFactory(new TTransportFactory());
            //---------------thrift数据传输方式------------------------------

            //设置处理器(Processor)工厂
            arg.processorFactory(new TProcessorFactory(multiplexedProcessor));

            //---------------thrift支持的服务模型------------------------------
            //1.TSimpleServer  简单的单线程服务模型，用于测试
            //2.TThreadPoolServer 多线程服务模型，使用的标准的阻塞式IO;运用了线程池，当线程池不够时会创建新的线程,当线程池出现大量空闲线程，线程池会对线程进行回收
            //3.TNonBlockingServer 多线程服务模型，使用非阻塞式IO（需要使用TFramedTransport数据传输方式）
            //4.THsHaServer YHsHa引入了线程池去处理（需要使用TFramedTransport数据传输方式），其模型把读写任务放到线程池去处理;Half-sync/Half-async（半同步半异步）的处理模式;Half-sync是在处理IO时间上（sccept/read/writr io）,Half-async用于handler对RPC的同步处理
            //----------------------------
            //根据参数实例化server
            //半同步半异步的server
            TServer server = new THsHaServer(arg);
            //---------------thrift支持的服务模型------------------------------

            log.info("Thrift server started; port: {}", properties.getPort());
            //启动server, 异步非阻塞的死循环
            server.serve();
        } catch (TTransportException e) {
            log.info("Thrift server start failed:");
            e.printStackTrace();
        }
        //finally {
        //    latch.countDown();
        //}
    }
}

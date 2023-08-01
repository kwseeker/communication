package top.kwseeker.communication.grpc.remote;

public class GRPCStreamServiceStatus {

    private volatile boolean status;

    public GRPCStreamServiceStatus(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

    public void finished() {
        this.status = true;
    }

    /**
     * Wait until success status reported.
     */
    public void wait4Finish() {
        long recheckCycle = 5;
        long hasWaited = 0L;
        long maxCycle = 30 * 1000L; // 30 seconds max.
        while (!status) {
            try2Sleep(recheckCycle);
            hasWaited += recheckCycle;

            if (recheckCycle >= maxCycle) {
                System.err.println("Collector traceSegment service doesn't response in " + hasWaited / 1000 + " seconds.");
            } else {
                recheckCycle = Math.min(recheckCycle * 2, maxCycle);
            }
        }
    }

    /**
     * Try to sleep, and ignore the {@link InterruptedException}
     *
     * @param millis the length of time to sleep in milliseconds
     */
    private void try2Sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {

        }
    }
}
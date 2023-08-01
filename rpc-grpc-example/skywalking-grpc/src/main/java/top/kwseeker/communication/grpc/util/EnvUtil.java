package top.kwseeker.communication.grpc.util;

public class EnvUtil {
    public static int getInt(String envName, int defaultValue) {
        int value = defaultValue;
        String envValue = System.getenv(envName);
        if (envValue != null) {
            try {
                value = Integer.parseInt(envValue);
            } catch (NumberFormatException e) {

            }
        }
        return value;
    }

    public static long getLong(String envName, long defaultValue) {
        long value = defaultValue;
        String envValue = System.getenv(envName);
        if (envValue != null) {
            try {
                value = Long.parseLong(envValue);
            } catch (NumberFormatException e) {

            }
        }
        return value;
    }
}
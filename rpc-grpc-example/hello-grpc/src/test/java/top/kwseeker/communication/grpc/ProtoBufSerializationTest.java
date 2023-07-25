package top.kwseeker.communication.grpc;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ProtoBufSerializationTest {

    @Test
    public void testSerialize() throws IOException {
        Person.Builder builder = Person.newBuilder()
                .setId(1234)
                //.setName("John Doe")
                .setEmail("jdoe@example.com")
                .addPhones(Person.PhoneNumber.newBuilder()
                        .setNumber("555-4321")
                        .setType(Person.PhoneType.PHONE_TYPE_HOME));

        //校验是否是所有的 required （proto2）成员都被赋值
        Assert.assertFalse(builder.isInitialized());
        builder.setName("John Doe");
        Assert.assertTrue(builder.isInitialized());
        Person john = builder.build();
        System.out.println(john);
        Assert.assertTrue(john.isInitialized());    //proto2 能走到这个一定是true proto2 required 字段没赋值会抛异常 UninitializedMessageException

        builder.clear();
        Assert.assertFalse(builder.isInitialized());

        System.out.println("------------------------------------");

        //拷贝
        Person.Builder builder2 = john.toBuilder();
        Assert.assertTrue(builder2.isInitialized());
        System.out.println(builder2.build());

        System.out.println("------------------------------------");

        //序列化与反序列化
        //https://protobuf.dev/programming-guides/encoding/
        Test1 test1 = Test1.newBuilder().setA(150).build();
        Assert.assertEquals("089601", HexUtil.printHex(test1.toByteArray()));

        byte[] byteArray = john.toByteArray();
        //以16进制格式打印byteArray
        System.out.println(HexUtil.printHex(byteArray));
        Person person1 = Person.parseFrom(byteArray);
        System.out.println(person1);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        john.writeTo(os);
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        Person person2 = Person.parseFrom(is);
        System.out.println(person2);
    }

    static class HexUtil {
        public static String printHex(byte[] bytes) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            HexOutputStream hexOutputStream = new HexOutputStream(byteArrayOutputStream);

            try {
                hexOutputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return byteArrayOutputStream.toString();
        }
    }

    static class HexOutputStream extends java.io.OutputStream {
        private final OutputStream out;

        public HexOutputStream(java.io.OutputStream out) {
            this.out = out;
        }

        public void write(int b) throws java.io.IOException {
            out.write(Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16)));
            out.write(Character.toUpperCase(Character.forDigit((b & 0xF), 16)));
        }

        public void write(byte[] buf, int off, int len) throws java.io.IOException {
            for (int i = off; i < off + len; i++) {
                write(buf[i]);
            }
        }
    }
}

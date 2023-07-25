# ProtoBuf

简介：

protocol buffers （ProtoBuf）是一种语言无关、平台无关、可扩展的序列化结构数据的方法，它可用于（数据）通信协议、数据存储等。

Protocol Buffers 是一种灵活，高效，自动化机制的结构数据序列化方法－可类比 XML，但是比 XML 更小（3 ~ 10倍）、更快（20 ~ 100倍）、更为简单。

json\xml都是基于文本格式，protobuf是二进制格式。

你可以通过 ProtoBuf 定义数据结构，然后通过 ProtoBuf 工具生成各种语言版本的数据结构类库，用于操作 ProtoBuf 协议数据。

参考文档：

+ [Language Guide (proto 3)](https://protobuf.dev/programming-guides/proto3/) 

  中文翻译文档：https://github.com/lixiangyun/protobuf_doc_ZH_CN/blob/master/README.md

+ 官方拓展文档

  + [Style Guide](https://protobuf.dev/programming-guides/style/)

    .proto 编码规范（使用两空格的缩进、包名小写、message名用帕斯卡命名、field用lower_snake_case命名、对重复字段使用repeated修饰、RPC服务和方法用帕斯卡命名）、文件结构。

    这只是建议，看有些规则有些中间件源码也没有遵守（比如 SkyWalking 用的4空格缩进、field也是用的帕斯卡命名法）。

  + [Enum Behavior](https://protobuf.dev/programming-guides/enum/)

    讲解闭合枚举和开放枚举在各语言平台的行为差异。

    闭合枚举是指枚举类型中的每个字段都必须在定义中列出，且不能动态添加新的字段。在 Protobuf 2 版本中，仅支持闭合枚举。

    开放枚举是指枚举类型可以动态添加新的字段。在 Protobuf 3 版本中，支持开放枚举。

  + [Encoding](https://protobuf.dev/programming-guides/encoding/)

    讲解Protobuf数据编码原理。

    比如一个消息，使用生成的某语言的类比如Java `Test1 test1 = Test1.newBuilder().setA(150).build();`这样一个对象序列化发送到输出流，只有3个字节`08 96 01`。

    这节就是讲解这三个字节是怎么编码出来的。

    ```protobuf
    message Test1 {
      optional int32 a = 1;
    }
    ```

  + [Techniques](https://protobuf.dev/programming-guides/techniques/)

    介绍一些处理 Protocol Buffer 的常用设计模式。

  + [Add-ons](https://protobuf.dev/programming-guides/addons/)

    第三方附加组件，功能拓展。

  + [Field Presence](https://protobuf.dev/programming-guides/field_presence/)

    字段是否有值判断，比如查询float字段返回0.0，这个值到底是默认值还是业务设置的就是这个值。

    参考：[区分 Protobuf 中缺失值和默认值](https://zhuanlan.zhihu.com/p/46603988)

  + [Proto Best Practices](https://protobuf.dev/programming-guides/dos-donts/)

    proto最佳实现的十几条建议。

    + [**Don’t** Re-use a Tag Number](https://protobuf.dev/programming-guides/dos-donts/#dont-re-use-a-tag-number)
    + [**Don’t** Change the Type of a Field](https://protobuf.dev/programming-guides/dos-donts/#dont-change-the-type-of-a-field)
    + [**Don’t** Add a Required Field](https://protobuf.dev/programming-guides/dos-donts/#dont-add-a-required-field)
    + [**Don’t** Make a Message with Lots of Fields](https://protobuf.dev/programming-guides/dos-donts/#dont-make-a-message-with-lots-of-fields)
    + [**Do** Include an Unspecified Value in an Enum](https://protobuf.dev/programming-guides/dos-donts/#do-include-an-unspecified-value-in-an-enum)
    + [**Don’t** Use C/C++ Macro Constants for Enum Values](https://protobuf.dev/programming-guides/dos-donts/#dont-use-cc-macro-constants-for-enum-values)
    + **Do** Use Well-Known Types and Common Types
    + [**Do** Define Widely-used Message Types in Separate Files](https://protobuf.dev/programming-guides/dos-donts/#do-define-widely-used-message-types-in-separate-files)
    + [**Do** Reserve Tag Numbers for Deleted Fields](https://protobuf.dev/programming-guides/dos-donts/#do-reserve-tag-numbers-for-deleted-fields)
    + [**Do** Reserve Numbers for Deleted Enum Values](https://protobuf.dev/programming-guides/dos-donts/#do-reserve-numbers-for-deleted-enum-values)
    + [**Don’t** Change the Default Value of a Field](https://protobuf.dev/programming-guides/dos-donts/#dont-change-the-default-value-of-a-field)
    + [**Don’t** Go from Repeated to Scalar](https://protobuf.dev/programming-guides/dos-donts/#dont-go-from-repeated-to-scalar)
    + [**Do** Follow the Style Guide for Generated Code](https://protobuf.dev/programming-guides/dos-donts/#do-follow-the-style-guide-for-generated-code)
    + [**Don’t** use Text Format Messages for Interchange](https://protobuf.dev/programming-guides/dos-donts/#dont-use-text-format-messages-for-interchange)
    + [**Never** Rely on Serialization Stability Across Builds](https://protobuf.dev/programming-guides/dos-donts/#never-rely-on-serialization-stability-across-builds)
    + [**Don’t** Generate Java Protos in the Same Java Package as Other Code](https://protobuf.dev/programming-guides/dos-donts/#dont-generate-java-protos-in-the-same-java-package-as-other-code)

  + [API Best Practices](https://protobuf.dev/programming-guides/api/)

    API最佳实现，也是讲的proto中的建议。

    + [Precisely, Concisely Document Most Fields and Messages](https://protobuf.dev/programming-guides/api/#precisely-concisely)
    + [Use Different Messages for Wire and Storage](https://protobuf.dev/programming-guides/api/#use-different-messages)
    + For Mutations, Support Partial Updates or Append-Only Updates, Not Full Replaces
    + [Don’t Include Primitive Types in a Top-level Request or Response Proto](https://protobuf.dev/programming-guides/api/#dont-include-primitive-types)
    + [Never Use Booleans for Something That Has Two States Now, but Might Have More Later](https://protobuf.dev/programming-guides/api/#never-use-booleans-for-two-states)
    + [Rarely Use an Integer Field for an ID](https://protobuf.dev/programming-guides/api/#integer-field-for-id)
    + Don’t Encode Data in a String That You Expect a Client to Construct or Parse
    + [Encode Opaque Data in Strings by Web-Safe Encoding Binary Proto Serialization](https://protobuf.dev/programming-guides/api/#encode-opaque-data-in-strings)
    + [Don’t Include Fields that Your Clients Can’t Possibly Have a Use for](https://protobuf.dev/programming-guides/api/#dont-include-fields)
    + [*Rarely* Define a Pagination API Without a Continuation Token](https://protobuf.dev/programming-guides/api/#define-pagination-api)
    + [Group Related Fields into a New Message. Nest Only Fields with High Cohesion](https://protobuf.dev/programming-guides/api/#group-related-fields)
    + [Include a Field Read Mask in Read Requests](https://protobuf.dev/programming-guides/api/#include-field-read-mask)
    + [Include a Version Field to Allow for Consistent Reads](https://protobuf.dev/programming-guides/api/#include-version-field)
    + [Use Consistent Request Options for RPCs that Return the Same Data Type](https://protobuf.dev/programming-guides/api/#use-consistent-request-options)
    + [Batch/multi-phase Requests](https://protobuf.dev/programming-guides/api/#batch-multi-phase-requests)
    + [Create Methods that Return or Manipulate Small Bits of Data and Expect Clients to Compose UIs from Batching Multiple Such Requests](https://protobuf.dev/programming-guides/api/#create-methods-manipulate-small-bits)
    + [Make a One-off RPC when the Alternative is Serial Round-trips on Mobile or Web](https://protobuf.dev/programming-guides/api/#make-one-off-rpc)
    + [Make Repeated Fields Messages, Not Scalars or Enums](https://protobuf.dev/programming-guides/api/#repeated-fields-messages-scalar-types)
    + [Use Proto Maps](https://protobuf.dev/programming-guides/api/#use-proto-maps)
    + [Prefer Idempotency](https://protobuf.dev/programming-guides/api/#prefer-idempotency)
    + [Be Mindful of Your Service Name, and Make it Globally Unique](https://protobuf.dev/programming-guides/api/#service-name-globally-unique)
    + [Ensure Every RPC Specifies and Enforces a (Permissive) Deadline](https://protobuf.dev/programming-guides/api/#every-rpc-deadline)
    + [Bound Request and Response Sizes](https://protobuf.dev/programming-guides/api/#bound-req-res-sizes)
    + [Propagate Status Codes Carefully](https://protobuf.dev/programming-guides/api/#propagate-status-codes)

    拓展阅读：

    [来也科技 Protobuf 最佳工程实践](https://laiye.com/news/post/2585.html)

+ 各编程语言使用ProtoBuf教程
  - [Go](https://protobuf.dev/getting-started/gotutorial/)
  - [Java](https://protobuf.dev/getting-started/javatutorial/)


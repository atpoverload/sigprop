load("@protobuf//bazel:java_proto_library.bzl", "java_proto_library")

proto_library(
    name = "jiffies_protos",
    visibility = ["//visibility:public"],
    srcs = ["jiffies.proto"],
    deps = ["//profiler/proto/yuca/profiler/linux:linux_protos"]
)

java_proto_library(
    name = "jiffies_java_protos",
    visibility = ["//visibility:public"],
    deps = [":jiffies_protos"],
)

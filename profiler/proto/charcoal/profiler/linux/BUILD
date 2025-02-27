load("@protobuf//bazel:java_proto_library.bzl", "java_proto_library")

proto_library(
    name = "linux_protos",
    visibility = ["//visibility:public"],
    srcs = ["linux.proto"],
)

java_proto_library(
    name = "linux_java_protos",
    visibility = ["//visibility:public"],
    deps = [":linux_protos"],
)

load("@protobuf//bazel:java_proto_library.bzl", "java_proto_library")

proto_library(
    name = "freq_protos",
    visibility = ["//visibility:public"],
    srcs = ["freq.proto"],
)

java_proto_library(
    name = "freq_java_protos",
    visibility = ["//visibility:public"],
    deps = [":freq_protos"],
)

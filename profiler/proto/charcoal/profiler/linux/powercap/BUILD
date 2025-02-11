load("@protobuf//bazel:java_proto_library.bzl", "java_proto_library")

proto_library(
    name = "powercap_protos",
    visibility = ["//visibility:public"],
    srcs = ["powercap.proto"],
)

java_proto_library(
    name = "powercap_java_protos",
    visibility = ["//visibility:public"],
    deps = [":powercap_protos"],
)

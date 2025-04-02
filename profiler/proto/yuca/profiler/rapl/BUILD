load("@rules_proto_grpc_java//:defs.bzl", "java_proto_library")

proto_library(
    name = "rapl_protos",
    visibility = ["//visibility:public"],
    srcs = ["rapl.proto"],
)

java_proto_library(
    name = "rapl_java_protos",
    visibility = ["//visibility:public"],
    protos = [":rapl_protos"],
)

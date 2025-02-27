load("@protobuf//bazel:java_proto_library.bzl", "java_proto_library")

proto_library(
    name = "profiler_protos",
    visibility = ["//visibility:public"],
    srcs = ["profiler.proto"],
    deps = ["//profiler/proto/charcoal/profiler/linux:linux_protos"]
)

java_proto_library(
    name = "profiler_java_protos",
    visibility = ["//visibility:public"],
    deps = [":profiler_protos"],
)

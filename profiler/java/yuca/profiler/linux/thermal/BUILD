BINARY_SRCS = ["PowercapSmokeTest.java"]

java_library(
    name = "thermal",
    visibility = ["//visibility:public"],
    srcs = glob(["*.java"], exclude = BINARY_SRCS),
    deps = [
        "//profiler/proto/yuca/profiler/linux:linux_java_protos",
        "//src/java/charcoal/prop",
        "//src/java/charcoal/util",
    ],
)

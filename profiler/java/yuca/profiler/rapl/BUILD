BINARY_SRCS = ["RaplSmokeTest.java"]

java_library(
    name = "rapl",
    visibility = ["//visibility:public"],
    srcs = glob(["*.java"], exclude = BINARY_SRCS),
    deps = [
        "//src/java/charcoal/util",
        # "//src/proto/jcarbon/signal:signal_java_protos",
    ],
    resources = ["//profiler/c/charcoal/rapl:libjrapl.so"],
)

java_binary(
    name = "smoke_test",
    main_class = "yuca.profiler.rapl.RaplSmokeTest",
    srcs = ["RaplSmokeTest.java"],
    deps = [
        ":rapl",
        "//src/java/charcoal/util",
        # "//src/proto/jcarbon/signal:signal_java_protos",
    ],
)
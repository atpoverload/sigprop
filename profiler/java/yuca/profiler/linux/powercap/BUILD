BINARY_SRCS = ["PowercapSmokeTest.java"]

java_library(
    name = "powercap",
    visibility = ["//visibility:public"],
    srcs = glob(["*.java"], exclude = BINARY_SRCS),
    deps = [
        "//profiler/java/yuca/profiler/linux",
        "//profiler/proto/yuca/profiler/linux:linux_java_protos",
        "//profiler/proto/yuca/profiler/linux/powercap:powercap_java_protos",
        "//src/java/charcoal",
        "//src/java/charcoal/prop",
        "//src/java/charcoal/util",
    ],
)

java_binary(
    name = "smoke_test",
    main_class = "yuca.profiler.linux.powercap.PowercapSmokeTest",
    srcs = ["PowercapSmokeTest.java"],
    deps = [
        ":powercap",
        "//profiler/proto/yuca/profiler/linux/powercap:powercap_java_protos",
        "//src/java/charcoal/util",
    ],
)
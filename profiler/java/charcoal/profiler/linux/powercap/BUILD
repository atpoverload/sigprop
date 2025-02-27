BINARY_SRCS = ["PowercapSmokeTest.java"]

java_library(
    name = "powercap",
    visibility = ["//visibility:public"],
    srcs = glob(["*.java"], exclude = BINARY_SRCS),
    deps = [
        "//profiler/java/charcoal/profiler/linux",
        "//profiler/proto/charcoal/profiler/linux:linux_java_protos",
        "//profiler/proto/charcoal/profiler/linux/powercap:powercap_java_protos",
        "//src/java/charcoal",
        "//src/java/charcoal/prop",
        "//src/java/charcoal/util",
    ],
)

java_binary(
    name = "smoke_test",
    main_class = "charcoal.profiler.linux.powercap.PowercapSmokeTest",
    srcs = ["PowercapSmokeTest.java"],
    deps = [
        ":powercap",
        "//profiler/proto/charcoal/profiler/linux/powercap:powercap_java_protos",
        "//src/java/charcoal/util",
    ],
)
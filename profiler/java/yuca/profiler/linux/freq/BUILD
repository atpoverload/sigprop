java_library(
    name = "freq",
    visibility = ["//visibility:public"],
    srcs = glob(["*.java"]),
    deps = [
        "//profiler/proto/yuca/profiler/linux:linux_java_protos",
        "//src/java/charcoal",
        "//src/java/charcoal/prop",
        "//src/java/charcoal/util",
    ],
)

java_binary(
    name = "freq_monitor",
    main_class = "yuca.profiler.linux.freq.CpuFreqMonitor",
    srcs = ["CpuFreqMonitor.java"],
    deps = [
        ":freq",
        "//profiler/proto/yuca/profiler/linux:linux_java_protos",
        "//src/java/charcoal/util",
    ],
)

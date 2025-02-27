java_library(
    name = "freq",
    visibility = ["//visibility:public"],
    srcs = glob(["*.java"]),
    deps = [
        "//profiler/proto/charcoal/profiler/linux:linux_java_protos",
        "//src/java/charcoal",
        "//src/java/charcoal/prop",
        "//src/java/charcoal/util",
    ],
)

java_binary(
    name = "freq_monitor",
    main_class = "charcoal.profiler.linux.freq.CpuFreqMonitor",
    srcs = ["CpuFreqMonitor.java"],
    deps = [
        ":freq",
        "//profiler/proto/charcoal/profiler/linux:linux_java_protos",
        "//src/java/charcoal/util",
    ],
)

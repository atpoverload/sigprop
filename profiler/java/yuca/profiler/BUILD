java_library(
    name = "profiler",
    visibility = ["//visibility:public"],
    srcs = glob(["*.java"]),
    deps = [
        "//profiler/java/yuca/profiler/emissions",
        "//profiler/java/yuca/profiler/linux",
        "//profiler/java/yuca/profiler/linux/freq",
        "//profiler/java/yuca/profiler/linux/jiffies",
        "//profiler/java/yuca/profiler/linux/powercap",
        "//profiler/java/yuca/profiler/linux/thermal",
        "//profiler/proto/yuca/profiler:profiler_java_protos",
        "//src/java/charcoal/prop",
        "//src/java/charcoal/prop/util",
        "//src/java/charcoal/util",
    ]
)

BENCHMARK_SRCS = [
    "YucaDacapoCallback.java",
    "YucaRenaissancePlugin.java",
]

java_library(
    name = "benchmarks",
    visibility = ["//benchmarks:__pkg__"],
    srcs = glob(["*.java"], exclude = BENCHMARK_SRCS),
    deps = [
        "//profiler/java/yuca/profiler",
        "//profiler/java/yuca/profiler/linux/jiffies",
        "//profiler/java/yuca/profiler/linux/powercap",
        "//profiler/proto/yuca/profiler:profiler_java_protos",
        "//src/java/charcoal/prop",
        "//src/java/charcoal/prop/util",
        "//src/java/charcoal/util",
    ],
    exports = [
        "//profiler/java/yuca/profiler",
        "//profiler/java/yuca/profiler/linux/jiffies",
        "//profiler/java/yuca/profiler/linux/powercap",
        "//profiler/proto/yuca/profiler:profiler_java_protos",
        "//src/java/charcoal/prop",
        "//src/java/charcoal/prop/util",
        "//src/java/charcoal/util",
    ]
)

java_binary(
    name = "dacapo",
    main_class = "Harness",
    visibility = ["//benchmarks:__pkg__"],
    srcs = ["YucaDacapoCallback.java"],
    deps = [
        ":benchmarks",
        "@dacapo",
    ],
    args = [
        "--callback yuca.benchmarks.YucaDacapoCallback",
        "--no-validation",
    ]
)

java_binary(
    name = "renaissance",
    main_class = "org.renaissance.core.Launcher",
    visibility = ["//benchmarks:__pkg__"],
    srcs = ["YucaRenaissancePlugin.java"],
    deps = [
        ":benchmarks",
        "@renaissance//jar",
    ],
    args = [
        "--plugin !yuca.benchmarks.YucaRenaissancePlugin",
    ]
)

BENCHMARK_SRCS = [
    "CharcoalDacapoCallback.java",
    "CharcoalRenaissancePlugin.java",
]

java_binary(
    name = "dacapo",
    main_class = "Harness",
    visibility = ["//benchmarks:__pkg__"],
    srcs = ["CharcoalDacapoCallback.java"],
    deps = [
        "//profiler/java/charcoal/profiler",
        "//profiler/java/charcoal/profiler/linux/jiffies",
        "//profiler/java/charcoal/profiler/linux/powercap",
        "//profiler/proto/charcoal/profiler:profiler_java_protos",
        "//profiler/proto/charcoal/profiler/linux/freq:freq_java_protos",
        "//profiler/proto/charcoal/profiler/linux:linux_java_protos",
        "//src/java/charcoal/prop",
        "//src/java/charcoal/prop/util",
        "//src/java/charcoal/util",
        "@dacapo",
    ],
    args = [
        "--callback charcoal.benchmarks.CharcoalDacapoCallback",
        "--no-validation",
    ]
)

java_binary(
    name = "renaissance",
    main_class = "org.renaissance.core.Launcher",
    visibility = ["//benchmarks:__pkg__"],
    srcs = ["CharcoalRenaissancePlugin.java"],
    deps = [
        "//profiler/java/charcoal/profiler",
        "//profiler/java/charcoal/profiler/linux/jiffies",
        "//profiler/java/charcoal/profiler/linux/powercap",
        "//profiler/proto/charcoal/profiler/linux:linux_java_protos",
        "//src/java/charcoal/prop",
        "//src/java/charcoal/prop/util",
        "//src/java/charcoal/util",
        "@renaissance//jar",
    ],
    args = [
        "--plugin !charcoal.benchmarks.CharcoalRenaissancePlugin",
    ]
)

BENCHMARK_SRCS = [
    "CharcoalDacapoCallback.java",
    "JCarbonPlugin.java",
]

java_binary(
    name = "dacapo",
    main_class = "Harness",
    visibility = ["//benchmarks:__pkg__"],
    srcs = ["CharcoalDacapoCallback.java"],
    deps = [
        "//profiler/java/charcoal/profiler/linux/jiffies",
        "//profiler/java/charcoal/profiler/linux/powercap",
        "//profiler/proto/charcoal/profiler/linux/jiffies:jiffies_java_protos",
        "//src/java/charcoal/prop",
        "//src/java/charcoal/prop/util",
        "//src/java/charcoal/util",
        "@dacapo",
    ],
    args = [
        "--callback jcarbon.benchmarks.CharcoalDacapoCallback",
        "--no-validation",
    ]
)

java_binary(
    name = "renaissance",
    main_class = "org.renaissance.core.Launcher",
    visibility = ["//benchmarks:__pkg__"],
    srcs = ["JCarbonPlugin.java"],
    deps = [
        "//benchmarks/src/main/java/jcarbon/benchmarks/util",
        "//src/jcarbon-proto:jcarbon",
        "@renaissance//jar",
    ],
    args = [
        "--plugin !jcarbon.benchmarks.JCarbonPlugin",
    ]
)

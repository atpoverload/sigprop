BENCHMARK_SRCS = [
    "YucaDacapoCallback.java",
    "YucaRenaissancePlugin.java",
]

java_library(
    name = "benchmarks",
    srcs = glob(["*.java"], exclude=BENCHMARK_SRCS),
    deps = [
        "//profiler/java/yuca/profiler",
        "//profiler/proto/yuca/profiler:profiler_java_protos",
    ]
)

java_binary(
    name = "dacapo",
    main_class = "Harness",
    visibility = ["//benchmarks:__pkg__"],
    srcs = ["YucaDacapoCallback.java"],
    deps = [
        ":benchmarks",
        "//profiler/java/yuca/profiler",
        "//profiler/proto/yuca/profiler:profiler_java_protos",
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
        "//profiler/java/yuca/profiler",
        "//profiler/proto/yuca/profiler:profiler_java_protos",
        "@renaissance//jar",
    ],
    args = [
        "--plugin !yuca.benchmarks.YucaRenaissancePlugin",
    ]
)

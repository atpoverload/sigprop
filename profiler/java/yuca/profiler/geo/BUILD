load("@rules_jvm_external//:defs.bzl", "artifact")

java_library(
    name = "geo",
    visibility = ["//visibility:public"],
    srcs = glob(["*.java"]),
    deps = [
        artifact("com.maxmind.geoip2:geoip2"),
    ]
)

java_binary(
    name = "geoloc",
    main_class = "yuca.profiler.util.GeoLocator",
    runtime_deps = [":util"],
)

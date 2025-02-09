cc_library(
    name = "time-lib",
    visibility = ["//visibility:public"],
    srcs = glob(["*.c"]),
    hdrs = glob(["*.h"]),
    deps = ["//src/c:copy_jni_hdr_lib"],
    alwayslink = True,
)

cc_binary(
    name = "libtime.so",
    visibility = ["//visibility:public"],
    linkshared = True,
    data = glob(["*.h"]),
    deps = [":time-lib"],
)

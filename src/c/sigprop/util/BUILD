cc_library(
    name = "time-lib",
    srcs = glob(["*.c"]),
    hdrs = glob(["*.h"]),
    deps = ["//src/c:copy_jni_hdr_lib"],
    alwayslink = True,
)

cc_binary(
    name = "libtime.so",
    visibility = ["//src/java/sigprop:__subpackages__"],
    linkshared = True,
    data = glob(["*.h"]),
    deps = [":time-lib"],
)

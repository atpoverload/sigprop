# Script to reproduce the energy accounting experiments with dacapo
DATA_DIR=online-yuca
DATA_DIR="experiment-data/${DATA_DIR}"
mkdir -p "${DATA_DIR}"

PERIOD_MS=100
ITERATIONS=5
LOCALE=USA

run_benchmark() {
    $(python3 benchmarks/run_benchmark.py \
        --profiler ONLINE_YUCA \
        --period "${PERIOD_MS}" \
        --output "${DATA_DIR}/${BENCHMARK}" \
        --locale USA \
        dacapo \
        --size "${SIZE}" \
        --iterations "${ITERATIONS}" \
        "${BENCHMARK}")
}

# default size dacapo benchmarks
SIZE=default
BENCHMARKS=(
    biojava
    cassandra
    fop
    graphchi
    h2o
    jme
    jython
    kafka
    luindex
    lusearch
    tradebeans
    tradesoap
    xalan
    zxing
)
for BENCHMARK in ${BENCHMARKS[@]}; do
    run_benchmark
done

# large size dacapo benchmarks
SIZE=large
BENCHMARKS=(
    avrora
    batik
    eclipse
    # TODO: need to update and setup the new dacapo with the big data
    # graphchi
    h2
    pmd
    sunflow
    tomcat
)
for BENCHMARK in ${BENCHMARKS[@]}; do
    run_benchmark
done

# huge size dacapo benchmarks
# TODO: need to update and setup the new dacapo with the big data
# BENCHMARKS=(
#     graphchi
# )

# SIZE=huge

# for BENCHMARK in ${BENCHMARKS[@]}; do
#     run_benchmark
# done

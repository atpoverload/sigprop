# Script to reproduce the energy accounting experiments with dacapo

DATA_DIR=data
mkdir -p "${DATA_DIR}"

ITERATIONS=1
LOCALE=USA

run_benchmark() {
    local data_dir="${DATA_DIR}/${BENCHMARK}"
    mkdir -p "${data_dir}"
    java -jar bazel-bin/benchmarks/java/charcoal/benchmarks/dacapo_deploy.jar \
        --callback charcoal.benchmarks.CharcoalDacapoCallback \
        --iterations ${ITERATIONS} \
        --size ${SIZE} \
        --no-validation \
        ${BENCHMARK} 
}

# default size dacapo benchmarks
BENCHMARKS=(
    biojava
    cassandra
    fop
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

SIZE=default

for BENCHMARK in ${BENCHMARKS[@]}; do
    run_benchmark
    exit
done

# large size dacapo benchmarks
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

SIZE=large

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

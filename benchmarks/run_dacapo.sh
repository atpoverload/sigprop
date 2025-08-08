# Script to reproduce the energy accounting experiments with dacapo

DATA_DIR=profiled
DATA_DIR="/tmp/${DATA_DIR}"
mkdir -p "${DATA_DIR}"

ITERATIONS=100
LOCALE=USA

PERIOD=100

run_benchmark() {
    java -Dyuca.benchmarks.period=$PERIOD -Dyuca.benchmarks.output=${DATA_DIR} \
        -jar bazel-bin/benchmarks/java/yuca/benchmarks/dacapo_deploy.jar \
        --callback yuca.benchmarks.YucaDacapoCallback \
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

SIZE=default

for BENCHMARK in ${BENCHMARKS[@]}; do
    run_benchmark
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

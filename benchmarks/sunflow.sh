# Script to reproduce the energy accounting experiments with dacapo

DATA_DIR=test
DATA_DIR="/tmp/${DATA_DIR}"
mkdir -p "${DATA_DIR}"

ITERATIONS=25
LOCALE=USA

run_benchmark() {
    java -Dyuca.benchmarks.period=$PERIOD -Dyuca.benchmarks.output=${DATA_DIR} \
        -jar bazel-bin/benchmarks/java/yuca/benchmarks/dacapo_deploy.jar \
        --callback yuca.benchmarks.YucaDacapoCallback \
        --iterations ${ITERATIONS} \
        --size ${SIZE} \
        --no-validation \
        ${BENCHMARK} 
}

PERIOD=0
BENCHMARK=sunflow
SIZE=default
run_benchmar

PERIOD=100
BENCHMARK=sunflow
SIZE=default
run_benchmark

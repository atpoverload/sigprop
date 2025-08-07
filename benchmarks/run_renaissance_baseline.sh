# Script to reproduce the energy accounting experiments with dacapo

DATA_DIR=baseline
DATA_DIR="/tmp/${DATA_DIR}"
mkdir -p "${DATA_DIR}"

ITERATIONS=25
LOCALE=USA

PERIOD=0

run_benchmark() {
    java -Dyuca.benchmarks.period=$PERIOD -Dyuca.benchmarks.output=${DATA_DIR} \
        -jar bazel-bin/benchmarks/java/yuca/benchmarks/renaissance_deploy.jar \
        --repetitions ${ITERATIONS} \
        --plugin "!yuca.benchmarks.YucaRenaissancePlugin" \
        ${BENCHMARK}
}

BENCHMARKS=(
    scrabble
    page-rank
    future-genetic
    akka-uct
    movie-lens
    scala-doku
    chi-square
    fj-kmeans
    rx-scrabble
    db-shootout
    neo4j-analytics
    finagle-http
    reactors
    dec-tree
    scala-stm-bench7
    naive-bayes
    als
    par-mnemonics
    scala-kmeans
    philosophers
    log-regression
    gauss-mix
    mnemonics
    dotty
    finagle-chirper
)

for BENCHMARK in ${BENCHMARKS[@]}; do
    run_benchmark
done

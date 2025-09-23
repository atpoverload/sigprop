# Script to reproduce the energy accounting experiments with dacapo
DATA_DIR=baseline
DATA_DIR="/experiment/${DATA_DIR}"
mkdir -p "${DATA_DIR}"

ITERATIONS=50
LOCALE=USA

run_benchmark() {
    $(python3 benchmarks/run_benchmark.py \
        --profiler END_TO_END \
        --output "/experiment/test/${BENCHMARK}" \
        --locale USA \
        renaissance \
        --repetitions "${ITERATIONS}" \
        "${BENCHMARK}")
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

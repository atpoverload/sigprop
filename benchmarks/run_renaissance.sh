# Script to reproduce the energy accounting experiments with renaissance

DATA_DIR=data
# mkdir -p "${DATA_DIR}"

ITERATIONS=25
LOCALE=USA

run_benchmark() {
    local data_dir="${DATA_DIR}/${BENCHMARK}"
    # mkdir -p "${data_dir}"
    java -jar bazel-bin/benchmarks/java/yuca/benchmarks/renaissance_deploy.jar \
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

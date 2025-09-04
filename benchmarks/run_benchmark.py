import os
import shutil

from argparse import ArgumentParser


def parse_args():
    parser = ArgumentParser(description="yuca benchmark command generator")
    subparser = parser.add_subparsers(help="benchmark suite", dest="suite")

    parser.add_argument("--profiler", type=str, default="online")
    parser.add_argument("--period", type=int, default=100)
    parser.add_argument("--output", type=str, default="/tmp/yuca")

    parser.add_argument("--locale", type=str, default="GLOBAL")
    parser.add_argument("--embodied", type=float, default=1.0)

    dacapo_parser = subparser.add_parser("dacapo")
    dacapo_parser.add_argument("-s", "--size", type=str, default="default")
    dacapo_parser.add_argument("-n", "--iterations", type=int, default=25)
    dacapo_parser.add_argument("benchmark")

    renaissance_parser = subparser.add_parser("renaissance")
    renaissance_parser.add_argument("-r", "--repetitions", type=int, default=25)
    renaissance_parser.add_argument("benchmark")

    return parser.parse_args()


def java_cmd(args):
    return [
        "java",
        f"-Dyuca.benchmarks.profiler={args.profiler.upper()}",
        f"-Dyuca.benchmarks.profiler.period={args.period}",
        f"-Dyuca.benchmarks.output={args.output}",
        f"-Dyuca.profiler.emissions.locale.default={args.locale.upper()}",
        f"-Dyuca.profiler.emissions.embodied.system={args.embodied}",
    ]


def main():
    args = parse_args()

    if args.suite == "dacapo":
        cmd = [
            *java_cmd(args),
            "-jar bazel-bin/benchmarks/java/yuca/benchmarks/dacapo_deploy.jar",
            "--callback yuca.benchmarks.YucaDacapoCallback",
            f"--size {args.size}",
            f"--iterations {args.iterations}",
            "--no-validation",
            "--scratch-directory=/tmp/scratch",
            f"{args.benchmark}",
        ]
    elif args.suite == "renaissance":
        cmd = [
            *java_cmd(args),
            "-jar bazel-bin/benchmarks/java/yuca/benchmarks/renaissance_deploy.jar",
            "--plugin !yuca.benchmarks.YucaRenaissancePlugin",
            f"--repetitions {args.repetitions}",
            "--scratch-base=/tmp/scratch",
            f"{args.benchmark}",
        ]
    if os.path.exists(args.output):
        shutil.rmtree(args.output)
    os.makedirs(args.output)
    print(" ".join(cmd))


if __name__ == "__main__":
    main()

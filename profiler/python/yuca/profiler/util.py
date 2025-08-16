import os

from argparse import ArgumentParser
from math import sqrt

import matplotlib.pyplot as plt
import pandas as pd

from tqdm import tqdm

from yuca.profiler.profiler_pb2 import YucaProfile

COLUMNS = ['case', 'suite', 'benchmark', 'iteration', 'metric', 'value']


def timestamp_diff(start, end):
    return (end.secs + end.nanos / 10**9) - (start.secs + start.nanos / 10**9)


def runtime(profile):
    start = None
    end = None
    for emissions in profile.socket_emissions:
        ts = emissions.timestamp
        ts = ts.secs + ts.nanos / 10**9
        if start is None or ts < start:
            start = ts
        if end is None or end >= end:
            end = ts

    return end - start


def socket_emissions(profile):
    carbon = 0
    last_emissions = None
    for emissions in profile.socket_emissions:
        if last_emissions is not None:
            elapsed = timestamp_diff(
                last_emissions.timestamp, emissions.timestamp)
            rate = sum(map(lambda e: e.emissions, last_emissions.emissions))
            carbon += rate * elapsed
        last_emissions = emissions

    return carbon


def task_emissions(profile):
    carbon = 0
    last_emissions = None
    for emissions in profile.task_emissions:
        if last_emissions is not None:
            elapsed = timestamp_diff(
                last_emissions.timestamp, emissions.timestamp)
            rate = sum(map(lambda e: e.emissions, last_emissions.emissions))
            carbon += rate * elapsed
        last_emissions = emissions

    return carbon


def amortized_emissions(profile):
    carbon = 0
    last_emissions = None
    for emissions in profile.amortized_emissions:
        if last_emissions is not None:
            elapsed = timestamp_diff(
                last_emissions.timestamp, emissions.timestamp)
            rate = sum(map(lambda e: e.emissions, last_emissions.emissions))
            carbon += rate * elapsed
        last_emissions = emissions

    return carbon


METRICS = [
    runtime,
    socket_emissions,
    task_emissions,
    amortized_emissions,
]


def parse_args():
    arg_parser = ArgumentParser()
    arg_parser.add_argument(
        nargs='+',
        type=str,
        help='any number of yuca protobuf profiles',
        dest='files',
    )

    arg_parser.add_argument(
        '--aggregate',
        default=True,
        action='store_false',
        help='whether to reduce the protobuf files to metrics',
        dest='aggregate',
    )

    return arg_parser.parse_args()


def main():
    args = parse_args()

    records = []
    with tqdm(args.files) as pbar:
        for f in args.files:
            dir_name = os.path.basename(os.path.dirname(f))
            suite, benchmark, i = os.path.basename(f).split('@')
            i = i.split(r'.')[0]
            pbar.set_description(','.join([dir_name, suite, benchmark, i]))
            profile = YucaProfile()
            with open(f, 'rb') as f:
                profile.ParseFromString(f.read())

            for metric in METRICS:
                records.append([
                    dir_name,
                    suite,
                    benchmark,
                    i,
                    metric.__name__,
                    metric(profile)
                ])
    df = pd.DataFrame(records, columns=COLUMNS)
    df.to_csv('signals.csv')

    metrics = df.groupby(
        ['benchmark', 'suite', 'metric', 'case']).value.agg(('mean', 'std'))

    print(metrics)
    metrics.to_csv('metrics.csv')


if __name__ == '__main__':
    main()

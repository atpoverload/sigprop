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
    time = 0
    carbon = 0
    last_emissions = None
    for emissions in profile.socket_emissions:
        if last_emissions is not None:
            elapsed = timestamp_diff(
                last_emissions.timestamp, emissions.timestamp)
            rate = sum(map(lambda e: e.emissions, last_emissions.emissions))
            time += elapsed
            carbon += rate * elapsed
        last_emissions = emissions

    return carbon


def task_emissions(profile):
    time = 0
    carbon = 0
    last_emissions = None
    for emissions in profile.task_emissions:
        if last_emissions is not None:
            elapsed = timestamp_diff(
                last_emissions.timestamp, emissions.timestamp)
            rate = sum(map(lambda e: e.emissions, last_emissions.emissions))
            time += elapsed
            carbon += rate * elapsed
        last_emissions = emissions

    return carbon

# def embodied_emissions(profile):


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
        is_flag=True,
        default=True,
        help='whether to reduce the protobuf files to metrics',
        dest='aggregate',
    )

    return arg_parser.parse_args()


def main():
    args = parse_args()

    records = []
    with tqdm(args.files) as pbar:
        for f in tqdm(args.files):
            dir_name = os.path.basename(os.path.dirname(f))
            suite, benchmark, i = os.path.basename(f).split('@')
            i = i.split(r'.')[0]
            pbar.set_description(','.join([dir_name, suite, benchmark, i]))
            profile = YucaProfile()
            with open(f, 'rb') as f:
                profile.ParseFromString(f.read())

            records.append([
                dir_name,
                suite,
                benchmark,
                i,
                'runtime',
                runtime(profile)
            ])
            records.append([
                dir_name,
                suite,
                benchmark,
                i,
                'socket_emissions',
                socket_emissions(profile)
            ])
            records.append([
                dir_name,
                suite,
                benchmark,
                i,
                'task_emissions',
                task_emissions(profile)
            ])
    df = pd.DataFrame(records, columns=COLUMNS)
    df.to_csv('signals.csv')

    metrics = df.groupby(
        ['benchmark', 'suite', 'metric', 'case']).value.agg(('mean', 'std'))

    print(metrics)
    metrics.to_csv('metrics.csv')


if __name__ == '__main__':
    main()

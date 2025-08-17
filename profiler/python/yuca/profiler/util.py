import os

from argparse import ArgumentParser
from copy import deepcopy
from math import sqrt

import matplotlib.pyplot as plt
import pandas as pd

from tqdm import tqdm

from yuca.profiler.profiler_pb2 import YucaProfile


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


def process(profile, metrics, pbar=None):
    records = []
    period = None
    if profile.session.HasField('period'):
        period = 1000 * profile.session.period.secs + \
            profile.session.period.nanos // 1000000
        if period < 1:
            period = profile.session.period.nanos // 1000
            if period < 1:
                period = profile.session.period.nanos
                if period < 1:
                    period = None
                else:
                    period = f'{period}ns'
            else:
                period = f'{period}us'
        else:
            period = f'{period}ms'
    metadata = dict((m.key, m.value) for m in profile.session.metadata)
    metadata['period'] = period
    if pbar is not None:
        pbar.set_description(
            f'period: {period}', list(map(lambda m: f'{m[0]}: {m[1]}', metadata.items())))
    for metric in metrics:
        print(metric)
        metadata['metric'] = metric.__name__
        metadata['value'] = metric(profile)
        print(metadata)
        records.append(deepcopy(metadata))
    return records


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
    arg_parser.add_argument(
        '-m'
        '--metrics',
        default=[],
        help=f'the metrics to compute; options are {METRICS}',
        dest='metrics',
    )

    return arg_parser.parse_args()


def main():
    args = parse_args()

    if len(args.metrics) > 0:
        metrics = [exec(m) for m in args.metrics]
    else:
        metrics = METRICS

    print(args.files)
    with tqdm(len(args.files)) as pbar:
        records = []
        for i, f in enumerate(args.files):
            print(f)
            profile = YucaProfile()
            with open(f, 'rb') as f:
                profile.ParseFromString(f.read())
            records.extend(process(profile, metrics, pbar))
            pbar.update(i)
            print(records)

    records = pd.DataFrame.from_dict(records)
    records.to_csv('iteration_metrics.csv')

    columns = [c for c in records.columns if c != 'value']
    metrics = records.groupby(columns).value.agg(('mean', 'std'))
    metrics.to_csv('aggregated_metrics.csv')

    print(metrics)


if __name__ == '__main__':
    main()

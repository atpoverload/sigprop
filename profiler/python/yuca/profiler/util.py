import os

from argparse import ArgumentParser
from math import sqrt

import matplotlib.pyplot as plt
import pandas as pd

from tqdm import tqdm

from yuca.profiler.profiler_pb2 import YucaProfile

COLUMNS = ['case', 'benchmark', 'iteration', 'metric', 'value']


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

# def embodied_emissions(profile):


def parse_args():
    arg_parser = ArgumentParser()
    arg_parser.add_argument(
        nargs='+',
        type=str,
        help='yuca profiles',
        dest='files',
    )

    return arg_parser.parse_args()


def main():
    args = parse_args()

    records = []
    for f in tqdm(args.files):
        dir_name = os.path.dirname(f)
        file_name = os.path.basename(f).split('-')
        if len(file_name) > 3:
            suite = file_name[0]
            i = file_name[-1]
            benchmark = '-'.join(file_name[1:-1])
        else:
            suite, benchmark, i = file_name
        i = i.split(r'.')[0]
        profile = YucaProfile()
        with open(f, 'rb') as f:
            profile.ParseFromString(f.read())

        records.append([dir_name, benchmark, i, 'runtime', runtime(profile)])
        records.append(
            [dir_name, benchmark, i, 'socket_emissions', socket_emissions(profile)])
    df = pd.DataFrame(records, columns=COLUMNS)
    df.to_csv('signals.csv')

    metrics = df.groupby(
        ['benchmark', 'metric', 'case']).value.agg(('mean', 'std'))
    data = metrics.loc[:, :, 'data']
    baseline = metrics.loc[:, :, 'baseline']
    u = 100 * (data['mean'] / baseline['mean'] - 1)
    s = (data['std'] / data['mean'])**2 + \
        (baseline['std'] / baseline['mean'])**2

    metrics.to_csv('metrics.csv')
    print(u)
    print(s)

    for (i1, u), (_, s) in zip(u.groupby('metric'), s.groupby('metric')):
        print(u)
        u.reset_index(['metric'], drop=True).plot.bar(
            yerr=s.reset_index(['metric'], drop=True), figsize=(16, 9))
        plt.savefig(f'{i1}.pdf', bbox_inches='tight')
        plt.close()


if __name__ == '__main__':
    main()

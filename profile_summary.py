from argparse import ArgumentParser
from copy import deepcopy

import pandas as pd

from tqdm import tqdm

from yuca.profiler.profiler_pb2 import YucaProfile
from yuca.profiler.util import runtime, socket_emissions, task_emissions, amortized_emissions

SIGNALS = [
    runtime,
    socket_emissions,
    task_emissions,
    amortized_emissions,
]


def process(profile, signals, pbar=None):
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
    for signal in signals:
        metadata['signal'] = signal.__name__
        metadata['value'] = signal(profile)
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
        '-s'
        '--signals',
        default=[],
        help=f'the signals to compute; options are {SIGNALS}',
        dest='signals',
    )
    return arg_parser.parse_args()


def main():
    args = parse_args()

    if len(args.signals) > 0:
        signals = [exec(m) for m in args.signals]
    else:
        signals = SIGNALS

    print(args.files)
    with tqdm(len(args.files)) as pbar:
        records = []
        for i, f in enumerate(args.files):
            profile = YucaProfile()
            with open(f, 'rb') as f:
                profile.ParseFromString(f.read())
            records.extend(process(profile, signals, pbar))
            pbar.update(i)
        pbar.close()

    records = pd.DataFrame.from_dict(records)
    records.to_csv('signals.csv')
    print(records)


if __name__ == '__main__':
    main()

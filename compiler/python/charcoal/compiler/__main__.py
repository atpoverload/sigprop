import os

from argparse import ArgumentParser

import yaml

from java.charcoal import CharcoalEnum, CharcoalSignal, CharcoalProgram


def parse_args():
    parser = ArgumentParser('charcoal compiler')
    parser.add_argument(nargs=1, dest='signal', type=str)

    return parser.parse_args()


def main():
    args = parse_args()

    for signal_file in args.signal:
        package, file_ext = os.path.splitext(signal_file)
        entities = []
        if file_ext == r'.yaml':
            with open(signal_file, encoding='utf') as f:
                program_file = yaml.load(f.read(), Loader=yaml.Loader)
            for name, value in program_file.items():
                if name == 'program':
                    program = CharcoalProgram(package, )
                elif 'type' not in value:
                    entities.append(CharcoalEnum(value, package))
                else:
                    signal = CharcoalSignal(value, package)
                    entities.append(signal.data)
                    entities.append(signal)
        # signal = []
        # for line in signal_text.split(os.linesep):
            # line = line.split('#')[0].strip()
            # if not line:
                # continue
            # signal.append(line)
        # signal = parse_signal(signal)
        # schema = parse_schema(signal)
        # program = parse_program(signal)
        # pprint.pprint(signal)
        # print(schema)
    list(map(print, map(lambda e: e.as_class_file(), entities)))


if __name__ == '__main__':
    main()

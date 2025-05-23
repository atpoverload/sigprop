import re
import os

import 

# SCHEMA_SIGNATURE = re.compile(
#     r'([a-z][a-z_]*)\(|([a-z][a-z_]*):\s+([a-z][a-z_]*)|(?:\):\s+([a-z][a-z_]*))'
# )


# def parse_schema(schema):
#     signal = {}
#     signature = p.findall(schema[0])
#     signal['name'] = signature[0][0]
#     signal['type'] = signature[-1][-1]

#     signal['params'] = {}
#     for param in signature[1:-1]:
#         name = param[1]
#         clazz = param[2]
#         if name in signal['params']:
#             raise ValueError(
#                 f'signal parameter {name} used twice for {signal['name']}')
#         signal['params'][name] = clazz

#     line = lines.pop(0)
#     fields = {}
#     while lines and '(' not in line:
#         if len(line) > 0:
#             field_name, field_type = [
#                 token.strip() for token in line.split(':')]
#             if field_name in fields:
#                 raise ValueError(
#                     f'signal field {field_name} used twice for {signal_name}')
#             fields[field_name] = field_type
#         line = lines.pop(0)
#     method_body = []
#     if '}' in line:
#         method_body.append(line)
#     else:
#         while lines and '}' not in line:
#             if len(line) > 0:
#                 method_body.append(line)
#             line = lines.pop(0)
#     return signal_name, signal_type, fields, params, method_body


# def generate_java_schema(signal_name, fields):
#     value = []
#     value.append(
#         f'package sigprop.compiler.generated.{signal_name};')
#     value.append('')
#     value.append(
#         'public final class %s {' % signal_name.title())
#     value_fields = []
#     for field_name, field_type in fields.items():
#         field_tokens = field_name.split('_')
#         field_name = field_tokens[0] + ''.join(
#             [token.title() for token in field_tokens[1:]])
#         if field_type in TIME_PRIMITIVES:
#             if not value[2].startswith('import '):
#                 value.insert(1, '')
#             field_type = field_type.title()
#             value.insert(2, f'import java.util.{field_type};')
#         value_fields.append(
#             f'  public final {field_type} {field_name};')
#     value.append(os.linesep.join(value_fields))
#     constructor_params = []
#     constructor_body = []
#     for field_name, field_type in fields.items():
#         field_tokens = field_name.split('_')
#         field_name = field_tokens[0] + ''.join(
#             [token.title() for token in field_tokens[1:]])
#         if field_type in TIME_PRIMITIVES:
#             field_type = field_type.title()
#         constructor_params.append(f'{field_type} {field_name}')
#         constructor_body.append(
#             f'    this.{field_name} = {field_name};')
#     if len(fields) > 0:
#         value.append('')
#     value.append('  %s(%s) {' % (
#         signal_name.title(),
#         ', '.join(constructor_params)
#     ))
#     for field in constructor_body:
#         value.append(field)
#     value.append('  }')
#     value.append('}')
#     return os.linesep.join(value)


# def generate_java_wiring(signal_name, signal_type, params, method_body):
#     signal = []
#     signal.append(
#         f'package sigprop.compiler.generated.{signal_name};')
#     signal.append('')
#     signal.append(
#         'public final class %sSignal extends %sSignal<%s> {' % (signal_name.title(), signal_type.title(), signal_name.title()))
#     signal_params = []
#     for param_name, param_type in params.items():
#         param_tokens = param_name.split('_')
#         param_name = param_tokens[0] + ''.join(
#             [token.title() for token in param_tokens[1:]])
#         if param_type in TIME_PRIMITIVES:
#             if not signal[2].startswith('import '):
#                 signal.insert(1, '')
#             param_type = param_type.title()
#             signal.insert(2, f'import java.util.{param_type};')
#         signal_params.append(
#             f'  public final {param_type} {param_name};')
#     signal.append(os.linesep.join(signal_params))
#     constructor_params = []
#     constructor_body = []
#     for param_name, param_type in params.items():
#         param_tokens = param_name.split('_')
#         param_name = param_tokens[0] + ''.join(
#             [token.title() for token in param_tokens[1:]])
#         if param_type in TIME_PRIMITIVES:
#             param_type = param_type.title()
#         constructor_params.append(f'{param_type} {param_name}')
#         constructor_body.append(
#             f'    this.{param_name} = {param_name};')
#     if len(params) > 0:
#         signal.append('')
#     signal.append('  %sSignal(%s) {' % (
#         signal_name.title(),
#         ', '.join(constructor_params)
#     ))
#     for field in constructor_body:
#         signal.append(field)
#     signal.append('  }')

#     print(method_body[0])
#     return_value = None
#     method_signature = method_body.pop(0).split(':')
#     if len(method_signature) > 1:
#         return_value = method_signature[1].strip()
#         method_signature = method_signature[0].strip()
#     method_signature = method_signature.strip()
#     method_name, method_args = method_signature.split('(')
#     method_name = method_name.strip()
#     method_args = method_args.strip()[:-1].split(',')

#     method = []
#     method.append('  @Override')
#     method.append('  public %s %s(%s) {' % (
#         signal_name, method_name, ', '.join(method_args)))

#     if len(method_body) > 0:
#         for line in method_body:
#             if '{' in line:
#                 if len(line) > 1:
#                     return_value = line.strip()

#     if not return_value:
#         raise ValueError(f'no return value found for {signal_name}')

#     print(return_value)

#     method.append(f'    return new {signal_name}({return_value});')
#     signal.append('  }')
#     # inline_return

#     signal.append('}')
#     return os.linesep.join(signal)


def parse_args():
    parser = ArgumentParser('sigprop compiler')
    parser.add_argument(nargs='+', dest='signal', type=str)

    return parser.parse_args()


def main():
    # args = parse_args()
    
    # for signal in args.signal:
    #     if os.path.splitext(signal)[1] == 'yaml':
            

    # p = re.compile(
    #     r'([a-z][a-z_]*)\(|([a-z][a-z_]*):\s+([a-z][a-z_]*)|(?:\):\s+([a-z][a-z_]*))'
    # )
    # signals = []
    # for signal in args.signal:
    #     schemas = []
    #     wiring = []
    #     lines = [line for line in signal.split(os.linesep) if line]
    #     while lines:
    #         line = lines.pop(0)
    #         # parse a schema
    #         m = p.findall(line)
    #         if len(m) > 0:
    #             signal_name, signal_type, fields, params, method_body = parse_schema(
    #                 m, lines)
    #             java_schema = generate_java_schema(signal_name, fields)
    #             java_wiring = generate_java_wiring(
    #                 signal_name, signal_type, params, method_body)

                # print(java_schema)
                # print(java_wiring)
            # break
            # if '(' in line:
                # pass
            # parse a wiring
            # elif '<' in line:
                # pass
            # print(p.(line))
            # if p.search(line):
            #     print(line)
            #     print(p.match(line))
            #     schema = []
            #     schema.append(line)
            #     while lines and '}' not in line:
            #         line = lines.pop(0)
            #         schema.append(line)
            #     schemas.append(schema)
            # elif '>' in line:
            #     wiring.append(line)
            #     while lines and '>' in line:
            #         line = lines.pop(0)
            #         wiring.append(line)

        signals.append([schemas, wiring])
    # print(*signals)


if __name__ == '__main__':
    main()

NUMERIC_PRIMITIVES = [
    'boolean',
    'int',
    'long',
    'float',
    'double'
]

TIME_PRIMITIVES = [
    'Instant',
    'Duration',
]


def lower_camel(name):
    return ''.join([word.title() if not name.startswith(word) else word for word in name.split('_')])


def upper_camel(name):
    return ''.join([word.title() for word in name.split('_')])


def as_java_type(clazz):
    return clazz if clazz in NUMERIC_PRIMITIVES else upper_camel(clazz)


def new_package(package_name):
    return f'package {package_name};'


def new_enum(enum_name, package_name, enum_values):
    class_file = []
    class_file.append(new_package(package_name))
    class_file.append('')
    class_file.append('public enum %s {' % upper_camel(enum_name))
    for value in enum_values:
        if value != enum_values[-1]:
            class_file.append(f'  {value.upper()},')
        else:
            class_file.append(f'  {value.upper()};')
    class_file.append('}')
    return '\n'.join(class_file)


def new_class(class_name):
    return 'public final class %s' % class_name


def new_argument(field, field_type):
    if field_type not in NUMERIC_PRIMITIVES:
        field_type = upper_camel(field_type)
    field_name = lower_camel(field_name)
    return f'{field_type} {field}'


def new_field(field, field_type):
    return f'  public final {field_type} {field};'


def new_assignment(field):
    return f'    this.{field} = {field};'


def new_constructor(class_name, fields):
    constructor = []
    if len(fields) == 0:
        constructor.append('  %s() {' % class_name)
    if len(fields) == 1:
        field, field_type = list(fields.items())[0]
        constructor.append('  %s(%s %s) {' % (class_name, field, field_type))
        constructor.append(new_assignment(field))
    else:
        constructor.append(f'  {class_name}(')
        for i, (field, field_type) in enumerate(fields.items()):
            if i < len(fields) - 1:
                constructor.append(f'      {field_type} {field},')
            else:
                constructor.append('      %s %s) {' % (field_type, field))
        for field in fields:
            constructor.append(new_assignment(field))
    constructor.append('  }')
    return constructor


def new_schema(class_name, package_name, fields={}):
    class_name = upper_camel(class_name)
    fields = {
        lower_camel(field): as_java_type(fields[field]) for field in fields
    }

    class_file = []
    class_file.append(new_package(package_name))
    class_file.append('')
    time_fields = set(fields.values()) & set(TIME_PRIMITIVES)
    if len(time_fields) > 0:
        for field in time_fields:
            class_file.append(f'import time.util.{field};')
        class_file.append('')
    class_file.append('%s {' % new_class(class_name))
    for field in fields:
        class_file.append(new_field(field, fields[field]))
    class_file.append('')
    class_file.extend(new_constructor(
        upper_camel(class_name), fields
    ))
    class_file.append('}')
    return '\n'.join(class_file)


def new_signal_type(class_name, signal_type, input_classes):
    if len(input_classes) > 0:
        return f'{signal_type}Signal<{', '.join(input_classes)}, {class_name}>'
    else:
        return f'{signal_type}Signal<{class_name}>'


def new_signal(class_name, package_name, signal_type, fields=[]):
    class_name = f'{upper_camel(class_name)}Signal'
    signal_type = f'{upper_camel(signal_type)}Signal'
    fields = {
        lower_camel(field): as_java_type(fields[field]) for field in fields
    }

    class_file = []
    class_file.append(new_package(package_name))
    class_file.append('')
    time_fields = set(fields.values()) & set(TIME_PRIMITIVES)
    if len(time_fields) > 0:
        for field in time_fields:
            class_file.append(f'import time.util.{field};')
        class_file.append('')
    class_file.append(
        '%s extends %s {' % (
            new_class(class_name),
            new_signal_type(class_name, signal_type, signal['input'] if 'input' in signal else [])
        ))
    for field in fields:
        class_file.append(new_field(fields[field], field))
    class_file.append('')
    class_file.extend(new_constructor(class_name, fields))
    # class_file.append('')
    # class_file.extend(signal_impl(class_name, fields))
    class_file.append('}')
    return '\n'.join(class_file)

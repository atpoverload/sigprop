NUMERIC_PRIMITIVES = [
    'boolean',
    'int',
    'long',
    'float',
    'double'
]


def lower_camel(name):
    tokens = name.split('_')
    return ''.join([w.title() if w != tokens[0] else w for w in tokens])


def upper_camel(name):
    return ''.join([word.title() for word in name.split('_')])


def as_java_type(clazz):
    return clazz if clazz in NUMERIC_PRIMITIVES else upper_camel(clazz)


class JavaEnum:
    def __init__(self, name, package, values=[]):
        self.name_ = upper_camel(name)
        self.values_ = [v.upper() for v in values]
        self.package_ = package
        self.full_name = f'{self.package_}.{self.name_}'


class JavaClass:
    def __init__(self, name, package, fields={}):
        self.name_ = upper_camel(name)
        self.fields_ = {
            lower_camel(name): as_java_type(fields[name]) for name in fields}
        self.package_ = package
        self.full_name = f'{self.package_}.{self.name_}'

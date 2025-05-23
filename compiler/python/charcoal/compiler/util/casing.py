def lower_camel(name):
    return ''.join([word.title() if not name.startswith(word) else word for word in name.split('_')])


def upper_camel(name):
    return ''.join([word.title() for word in name.split('_')])

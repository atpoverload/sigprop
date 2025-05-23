import os

from java.java_class import JavaClass, JavaEnum, upper_camel

TIME_PRIMITIVES = [
    'Instant',
    'Duration',
]


CHARCOAL_PACKAGE = 'charcoal.compiler.generated'


def as_charcoal_package(package):
    return f'{CHARCOAL_PACKAGE}.{package}'


CHARCOAL_ENUM = '''
package %s;

public enum %s {
%s
}
'''


class CharcoalEnum(JavaEnum):
    def __init__(self, enum, package):
        super().__init__(
            enum['name'],
            as_charcoal_package(package),
            enum['values'])

    def values(self):
        values = []
        # TODO: support for fields in an enum
        for i, value in enumerate(self.values_):
            if i < len(self.values_) - 1:
                values.append(f'  {value.upper()},')
            else:
                values.append(f'  {value.upper()};')
        return os.linesep.join(values)

    def as_class_file(self):
        return CHARCOAL_ENUM % (self.package_, self.name_, self.values())


def new_field(field, field_type):
    return f'  public final {field_type} {field};'


def new_arg(field, field_type):
    return f'      {field_type} = {field}'


def new_assignment(field):
    return f'    this.{field} = {field};'


CHARCOAL_DATA = '''
package %s;
// imports
%s
public final class %s {
  // fields
%s

  // constructor
  %s(
%s) {
%s
  }
}
'''


class CharcoalData(JavaClass):
    def __init__(self, signal, package):
        super().__init__(
            signal['name'],
            as_charcoal_package(package),
            signal['data']['fields'])

    def imports(self):
        imports = ['']
        # check for time fields
        for field in self.fields_:
            if field in TIME_PRIMITIVES:
                imports.append(f'import java.time.{upper_camel(field)}')
        return '\n'.join(imports)

    def fields(self):
        fields = []
        for field, field_type in self.fields_.items():
            fields.append(new_field(field, field_type))
        return os.linesep.join(fields)

    def constructor_args(self):
        constructor_args = []
        field_count = len(self.fields_)
        for i, (field, field_type) in enumerate(self.fields_.items()):
            if i < field_count - 1:
                constructor_args.append(f'{new_arg(field, field_type)},')
            else:
                constructor_args.append(f'{new_arg(field, field_type)}')
        return os.linesep.join(constructor_args)

    def assignment(self):
        assignment = []
        for field in self.fields_:
            assignment.append(new_assignment(field))
        return os.linesep.join(assignment)

    def new_instance(self, values):
        instance = []
        instance.append(f'new {self.name_}(')
        for i, value in enumerate(values):
            if i < len(values) - 1:
                instance.append(f'      {values[value]},')
            else:
                instance.append(f'      {values[value]})')
        return os.linesep.join(instance)

    def as_class_file(self):
        return CHARCOAL_DATA % (
            self.package_,
            self.imports(),
            self.name_,
            self.fields(),
            self.name_,
            self.constructor_args(),
            self.assignment()
        )


CHARCOAL_SIGNAL = '''
package %s;

// imports
%s
import java.util.concurrent.ScheduledExecutorService;

public final class %s extends %s {
  // fields
%s

  // constructor
  %s(
%s
      ScheduledExecutorService executor) {
    super(executor);
%s
  }
  
  // computation
  @Override
  public %s compute(%s) {
    %s
    return %s;
  }
}
'''


class CharcoalSignal(JavaClass):
    def __init__(self, signal, package):
        super().__init__(
            f'{signal['name']}_signal',
            as_charcoal_package(package),
            signal['signal']['fields'])
        # signal value schema
        self.data = CharcoalData(signal, package)

        # signal class
        self.signal_type = f'{upper_camel(signal['type'])}Signal'

        # propagation method
        if 'input' in signal['compute']:
            self.input = signal['compute']['input']
        else:
            self.input = ''
        self.output = signal['compute']['output']

    def package(self):
        return f'package {self.package_};'

    def imports(self):
        imports = []
        # import the base signal type
        imports.append(f'import charcoal.prop.{self.signal_type};')
        # check for time fields
        for field in self.fields_:
            if field in TIME_PRIMITIVES:
                imports.append(f'import java.time.{field}')
        return os.linesep.join(imports)

    def extension(self):
        return f'{self.signal_type}<{self.data.name_}>'

    def fields(self):
        fields = []
        for field, field_type in self.fields_.items():
            fields.append(new_field(field, field_type))
        return os.linesep.join(fields)

    def constructor_args(self):
        constructor_args = []
        field_count = len(self.fields_)
        for i, (field, field_type) in enumerate(self.fields_.items()):
            if i < field_count - 1:
                constructor_args.append(f'{new_arg(field, field_type)},')
            else:
                constructor_args.append(new_arg(field, field_type))
        return os.linesep.join(constructor_args)

    def assignment(self):
        assignment = []
        for field in self.fields_:
            assignment.append(new_assignment(field))
        return os.linesep.join(assignment)

    def compute(self):
        return ''

    def as_class_file(self):
        return CHARCOAL_SIGNAL % (
            self.package_,
            self.imports(),
            self.name_,
            self.extension(),
            self.fields(),
            self.name_,
            self.constructor_args(),
            self.assignment(),
            self.data.name_,
            self.input,
            self.compute(),
            self.data.new_instance(self.output)
        )

    def new_instance(self, values):
        instance = []
        instance.append(f'new {self.name_}(')
        for i, value in enumerate(values):
            if i < len(values) - 1:
                instance.append(f'      {values[value]},')
            else:
                instance.append(f'      {values[value]})')
        return os.linesep.join(instance)


CHARCOAL_PROGRAM = '''
package %s;

// imports
%s
import java.util.concurrent.ScheduledExecutorService;

public final class %s {
  private %s() { }

  public static void main(String[] args) {
%s
  }
}
'''


class CharcoalProgram(JavaClass):
    def __init__(self, program, package):
        super().__init__(
            f'{upper_camel(package)}SignalProgram',
            as_charcoal_package(package))
        self.program_ = program

    def imports(self):
        return ''

    def program(self):
        return ''

    def as_class_file(self):
        return CHARCOAL_PROGRAM % (
            self.package_,
            self.imports(),
            self.name_,
            self.name_,
            self.program()
        )

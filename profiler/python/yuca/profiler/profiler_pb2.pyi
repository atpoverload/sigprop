from yuca.profiler.linux import linux_pb2 as _linux_pb2
from google.protobuf.internal import containers as _containers
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from collections.abc import Iterable as _Iterable, Mapping as _Mapping
from typing import ClassVar as _ClassVar, Optional as _Optional, Union as _Union

DESCRIPTOR: _descriptor.FileDescriptor

class Timestamp(_message.Message):
    __slots__ = ("secs", "nanos")
    SECS_FIELD_NUMBER: _ClassVar[int]
    NANOS_FIELD_NUMBER: _ClassVar[int]
    secs: int
    nanos: int
    def __init__(self, secs: _Optional[int] = ..., nanos: _Optional[int] = ...) -> None: ...

class Duration(_message.Message):
    __slots__ = ("secs", "nanos")
    SECS_FIELD_NUMBER: _ClassVar[int]
    NANOS_FIELD_NUMBER: _ClassVar[int]
    secs: int
    nanos: int
    def __init__(self, secs: _Optional[int] = ..., nanos: _Optional[int] = ...) -> None: ...

class YucaProfilingSession(_message.Message):
    __slots__ = ("period",)
    PERIOD_FIELD_NUMBER: _ClassVar[int]
    period: Duration
    def __init__(self, period: _Optional[_Union[Duration, _Mapping]] = ...) -> None: ...

class YucaProfile(_message.Message):
    __slots__ = ("session", "cpu_freq", "socket_power", "socket_emissions", "task_activity", "task_power", "task_emissions")
    class CpusFrequencies(_message.Message):
        __slots__ = ("timestamp", "frequency")
        TIMESTAMP_FIELD_NUMBER: _ClassVar[int]
        FREQUENCY_FIELD_NUMBER: _ClassVar[int]
        timestamp: Timestamp
        frequency: _containers.RepeatedCompositeFieldContainer[_linux_pb2.CpuFrequency]
        def __init__(self, timestamp: _Optional[_Union[Timestamp, _Mapping]] = ..., frequency: _Optional[_Iterable[_Union[_linux_pb2.CpuFrequency, _Mapping]]] = ...) -> None: ...
    class ThermalZonesTemperatures(_message.Message):
        __slots__ = ("timestamp", "temperature")
        TIMESTAMP_FIELD_NUMBER: _ClassVar[int]
        TEMPERATURE_FIELD_NUMBER: _ClassVar[int]
        timestamp: Timestamp
        temperature: _containers.RepeatedCompositeFieldContainer[_linux_pb2.ThermalZoneTemperature]
        def __init__(self, timestamp: _Optional[_Union[Timestamp, _Mapping]] = ..., temperature: _Optional[_Iterable[_Union[_linux_pb2.ThermalZoneTemperature, _Mapping]]] = ...) -> None: ...
    class SocketsPowers(_message.Message):
        __slots__ = ("timestamp", "power")
        TIMESTAMP_FIELD_NUMBER: _ClassVar[int]
        POWER_FIELD_NUMBER: _ClassVar[int]
        timestamp: Timestamp
        power: _containers.RepeatedCompositeFieldContainer[_linux_pb2.SocketPower]
        def __init__(self, timestamp: _Optional[_Union[Timestamp, _Mapping]] = ..., power: _Optional[_Iterable[_Union[_linux_pb2.SocketPower, _Mapping]]] = ...) -> None: ...
    class SocketsEmissionsRates(_message.Message):
        __slots__ = ("timestamp", "emissions")
        TIMESTAMP_FIELD_NUMBER: _ClassVar[int]
        EMISSIONS_FIELD_NUMBER: _ClassVar[int]
        timestamp: Timestamp
        emissions: _containers.RepeatedCompositeFieldContainer[_linux_pb2.SocketEmissionsRate]
        def __init__(self, timestamp: _Optional[_Union[Timestamp, _Mapping]] = ..., emissions: _Optional[_Iterable[_Union[_linux_pb2.SocketEmissionsRate, _Mapping]]] = ...) -> None: ...
    class TasksActivities(_message.Message):
        __slots__ = ("timestamp", "activity")
        TIMESTAMP_FIELD_NUMBER: _ClassVar[int]
        ACTIVITY_FIELD_NUMBER: _ClassVar[int]
        timestamp: Timestamp
        activity: _containers.RepeatedCompositeFieldContainer[_linux_pb2.TaskActivityRate]
        def __init__(self, timestamp: _Optional[_Union[Timestamp, _Mapping]] = ..., activity: _Optional[_Iterable[_Union[_linux_pb2.TaskActivityRate, _Mapping]]] = ...) -> None: ...
    class TasksPowers(_message.Message):
        __slots__ = ("timestamp", "power")
        TIMESTAMP_FIELD_NUMBER: _ClassVar[int]
        POWER_FIELD_NUMBER: _ClassVar[int]
        timestamp: Timestamp
        power: _containers.RepeatedCompositeFieldContainer[_linux_pb2.TaskPower]
        def __init__(self, timestamp: _Optional[_Union[Timestamp, _Mapping]] = ..., power: _Optional[_Iterable[_Union[_linux_pb2.TaskPower, _Mapping]]] = ...) -> None: ...
    class TasksEmissionsRates(_message.Message):
        __slots__ = ("timestamp", "emissions")
        TIMESTAMP_FIELD_NUMBER: _ClassVar[int]
        EMISSIONS_FIELD_NUMBER: _ClassVar[int]
        timestamp: Timestamp
        emissions: _containers.RepeatedCompositeFieldContainer[_linux_pb2.TaskEmissionsRate]
        def __init__(self, timestamp: _Optional[_Union[Timestamp, _Mapping]] = ..., emissions: _Optional[_Iterable[_Union[_linux_pb2.TaskEmissionsRate, _Mapping]]] = ...) -> None: ...
    SESSION_FIELD_NUMBER: _ClassVar[int]
    CPU_FREQ_FIELD_NUMBER: _ClassVar[int]
    SOCKET_POWER_FIELD_NUMBER: _ClassVar[int]
    SOCKET_EMISSIONS_FIELD_NUMBER: _ClassVar[int]
    TASK_ACTIVITY_FIELD_NUMBER: _ClassVar[int]
    TASK_POWER_FIELD_NUMBER: _ClassVar[int]
    TASK_EMISSIONS_FIELD_NUMBER: _ClassVar[int]
    session: YucaProfilingSession
    cpu_freq: _containers.RepeatedCompositeFieldContainer[YucaProfile.CpusFrequencies]
    socket_power: _containers.RepeatedCompositeFieldContainer[YucaProfile.SocketsPowers]
    socket_emissions: _containers.RepeatedCompositeFieldContainer[YucaProfile.SocketsEmissionsRates]
    task_activity: _containers.RepeatedCompositeFieldContainer[YucaProfile.TasksActivities]
    task_power: _containers.RepeatedCompositeFieldContainer[YucaProfile.TasksPowers]
    task_emissions: _containers.RepeatedCompositeFieldContainer[YucaProfile.TasksEmissionsRates]
    def __init__(self, session: _Optional[_Union[YucaProfilingSession, _Mapping]] = ..., cpu_freq: _Optional[_Iterable[_Union[YucaProfile.CpusFrequencies, _Mapping]]] = ..., socket_power: _Optional[_Iterable[_Union[YucaProfile.SocketsPowers, _Mapping]]] = ..., socket_emissions: _Optional[_Iterable[_Union[YucaProfile.SocketsEmissionsRates, _Mapping]]] = ..., task_activity: _Optional[_Iterable[_Union[YucaProfile.TasksActivities, _Mapping]]] = ..., task_power: _Optional[_Iterable[_Union[YucaProfile.TasksPowers, _Mapping]]] = ..., task_emissions: _Optional[_Iterable[_Union[YucaProfile.TasksEmissionsRates, _Mapping]]] = ...) -> None: ...

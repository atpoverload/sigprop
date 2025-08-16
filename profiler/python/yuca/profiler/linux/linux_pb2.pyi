from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from collections.abc import Mapping as _Mapping
from typing import ClassVar as _ClassVar, Optional as _Optional, Union as _Union

DESCRIPTOR: _descriptor.FileDescriptor

class SocketPower(_message.Message):
    __slots__ = ("socket", "power", "package", "dram")
    SOCKET_FIELD_NUMBER: _ClassVar[int]
    POWER_FIELD_NUMBER: _ClassVar[int]
    PACKAGE_FIELD_NUMBER: _ClassVar[int]
    DRAM_FIELD_NUMBER: _ClassVar[int]
    socket: int
    power: float
    package: float
    dram: float
    def __init__(self, socket: _Optional[int] = ..., power: _Optional[float] = ..., package: _Optional[float] = ..., dram: _Optional[float] = ...) -> None: ...

class SocketEmissionsRate(_message.Message):
    __slots__ = ("socket", "emissions")
    SOCKET_FIELD_NUMBER: _ClassVar[int]
    EMISSIONS_FIELD_NUMBER: _ClassVar[int]
    socket: int
    emissions: float
    def __init__(self, socket: _Optional[int] = ..., emissions: _Optional[float] = ...) -> None: ...

class Task(_message.Message):
    __slots__ = ("process_id", "task_id", "name")
    PROCESS_ID_FIELD_NUMBER: _ClassVar[int]
    TASK_ID_FIELD_NUMBER: _ClassVar[int]
    NAME_FIELD_NUMBER: _ClassVar[int]
    process_id: int
    task_id: int
    name: str
    def __init__(self, process_id: _Optional[int] = ..., task_id: _Optional[int] = ..., name: _Optional[str] = ...) -> None: ...

class TaskActivityRate(_message.Message):
    __slots__ = ("task", "cpu", "activity")
    TASK_FIELD_NUMBER: _ClassVar[int]
    CPU_FIELD_NUMBER: _ClassVar[int]
    ACTIVITY_FIELD_NUMBER: _ClassVar[int]
    task: Task
    cpu: int
    activity: float
    def __init__(self, task: _Optional[_Union[Task, _Mapping]] = ..., cpu: _Optional[int] = ..., activity: _Optional[float] = ...) -> None: ...

class TaskPower(_message.Message):
    __slots__ = ("task", "cpu", "power")
    TASK_FIELD_NUMBER: _ClassVar[int]
    CPU_FIELD_NUMBER: _ClassVar[int]
    POWER_FIELD_NUMBER: _ClassVar[int]
    task: Task
    cpu: int
    power: float
    def __init__(self, task: _Optional[_Union[Task, _Mapping]] = ..., cpu: _Optional[int] = ..., power: _Optional[float] = ...) -> None: ...

class TaskEmissionsRate(_message.Message):
    __slots__ = ("task", "cpu", "emissions")
    TASK_FIELD_NUMBER: _ClassVar[int]
    CPU_FIELD_NUMBER: _ClassVar[int]
    EMISSIONS_FIELD_NUMBER: _ClassVar[int]
    task: Task
    cpu: int
    emissions: float
    def __init__(self, task: _Optional[_Union[Task, _Mapping]] = ..., cpu: _Optional[int] = ..., emissions: _Optional[float] = ...) -> None: ...

class CpuFrequency(_message.Message):
    __slots__ = ("cpu", "governor", "frequency", "set_frequency")
    CPU_FIELD_NUMBER: _ClassVar[int]
    GOVERNOR_FIELD_NUMBER: _ClassVar[int]
    FREQUENCY_FIELD_NUMBER: _ClassVar[int]
    SET_FREQUENCY_FIELD_NUMBER: _ClassVar[int]
    cpu: int
    governor: str
    frequency: int
    set_frequency: int
    def __init__(self, cpu: _Optional[int] = ..., governor: _Optional[str] = ..., frequency: _Optional[int] = ..., set_frequency: _Optional[int] = ...) -> None: ...

class ThermalZoneTemperature(_message.Message):
    __slots__ = ("zone_id", "zone_type", "temperature")
    ZONE_ID_FIELD_NUMBER: _ClassVar[int]
    ZONE_TYPE_FIELD_NUMBER: _ClassVar[int]
    TEMPERATURE_FIELD_NUMBER: _ClassVar[int]
    zone_id: int
    zone_type: str
    temperature: int
    def __init__(self, zone_id: _Optional[int] = ..., zone_type: _Optional[str] = ..., temperature: _Optional[int] = ...) -> None: ...

class AgingRate(_message.Message):
    __slots__ = ("socket", "aging")
    SOCKET_FIELD_NUMBER: _ClassVar[int]
    AGING_FIELD_NUMBER: _ClassVar[int]
    socket: int
    aging: float
    def __init__(self, socket: _Optional[int] = ..., aging: _Optional[float] = ...) -> None: ...

class AmortizedEmissionsRate(_message.Message):
    __slots__ = ("socket", "emissions")
    SOCKET_FIELD_NUMBER: _ClassVar[int]
    EMISSIONS_FIELD_NUMBER: _ClassVar[int]
    socket: int
    emissions: float
    def __init__(self, socket: _Optional[int] = ..., emissions: _Optional[float] = ...) -> None: ...

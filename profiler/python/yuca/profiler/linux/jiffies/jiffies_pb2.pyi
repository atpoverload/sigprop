from yuca.profiler.linux import linux_pb2 as _linux_pb2
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from collections.abc import Mapping as _Mapping
from typing import ClassVar as _ClassVar, Optional as _Optional, Union as _Union

DESCRIPTOR: _descriptor.FileDescriptor

class TaskJiffies(_message.Message):
    __slots__ = ("task", "cpu", "jiffies", "user_jiffies", "system_jiffies")
    TASK_FIELD_NUMBER: _ClassVar[int]
    CPU_FIELD_NUMBER: _ClassVar[int]
    JIFFIES_FIELD_NUMBER: _ClassVar[int]
    USER_JIFFIES_FIELD_NUMBER: _ClassVar[int]
    SYSTEM_JIFFIES_FIELD_NUMBER: _ClassVar[int]
    task: _linux_pb2.Task
    cpu: int
    jiffies: int
    user_jiffies: int
    system_jiffies: int
    def __init__(self, task: _Optional[_Union[_linux_pb2.Task, _Mapping]] = ..., cpu: _Optional[int] = ..., jiffies: _Optional[int] = ..., user_jiffies: _Optional[int] = ..., system_jiffies: _Optional[int] = ...) -> None: ...

class TaskJiffiesRate(_message.Message):
    __slots__ = ("task", "cpu", "rate")
    TASK_FIELD_NUMBER: _ClassVar[int]
    CPU_FIELD_NUMBER: _ClassVar[int]
    RATE_FIELD_NUMBER: _ClassVar[int]
    task: _linux_pb2.Task
    cpu: int
    rate: float
    def __init__(self, task: _Optional[_Union[_linux_pb2.Task, _Mapping]] = ..., cpu: _Optional[int] = ..., rate: _Optional[float] = ...) -> None: ...

class CpuJiffies(_message.Message):
    __slots__ = ("cpu", "jiffies", "user", "nice", "system", "idle", "iowait", "irq", "softirq", "steal", "guest", "guest_nice")
    CPU_FIELD_NUMBER: _ClassVar[int]
    JIFFIES_FIELD_NUMBER: _ClassVar[int]
    USER_FIELD_NUMBER: _ClassVar[int]
    NICE_FIELD_NUMBER: _ClassVar[int]
    SYSTEM_FIELD_NUMBER: _ClassVar[int]
    IDLE_FIELD_NUMBER: _ClassVar[int]
    IOWAIT_FIELD_NUMBER: _ClassVar[int]
    IRQ_FIELD_NUMBER: _ClassVar[int]
    SOFTIRQ_FIELD_NUMBER: _ClassVar[int]
    STEAL_FIELD_NUMBER: _ClassVar[int]
    GUEST_FIELD_NUMBER: _ClassVar[int]
    GUEST_NICE_FIELD_NUMBER: _ClassVar[int]
    cpu: int
    jiffies: int
    user: int
    nice: int
    system: int
    idle: int
    iowait: int
    irq: int
    softirq: int
    steal: int
    guest: int
    guest_nice: int
    def __init__(self, cpu: _Optional[int] = ..., jiffies: _Optional[int] = ..., user: _Optional[int] = ..., nice: _Optional[int] = ..., system: _Optional[int] = ..., idle: _Optional[int] = ..., iowait: _Optional[int] = ..., irq: _Optional[int] = ..., softirq: _Optional[int] = ..., steal: _Optional[int] = ..., guest: _Optional[int] = ..., guest_nice: _Optional[int] = ...) -> None: ...

class CpuJiffiesRate(_message.Message):
    __slots__ = ("cpu", "rate")
    CPU_FIELD_NUMBER: _ClassVar[int]
    RATE_FIELD_NUMBER: _ClassVar[int]
    cpu: int
    rate: float
    def __init__(self, cpu: _Optional[int] = ..., rate: _Optional[float] = ...) -> None: ...

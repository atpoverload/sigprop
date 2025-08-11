from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from typing import ClassVar as _ClassVar, Optional as _Optional

DESCRIPTOR: _descriptor.FileDescriptor

class PowercapEnergy(_message.Message):
    __slots__ = ("socket", "energy", "pkg", "dram", "core", "gpu")
    SOCKET_FIELD_NUMBER: _ClassVar[int]
    ENERGY_FIELD_NUMBER: _ClassVar[int]
    PKG_FIELD_NUMBER: _ClassVar[int]
    DRAM_FIELD_NUMBER: _ClassVar[int]
    CORE_FIELD_NUMBER: _ClassVar[int]
    GPU_FIELD_NUMBER: _ClassVar[int]
    socket: int
    energy: float
    pkg: float
    dram: float
    core: float
    gpu: float
    def __init__(self, socket: _Optional[int] = ..., energy: _Optional[float] = ..., pkg: _Optional[float] = ..., dram: _Optional[float] = ..., core: _Optional[float] = ..., gpu: _Optional[float] = ...) -> None: ...

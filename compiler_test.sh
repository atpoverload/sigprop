python src/python/sigprop/compiler.py '
particle(n: long, name: particle): generating
  count: long
  name: particle
  emission_time: instant
  generate(): {count: n, particle: particle, emission_time: now()}

> particle($EMISSION_RATE)
>> log $LOG_PATH
'
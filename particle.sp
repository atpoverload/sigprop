enum species:
  hydrogen
  helium
  lithium
  berylium

particle(n: long): generating
  count: long
  species: species
  emission_time: instant
  generate() {count: n, species: hydrogen, emission_time: now()}

>particle($EMISSION_RATE)
>>log $LOG_PATH

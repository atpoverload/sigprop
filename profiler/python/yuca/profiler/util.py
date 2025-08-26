def timestamp_diff(start, end):
    return (end.secs + end.nanos / 10**9) - (start.secs + start.nanos / 10**9)


def runtime(profile):
    start = None
    end = None
    for emissions in profile.socket_emissions:
        ts = emissions.timestamp
        ts = ts.secs + ts.nanos / 10**9
        if start is None or ts < start:
            start = ts
        if end is None or end >= end:
            end = ts

    return end - start


def socket_emissions(profile):
    carbon = 0
    last_emissions = None
    for emissions in profile.socket_emissions:
        if last_emissions is not None:
            elapsed = timestamp_diff(
                last_emissions.timestamp, emissions.timestamp)
            rate = sum(map(lambda e: e.emissions, last_emissions.emissions))
            carbon += rate * elapsed
        last_emissions = emissions

    return carbon


def task_emissions(profile):
    carbon = 0
    last_emissions = None
    for emissions in profile.task_emissions:
        if last_emissions is not None:
            elapsed = timestamp_diff(
                last_emissions.timestamp, emissions.timestamp)
            rate = sum(map(lambda e: e.emissions, last_emissions.emissions))
            carbon += rate * elapsed
        last_emissions = emissions

    return carbon


def amortized_emissions(profile):
    carbon = 0
    last_emissions = None
    for emissions in profile.amortized_emissions:
        if last_emissions is not None:
            elapsed = timestamp_diff(
                last_emissions.timestamp, emissions.timestamp)
            rate = sum(map(lambda e: e.emissions, last_emissions.emissions))
            carbon += rate * elapsed
        last_emissions = emissions

    return carbon

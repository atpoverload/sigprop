package yuca.benchmarks;

import static charcoal.util.LoggerUtil.getLogger;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import yuca.profiler.linux.SocketPower;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import yuca.profiler.linux.thermal.SysThermal;
    
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class ThermalZoneCooldown {
      private static final Logger logger = getLogger();
      private static final String X86_ZONE = "x86_pkg_temp";
    
      private static class CooldownArgs {
        private final int periodMillis;
        private final int targetTemperature;
    
        private CooldownArgs(int periodMillis, int targetTemperature) {
          this.periodMillis = periodMillis;
          this.targetTemperature = targetTemperature;
        }
      }
    
      private static final Integer DEFAULT_PERIOD_MILLIS = 1000;

      private static CooldownArgs getCooldownArgs(String[] args) throws Exception {
        Option periodOption =
            Option.builder("p")
                .hasArg(true)
                .longOpt("period")
                .desc("period in milliseconds to sample at")
                .type(Integer.class)
                .build();
        Option temperatureOption =
            Option.builder("t")
                .hasArg(true)
                .longOpt("temperature")
                .desc("the target temperature in celsius")
                .type(Integer.class)
                .build();
        Options options = new Options().addOption(periodOption).addOption(temperatureOption);
        CommandLine cmd = new DefaultParser().parse(options, args);
        return new CooldownArgs(
            cmd.getParsedOptionValue(periodOption, DEFAULT_PERIOD_MILLIS).intValue(),
            cmd.getParsedOptionValue(temperatureOption).intValue());
      }

      private static int[] getThermalZonesByType(String type) {
        ArrayList<Integer> zones = new ArrayList<>();
        for(int zone: SysThermal.ZONE_COUNT){
          if(SysThermal.getZoneType(zone).equals(type)) {zones.add(zone);}
        }
        return zones.stream().mapToInt(Integer::intValue).toArray();
      }

      private static void cooldown(int periodMillis, int targetTemperature, int[] zones) throws Exception {
        int k = 10;
        int p = Double.valueOf(0.80 * k).intValue();

        Instant start = Instant.now();
        int[][] samples = new int[zones.length][k];
        while (true) {
          for (int zone: zones) {
            samples.get(zone).add(SysThermal.getTemperature(zone));
          }
          HashMap<Integer, Boolean> values = new HashMap<>();
          for (int zone: zones) {
            ArrayList<Integer> zoneSamples = samples.get(zone);
            values.put(zone, zoneSamples.stream().skip(Math.max(0, zoneSamples.size() - k)).filter(i -> targetTemperature >= i).count() >= p);
          }
          values.values().stream().filter(Boolean::booleanValue).count()
          String message = 
          String.format("%d of the thermal zones met the target of %d C (%s)", cooled, targetTemperature, values);
          System.out.print(message);
          System.out.print("\b".repeat(message.length()));
          if (found >= p) {
            break;
          }
          Thread.sleep(periodMillis);
        }
        Instant end = Instant.now();
        logger.info(String.format("cooled down to %s C in %s", targetTemperature, Duration.between(start, end)));
      }

      private ThermalZoneCooldown() {}

      public static void main(String[] args)throws Exception  {
        CooldownArgs args = getCooldownArgs(args);
        int[] zones = getThermalZonesByType(X86_ZONE);
        logger.info(String.format("found %d %s zones: %s", zones.length, X86_ZONE, Arrays.toString(zones)));
        cooldown(args.periodMillis, args.targetTemperature, zones);
    }
}
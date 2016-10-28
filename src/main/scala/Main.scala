import java.nio.file.Paths

import com.pacb.itg.metrics.sts.Sts
import falkner.jayson.metrics.io.{CSV, JSON}

/**
  * Exports PipeStats (aka *.sts.xml) files as CSV or JSON
  *
  * Usage: sbt "run m54088_160923_213709.sts.xml m54088_160923_213709.csv"
  */
object Main extends App {
  if (args.last.endsWith("json"))
    JSON.write(Paths.get(args.last), Sts(Paths.get(args.head)))
  else if (args.last.endsWith("csv"))
    CSV(Paths.get(args.last), Sts(Paths.get(args.head)))
}

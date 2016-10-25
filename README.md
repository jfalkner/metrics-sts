# PipeStats (aka *.sts.xml) Metrics

Exports all the commonly used PipeStats metrics from the PacBio
supported pipeline. It can be a slow and tedious to sort out where 
metrics exist and how they were calculated. This project aims to solve
that with docs, tests and exports to JSON and CSV. Use the data!

Key goals of this project.

- Export data from PipeStats (*.sts.xml) files to convenient to use CSV or JSON
- Support all known versions. Don't assume the latest.
- Scala API for quick access to typed data or raw values from the input files
- Documentation
  - Keep a version history for sts.xml schema changes
  - Explain what each metric means in [easy-to-use documentation](#metrics)

See the overall PacBio Metrics project if you want to aggregate metrics
from more than just PipeStats files.

## Usage

Export metrics by passing one or more files and directories that contain sts.xml files. First build a JAR.

```
# Option 1: Build a JAR for standalone use (example below)
sbt run
```

Exporting aligned movies is the most common use case. See [ITG-190](https://jira.pacificbiosciences.com/browse/ITG-190) for an example.

```
# Export all the data to a CSV named ITG-190.csv
sbt "run m54088_160923_213709.sts.xml m54088_160923_213709.csv"

# Another option is to export to a JSON
sbt "run m54088_160923_213709.sts.xml m54088_160923_213709.json"
```

## Metrics

PipeStats is a dumping group of metrics and stats. You'll find that it has
a lot of numbers and distributions that may not be what you expect. Here
is a list of key points to consider before reading each metric.

- The instrument makes PipeStats calculations based on *all* ZMWs. It does
  this while subreads are being called and before any alignment.
- Many ZMWs will not generate reads or make unusable data (e.g. two polymerases in the same well)
- You probably want to remove the 0 values from the distributions. If you don't,
  the mean and medians are not useful. This code will also do this and prefix metrics with "NZ" (No Zeros). e.g. NZ Read Length
- A common strategy for refining PipeStats metrics is to sequence a 
  reference, do an alignment to the known genome, then re-generate similar
  stats using only the ZMWs that provided alignable reads.
- It isn't always smart to ignore the 0's. If you are trying to improve
  loading, then you need to consider what % ZMWs made usable data.
  
Here is a full list of metrics exported from the latest PipeStats file (version 3.0.1).

TODO: clean this up. Mostly a copy-paste of the code right now without descriptions.
 
- Str("Path", p.toAbsolutePath.toString),
- Str("Movie Name", () => (xml \ "MovieName").text),
- Num("Movie Length", () => (xml \ "MovieLength").text),
- Num("Sequencing ZMWs", (xml \ "NumSequencingZmws").text),
- Num("Total Base Fraction: A", () => totalBaseFractionPerChannel(xml, "A")),
- Num("Total Base Fraction: C", () => totalBaseFractionPerChannel(xml, "C")),
- Num("Total Base Fraction: G", () => totalBaseFractionPerChannel(xml, "G")),
- Num("Total Base Fraction: T", () => totalBaseFractionPerChannel(xml, "T")),
- StsDist("Baseline level: A", (root) => (root \ "BaselineLevelDist").filter(n => (n \ "@Channel").text == "A").head),
- StsDist("Baseline level: C", (root) => (root \ "BaselineLevelDist").filter(n => (n \ "@Channel").text == "C").head),
- StsDist("Baseline level: G", (root) => (root \ "BaselineLevelDist").filter(n => (n \ "@Channel").text == "G").head),
- StsDist("Baseline level: T", (root) => (root \ "BaselineLevelDist").filter(n => (n \ "@Channel").text == "T").head),
- StsDist("Baseline StdDev: A", (root) => (root \ "BaselineStdDist").filter(n => (n \ "@Channel").text == "A").head),
- StsDist("Baseline StdDev: C", (root) => (root \ "BaselineStdDist").filter(n => (n \ "@Channel").text == "C").head),
- StsDist("Baseline StdDev: G", (root) => (root \ "BaselineStdDist").filter(n => (n \ "@Channel").text == "G").head),
- StsDist("Baseline StdDev: T", (root) => (root \ "BaselineStdDist").filter(n => (n \ "@Channel").text == "T").head),
- StsDist("SNR: A", (root) => (root \ "SnrDist").filter(n => (n \ "@Channel").text == "A").head),
- StsDist("SNR: C", (root) => (root \ "SnrDist").filter(n => (n \ "@Channel").text == "C").head),
- StsDist("SNR: G", (root) => (root \ "SnrDist").filter(n => (n \ "@Channel").text == "G").head),
- StsDist("SNR: T", (root) => (root \ "SnrDist").filter(n => (n \ "@Channel").text == "T").head),
- StsDist("HQ Region SNR: A", (root) => (root \ "HqRegionSnrDist").filter(n => (n \ "@Channel").text == "A").head),
- StsDist("HQ Region SNR: C", (root) => (root \ "HqRegionSnrDist").filter(n => (n \ "@Channel").text == "C").head),
- StsDist("HQ Region SNR: G", (root) => (root \ "HqRegionSnrDist").filter(n => (n \ "@Channel").text == "G").head),
- StsDist("HQ Region SNR: T", (root) => (root \ "HqRegionSnrDist").filter(n => (n \ "@Channel").text == "T").head),
- StsDist("Pk Mid: A", (root) => (root \ "HqBasPkMidDist").filter(n => (n \ "@Channel").text == "A").head),
- StsDist("Pk Mid: C", (root) => (root \ "HqBasPkMidDist").filter(n => (n \ "@Channel").text == "C").head),
- StsDist("Pk Mid: G", (root) => (root \ "HqBasPkMidDist").filter(n => (n \ "@Channel").text == "G").head),
- StsDist("Pk Mid: T", (root) => (root \ "HqBasPkMidDist").filter(n => (n \ "@Channel").text == "T").head),
- StsDist("Pausiness", (root) => (root \ "PausinessDist").head),
- StsDist("Control Read Len", (root) => (root \ "ControlReadLenDist").head),
- StsDist("Control Read Len Qual", (root) => (root \ "ControlReadLenQual").head),
- CatDist("Productivity",
  - ("Empty" -> (xml \ "ProdDist" \ "BinCounts" \ "BinCount") (0).text.toInt),
  - ("Productive" -> (xml \ "ProdDist" \ "BinCounts" \ "BinCount") (0).text.toInt),
  - ("Other" -> (xml \ "ProdDist" \ "BinCounts" \ "BinCount") (2).text.toInt),
  - ("Undefined" -> (xml \ "ProdDist" \ "BinCounts" \ "BinCount") (3).text.toInt)
- CatDist("Read Type",
  - ("Empty" -> (xml \ "ReadTypeDist" \ "BinCounts" \ "BinCount")(0).text.toInt),
  - ("FullHqRead0" -> (xml \ "ReadTypeDist" \ "BinCounts" \ "BinCount")(1).text.toInt),
  - ("FullHqRead1" -> (xml \ "ReadTypeDist" \ "BinCounts" \ "BinCount")(2).text.toInt),
  - ("PartialHqRead0" -> (xml \ "ReadTypeDist" \ "BinCounts" \ "BinCount")(3).text.toInt),
  - ("PartialHqRead1" -> (xml \ "ReadTypeDist" \ "BinCounts" \ "BinCount")(4).text.toInt),
  - ("PartialHqRead2" -> (xml \ "ReadTypeDist" \ "BinCounts" \ "BinCount")(5).text.toInt),
  - ("Indeterminate" -> (xml \ "ReadTypeDist" \ "BinCounts" \ "BinCount")(6).text.toInt)
- Str("PkMidCV: A", pkMidCVPerChannel(xml, "A")),
- Str("PkMidCV: C", pkMidCVPerChannel(xml, "C")),
- Str("PkMidCV: G", pkMidCVPerChannel(xml, "G")),
- Str("PkMidCV: T", pkMidCVPerChannel(xml, "T")),
- StsDist("Pulse Rate", (root) => (root \ "PulseRateDist").head),
- StsDist("Pulse Width", (root) => (root \ "PulseWidthDist").head),
- StsDist("Base Rate", (root) => (root \ "BaseRateDist").head),
- StsDist("Base Width", (root) => (root \ "BaseWidthDist").head),
- StsDist("Unfiltered Basecalls", (root) => (root \ "NumUnfilteredBasecallsDist").head),
- StsDist("Read Length", (root) => (root \ "ReadLenDist").head),
- StsDist("Read Qual", (root) => (root \ "ReadQualDist").head),
- StsDist("Insert Read Length", (root) => (root \ "InsertReadLenDist").head),
- StsDist("Insert Read Qual", (root) => (root \ "InsertReadQualDist").head),
- StsDist("Median Insert", (root) => (root \ "MedianInsertDist").head),
- StsDist("HQ Base Fraction", (root) => (root \ "HqBaseFractionDist").head),
- StsDist("Baseline Level Sequencing: AC", (root) => baselineLevelSequencing(xml, "AC")),
- StsDist("Baseline Level Sequencing: GT", (root) => baselineLevelSequencing(xml, "GT"))
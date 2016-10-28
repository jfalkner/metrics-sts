package com.pacb.itg.metrics.sts

import java.nio.file.Path

import falkner.jayson.metrics._

import scala.xml.{Elem, Node, XML}

object Sts_v3_0_1 {
  val version = "3.0.1"

  def apply(p: Path, parsedXml: Elem): Sts_v3_0_1 = apply(p, Some(parsedXml))

  def apply(p: Path, parsedXml: Option[Elem] = None): Sts_v3_0_1 = {
    parsedXml match {
      case Some(xml) => new Sts_v3_0_1(p, xml)
      case None => new Sts_v3_0_1(p, XML.loadFile(p.toFile))
    }
  }
}

class Sts_v3_0_1 (val p: Path, val xml: Node) extends Metrics {
  override val namespace = "STS"
  override val version = s"${Sts.version}~${Sts_v3_0_1.version}"
  override val values: List[Metric] = List(
    Str("Path", p.toAbsolutePath.toString),
    Str("Movie Name", (xml \ "MovieName").text),
    Num("Movie Length", (xml \ "MovieLength").text),
    Num("Sequencing ZMWs", (xml \ "NumSequencingZmws").text),
    Num("Total Base Fraction: A", totalBaseFractionPerChannel(xml, "A")),
    Num("Total Base Fraction: C", totalBaseFractionPerChannel(xml, "C")),
    Num("Total Base Fraction: G", totalBaseFractionPerChannel(xml, "G")),
    Num("Total Base Fraction: T", totalBaseFractionPerChannel(xml, "T")),
    StsDist("Baseline level: A", (root) => (root \ "BaselineLevelDist").filter(n => (n \ "@Channel").text == "A").head),
    StsDist("Baseline level: C", (root) => (root \ "BaselineLevelDist").filter(n => (n \ "@Channel").text == "C").head),
    StsDist("Baseline level: G", (root) => (root \ "BaselineLevelDist").filter(n => (n \ "@Channel").text == "G").head),
    StsDist("Baseline level: T", (root) => (root \ "BaselineLevelDist").filter(n => (n \ "@Channel").text == "T").head),
    StsDist("Baseline StdDev: A", (root) => (root \ "BaselineStdDist").filter(n => (n \ "@Channel").text == "A").head),
    StsDist("Baseline StdDev: C", (root) => (root \ "BaselineStdDist").filter(n => (n \ "@Channel").text == "C").head),
    StsDist("Baseline StdDev: G", (root) => (root \ "BaselineStdDist").filter(n => (n \ "@Channel").text == "G").head),
    StsDist("Baseline StdDev: T", (root) => (root \ "BaselineStdDist").filter(n => (n \ "@Channel").text == "T").head),
    StsDist("SNR: A", (root) => (root \ "SnrDist").filter(n => (n \ "@Channel").text == "A").head),
    StsDist("SNR: C", (root) => (root \ "SnrDist").filter(n => (n \ "@Channel").text == "C").head),
    StsDist("SNR: G", (root) => (root \ "SnrDist").filter(n => (n \ "@Channel").text == "G").head),
    StsDist("SNR: T", (root) => (root \ "SnrDist").filter(n => (n \ "@Channel").text == "T").head),
    StsDist("HQ Region SNR: A", (root) => (root \ "HqRegionSnrDist").filter(n => (n \ "@Channel").text == "A").head),
    StsDist("HQ Region SNR: C", (root) => (root \ "HqRegionSnrDist").filter(n => (n \ "@Channel").text == "C").head),
    StsDist("HQ Region SNR: G", (root) => (root \ "HqRegionSnrDist").filter(n => (n \ "@Channel").text == "G").head),
    StsDist("HQ Region SNR: T", (root) => (root \ "HqRegionSnrDist").filter(n => (n \ "@Channel").text == "T").head),
    StsDist("Pk Mid: A", (root) => (root \ "HqBasPkMidDist").filter(n => (n \ "@Channel").text == "A").head),
    StsDist("Pk Mid: C", (root) => (root \ "HqBasPkMidDist").filter(n => (n \ "@Channel").text == "C").head),
    StsDist("Pk Mid: G", (root) => (root \ "HqBasPkMidDist").filter(n => (n \ "@Channel").text == "G").head),
    StsDist("Pk Mid: T", (root) => (root \ "HqBasPkMidDist").filter(n => (n \ "@Channel").text == "T").head),
    StsDist("Pausiness", (root) => (root \ "PausinessDist").head),
    StsDist("Control Read Len", (root) => (root \ "ControlReadLenDist").head),
    StsDist("Control Read Len Qual", (root) => (root \ "ControlReadLenQual").head),
    CatDist("Productivity",
      ((xml \ "ProdDist" \ "BinCounts" \ "BinCount") (0).text.toInt +
        (xml \ "ProdDist" \ "BinCounts" \ "BinCount") (0).text.toInt +
        (xml \ "ProdDist" \ "BinCounts" \ "BinCount") (2).text.toInt +
        (xml \ "ProdDist" \ "BinCounts" \ "BinCount") (3).text.toInt),
      Map(
        ("Empty" -> (xml \ "ProdDist" \ "BinCounts" \ "BinCount") (0).text.toInt),
        ("Productive" -> (xml \ "ProdDist" \ "BinCounts" \ "BinCount") (0).text.toInt),
        ("Other" -> (xml \ "ProdDist" \ "BinCounts" \ "BinCount") (2).text.toInt),
        ("Undefined" -> (xml \ "ProdDist" \ "BinCounts" \ "BinCount") (3).text.toInt)
      )),
    CatDist("Read Type",
      (xml \ "ReadTypeDist" \ "BinCounts" \ "BinCount").map(_.text.toInt).sum,
      Map[String, AnyVal](
        ("Empty" -> (xml \ "ReadTypeDist" \ "BinCounts" \ "BinCount")(0).text.toInt),
        ("FullHqRead0" -> (xml \ "ReadTypeDist" \ "BinCounts" \ "BinCount")(1).text.toInt),
        ("FullHqRead1" -> (xml \ "ReadTypeDist" \ "BinCounts" \ "BinCount")(2).text.toInt),
        ("PartialHqRead0" -> (xml \ "ReadTypeDist" \ "BinCounts" \ "BinCount")(3).text.toInt),
        ("PartialHqRead1" -> (xml \ "ReadTypeDist" \ "BinCounts" \ "BinCount")(4).text.toInt),
        ("PartialHqRead2" -> (xml \ "ReadTypeDist" \ "BinCounts" \ "BinCount")(5).text.toInt),
        ("Indeterminate" -> (xml \ "ReadTypeDist" \ "BinCounts" \ "BinCount")(6).text.toInt)
      )),
    Str("PkMidCV: A", pkMidCVPerChannel(xml, "A")),
    Str("PkMidCV: C", pkMidCVPerChannel(xml, "C")),
    Str("PkMidCV: G", pkMidCVPerChannel(xml, "G")),
    Str("PkMidCV: T", pkMidCVPerChannel(xml, "T")),
    StsDist("Pulse Rate", (root) => (root \ "PulseRateDist").head),
    StsDist("Pulse Width", (root) => (root \ "PulseWidthDist").head),
    StsDist("Base Rate", (root) => (root \ "BaseRateDist").head),
    StsDist("Base Width", (root) => (root \ "BaseWidthDist").head),
    StsDist("Unfiltered Basecalls", (root) => (root \ "NumUnfilteredBasecallsDist").head),
    StsDist("Read Length", (root) => (root \ "ReadLenDist").head),
    StsDist("Read Qual", (root) => (root \ "ReadQualDist").head),
    StsDist("Insert Read Length", (root) => (root \ "InsertReadLenDist").head),
    StsDist("Insert Read Qual", (root) => (root \ "InsertReadQualDist").head),
    StsDist("Median Insert", (root) => (root \ "MedianInsertDist").head),
    StsDist("HQ Base Fraction", (root) => (root \ "HqBaseFractionDist").head),
    StsDist("Baseline Level Sequencing: AC", (root) => baselineLevelSequencing(xml, "AC")),
    StsDist("Baseline Level Sequencing: GT", (root) => baselineLevelSequencing(xml, "GT"))
  )

  def totalBaseFractionPerChannel(xml: Node, n: String): String = (xml \ "TotalBaseFractionPerChannel").filter(n => (n \ "@Channel").text == n).text

  def pkMidCVPerChannel(xml: Node, n: String): String = (xml \ "PkMidCVPerChannel").filter(n => (n \ "@Channel").text == n).text

  def baselineLevelSequencing(xml: Node, n: String): Node = (xml \ "BaselineLevelSequencingDist").filter(n => (n \ "@Channel").text == n).head

  case class StsDist(name: String, node: (Node) => Node) extends Metric {
    val metrics: List[Metric] = List(
      Num(s"$name: Samples", (node(xml) \ "SampleSize").text),
      Num(s"$name: Mean", (node(xml) \ "SampleMean").text),
      Num(s"$name: Median", (node(xml) \ "SampleMed").text),
      Num(s"$name: StdDev", (node(xml) \ "SampleStd").text),
      Num(s"$name: 95th Pct", (node(xml) \ "Sample95thPct").text),
      Num(s"$name: Num Bins", (node(xml) \ "NumBins").text),
      Num(s"$name: Bin Width", (node(xml) \ "BinWidth").text),
      Num(s"$name: Min Outlier", (node(xml) \ "MinOutlierValue").text),
      Num(s"$name: Min", (node(xml) \ "MinBinValue").text),
      Num(s"$name: Max Outlier", (node(xml) \ "MaxOutlierValue").text),
      Num(s"$name: Max", (node(xml) \ "MaxBinValue").text),
      NumArray(s"$name: Bins", (node(xml) \ "BinCounts" \ "BinCount").map(_.text.toInt))
    )
  }

  def asStsDist(name: String): StsDist = metric(name) match {
    case d: StsDist => d
  }
}
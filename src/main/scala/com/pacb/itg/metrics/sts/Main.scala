package com.pacb.itg.metrics.sts

import java.nio.file.Paths

/**
  * Exports PipeStats (aka *.sts.xml) metrics to CSV or JSON
  */
object Main extends App {

  // location of test data
  val testData = "/pbi/dept/itg/test-data"
  val movie = "/pbi/collections/320/3200035/r54088_20160923_213253/1_A01/"
  val movieTsName = "m54088_160923_213709"
  val movieDir = Paths.get(testData+movie)


  val stsFileName = s"$movieTsName.sts.xml"
  val pMovieName = "/data/pa/m54088_160923_213709.baz"
  val stsPath = Paths.get(testData + movie + stsFileName)
  println(stsPath)
  lazy val sts = Sts(movieDir)
  println("Movie Name: "+sts.asString("Movie Name"))

}

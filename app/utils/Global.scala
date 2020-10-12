package utils

import java.io.File

object Global {

  val BAD_400 = 400


  val isWindow: Boolean = {
    System.getProperties.getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1
  }

  val windowsPath = "I:/fudan_out_sample"
  val linuxPath = "/mnt/vdb/xwq/projects/fudan_out_sample"
  val path: String = {
    if (isWindow) windowsPath else linuxPath
  }

  val suffix: String = {
    if (new File(windowsPath).exists()) ".exe" else " "
  }

  val tmpPath: String = path + "/tmp"

  val enrichPath: String = path + "/enrichData/"

}

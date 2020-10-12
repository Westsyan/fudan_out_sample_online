package utils

import java.text.SimpleDateFormat
import java.util.Date

object Utils {

  def date: String = {
    val now = new Date
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date = dateFormat.format(now)
    date
  }




}

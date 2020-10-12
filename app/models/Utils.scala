package models

import java.text.SimpleDateFormat
import java.util.Date

object Utils {

  def date: String = {
    val now = new Date
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date = dateFormat.format(now)
    date
  }

  def validApplyStatus(status:String) = {
    status match {
      case "0" => "未审核"
      case "1" => "同意"
      case "2" => "不同意"
    }
  }

  def validApplySign(sign:String) = {
    sign match{
      case "0" => "未选择"
      case "1" => "线上签名"
      case "2" => "线下签名"
    }
  }

}

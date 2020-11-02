package utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.{Executors, TimeUnit}

import com.aliyuncs.DefaultAcsClient
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest
import com.aliyuncs.http.MethodType
import com.aliyuncs.profile.DefaultProfile
import models.Tables.AliyunKeyRow
import play.Logger
import play.api.libs.json.Json

import scala.collection.mutable
import scala.util.Random

object SendValidMessage {

  var aliyunKeyRow : AliyunKeyRow = AliyunKeyRow("","",0)

  var validMap: mutable.HashMap[String, String] = mutable.HashMap()

  var validTimeMap: mutable.HashMap[String, Long] = mutable.HashMap()

  def validConfig = {
    val runnable = new Runnable {
      override def run() = {
        val time = System.currentTimeMillis()
        val clean = validTimeMap.filter(x => (time - x._2) / 1000.0 > 600)
        clean.keys.foreach { x =>
          validMap.remove(x)
        }
        validTimeMap = validTimeMap.filter(x => (time - x._2) / 1000.0 <= 600)
        Logger.info(date + "清理完成")
      }
    }
    val service = Executors.newSingleThreadScheduledExecutor()
    // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
    service.scheduleAtFixedRate(runnable, 1, 10, TimeUnit.MINUTES)
  }

  def sendMessage(phone: String, row: AliyunKeyRow) = {
    System.setProperty("sun.net.client.defaultConnectTimeout", "10000")
    System.setProperty("sun.net.client.defaultReadTimeout", "10000")
    val product = "Dysmsapi"
    val domain = "dysmsapi.aliyuncs.com"
    val accessKeyId = row.keyId
    val accessKeySecret = row.secret
    val profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret)
    DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain)
    val acsClient = new DefaultAcsClient(profile)
    val request = new SendSmsRequest
    request.setMethod(MethodType.POST)
    request.setPhoneNumbers(phone)
    request.setSignName("带出样本在线申请系统")
    request.setTemplateCode("SMS_195705252")
    val code = productValidCode
    val json = Json.obj("code" -> code)
    val jsonString = Json.stringify(json)
    request.setTemplateParam(jsonString)
    val sendSmsResponse = acsClient.getAcsResponse(request)
    val responseCode = sendSmsResponse.getCode
    if (sendSmsResponse.getCode != null && sendSmsResponse.getCode.equals("OK")) {
      (true, code, responseCode)
    } else {
      println(sendSmsResponse.getCode)
      (false, code, responseCode)
    }
  }

  def main(args: Array[String]): Unit = {
    sendMessageApplicat("13818161194","薛为琪")
  }

  def sendMessageApplicat(phone: String, mtname:String) = {
    sendMessageApply(phone,aliyunKeyRow,mtname,"SMS_204755960")
  }

  def sendMessagePass(phone: String, mtname:String) = {
    sendMessageApply(phone,aliyunKeyRow,mtname,"SMS_204746115")
  }

  def sendMessageNoPass(phone: String, mtname:String) = {
    sendMessageApply(phone,aliyunKeyRow,mtname,"SMS_204760873")
  }

  def sendMessageApply(phone: String, row: AliyunKeyRow,mtname:String,code:String) = {
    System.setProperty("sun.net.client.defaultConnectTimeout", "10000")
    System.setProperty("sun.net.client.defaultReadTimeout", "10000")
    val product = "Dysmsapi"
    val domain = "dysmsapi.aliyuncs.com"
    val accessKeyId = row.keyId
    val accessKeySecret = row.secret
    val profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret)
    DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain)
    val acsClient = new DefaultAcsClient(profile)
    val request = new SendSmsRequest
    request.setMethod(MethodType.POST)
    request.setPhoneNumbers(phone)
    request.setSignName("带出样本在线申请系统")
    request.setTemplateCode(code)
    val json = Json.obj("mtname" -> mtname)
    val jsonString = Json.stringify(json)
    request.setTemplateParam(jsonString)
    val sendSmsResponse = acsClient.getAcsResponse(request)
    val responseCode = sendSmsResponse.getCode
    if (sendSmsResponse.getCode != null && sendSmsResponse.getCode.equals("OK")) {
      (true, responseCode)
    } else {
      println(sendSmsResponse.getCode)
      (false, responseCode)
    }
  }

  def productValidCode = {
    "000000".map { i =>
      (Random.nextInt(10) + '0').toChar
    }
  }

  def date: String = {
    val now = new Date()
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date = dateFormat.format(now)
    date
  }



}

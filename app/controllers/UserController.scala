package controllers

import config.MyAwait
import dao.UserDao
import javax.inject.Inject
import models.Tables.UserRow
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Session}
import utils.{Global, SendValidMessage}

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject()(cc: ControllerComponents, userDao: UserDao)
                              (implicit exec: ExecutionContext) extends AbstractController(cc) with MyAwait {


  def loginPage: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.user.login()).withNewSession
  }

  def testLoginPage: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.user.testLogin()).withNewSession
  }

  def registerPage: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.user.register()).withNewSession
  }

  def registerSuccessPage: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.user.success()).withNewSession
  }

  def getValidCode: Action[AnyContent] = Action { implicit request =>
    val phone = FormUtils.PhoneForm.bindFromRequest().get.phone
    val send = SendValidMessage
    val validCode = send.validMap.getOrElse(phone, "0")
    var valid = "true"
    var second = 0
    if (validCode == "0") {
      putValidMap(phone)
    } else {
      val validTime = send.validTimeMap(phone)
      val time = System.currentTimeMillis()
      if ((time - validTime) / 1000.0 > 60) {
        putValidMap(phone)
      } else {
        valid = "false"
        val time = System.currentTimeMillis()
        second = ((time - validTime) / 1000).toInt
      }
    }
    Ok(Json.obj("valid" -> valid, "second" -> second))
  }

  def putValidMap(phone: String): Unit = {
    val send = SendValidMessage
     val getValid = send.sendMessage(phone, send.aliyunKeyRow)
  //  val getValid = ("", "1", "")

    val time = System.currentTimeMillis()
    send.validMap.put(phone, getValid._2)
    send.validTimeMap.put(phone, time)
  }


  def pwdLogin: Action[AnyContent] = Action.async { implicit request =>
    FormUtils.PwdLoginForm.bindFromRequest.fold(
      errorForm=>{
        Future.successful(Ok(Json.toJson(Global.BAD_400)))
      },
      data=>{
        userDao.checkUser(data.phone, data.pwd).map { x =>
          var session = new Session()
          val code = if (x.isEmpty) 500 else {
              session = session + ("id" -> x.head.id.toString)+("name" -> x.head.name)+ ("post" -> x.head.post)
              0
          }
          Ok(Json.toJson(code)).withSession(session)
        }
      }
    )
  }

  def msgLogin = Action.async { implicit request =>
    val data = FormUtils.MsgLoginForm.bindFromRequest.get
    userDao.checkPhone(data.phone).map { x =>
      var session = new Session()
      val code = if (x.isEmpty) 500 else {
        val validCode = SendValidMessage.validMap.getOrElse(data.phone, "0")
        if (validCode == data.code) {
            session = session + ("id" -> x.head.id.toString)+("name" -> x.head.name) + ("post" -> x.head.post)
            0
        } else 502
      }
      Ok(Json.toJson(code)).withSession(session)
    }
  }


  def phoneIsExsit: Action[AnyContent] = Action.async { implicit request =>
    val data = FormUtils.PhoneForm.bindFromRequest.get
    userDao.phoneIsExist(data.phone).map { x =>
      Ok(Json.obj("valid" -> (!x).toString))
    }
  }

  def register: Action[AnyContent] = Action { implicit request =>
    val data = FormUtils.RegisterForm.bindFromRequest.get
    val validCode = SendValidMessage.validMap.getOrElse(data.phone, "0")
    var valid = "true"
    var msg = ""
    validCode match {
      case "0" => valid = "false"; msg = "验证码失效，请重新发送验证码！"
      case data.code =>
        val row = UserRow(0, data.phone, data.pwd, data.name, data.post)
        userDao.addUser(row).toAwait
      case _ => valid = "false"; msg = "验证码错误！"
    }
    Ok(Json.obj("valid" -> valid, "msg" -> msg))
  }


}

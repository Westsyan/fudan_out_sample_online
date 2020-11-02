package controllers

import config.MyRequest
import dao.{ApplicatAuditPersonDao, ApplyReportDao, UserDao}
import javax.inject.Inject
import models.Tables._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents, Request, Result}

import scala.concurrent.{ExecutionContext, Future}

class AccountController @Inject()(cc: ControllerComponents,
                                  userDao: UserDao,
                                  applyDao: ApplyReportDao,
                                  applicatDao: ApplicatAuditPersonDao)
                                 (implicit exec: ExecutionContext) extends AbstractController(cc) with MyRequest {

  def validAdmin[T](x: Result, request: Request[T]) = {
    if (request.post == "管理员") {
      x
    } else {
      Redirect(routes.UserController.loginPage())
    }
  }


  def managePage = Action { implicit request =>
    validAdmin(Ok(views.html.account.managePage()), request)
  }

  def viewApplyPage = Action { implicit request =>
    validAdmin(Ok(views.html.account.viewApplyPage()), request)
  }

  def applyDetailPage(id: Int) = Action.async { implicit request =>
    SuccessOrFutureError(request.userId == 1,
      applyDao.getDataById(id).flatMap { x =>
        val ids = Array(x.userid.toString, x.team, x.department, x.project).map(_.toInt)
        userDao.getByIds(ids).map { y =>
          def getName(id: String) = y.find(_.id == id.toInt).get.name

          Ok(views.html.account.applyDetailPage(x, getName(x.userid.toString), getName(x.team), getName(x.department), getName(x.project)))
        }
      }
    )
  }

  def SuccessOrFutureError(position: Boolean, x: Future[Result]) = {
    if (position) {
      x
    } else {
      Future.successful(BadRequest)
    }
  }

  def getAllUser = Action.async { implicit request =>
    userDao.getAllUser.map { x =>
      val json = x.map { y =>
        Json.obj("id" -> y.id, "phone" -> y.phone, "name" -> y.name, "post" -> y.post)
      }
      Ok(Json.toJson(json))
    }
  }

  def addAccountPage = Action { implicit request =>
    validAdmin(Ok(views.html.account.addAccountPage()), request)
  }

  case class UserData(phone: String, name: String, post: String, team: Option[Int], department: Option[Int], project: Option[Int])

  val UserForm = Form(
    mapping(
      "phone" -> text,
      "name" -> text,
      "post" -> text,
      "team" -> optional(number),
      "department" -> optional(number),
      "project" -> optional(number),
    )(UserData.apply)(UserData.unapply)
  )


  def addUser = Action.async { implicit request =>
    val data = UserForm.bindFromRequest.get
    val row = UserRow(0, data.phone, "123456", data.name, data.post)
    userDao.addUser(row).map { x =>
      if (data.post == "申请人") {
        applicatDao.addApplicat(ApplicatauditpersonRow(x, data.team.get, data.department.get, data.project.get))
      }
      Ok(Json.obj("code" -> 200))
    }
  }

  def getAuditPeople = Action.async { implicit request =>
    userDao.getAllUser.flatMap { x =>
      applicatDao.getById(request.userId).map { y =>
        def getName(id: Int) = x.find(_.id == id).get.name
        Ok(Json.obj("team" -> getName(y.team), "department" -> getName(y.department), "project" -> getName(y.project)))
      }
    }
  }

  def deleteUserById(id: Int) = Action.async { implicit request =>
    val post = request.post
    if (post == "管理员") {
      userDao.deleteById(id).map { _ =>
        Ok(Json.obj("code" -> 200))
      }
    } else {
      Future.successful(Redirect(routes.UserController.loginPage()))
    }
  }


}

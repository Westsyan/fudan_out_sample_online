package controllers

import config.{MyAwait, MyFile, MyRequest}
import dao.{ApplyReportDao, ApplyReportGdDao, UserDao}
import javax.inject.Inject
import models.Tables.{ApplyreportGdRow, ApplyreportRow, UserRow}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.{Global, SendValidMessage, Utils}

import scala.concurrent.{ExecutionContext, Future}

class AuditController @Inject()(cc: ControllerComponents,
                                applyDao: ApplyReportDao,
                                userDao: UserDao,
                                applyreportGdDao:ApplyReportGdDao)
                               (implicit exec: ExecutionContext) extends AbstractController(cc) with MyRequest with MyFile with MyAwait {


  def auditPage = Action { implicit request =>
    if (request.post == "课题组负责人" || request.post == "接收部门负责人" || request.post == "项目负责人") {
      Ok(views.html.audit.auditPage())
    } else {
      Redirect(routes.UserController.loginPage())
    }
  }

  def teamAuditPage = Action { implicit request =>
    if (request.post == "课题组负责人和项目负责人") {
      Ok(views.html.teamAndProject.teamAuditPage())
    }else {
      Redirect(routes.UserController.loginPage())
    }
  }

  def projectAuditPage = Action { implicit request =>
    if (request.post == "课题组负责人和项目负责人") {
      Ok(views.html.teamAndProject.projectAuditPage())
    }else {
      Redirect(routes.UserController.loginPage())
    }
  }

  def getAuditData = Action.async { implicit request =>
    val id = request.userId.toString
    val x = request.post match {
      case "课题组负责人" => applyDao.getTeamAduitData(id).toAwait
      case "接收部门负责人" => applyDao.getDepartmentAduitData(id).toAwait
      case "项目负责人" => applyDao.getProjectAduitData(id).toAwait
    }
    val ids = getIds(x)
    userDao.getByIds(ids).map { user =>
      val json = x.sortBy(_.times).reverse.map { y =>
        val (applyStatus, signType, auditTime) = request.post match {
          case "课题组负责人" => (y.teamAudit, y.teamSign, y.teamTime)
          case "接收部门负责人" => (y.departmentAudit, y.departmentSign, y.departmentTime)
          case "项目负责人" => (y.projectAudit, y.projectSign, y.projectTime)
        }
        getJson(y, user, applyStatus, signType, auditTime)
      }
      Ok(Json.toJson(json))
    }
  }

  def getPreviewPdfById(id: Int) = Action.async { implicit request =>
    applyDao.getDataById(id).map { x =>
      val verifiedInactivation = x.verifiedInactivation match {
        case "on" =>"是"
        case _ => "否"
      }

      Ok(Json.obj("id" -> x.id, "applyTime" -> x.times, "applyCode" -> x.applyCode, "sampleName" -> x.sampleName,
        "sampleType" -> x.sampleType, "inactivation" -> x.inactivation, "verifiedInactivation" -> verifiedInactivation,
        "sampleCode" -> x.sampleCode, "application" -> x.application, "outNums" -> x.outNums, "position" -> x.position))
    }
  }

  def updateApplyStatus(id: Int, status: String) = Action.async { implicit request =>
    applyDao.getDataById(id).map { x =>
      val postId = request.post match {
        case "课题组负责人" => x.team
        case "接收部门负责人" => x.department
        case "项目负责人" => x.project
      }
      try {
        val date = Utils.date
        val user = userDao.getById(x.userid).toAwait
        if (request.userId == postId.toInt) {
          request.post match {
            case "课题组负责人" => applyDao.updateTeamStatus(id, status, date).toAwait
              if(status == "1"){
                val department = userDao.getById(x.department.toInt).toAwait
                SendValidMessage.sendMessageApplicat(department.phone,user.name)
              }else{
                SendValidMessage.sendMessageNoPass(user.phone,x.applyCode)
              }
            case "接收部门负责人" => applyDao.updateDepartmentStatus(id, status, date).toAwait
              if(status == "1"){
                val project = userDao.getById(x.project.toInt).toAwait
                SendValidMessage.sendMessageApplicat(project.phone,user.name)
              }else{
                SendValidMessage.sendMessageNoPass(user.phone,x.applyCode)
              }
            case "项目负责人" => applyDao.updateProjectStatus(id, status, date).toAwait
              if(status == "1"){
                SendValidMessage.sendMessagePass(user.phone,x.applyCode)
              }else{
                SendValidMessage.sendMessageNoPass(user.phone,x.applyCode)
              }
          }
        }
        Ok(Json.obj("code" -> 200))
      } catch {
        case e: Exception => Ok(Json.obj("code" -> 400, "error" -> e.getMessage))
      }
    }
  }

  def updateApplySign(id: Int, signCode: String) = Action.async { implicit request =>
    val path = s"${Global.path}/data/${request.userId}/signPdf.png"
    if (path.toFile.exists() || signCode == "2") {

      applyDao.getDataById(id).map { x =>
        val postId = request.post match {
          case "课题组负责人" => x.team
          case "接收部门负责人" => x.department
          case "项目负责人" => x.project
        }
        try {
          if (request.userId == postId.toInt) {
            request.post match {
              case "课题组负责人" => applyDao.updateTeamSign(id, signCode).toAwait
              case "接收部门负责人" => applyDao.updateDepartmentSign(id, signCode).toAwait
              case "项目负责人" => applyDao.updateProjectSign(id, signCode).toAwait
            }
          }
          Ok(Json.obj("code" -> 200))
        } catch {
          case e: Exception => Ok(Json.obj("code" -> 400, "error" -> e.getMessage))
        }
      }
    } else {
      request.post match {
        case "课题组负责人" => applyDao.updateTeamStatus(id, "0", "-").toAwait
        case "接收部门负责人" => applyDao.updateDepartmentStatus(id, "0", "-").toAwait
        case "项目负责人" => applyDao.updateProjectStatus(id, "0", "-").toAwait
      }
      Future.successful(Ok(Json.obj("code" -> 401)))
    }
  }

  def getTeamAuditData = Action.async { implicit request =>
    val x = applyDao.getTeamAduitData(request.userId.toString).toAwait
    val ids = getIds(x)
    userDao.getByIds(ids).map { user =>
      val json = x.sortBy(_.times).reverse.map { y =>
        getJson(y, user, y.teamAudit, y.teamSign, y.teamTime)
      }
      Ok(Json.toJson(json))
    }
  }

  def updateTeamApplyStatus(id: Int, status: String) = Action.async { implicit request =>
    applyDao.getDataById(id).map { x =>
      try {
        val date = Utils.date
        if (request.userId == x.team.toInt) {
          applyDao.updateTeamStatus(id, status, date).toAwait
        }
        val user = userDao.getById(x.userid).toAwait
        if(status == "1"){
          val department = userDao.getById(x.department.toInt).toAwait
          println(department.phone)
          SendValidMessage.sendMessageApplicat(department.phone,user.name)
        }else{
          SendValidMessage.sendMessageNoPass(user.phone,x.applyCode)
        }
        Ok(Json.obj("code" -> 200))
      } catch {
        case e: Exception => Ok(Json.obj("code" -> 400, "error" -> e.getMessage))
      }
    }
  }

  def updateTeamApplySign(id: Int, signCode: String) = Action.async { implicit request =>
    val path = s"${Global.path}/data/${request.userId}/signPdf.png"
    if (path.toFile.exists() || signCode == "2") {
      applyDao.getDataById(id).map { x =>
        try {
          if (request.userId == x.team.toInt) {
            applyDao.updateTeamSign(id, signCode).toAwait
          }
          Ok(Json.obj("code" -> 200))
        } catch {
          case e: Exception => Ok(Json.obj("code" -> 400, "error" -> e.getMessage))
        }
      }
    } else {
      applyDao.updateTeamStatus(id, "0", "-").toAwait
      Future.successful(Ok(Json.obj("code" -> 401)))
    }
  }

  def getProjectAuditData = Action.async { implicit request =>
    val x = applyDao.getProjectAduitData(request.userId.toString).toAwait
    val ids = getIds(x)
    userDao.getByIds(ids).map { user =>
      val json = x.sortBy(_.times).reverse.map { y =>
        getJson(y, user, y.projectAudit, y.projectSign, y.projectTime)
      }
      Ok(Json.toJson(json))
    }
  }

  def updateProjectApplyStatus(id: Int, status: String) = Action.async { implicit request =>
    applyDao.getDataById(id).map { x =>
      try {
        val date = Utils.date
        if (request.userId == x.project.toInt) {
          applyDao.updateProjectStatus(id, status, date).toAwait
        }
        val user = userDao.getById(x.userid).toAwait
        if(status == "1"){
          if(!applyreportGdDao.existByReportId(id).toAwait){
            applyreportGdDao.addRow(ApplyreportGdRow(0,id)).toAwait
          }
          SendValidMessage.sendMessagePass(user.phone,x.applyCode)
        }else{
          SendValidMessage.sendMessageNoPass(user.phone,x.applyCode)
        }
        Ok(Json.obj("code" -> 200))
      } catch {
        case e: Exception => Ok(Json.obj("code" -> 400, "error" -> e.getMessage))
      }
    }
  }

  def updateProjectApplySign(id: Int, signCode: String) = Action.async { implicit request =>
    val path = s"${Global.path}/data/${request.userId}/signPdf.png"
    if (path.toFile.exists() || signCode == "2") {
      applyDao.getDataById(id).map { x =>
        try {
          if (request.userId == x.project.toInt) {
            applyDao.updateProjectSign(id, signCode).toAwait
          }
          Ok(Json.obj("code" -> 200))
        } catch {
          case e: Exception => Ok(Json.obj("code" -> 400, "error" -> e.getMessage))
        }
      }
    } else {
      applyDao.updateProjectStatus(id, "0", "-").toAwait
      Future.successful(Ok(Json.obj("code" -> 401)))
    }
  }

  def getIds(x: Seq[ApplyreportRow]): Seq[Int] = (x.map(_.userid.toString) ++ x.map(_.team) ++ x.map(_.department) ++ x.map(_.project)).distinct.map(_.toInt)

  def getName(user: Seq[UserRow], id: Int): String = user.find(_.id == id).get.name

  def getJson(y: ApplyreportRow, user: Seq[UserRow], applyStatus: String, signType: String, auditTime: String): JsObject =
    Json.obj("id" -> y.id, "applyStatus" -> applyStatus, "applyCode" -> y.applyCode, "applyTime" -> y.times,
      "sample" -> y.sampleName, "times" -> y.times,
      "team" -> getName(user, y.team.toInt), "teamAudit" -> y.teamAudit,
      "signType" -> signType, "auditTime" -> auditTime,
      "department" -> getName(user, y.department.toInt), "departmentAudit" -> y.departmentAudit,
      "project" -> getName(user, y.project.toInt), "projectAudit" -> y.projectAudit,
      "applyPeople" -> getName(user, y.userid))

}



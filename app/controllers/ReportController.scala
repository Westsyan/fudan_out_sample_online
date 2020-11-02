package controllers

import config.{MyAwait, MyFile, MyRequest}
import dao.{ApplicatAuditPersonDao, ApplyReportDao, ApplyReportGdDao, UserDao}
import javax.inject.Inject
import models.Tables.ApplyreportRow
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents, Result}
import utils.{CreatePdf, Global, SendValidMessage, Utils}

import scala.concurrent.{ExecutionContext, Future}

class ReportController @Inject()(cc: ControllerComponents,
                                 applyDao: ApplyReportDao,
                                 applyGdDao:ApplyReportGdDao,
                                 userDao: UserDao,
                                 applicatDao: ApplicatAuditPersonDao)
                                (implicit exec: ExecutionContext) extends AbstractController(cc) with MyRequest with MyFile with MyAwait{

  def reportApplyPage = Action { implicit request =>
    if (request.post == "申请人") {
      Ok(views.html.report.reportApplyPage())
    } else {
      Redirect(routes.UserController.loginPage())
    }
  }

  def applyReport = Action.async { implicit request =>
    FormUtils.ApplyReportForm.bindFromRequest().fold(
      errorForm => {
        Future.successful(Ok(Json.obj("code" -> Global.BAD_400)))
      },
      data => {
        val time = Utils.date
        val id = request.userId
        applicatDao.getById(id).flatMap { post =>
          val team = userDao.getById(post.team).toAwait
          val user = userDao.getById(id).toAwait
          val inactivation = if (data.inactivation == "other") data.inactivation_other.getOrElse("") else data.inactivation
          val row = ApplyreportRow(0, id, data.project_name, data.project_code, time, "", data.sample_name, data.sample_type, inactivation,
            data.verified_inactivation, data.sample_code, data.out_nums, data.application, data.position, post.team.toString,
            post.department.toString, post.project.toString, "0", "0", "0", "", "", "", "0", "0", "0")
          applyDao.addApplyReport(row).map { x =>
            SendValidMessage.sendMessageApplicat(team.phone,user.name)
            Ok(Json.obj("code" -> 200))
          }
        }
      }
    )
  }

  def reportStatusPage = Action { implicit request =>
    if (request.post == "申请人") {
      Ok(views.html.report.reportStatusPage())
    } else {
      Redirect(routes.UserController.loginPage())
    }
  }

  def getReportStatusData = Action.async { implicit request =>
    applyDao.getDataByUserid(request.userId).flatMap { x =>
      userDao.getByIds((x.map(_.team) ++ x.map(_.department) ++ x.map(_.project)).distinct.map(_.toInt)).map { user =>
        val json =
          x.sortBy(_.times).reverse.map { y =>
            Json.obj("id" -> y.id, "applyCode" -> y.applyCode, "sample" -> y.sampleName, "times" -> y.times,
              "team" -> user.find(_.id == y.team.toInt).get.name, "teamAudit" -> y.teamAudit, "teamAuditTime" -> y.teamTime, "teamAuditSign" -> y.teamSign,
              "department" -> user.find(_.id == y.department.toInt).get.name, "departmentAudit" -> y.departmentAudit, "departmentAuditTime" -> y.departmentTime,
              "project" -> user.find(_.id == y.project.toInt).get.name, "projectAudit" -> y.projectAudit, "projectAuditTime" -> y.projectTime)
          }
        Ok(Json.toJson(json))
      }
    }
  }

  def getAllApplyData = Action.async { implicit request =>
    SuccessOrFutureError(request.userId == 1,
      applyDao.getAllData.flatMap { x =>
        userDao.getAllUser.map { user =>
          val json =
            x.sortBy(_.times).reverse.map { y =>
              Json.obj("id" -> y.id, "applyCode" -> y.applyCode, "applyPerson" -> user.find(_.id == y.userid).get.name, "sample" -> y.sampleName, "times" -> y.times,
                "team" -> user.find(_.id == y.team.toInt).get.name, "teamAudit" -> y.teamAudit, "teamAuditTime" -> y.teamTime, "teamAuditSign" -> y.teamSign,
                "department" -> user.find(_.id == y.department.toInt).get.name, "departmentAudit" -> y.departmentAudit, "departmentAuditTime" -> y.departmentTime,
                "project" -> user.find(_.id == y.project.toInt).get.name, "projectAudit" -> y.projectAudit, "projectAuditTime" -> y.projectTime)
            }
          Ok(Json.toJson(json))
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

  def downloadRepoprt(id: Int) = Action.async { implicit request =>
    applyDao.getDataById(id).flatMap { x =>
      userDao.getAllUser.map { user =>
        val dir = s"${Global.path}/data/${x.userid}/${x.id}"
        dir.mkdirs
        val path = s"$dir/report.pdf"

        def getUser(id: String) = user.find(_.id == id.toInt).get.name

        val gdRow = applyGdDao.getIdByReportId(id).toAwait

        val gd = if(gdRow.isEmpty) 0 else gdRow.head.id

        CreatePdf.createPdf(path, x, getUser(x.team), getUser(x.department), getUser(x.project),gd)
        Ok.sendFile(path.toFile).withHeaders(
          //缓存
          CACHE_CONTROL -> "max-age=3600",
          CONTENT_DISPOSITION -> s"attachment; filename=${x.applyCode}.pdf",
          "HttpResponse.entity.contentType" -> "application/x-download"
        )
      }
    }
  }


  def getAllAuditPeople = Action.async { implicit request =>
    userDao.getAllUser.map { x =>
      def getPostJson(post: String) = {
        x.filter(_.post == post).map { y =>
          Json.obj("text" -> y.name, "id" -> y.id)
        }
      }

      val team = getPostJson("课题组负责人") ++ getPostJson("课题组负责人和项目负责人")
      val department = getPostJson("接收部门负责人")
      val project = getPostJson("项目负责人") ++ getPostJson("课题组负责人和项目负责人")
      Ok(Json.obj("team" -> team, "department" -> department, "project" -> project))
    }
  }


}

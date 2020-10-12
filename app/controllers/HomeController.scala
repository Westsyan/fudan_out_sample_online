package controllers

import config.MyRequest
import javax.inject._
import play.api.mvc._


@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) with MyRequest {


  def index = Action { implicit request =>
    request.session.get("post") match {
      case Some("管理员") => Redirect(routes.AccountController.managePage())
      case Some("申请人") => Redirect(routes.ReportController.reportApplyPage())
      case x if x == Some("课题组负责人") || x == Some("项目负责人") || x == Some("接收部门负责人")  => Redirect(routes.AuditController.auditPage())
      case Some("课题组负责人和项目负责人") => Redirect(routes.AuditController.teamAuditPage())
      case _ => Redirect(routes.UserController.loginPage())
    }


  }


}

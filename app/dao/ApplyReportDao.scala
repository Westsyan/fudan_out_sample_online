package dao

import java.text.SimpleDateFormat
import java.util.Date

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class ApplyReportDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                              (implicit exec: ExecutionContext) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def getAllData : Future[Seq[ApplyreportRow]] = {
    db.run(Applyreport.result)
  }

  def addApplyReport(row:ApplyreportRow) : Future[Unit] = {
   val id = db.run(Applyreport returning Applyreport.map(_.id) += row)
    id.map{x=>
      val now = new Date
      val dateFormat = new SimpleDateFormat("yyyy")
      val date = dateFormat.format(now)

      val appllyCode = "BSL-3-" + date + "0"*(6-x.toString.length) + x
      db.run(Applyreport.filter(_.id === x).map(_.applyCode).update(appllyCode)).map(_=>())
    }
  }

  def getDataByUserid(userid:Int) : Future[Seq[ApplyreportRow]] = {
    db.run(Applyreport.filter(_.userid === userid).result)
  }

  def getDataById(id:Int) : Future[ApplyreportRow] = {
    db.run(Applyreport.filter(_.id === id).result.head)
  }

  def getTeamAduitData(teamId:String) : Future[Seq[ApplyreportRow]] = {
    db.run(Applyreport.filter(_.team === teamId).result)
  }

  def getDepartmentAduitData(departmentId:String) : Future[Seq[ApplyreportRow]] = {
    db.run(Applyreport.filter(_.department === departmentId).filter(_.teamAudit === "1").result)
  }

  def getProjectAduitData(projectId:String) : Future[Seq[ApplyreportRow]] = {
    db.run(Applyreport.filter(_.project === projectId).filter(_.teamAudit === "1").filter(_.departmentAudit === "1").result)
  }

  def updateTeamStatus(id:Int,status:String,date:String) : Future[Unit] = {
    db.run(Applyreport.filter(_.id === id).map(x=> (x.teamAudit,x.teamTime)).update((status,date))).map(_=>())
  }

  def updateDepartmentStatus(id:Int,status:String,date:String) : Future[Unit] = {
    db.run(Applyreport.filter(_.id === id).map(x=> (x.departmentAudit,x.departmentTime)).update((status,date))).map(_=>())
  }

  def updateProjectStatus(id:Int,status:String,date:String) : Future[Unit] = {
    db.run(Applyreport.filter(_.id === id).map(x=>(x.projectAudit,x.projectTime)).update((status,date))).map(_=>())
  }

  def updateTeamSign(id:Int,sign:String) : Future[Unit] = {
    db.run(Applyreport.filter(_.id === id).map(_.teamSign).update(sign)).map(_=>())
  }

  def updateDepartmentSign(id:Int,sign:String) : Future[Unit] = {
    db.run(Applyreport.filter(_.id === id).map(_.departmentSign).update(sign)).map(_=>())
  }

  def updateProjectSign(id:Int,sign:String) : Future[Unit] = {
    db.run(Applyreport.filter(_.id === id).map(_.projectSign).update(sign)).map(_=>())
  }



}

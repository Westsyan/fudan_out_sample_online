package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class ApplyReportGdDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                                (implicit exec: ExecutionContext) extends
  HasDatabaseConfigProvider[JdbcProfile]  {

  import profile.api._

  def addRow(row:ApplyreportGdRow) : Future[Unit] = {
    db.run(ApplyreportGd += row).map(_=>())
  }

  def existByReportId(reportId:Int) : Future[Boolean] = {
    db.run(ApplyreportGd.filter(_.reportId === reportId).exists.result)
  }

  def getIdByReportId(reportId:Int) : Future[Seq[ApplyreportGdRow]] = {
    db.run(ApplyreportGd.filter(_.reportId === reportId).result)
  }

}

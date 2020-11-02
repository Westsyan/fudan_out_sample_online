package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class ApplicatAuditPersonDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                                      (implicit exec: ExecutionContext) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def addApplicat(row: ApplicatauditpersonRow): Future[Unit] = {
    db.run(Applicatauditperson += row).map(_ => ())
  }

  def getById(id: Int): Future[ApplicatauditpersonRow] = {
    db.run(Applicatauditperson.filter(_.userid === id).result.head)
  }

}

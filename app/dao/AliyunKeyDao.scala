package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class AliyunKeyDao  @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                             (implicit exec: ExecutionContext) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def getKey : Future[AliyunKeyRow] = {
    db.run(AliyunKey.result.head)
  }


}

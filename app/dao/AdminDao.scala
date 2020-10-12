package dao

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

class AdminDao  @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                         (implicit exec: ExecutionContext) extends
  HasDatabaseConfigProvider[JdbcProfile] {






}

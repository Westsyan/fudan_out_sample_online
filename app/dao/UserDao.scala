package dao

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import models.Tables._

import scala.concurrent.{ExecutionContext, Future}

class UserDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                       (implicit exec: ExecutionContext) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def phoneIsExist(phone: String): Future[Boolean] = {
    db.run(User.filter(_.phone === phone).exists.result)
  }

  def addUser(row: UserRow): Future[Int] = {
    db.run(User returning User.map(_.id) += row)
  }

  def checkUser(phone: String, pwd: String): Future[Seq[UserRow]] = {
    db.run(User.filter(_.phone === phone).filter(_.pwd === pwd).result)
  }

  def checkPhone(phone:String) : Future[Seq[UserRow]] = {
    db.run(User.filter(_.phone === phone).result)
  }

  def getAllUser : Future[Seq[UserRow]] = {
    db.run(User.result)
  }

  def getByIds(ids:Seq[Int]) : Future[Seq[UserRow]] = {
    db.run(User.filter(_.id.inSetBind(ids)).result)
  }

  def getById(id:Int) : Future[UserRow] = {
    db.run(User.filter(_.id === id).result.head)
  }

  def deleteById(id:Int) : Future[Unit] = {
    db.run(User.filter(_.id === id).delete).map(_=>())
  }

}

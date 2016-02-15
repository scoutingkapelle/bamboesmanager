package models.daos

import java.util.UUID
import javax.inject.Inject

import models.daos.tables.UserTable
import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

class UserDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  private val users = TableQuery[UserTable]

  def all: Future[Seq[User]] = db.run(users.result)

  def get(id: UUID): Future[Option[User]] = db.run(users.filter(_.id === id).result.headOption)

  def save(user: User) = db.run(users.insertOrUpdate(user))
}
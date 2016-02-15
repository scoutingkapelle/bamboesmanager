package models.daos

import java.util.UUID
import javax.inject.Inject

import models.daos.tables.{PasswordInfo, PasswordTable}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

class PasswordDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {
  private val passwords = TableQuery[PasswordTable]

  def get(user_id: UUID): Future[Option[PasswordInfo]] =
    db.run(passwords.filter(_.user_id === user_id).result.headOption)

  def save(password: PasswordInfo) = db.run(passwords.insertOrUpdate(password))
}
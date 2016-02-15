package models.daos.tables

import java.util.UUID

import slick.driver.PostgresDriver.api._
import slick.lifted.Tag

case class PasswordInfo(user_id: UUID, hash: String)

class PasswordTable(tag: Tag) extends Table[PasswordInfo](tag, "passwords") {

  val users = TableQuery[UserTable]

  def * = (user_id, hash) <>(PasswordInfo.tupled, PasswordInfo.unapply)

  def user_id = column[UUID]("user_id", O.PrimaryKey)

  def hash = column[String]("password")

  def user = foreignKey("user_fk", user_id, users)(_.id)

}
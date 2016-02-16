package models.daos.tables

import java.util.UUID

import models.User
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape.proveShapeOf

trait DBTableDefinitions {
  protected val driver: JdbcProfile

  import driver.api._

  val users = TableQuery[Users]
  val passwords = TableQuery[Passwords]

  class Users(tag: Tag) extends Table[User](tag, "users") {
    def * = (id, name, email) <>(User.tupled, User.unapply)

    def id = column[UUID]("id", O.PrimaryKey)

    def name = column[String]("name")

    def email = column[String]("email")
  }

  case class DBPasswordInfo(hash: String, password: String, salt: Option[String], email: String)

  class Passwords(tag: Tag) extends Table[DBPasswordInfo](tag, "passwords") {
    def * = (hash, password, salt, email) <>(DBPasswordInfo.tupled, DBPasswordInfo.unapply)

    def hash = column[String]("hash")

    def password = column[String]("password")

    def salt = column[Option[String]]("salt")

    def email = column[String]("email", O.PrimaryKey)
  }
}
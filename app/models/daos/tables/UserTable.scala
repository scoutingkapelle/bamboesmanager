package models.daos.tables

import java.util.UUID

import models.User
import slick.driver.PostgresDriver.api._
import slick.lifted.Tag

class UserTable(tag: Tag) extends Table[User](tag, "users") {

  def * = (id, name, name, email) <>(User.tupled, User.unapply)

  def id = column[UUID]("id", O.PrimaryKey)

  def name = column[String]("name")

  def email = column[String]("email")

  def username = column[String]("username")
}
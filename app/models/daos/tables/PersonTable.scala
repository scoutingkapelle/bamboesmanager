package models.daos.tables

import java.util.UUID

import slick.driver.PostgresDriver.api._
import slick.lifted.Tag

case class DBPerson(id: UUID, name: String, email: String, age: Int, group_id: UUID)

class PersonTable(tag: Tag) extends Table[DBPerson](tag, "persons") {
  val organisations = TableQuery[OrganisationTable]

  def * = (id, name, email, age, group_id) <>(DBPerson.tupled, DBPerson.unapply)

  def name = column[String]("name")

  def email = column[String]("email")

  def age = column[Int]("age")

  def id = column[UUID]("id", O.PrimaryKey)

  def group_id = column[UUID]("group_id")

  def group = foreignKey("group_fk", group_id, organisations)(_.id)
}
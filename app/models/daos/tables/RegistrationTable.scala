package models.daos.tables

import java.util.UUID

import slick.driver.PostgresDriver.api._
import slick.lifted.Tag

case class DBRegistration(id: UUID, person_id: UUID, friday: Boolean, saturday: Boolean, sorting: Boolean, category_id: UUID, team_leader: Boolean)

class RegistrationTable(tag: Tag) extends Table[DBRegistration](tag, "registrations") {
  val categories = TableQuery[CategoryTable]
  val persons = TableQuery[PersonTable]

  def * = (id, person_id, friday, saturday, sorting, category_id, team_leader) <>(DBRegistration.tupled, DBRegistration.unapply)

  def friday = column[Boolean]("friday")

  def saturday = column[Boolean]("saturday")

  def sorting = column[Boolean]("sorting")

  def id = column[UUID]("id", O.PrimaryKey)

  def category_id = column[UUID]("category_id")

  def person_id = column[UUID]("person_id")

  def team_leader = column[Boolean]("team_leader")

  def category = foreignKey("category_fk", category_id, categories)(_.id)

  def person = foreignKey("person_fk", person_id, persons)(_.id)
}
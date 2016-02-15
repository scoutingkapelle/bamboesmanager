package models.daos.tables

import java.util.UUID

import models.Organisation
import slick.driver.PostgresDriver.api._
import slick.lifted.Tag

class OrganisationTable(tag: Tag) extends Table[Organisation](tag, "organisations") {
  def * = (id, name) <>(Organisation.tupled, Organisation.unapply)

  def id = column[UUID]("id", O.PrimaryKey)

  def name = column[String]("name")
}
package models.daos.tables

import java.util.UUID

import models.Organisation
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

class OrganisationTable(tag: Tag) extends Table[Organisation](tag, "organisations") {
  def * = (id, name) <> (Organisation.tupled, Organisation.unapply)

  def id = column[UUID]("id", O.PrimaryKey)

  def name = column[String]("name")
}
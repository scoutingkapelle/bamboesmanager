package models.daos.tables

import java.util.UUID

import models.Category
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

class CategoryTable(tag: Tag) extends Table[Category](tag, "categories") {
  def * = (id, name) <> (Category.tupled, Category.unapply)

  def id = column[UUID]("id", O.PrimaryKey)

  def name = column[String]("name")
}
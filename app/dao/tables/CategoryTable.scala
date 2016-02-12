package dao.tables

import java.util.UUID

import models.Category
import slick.driver.PostgresDriver.api._
import slick.lifted.Tag

class CategoryTable(tag: Tag) extends Table[Category](tag, "categories") {
  def * = (id, name) <>(Category.tupled, Category.unapply)

  def id = column[UUID]("id", O.PrimaryKey)

  def name = column[String]("name")
}
package models.daos

import java.util.UUID
import javax.inject.Inject

import models.daos.tables.CategoryTable
import models.Category
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

class CategoryDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {
  private val categories = TableQuery[CategoryTable]

  def all: Future[Seq[Category]] = db.run(categories.result)

  def get(id: UUID): Future[Option[Category]] = db.run(categories.filter(_.id === id).result.headOption)

  def save(category: Category) = db.run(categories.insertOrUpdate(category))
}
package dao

import java.util.UUID
import javax.inject.Inject

import dao.tables.OrganisationTable
import models.Organisation
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

class OrganisationDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {
  private val organisations = TableQuery[OrganisationTable]

  def all: Future[Seq[Organisation]] = db.run(organisations.result)

  def get(id: UUID): Future[Option[Organisation]] = db.run(organisations.filter(_.id === id).result.headOption)

  def save(organisation: Organisation) = db.run(organisations.insertOrUpdate(organisation))
}
package models.daos

import java.util.UUID
import javax.inject.Inject

import models.daos.tables.{GroupTable, OrganisationTable}
import models.{Group, Organisation}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class OrganisationDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  private val organisations = TableQuery[OrganisationTable]
  private val groups = TableQuery[GroupTable]

  def all: Future[Seq[Organisation]] = db.run(organisations.result)

  def groups(organisation_id: UUID): Future[Seq[Group]] = {
    val query = for {
      g <- groups if g.organisation_id === organisation_id
      o <- organisations if o.id === g.organisation_id
    } yield (g, o)

    db.run(query.result).map(rows => rows.map {
      case (g, o) => Group(g.id, g.name, o)
    })
  }

  def get(id: UUID): Future[Option[Organisation]] = db.run(organisations.filter(_.id === id).result.headOption)

  def save(organisation: Organisation): Future[Int] = db.run(organisations.insertOrUpdate(organisation))
}
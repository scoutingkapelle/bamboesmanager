package dao

import java.util.UUID
import javax.inject.Inject

import dao.tables.{DBGroup, GroupTable, OrganisationTable}
import models.{Group, Organisation}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GroupDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {
  private val groups = TableQuery[GroupTable]
  private val organisations = TableQuery[OrganisationTable]

  def all: Future[Seq[Group]] = {
    val query = for {
      g <- groups
      o <- organisations if o.id === g.organisation_id
    } yield (g, o)

    db.run(query.result).map(rows => rows.map {
      case (g, o) =>
        val organisation = Organisation(o.id, o.name)
        Group(g.id, g.name, organisation)
    })
  }

  def get(id: UUID): Future[Option[Group]] = {
    val query = for {
      g <- groups if g.id === id
      o <- organisations if o.id == g.organisation_id
    } yield (g, o)

    db.run(query.result.headOption).map(rows => rows.map {
      case (g, o) =>
        val organisation = Organisation(o.id, o.name)
        Group(g.id, g.name, organisation)
    })
  }

  def save(group: Group) = db.run(groups.insertOrUpdate(toDBGroup(group)))

  private def toDBGroup(group: Group) = DBGroup(group.id, group.name, group.organisation.id)
}
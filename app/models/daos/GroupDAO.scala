package models.daos

import models.daos.tables.DAOSlick
import models.{Group, Person}
import play.api.db.slick.DatabaseConfigProvider

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GroupDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends DAOSlick {

  import profile.api._

  def all: Future[Seq[Group]] = {
    val query = for {
      g <- groups
      o <- organisations if o.id === g.organisation_id
    } yield (g, o)

    db.run(query.result).map(rows => rows.map {
      case (g, o) => Group(g.id, g.name, o)
    })
  }

  def members(group_id: UUID): Future[Seq[Person]] = {
    val query = for {
      p <- persons if p.group_id === group_id
      g <- groups if g.id === p.group_id
      o <- organisations if o.id === g.organisation_id
    } yield (p, g, o)

    db.run(query.result).map(rows => rows.map {
      case (p, g, o) =>
        val group = Group(g.id, g.name, o)
        Person(p.id, p.name, p.email, p.age, group)
    })
  }

  def get(id: UUID): Future[Option[Group]] = {
    val query = for {
      g <- groups if g.id === id
      o <- organisations if o.id === g.organisation_id
    } yield (g, o)

    db.run(query.result.headOption).map(rows => rows.map {
      case (g, o) => Group(g.id, g.name, o)
    })
  }

  def save(group: Group): Future[Int] = db.run(groups.insertOrUpdate(group))
}

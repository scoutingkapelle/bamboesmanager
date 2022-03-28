package models.daos

import models._
import models.daos.tables.DAOSlick
import play.api.db.slick.DatabaseConfigProvider

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PersonDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends DAOSlick {

  import profile.api._

  def all: Future[Seq[Person]] = {
    val query = for {
      p <- persons
      g <- groups if g.id === p.group_id
      o <- organisations if o.id === g.organisation_id
    } yield (p, g, o)

    db.run(query.result).map(rows => rows.map {
      case (p, g, o) =>
        val group = Group(g.id, g.name, o)
        Person(p.id, p.name, p.email, p.age, group)
    })
  }

  def get(id: UUID): Future[Option[Person]] = {
    val query = for {
      p <- persons if p.id === id
      g <- groups if g.id === p.group_id
      o <- organisations if o.id === g.organisation_id
    } yield (p, g, o)

    db.run(query.result.headOption).map(rows => rows.map {
      case (p, g, o) =>
        val group = Group(g.id, g.name, o)
        Person(p.id, p.name, p.email, p.age, group)
    })
  }

  def save(person: Person): Future[Person] = db.run(persons.insertOrUpdate(person)).map(_ => person)

  def delete(id: UUID): Future[Int] = {
    val query = persons.filter(_.id === id).delete
    db.run(query)
  }
}

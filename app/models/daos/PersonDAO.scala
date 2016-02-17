package models.daos

import java.util.UUID
import javax.inject.Inject

import models._
import models.daos.tables.{DBPerson, GroupTable, OrganisationTable, PersonTable}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PersonDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  private val persons = TableQuery[PersonTable]
  private val groups = TableQuery[GroupTable]
  private val organisations = TableQuery[OrganisationTable]

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

  def save(person: Person): Future[Person] = db.run(persons.insertOrUpdate(toDBPerson(person))).map(_ => person)

  def toDBPerson(person: Person) = DBPerson(person.id, person.name, person.email, person.age, person.group.id)
}
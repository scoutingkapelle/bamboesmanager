package models.daos

import models._
import models.daos.tables.DAOSlick
import play.api.db.slick.DatabaseConfigProvider

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegistrationDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends DAOSlick {

  import profile.api._

  def all: Future[Seq[Registration]] = {
    val query = for {
      ((r, c), s) <- registrations joinLeft categories on (_.category_id === _.id) joinLeft categories on (_._1.second_choice_id === _.id)
      p <- persons if p.id === r.person_id
      g <- groups if g.id === p.group_id
      o <- organisations if o.id === g.organisation_id
    } yield (r, p, g, o, c, s)

    db.run(query.result).map(rows => rows.map {
      case (r, p, g, o, c, s) =>
        val group = Group(g.id, g.name, o)
        val person = Person(p.id, p.name, p.email, p.age, group)
        Registration(r.id, person, r.friday, r.saturday, r.sorting, c, s, r.team_leader)
    })
  }

  def category(category_id: UUID): Future[Seq[Registration]] = {
    val query = for {
      ((r, c), s) <- registrations joinLeft categories on (_.category_id === _.id) joinLeft categories on (_._1.second_choice_id === _.id)
      p <- persons if p.id === r.person_id && r.category_id === category_id
      g <- groups if g.id === p.group_id
      o <- organisations if o.id === g.organisation_id
    } yield (r, p, g, o, c, s)

    db.run(query.result).map(rows => rows.map {
      case (r, p, g, o, c, s) =>
        val group = Group(g.id, g.name, o)
        val person = Person(p.id, p.name, p.email, p.age, group)
        Registration(r.id, person, r.friday, r.saturday, r.sorting, c, s, r.team_leader)
    })
  }

  def group(group_id: UUID): Future[Seq[Registration]] = {
    val query = for {
      ((r, c), s) <- registrations joinLeft categories on (_.category_id === _.id) joinLeft categories on (_._1.second_choice_id === _.id)
      p <- persons if p.id === r.person_id && p.group_id === group_id
      g <- groups if g.id === group_id
      o <- organisations if o.id === g.organisation_id
    } yield (r, p, g, o, c, s)

    db.run(query.result).map(rows => rows.map {
      case (r, p, g, o, c, s) =>
        val group = Group(g.id, g.name, o)
        val person = Person(p.id, p.name, p.email, p.age, group)
        Registration(r.id, person, r.friday, r.saturday, r.sorting, c, s, r.team_leader)
    })
  }

  def get(id: UUID): Future[Option[Registration]] = {
    val query = for {
      ((r, c), s) <- registrations  joinLeft categories on (_.category_id === _.id) joinLeft categories on (_._1.second_choice_id === _.id) if r.id === id
      p <- persons if p.id === r.person_id
      g <- groups if g.id === p.group_id
      o <- organisations if o.id === g.organisation_id
    } yield (r, p, g, o, c, s)

    db.run(query.result.headOption).map(rows => rows.map {
      case (r, p, g, o, c, s) =>
        val group = Group(g.id, g.name, o)
        val person = Person(p.id, p.name, p.email, p.age, group)
        Registration(r.id, person, r.friday, r.saturday, r.sorting, c, s, r.team_leader)
    })
  }

  def save(registration: Registration): Future[Registration] =
    db.run(registrations.insertOrUpdate(registration)).map(_ => registration)

  def delete(id: UUID): Future[Int] = {
    val query = registrations.filter(_.id === id).delete
    db.run(query)
  }
}

package models.daos

import java.util.UUID
import javax.inject.Inject

import models.daos.tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
class StatisticsDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  private val registrations = TableQuery[RegistrationTable]
  private val persons = TableQuery[PersonTable]
  private val groups = TableQuery[GroupTable]
  private val organisations = TableQuery[OrganisationTable]

  def friday: Future[Seq[(String, Int)]] = {
    val query = (for {
      r <- registrations if r.friday
      p <- persons if p.id === r.person_id
      g <- groups if g.id === p.group_id
      o <- organisations if o.id === g.organisation_id
    } yield o).groupBy(_.name).map {
      organisation => (organisation._1, organisation._2.length)
    }

    db.run(query.result)
  }

  def saturday: Future[Seq[(String, Int)]] = {
    val query = (for {
      r <- registrations if r.saturday
      p <- persons if p.id === r.person_id
      g <- groups if g.id === p.group_id
      o <- organisations if o.id === g.organisation_id
    } yield o).groupBy(_.name).map {
      organisation => (organisation._1, organisation._2.length)
    }

    db.run(query.result)
  }

  def sorting: Future[Seq[(String, Int)]] = {
    val query = (for {
      r <- registrations if r.sorting
      p <- persons if p.id === r.person_id
      g <- groups if g.id === p.group_id
      o <- organisations if o.id === g.organisation_id
    } yield o).groupBy(_.name).map {
      organisation => (organisation._1, organisation._2.length)
    }

    db.run(query.result)
  }

  def selling: Future[Seq[(String, Int)]] = {
    val query = (for {
      r <- registrations if !r.category_id.isEmpty
      p <- persons if p.id === r.person_id
      g <- groups if g.id === p.group_id
      o <- organisations if o.id === g.organisation_id
    } yield o).groupBy(_.name).map {
      organisation => (organisation._1, organisation._2.length)
    }

    db.run(query.result)
  }

  def category: Future[Seq[(Option[UUID], Int)]] = {
    val query = registrations.groupBy(_.category_id).map {
      case (id, r) => (id, r.length)
    }

    db.run(query.result)
  }

  def group: Future[Map[UUID, Int]] = {
    val query = (for {
      r <- registrations
      p <- persons if p.id === r.person_id
      g <- groups if g.id === p.group_id
    } yield g).groupBy(_.id).map {
      case (i, r) => (i, r.length)
    }

    db.run(query.result).map(_.toMap)
  }

  def organisation(id: UUID): Future[Map[UUID, Int]] = {
    val query = (for {
      r <- registrations
      p <- persons if p.id === r.person_id
      g <- groups if g.id === p.group_id
      o <- organisations if o.id === p.group_id && o.id === id
    } yield g).groupBy(_.id).map {
      case (i, r) => (i, r.length)
    }

    db.run(query.result).map(_.toMap)
  }

  def organisation: Future[Map[UUID, Int]] = {
    val query = (for {
      r <- registrations
      p <- persons if p.id === r.person_id
      g <- groups if g.id === p.group_id
      o <- organisations if o.id === p.group_id
    } yield o).groupBy(_.id).map {
      case (i, r) => (i, r.length)
    }

    db.run(query.result).map(_.toMap)
  }
}

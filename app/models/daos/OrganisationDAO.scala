package models.daos

import models.daos.tables.DAOSlick
import models.{Group, Organisation}
import play.api.db.slick.DatabaseConfigProvider

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class OrganisationDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends DAOSlick {

  import profile.api._

  def all: Future[Seq[Organisation]] = db.run(organisations.result)

  def members(organisation_id: UUID): Future[Seq[Group]] = {
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

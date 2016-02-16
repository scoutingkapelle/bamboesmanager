package models.daos

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import models.User
import models.daos.tables.DAOSlick
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

class UserDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends IdentityService[User] with DAOSlick {

  import driver.api._

  def find(id: UUID): Future[Option[User]] = db.run(users.filter(_.id === id).result.headOption)

  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = db.run(users.filter(_.email === loginInfo.providerKey).result.headOption)

  def save(user: User): Future[User] = db.run(users.insertOrUpdate(user)).map(_ => user)
}
package models.daos

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import models.User
import models.daos.tables.DAOSlick
import play.api.db.slick.DatabaseConfigProvider

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends IdentityService[User] with DAOSlick {

  import profile.api._

  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = db.run(users.filter(_.email === loginInfo.providerKey).result.headOption)

  def save(user: User): Future[User] = db.run(users.insertOrUpdate(user)).map(_ => user)
}

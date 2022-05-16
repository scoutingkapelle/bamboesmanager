package models.daos

import io.github.honeycombcheesecake.play.silhouette.api.LoginInfo
import io.github.honeycombcheesecake.play.silhouette.api.util.PasswordInfo
import io.github.honeycombcheesecake.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import models.daos.tables.DAOSlick
import play.api.db.slick.DatabaseConfigProvider

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

class PasswordInfoDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                               (implicit val classTag: ClassTag[PasswordInfo], ec: ExecutionContext)
  extends DelegableAuthInfoDAO[PasswordInfo] with DAOSlick {

  import profile.api._

  def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
    val query = passwords.filter(_.email === loginInfo.providerKey)
    db.run(query.result.headOption).map { dbPasswordInfoOption =>
      dbPasswordInfoOption.map(dbPasswordInfo =>
        PasswordInfo(dbPasswordInfo.hash, dbPasswordInfo.password, dbPasswordInfo.salt))
    }
  }

  def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = save(loginInfo, authInfo)

  def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    val query = passwords.insertOrUpdate(loginInfo, authInfo)
    db.run(query).map(_ => authInfo)
  }

  def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = save(loginInfo, authInfo)

  def remove(loginInfo: LoginInfo): Future[Unit] = {
    val query = passwords.filter(_.email === loginInfo.providerKey)
    db.run(query.delete).map(_ => ())
  }
}

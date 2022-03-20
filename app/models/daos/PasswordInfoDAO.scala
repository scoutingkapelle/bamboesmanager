package models.daos

import javax.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import models.daos.tables.DAOSlick
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.reflect.ClassTag

class PasswordInfoDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit val classTag: ClassTag[PasswordInfo])
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
    val dbPasswordInfo = DBPasswordInfo(authInfo.hasher, authInfo.password, authInfo.salt, loginInfo.providerKey)
    val query = passwords.insertOrUpdate(dbPasswordInfo)
    db.run(query).map(_ => authInfo)
  }

  def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = save(loginInfo, authInfo)

  def remove(loginInfo: LoginInfo): Future[Unit] = {
    val query = passwords.filter(_.email === loginInfo.providerKey)
    db.run(query.delete).map(_ => ())
  }
}

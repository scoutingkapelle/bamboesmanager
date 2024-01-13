package utils

import models.User
import play.silhouette.api.Env
import play.silhouette.impl.authenticators.SessionAuthenticator

trait DefaultEnv extends Env {
  type I = User
  type A = SessionAuthenticator
}

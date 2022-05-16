package utils

import io.github.honeycombcheesecake.play.silhouette.api.Env
import io.github.honeycombcheesecake.play.silhouette.impl.authenticators.SessionAuthenticator
import models.User

trait DefaultEnv extends Env {
  type I = User
  type A = SessionAuthenticator
}

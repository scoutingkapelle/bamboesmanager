package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, LogoutEvent, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import forms._
import models.User
import play.api.i18n.MessagesApi

import scala.concurrent.Future

class Application @Inject()(val messagesApi: MessagesApi, val env: Environment[User, SessionAuthenticator])
  extends Silhouette[User, SessionAuthenticator] {

  def index = UserAwareAction.async { implicit request =>
    Future.successful(Ok(views.html.index(request.identity)))
  }

  def dashboard = SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.dashboard(request.identity)))
  }

  def signIn = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(_) => Future.successful(Redirect(routes.Application.index()))
      case None => Future.successful(Ok(views.html.signIn(SignInForm.form)))
    }
  }

  def signUp = SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.signUp(SignUpForm.form)))
  }

  def signOut = SecuredAction.async { implicit request =>
    val result = Redirect(routes.Application.index())
    env.eventBus.publish(LogoutEvent(request.identity, request, request2Messages))
    env.authenticatorService.discard(request.authenticator, result)
  }
}
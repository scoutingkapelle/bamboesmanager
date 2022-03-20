package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{LogoutEvent, Silhouette}
import forms.{SignInForm, SignUpForm}
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.DefaultEnv

import scala.concurrent.Future

class Application @Inject()(components: ControllerComponents,
                            silhouette: Silhouette[DefaultEnv],
                            indexTemplate: views.html.index,
                            dashboardTemplate: views.html.dashboard,
                            signInTemplate: views.html.signIn,
                            signUpTemplate: views.html.signUp)
  extends AbstractController(components) with I18nSupport {

  def index = silhouette.UserAwareAction { implicit request =>
    Ok(indexTemplate(request.identity))
  }

  def dashboard = silhouette.SecuredAction { implicit request =>
    Ok(dashboardTemplate(request.identity))
  }

  def signIn = silhouette.UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(_) => Future.successful(Redirect(routes.Application.index))
      case None => Future.successful(Ok(signInTemplate(SignInForm.form)))
    }
  }

  def signUp = silhouette.SecuredAction { implicit request =>
    Ok(signUpTemplate(SignUpForm.form))
  }

  def signOut = silhouette.SecuredAction.async { implicit request =>
    val result = Redirect(routes.Application.index)
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, result)
  }
}

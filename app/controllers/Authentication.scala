package controllers

import javax.inject.Inject
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import forms.SignInForm
import models.daos.UserDAO
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import utils.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class Authentication @Inject()(credentialsProvider: CredentialsProvider,
                               userDAO: UserDAO,
                               components: ControllerComponents,
                               silhouette: Silhouette[DefaultEnv],
                               signInTemplate: views.html.signIn)
  extends AbstractController(components) with I18nSupport {

  def authenticate(): Action[AnyContent] = Action.async { implicit request =>
    SignInForm.form.bindFromRequest().fold(
      form => Future.successful(BadRequest(signInTemplate(form))),
      data => {
        val credentials = Credentials(data.email, data.password)
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          val result = Redirect(routes.Application.dashboard())
          userDAO.retrieve(loginInfo).flatMap {
            case Some(user) =>
              silhouette.env.authenticatorService.create(loginInfo).flatMap { authenticator =>
                silhouette.env.eventBus.publish(LoginEvent(user, request))
                silhouette.env.authenticatorService.init(authenticator).flatMap { v =>
                  silhouette.env.authenticatorService.embed(v, result)
                }
              }
            case None => Future.failed(new IdentityNotFoundException(Messages("user.not_found")))
          }
        }.recover {
          case _: ProviderException =>
            Redirect(routes.Application.signIn()).flashing("error" -> Messages("invalid.credentials"))
        }
      }
    )
  }
}

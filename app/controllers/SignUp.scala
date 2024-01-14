package controllers

import forms.SignUpForm
import models.User
import models.daos.UserDAO
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import play.silhouette.api._
import play.silhouette.api.repositories.AuthInfoRepository
import play.silhouette.api.util.PasswordHasher
import play.silhouette.impl.providers._
import utils.DefaultEnv

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SignUp @Inject()(userDAO: UserDAO,
                       authInfoRepository: AuthInfoRepository,
                       passwordHasher: PasswordHasher,
                       components: ControllerComponents,
                       silhouette: Silhouette[DefaultEnv],
                       signUpTemplate: views.html.signUp)
                      (implicit ec: ExecutionContext)
  extends AbstractController(components) with I18nSupport {

  def signUp(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    SignUpForm.form.bindFromRequest().fold(
      form => Future.successful(BadRequest(signUpTemplate(form))),
      data => {
        val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
        userDAO.retrieve(loginInfo).flatMap {
          case Some(_) =>
            Future.successful(Redirect(routes.Application.signUp()).flashing("error" -> Messages("user.exists")))
          case None =>
            val authInfo = passwordHasher.hash(data.password)
            val user = User(
              id = UUID.randomUUID(),
              name = data.name,
              email = data.email
            )
            for {
              user <- userDAO.save(user)
              _ <- authInfoRepository.add(loginInfo, authInfo)
              authenticator <- silhouette.env.authenticatorService.create(loginInfo)
              value <- silhouette.env.authenticatorService.init(authenticator)
              result <- silhouette.env.authenticatorService.embed(value, Redirect(routes.Application.dashboard()))
            } yield {
              silhouette.env.eventBus.publish(SignUpEvent(user, request))
              silhouette.env.eventBus.publish(LoginEvent(user, request))
              result
            }
        }
      }
    )
  }
}

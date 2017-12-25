package controllers

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import models.daos._
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class Persons @Inject()(personDAO: PersonDAO,
                        components: ControllerComponents,
                        silhouette: Silhouette[DefaultEnv])
  extends AbstractController(components) with I18nSupport {

  def all = silhouette.SecuredAction.async {
    personDAO.all.map(persons => Ok(Json.toJson(persons)))
  }

  def get(id: String) = silhouette.SecuredAction.async { implicit request =>
    try {
      personDAO.get(UUID.fromString(id)).map {
        case Some(person) => Ok(Json.toJson(person))
        case None => NotFound(Json.toJson(Messages("person.not_found")))
      }
    } catch {
      case _: IllegalArgumentException => Future.successful(BadRequest(Json.toJson(Messages("uuid.invalid"))))
    }
  }
}
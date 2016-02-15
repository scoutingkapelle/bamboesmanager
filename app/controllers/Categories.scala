package controllers

import java.util.UUID
import javax.inject.Inject

import models.daos.CategoryDAO
import models.Category
import play.api.Play.current
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class Categories @Inject()(categoryDAO: CategoryDAO) extends Controller {
  implicit val categoryWrites = Json.writes[Category]

  def all = Action.async {
    categoryDAO.all.map(categories => Ok(Json.toJson(categories)))
  }

  def get(id: String) = Action.async {
    try {
      val uuid = UUID.fromString(id)
      categoryDAO.get(uuid).map {
        case Some(category) => Ok(Json.toJson(category))
        case None => NotFound(Json.toJson(Messages("cat.not_found")))
      }
    } catch {
      case _: IllegalArgumentException => Future(BadRequest(Json.toJson(Messages("uuid.invalid"))))
    }
  }
}
package controllers

import javax.inject.Inject
import io.github.honeycombcheesecake.play.silhouette.api.Silhouette
import models._
import models.daos._
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import utils.DefaultEnv

import scala.collection.immutable.ListMap
import scala.concurrent.ExecutionContext

class Statistics @Inject()(statisticsDAO: StatisticsDAO,
                           organisationDAO: OrganisationDAO,
                           components: ControllerComponents,
                           silhouette: Silhouette[DefaultEnv],
                           statisticsTemplate: views.html.statistics)
                          (implicit ec: ExecutionContext)
  extends AbstractController(components) with I18nSupport {

  def statistics(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    for {
      friday <- statisticsDAO.friday
      saturday <- statisticsDAO.saturday
      sorting <- statisticsDAO.sorting
      selling <- statisticsDAO.selling
      organisations <- organisationDAO.all
    } yield {
      val statistics = ListMap(
        Messages("friday") -> fill(organisations, friday),
        Messages("saturday") -> fill(organisations, saturday),
        Messages("sorting") -> fill(organisations, sorting),
        Messages("selling") -> fill(organisations, selling)
      )
      Ok(statisticsTemplate(statistics, request.identity))
    }
  }

  private def fill(organisations: Seq[Organisation], statistic: Map[String, Int]): Map[String, Int] =
    organisations.map(organisation => (organisation.name, statistic.getOrElse(organisation.name, 0))).toMap

  def friday(): Action[AnyContent] = silhouette.SecuredAction.async {
    statisticsDAO.friday.map(statistics => Ok(Json.toJson(statistics)))
  }

  def saturday(): Action[AnyContent] = silhouette.SecuredAction.async {
    statisticsDAO.saturday.map(statistics => Ok(Json.toJson(statistics)))
  }

  def sorting(): Action[AnyContent] = silhouette.SecuredAction.async {
    statisticsDAO.sorting.map(statistics => Ok(Json.toJson(statistics)))
  }

  def selling(): Action[AnyContent] = silhouette.SecuredAction.async {
    statisticsDAO.selling.map(statistics => Ok(Json.toJson(statistics)))
  }
}

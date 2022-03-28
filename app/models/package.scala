import com.mohiva.play.silhouette.api.Identity
import play.api.libs.json.{Json, Writes}

import java.util.UUID

package object models {

  case class Person(id: UUID, name: String, email: String, age: Int, group: Group)

  case class Organisation(id: UUID, name: String)

  case class Group(id: UUID, name: String, organisation: Organisation)

  case class Category(id: UUID, name: String)

  case class Registration(id: UUID, person: Person, friday: Boolean, saturday: Boolean, sorting: Boolean, category: Option[Category], teamLeader: Boolean)

  case class User(id: UUID, name: String, email: String) extends Identity

  implicit val organisationWrites: Writes[Organisation] = Json.writes[Organisation]
  implicit val groupWrites: Writes[Group] = Json.writes[Group]
  implicit val personWrites: Writes[Person] = Json.writes[Person]
  implicit val categoryWrites: Writes[Category] = Json.writes[Category]
  implicit val registrationWrites: Writes[Registration] = Json.writes[Registration]

  def categoriesTupled(categories: Seq[Category]): Seq[(String, String)] =
    categories.sortBy(_.name).map(category => (category.id.toString, category.name))

  def groupsTupled(groups: Seq[Group]): Seq[(String, String)] =
    groups.sortBy(group => (group.organisation.name, group.name)).map { group =>
      (group.organisation.id.toString + "#" + group.id.toString, group.organisation.name + ": " + group.name)
    }

  def organisationsTupled(organisations: Seq[Organisation]): Seq[(String, String)] =
    organisations.sortBy(_.name).map(organisation => (organisation.id.toString, organisation.name))
}

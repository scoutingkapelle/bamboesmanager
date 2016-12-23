import play.api.libs.json.Json

package object models {

  implicit val organisationWrites = Json.writes[Organisation]
  implicit val groupWrites = Json.writes[Group]
  implicit val personWrites = Json.writes[Person]
  implicit val categoryWrites = Json.writes[Category]
  implicit val registrationWrites = Json.writes[Registration]

  def categoriesTupled(categories: Seq[Category]) =
    categories.sortBy(_.name).map(category => (category.id.toString, category.name))

  def groupsTupled(groups: Seq[Group]) =
    groups.sortBy(_.name).map(group =>
      (group.organisation.id + "#" + group.id.toString, group.organisation.name + ": " + group.name))

  def organisationsTupled(organisations: Seq[Organisation]) =
    organisations.sortBy(_.name).map(organisation => (organisation.id.toString, organisation.name))

  def fullName(firstName: String, prefix: String, surName: String) =
    (firstName.toLowerCase.split(" ").map(_.capitalize).mkString(" ") + " " +
      prefix.toLowerCase + " " +
      surName.toLowerCase.split(" ").map(_.capitalize).mkString(" ")).trim.replaceAll("( )+", " ")
}
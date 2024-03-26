package models.daos.tables

import models.{Category, Group, Organisation, Person, Registration}
import slick.jdbc.JdbcProfile

import java.util.UUID
import scala.language.implicitConversions

trait TableDefinitions {
  protected val profile: JdbcProfile

  import profile.api._

  val categories = TableQuery[CategoryTable]
  val groups = TableQuery[GroupTable]
  val organisations = TableQuery[OrganisationTable]
  val persons = TableQuery[PersonTable]
  val registrations = TableQuery[RegistrationTable]

  class CategoryTable(tag: Tag) extends Table[Category](tag, "categories") {
    def * = (id, name, second_choice) <> (Category.tupled, Category.unapply)

    def id = column[UUID]("id", O.PrimaryKey)

    def name = column[String]("name")

    def second_choice = column[Boolean]("second_choice")
  }

  class GroupTable(tag: Tag) extends Table[DBGroup](tag, "groups") {
    def * = (id, name, organisation_id) <> (DBGroup.tupled, DBGroup.unapply)

    def id = column[UUID]("id", O.PrimaryKey)

    def name = column[String]("name")

    def organisation_id = column[UUID]("organisation_id")

    def organisation = foreignKey("organisation_fk", organisation_id, organisations)(_.id)
  }

  case class DBGroup(id: UUID, name: String, organisation_id: UUID)

  implicit def toDBGroup(group: Group): DBGroup = DBGroup(group.id, group.name, group.organisation.id)

  class OrganisationTable(tag: Tag) extends Table[Organisation](tag, "organisations") {
    def * = (id, name) <> (Organisation.tupled, Organisation.unapply)

    def id = column[UUID]("id", O.PrimaryKey)

    def name = column[String]("name")
  }

  class PersonTable(tag: Tag) extends Table[DBPerson](tag, "persons") {
    def * = (id, name, email, age, group_id) <> (DBPerson.tupled, DBPerson.unapply)

    def name = column[String]("name")

    def email = column[String]("email")

    def age = column[Int]("age")

    def id = column[UUID]("id", O.PrimaryKey)

    def group = foreignKey("group_fk", group_id, organisations)(_.id)

    def group_id = column[UUID]("group_id")
  }

  case class DBPerson(id: UUID, name: String, email: String, age: Int, group_id: UUID)

  implicit def toDBPerson(person: Person): DBPerson = DBPerson(person.id, person.name, person.email, person.age, person.group.id)

  class RegistrationTable(tag: Tag) extends Table[DBRegistration](tag, "registrations") {
    def * = (id, person_id, friday, saturday, sorting, category_id, second_choice_id, team_leader) <> (DBRegistration.tupled, DBRegistration.unapply)

    def friday = column[Boolean]("friday")

    def saturday = column[Boolean]("saturday")

    def sorting = column[Boolean]("sorting")

    def id = column[UUID]("id", O.PrimaryKey)

    def person_id = column[UUID]("person_id")

    def team_leader = column[Boolean]("team_leader")

    def category_id = column[Option[UUID]]("category_id")

    def second_choice_id = column[Option[UUID]]("second_choice_id")

    def category = foreignKey("category_fk", category_id, categories)(_.id.?)

    def person = foreignKey("person_fk", person_id, persons)(_.id)
  }

  case class DBRegistration(id: UUID, person_id: UUID, friday: Boolean, saturday: Boolean, sorting: Boolean, category_id: Option[UUID], second_choice_id: Option[UUID], team_leader: Boolean)

  implicit def toDBRegistration(registration: Registration): DBRegistration =
    DBRegistration(
      registration.id,
      registration.person.id,
      registration.friday,
      registration.saturday,
      registration.sorting,
      registration.category.map(_.id),
      registration.secondChoice.map(_.id),
      registration.teamLeader
    )
}

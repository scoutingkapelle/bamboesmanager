package models.daos.tables

import slick.driver.JdbcProfile
import play.api.db.slick.HasDatabaseConfigProvider

trait DAOSlick extends DBTableDefinitions with HasDatabaseConfigProvider[JdbcProfile]
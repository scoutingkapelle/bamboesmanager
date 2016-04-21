name := "bamboesmanager"

version := "0.5.4"

lazy val `bamboesmanager` = (project in file(".")).enablePlugins(PlayScala, RpmPlugin)

routesGenerator := InjectedRoutesGenerator

scalaVersion := "2.11.8"

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  cache, ws, specs2 % Test, evolutions, filters,
  "com.typesafe.play" %% "play-slick" % "1.1.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.1.1",
  "com.h2database" % "h2" % "1.4.191",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "org.webjars" %% "webjars-play" % "2.4.0-1",
  "org.webjars" % "bootstrap" % "3.3.6" exclude("org.webjars", "jquery"),
  "org.webjars" % "bootstrap-table" % "1.9.1" exclude("org.webjars", "jquery"),
  "org.webjars" % "bootstrap-switch" % "3.3.2" exclude("org.webjars", "jquery"),
  "org.webjars.bower" % "tableExport.jquery.plugin" % "1.1.4" exclude("org.webjars.bower", "jquery") exclude("org.webjars.bower", "jspdf") exclude("org.webjars.bower", "jspdf-autotable") exclude("org.webjars.bower", "file-saver.js"),
  "org.webjars" % "jquery" % "1.9.1",
  "com.adrianhurt" %% "play-bootstrap3" % "0.4.4-P24" exclude("org.webjars", "bootstrap") exclude("org.webjars", "jquery"),
  "com.mohiva" %% "play-silhouette" % "3.0.4",
  "com.mohiva" %% "play-silhouette-testkit" % "3.0.4" % "test",
  "net.codingwell" %% "scala-guice" % "4.0.1",
  "net.ceedubs" %% "ficus" % "1.1.2",
  "com.typesafe.play" %% "play-mailer" % "4.0.0-M1"
)

javaOptions in Universal ++= Seq(
  s"-Dpidfile.path=/var/run/${packageName.value}/play.pid",
  s"-Dconfig.file=/usr/share/${packageName.value}/conf/production.conf"
)

rpmVendor := "wjglerum.nl"

rpmRelease := "1"

rpmLicense := Some("MIT")

rpmGroup := Some("Scouting Kapelle")

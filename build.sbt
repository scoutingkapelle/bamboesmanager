name := "bamboesmanager"

version := "0.6.0-SNAPSHOT"

lazy val `bamboesmanager` = (project in file(".")).enablePlugins(PlayScala)

routesGenerator := InjectedRoutesGenerator

scalaVersion := "2.13.8"

resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  ehcache, ws, specs2 % Test, evolutions, filters, guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
  "com.typesafe.play" %% "play-json" % "2.9.2",
  "com.h2database" % "h2" % "2.1.210",
  "org.postgresql" % "postgresql" % "42.3.3",
  "org.webjars" %% "webjars-play" % "2.8.13",
  "org.webjars" % "bootstrap" % "5.1.3" exclude("org.webjars", "jquery"),
  "org.webjars" % "bootstrap-table" % "1.16.0" exclude("org.webjars", "jquery"),
  "org.webjars" % "bootstrap-switch" % "3.3.4" exclude("org.webjars", "jquery"),
  "org.webjars.bower" % "tableExport.jquery.plugin" % "1.9.3" exclude("org.webjars.bower", "jquery") exclude("org.webjars.bower", "jspdf") exclude("org.webjars.bower", "jspdf-autotable") exclude("org.webjars.bower", "file-saver.js") exclude("org.webjars.bower", "html2canvas"),
  "org.webjars" % "jquery" % "1.12.4",
  "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P28-B3" exclude("org.webjars", "bootstrap") exclude("org.webjars", "jquery"),
  "com.mohiva" %% "play-silhouette" % "7.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "7.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "7.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "7.0.0",
  "com.mohiva" %% "play-silhouette-testkit" % "7.0.0" % "test",
  "net.codingwell" %% "scala-guice" % "5.0.2",
  "com.iheart" %% "ficus" % "1.5.2",
  "com.typesafe.play" %% "play-mailer" % "8.0.1",
  "com.typesafe.play" %% "play-mailer-guice" % "8.0.1"
)

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-deprecation",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard"
)

Global / onChangedBuildSource := ReloadOnSourceChanges

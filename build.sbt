name := "bamboesmanager"

version := "0.6.0-SNAPSHOT"

lazy val `bamboesmanager` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.16"

libraryDependencies ++= Seq(
  ehcache, ws, specs2 % Test, evolutions, filters, guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,
  "org.playframework" %% "play-slick" % "6.2.0",
  "org.playframework" %% "play-slick-evolutions" % "6.2.0",
  "org.playframework" %% "play-json" % "3.0.4",
  "org.playframework" %% "play-mailer" % "10.1.0",
  "org.playframework" %% "play-mailer-guice" % "10.1.0",
  "com.h2database" % "h2" % "2.3.232",
  "org.postgresql" % "postgresql" % "42.7.6",
  "org.webjars" %% "webjars-play" % "3.0.2",
  "org.webjars" % "bootstrap" % "3.4.1" exclude("org.webjars", "jquery"),
  "org.webjars" % "bootstrap-table" % "1.16.0" exclude("org.webjars", "jquery"),
  "org.webjars" % "bootstrap-switch" % "3.3.4" exclude("org.webjars", "jquery"),
  "org.webjars.bower" % "tableExport.jquery.plugin" % "1.9.3" exclude("org.webjars.bower", "jquery") exclude("org.webjars.bower", "jspdf") exclude("org.webjars.bower", "jspdf-autotable") exclude("org.webjars.bower", "file-saver.js") exclude("org.webjars.bower", "html2canvas"),
  "org.webjars" % "jquery" % "1.12.4",
  "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P28-B3" exclude("org.webjars", "bootstrap") exclude("org.webjars", "jquery"),
  "org.playframework.silhouette" %% "play-silhouette" % "10.0.3",
  "org.playframework.silhouette" %% "play-silhouette-persistence" % "10.0.3",
  "org.playframework.silhouette" %% "play-silhouette-crypto-jca" % "10.0.3",
  "org.playframework.silhouette" %% "play-silhouette-password-bcrypt" % "10.0.3",
  "org.playframework.silhouette" %% "play-silhouette-testkit" % "10.0.3" % "test",
  "net.codingwell" %% "scala-guice" % "6.0.0",
  "com.iheart" %% "ficus" % "1.5.2",
)

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-deprecation",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Werror",
  "-Xlint",
)

// https://stackoverflow.com/questions/37413032/ywarn-unused-import-triggering-on-play-routes-file
scalacOptions ++= Seq(
  "-Wconf:cat=unused-imports&site=.*views.html.*:s", // Silence import warnings in Play html files
  "-Wconf:cat=unused-imports&site=<empty>:s", // Silence import warnings on Play `routes` files
  "-Wconf:cat=unused-imports&site=router:s", // Silence import warnings on Play `routes` files
)

Global / onChangedBuildSource := ReloadOnSourceChanges

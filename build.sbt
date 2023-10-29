name := "bamboesmanager"

version := "0.6.0-SNAPSHOT"

lazy val `bamboesmanager` = (project in file(".")).enablePlugins(PlayScala)

routesGenerator := InjectedRoutesGenerator

scalaVersion := "2.13.12"

resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  ehcache, ws, specs2 % Test, evolutions, filters, guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
  "com.typesafe.play" %% "play-slick" % "5.1.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.1.0",
  "org.playframework" %% "play-json" % "3.0.0",
  "com.h2database" % "h2" % "2.2.224",
  "org.postgresql" % "postgresql" % "42.6.0",
  "org.webjars" %% "webjars-play" % "2.9.0",
  "org.webjars" % "bootstrap" % "3.4.1" exclude("org.webjars", "jquery"),
  "org.webjars" % "bootstrap-table" % "1.16.0" exclude("org.webjars", "jquery"),
  "org.webjars" % "bootstrap-switch" % "3.3.4" exclude("org.webjars", "jquery"),
  "org.webjars.bower" % "tableExport.jquery.plugin" % "1.9.3" exclude("org.webjars.bower", "jquery") exclude("org.webjars.bower", "jspdf") exclude("org.webjars.bower", "jspdf-autotable") exclude("org.webjars.bower", "file-saver.js") exclude("org.webjars.bower", "html2canvas"),
  "org.webjars" % "jquery" % "1.12.4",
  "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P28-B3" exclude("org.webjars", "bootstrap") exclude("org.webjars", "jquery"),
  "io.github.honeycomb-cheesecake" %% "play-silhouette" % "8.0.2",
  "io.github.honeycomb-cheesecake" %% "play-silhouette-persistence" % "8.0.2",
  "io.github.honeycomb-cheesecake" %% "play-silhouette-crypto-jca" % "8.0.2",
  "io.github.honeycomb-cheesecake" %% "play-silhouette-password-bcrypt" % "8.0.2",
  "io.github.honeycomb-cheesecake" %% "play-silhouette-testkit" % "8.0.2" % "test",
  "net.codingwell" %% "scala-guice" % "7.0.0",
  "com.iheart" %% "ficus" % "1.5.2",
  "com.typesafe.play" %% "play-mailer" % "9.0.0",
  "com.typesafe.play" %% "play-mailer-guice" % "9.0.0"
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

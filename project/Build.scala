import sbt._
import Keys._

object Play2MorphiaPluginBuild extends Build {

  import Resolvers._
  import Dependencies._
  import BuildSettings._

  lazy val Play2MorphiaPlugin = Project(
    "play2-morphia-plugin",
    file("."),
    settings = buildSettings ++ Seq(
      libraryDependencies := runtime ++ test,
      publishMavenStyle := true,
      publishTo := {
        if (buildVersion.trim.endsWith("SNAPSHOT"))
          Some(dropboxSnapshotRepository)
        else
          Some(dropboxReleaseRepository)
      },
      scalacOptions ++= Seq("-Xlint", "-deprecation", "-unchecked", "-encoding", "utf8"),
      javacOptions ++= Seq("-source", "1.6", "-encoding", "utf8"),
      resolvers ++= Seq(DefaultMavenRepository, Resolvers.typesafeRepository),
      checksums := Nil // To prevent proxyToys downloding fails https://github.com/leodagdag/play2-morphia-plugin/issues/11
    )
  ).settings()

  object Resolvers {
    val githubRepository = Resolver.file("GitHub Repository", Path.userHome / "dev" / "leodagdag.github.com" / "repository" asFile)(Resolver.ivyStylePatterns)
    val dropboxReleaseRepository = Resolver.file("Dropbox Repository", Path.userHome / "Dropbox" / "Public" / "repository" / "releases" asFile)
    val dropboxSnapshotRepository = Resolver.file("Dropbox Repository", Path.userHome / "Dropbox" / "Public" / "repository" / "snapshots" asFile)
    val typesafeRepository = "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
  }

  object Dependencies {
    val runtime = Seq(
      "org.mongodb.morphia" % "morphia" % "0.105",
      "org.mongodb.morphia" % "morphia-logging-slf4j" % "0.105",
      "org.mongodb.morphia" % "morphia-validation" % "0.105",
      "org.mongodb" % "mongo-java-driver" % "2.11.0",
      "play" %% "play-java" % "2.1.0" % "provided"
    )
    val test = Seq(
      "play" %% "play-test" % "2.1.0" % "test"
    )
  }

  object BuildSettings {
    val buildOrganization = "leodagdag"
    val buildVersion = "0.0.15-SNAPSHOT"
    val buildScalaVersion = "2.10.0"
    val buildSbtVersion = "0.12.2"
    val buildSettings = Defaults.defaultSettings ++ Seq(
      organization := buildOrganization,
      version := buildVersion,
      scalaVersion := buildScalaVersion
    )
  }
}

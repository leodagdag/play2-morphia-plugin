import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "yabe"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
       "leodagdag" %% "play2-morphia-plugin" % "0.0.14"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
        resolvers += "LeoDagDag repository" at "http://leodagdag.github.com/repository/",
        checksums := Nil
    )   
}

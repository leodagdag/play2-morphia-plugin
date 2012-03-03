logLevel := Level.Warn

resolvers += Classpaths.typesafeResolver

resolvers += Resolver.url("scalasbt", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

// Git in SBT
addSbtPlugin("com.jsuereth" % "sbt-git-plugin" % "0.4")

//SBT Scala format
addSbtPlugin("com.typesafe.sbtscalariform" % "sbtscalariform" % "0.3.0")

// Generate eclipse project
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.0.0")
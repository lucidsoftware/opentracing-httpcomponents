lazy val httpcore = project

lazy val `httpclient-active` = project.dependsOn(httpcore)

inScope(Global)(Seq(
  autoScalaLibrary := false,
  crossPaths := false
))

lazy val httpcore = project

lazy val httpclient = project.dependsOn(httpcore)

lazy val httpasyncclient = project.dependsOn(httpclient)

inScope(Global)(Seq(
  autoScalaLibrary := false,
  credentials += Credentials(
    "Sonatype Nexus Repository Manager",
    "oss.sonatype.org",
    sys.env.getOrElse("SONATYPE_USERNAME", ""),
    sys.env.getOrElse("SONATYPE_PASSWORD", ""
  )),
  crossPaths := false,
  developers += Developer("pauldraper", "Paul Draper", "paulddraper@gmail.com", url("https://github.com/pauldraper")),
  homepage := Some(url("https://git.lucidchart.com/lucidsoftware/opentracing-httpcomponents")),
  licenses += "Apache 2.0 License" -> url("https://www.apache.org/licenses/LICENSE-2.0"),
  organization := "com.lucidchart",
  organizationHomepage := Some(url("http://opentracing.io/")),
  organizationName := "OpenTracing",
  PgpKeys.pgpPassphrase := Some(Array.emptyCharArray),
  resolvers += Resolver.typesafeRepo("releases"),
  scmInfo := Some(ScmInfo(
    url("https://github.com/lucidsoftware/opentracing-httpcomponents"),
    "scm:git:git@github.com:lucidsoftware/opentracing-httpcomponents.git"
  )),
  startYear := Some(2017),
  version := sys.props.getOrElse("build.version", "0-SNAPSHOT")
))

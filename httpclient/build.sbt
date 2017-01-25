libraryDependencies ++= Seq(
  "io.opentracing" % "opentracing-api" % "0.20.7",
  "io.opentracing.contrib" % "opentracing-globaltracer" % "0.1.0",
  "io.opentracing.contrib" % "opentracing-spanmanager" % "0.0.1",
  "org.apache.httpcomponents" % "httpclient" % "4.5.2"
)

moduleName := s"opentracing-$name"

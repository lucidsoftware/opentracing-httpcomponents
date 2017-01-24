libraryDependencies ++= Seq(
  "io.opentracing.contrib" % "opentracing-globaltracer" % "0.0.1",
  "io.opentracing.contrib" % "opentracing-spanmanager" % "0.0.1"
)

moduleName := s"opentracing-${name.value}"

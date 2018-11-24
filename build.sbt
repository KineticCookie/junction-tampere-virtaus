enablePlugins(DockerPlugin)

name := "junction-backend"

version := IO.read(file("version"))

scalaVersion := "2.12.7"

resolvers += Resolver.bintrayRepo("monsanto", "maven")

scalacOptions += "-Ypartial-unification"

libraryDependencies := Dependencies.all

dockerfile in docker := {
  val jarFile: File = sbt.Keys.`package`.in(Compile, packageBin).value
  val classpath = (dependencyClasspath in Compile).value
  val dockerFilesLocation = baseDirectory.value / "src/main/docker/"

  new sbtdocker.Dockerfile {
    // Base image
    from("openjdk:8-jre-slim")

    add(dockerFilesLocation, "/app/")
    run("chmod", "+x", "/app/start.sh")
    
    // Add all files on the classpath
    add(classpath.files, "/app/lib/")
    // Add the JAR file
    add(jarFile, "/app/main.jar")

    cmd("/app/start.sh")
  }
}

imageNames in docker := Seq(
  ImageName(s"kineticcookie/junction-backend:${version.value}"),
  ImageName(s"kineticcookie/junction-backend:latest"),
)
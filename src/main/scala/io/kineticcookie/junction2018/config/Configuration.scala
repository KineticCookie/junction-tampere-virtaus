package io.kineticcookie.junction2018.config

case class HttpConfig(port: Int = 9090)

case class DbConfig(
  driverClassname: String = "org.postgresql.Driver",
  jdbcUrl: String = "jdbc:postgresql://localhost:5432/docker",
  username: String = "docker",
  password: String = "docker",
  maximumPoolSize: Int = 16
)

case class ServiceConfig(
  router: String = "localhost:9092"
)

case class Configuration(
  http: HttpConfig = HttpConfig(),
  db: DbConfig = DbConfig(),
  services: ServiceConfig = ServiceConfig()
) {
  implicit val httpImpl = http
  implicit val dbImpl = db
  implicit val servicesImpl = services
}

object Configuration {
  def loadOrFail = {
    pureconfig.loadConfig[Configuration].right.get
  }
}
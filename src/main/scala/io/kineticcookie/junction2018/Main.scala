package io.kineticcookie.junction2018

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import cats.effect.IO
import com.typesafe.scalalogging.LazyLogging
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.util.transactor.Transactor
import io.kineticcookie.junction2018.config.Configuration
import io.kineticcookie.junction2018.http.{Server, UserController}
import io.kineticcookie.junction2018.repository._
import io.kineticcookie.junction2018.service._
import org.flywaydb.core.Flyway
import cats.implicits._

import scala.concurrent.ExecutionContext

object Main extends App with LazyLogging {
  logger.info("Junction 2018 backend.")

  implicit val config = Configuration.loadOrFail

  import config._

  implicit val ec = ExecutionContext.global

  val hikariConfig = new HikariConfig()
  hikariConfig.setJdbcUrl(db.jdbcUrl)
  hikariConfig.setUsername(db.username)
  hikariConfig.setPassword(db.password)
  hikariConfig.setDriverClassName(db.driverClassname)
  hikariConfig.setMaximumPoolSize(db.maximumPoolSize)
  hikariConfig.setInitializationFailTimeout(20000)

  val dataSource = new HikariDataSource(hikariConfig)

  val flyway = new Flyway()
  flyway.setDataSource(dataSource)
  flyway.migrate()

  implicit val cs = IO.contextShift(ec)

  implicit val t = Transactor.fromDataSource[IO](dataSource, ec, ec)

  implicit val busRepository = new BusRepositoryImpl[IO]()
  implicit val userRepository = new UserRepositoryImpl[IO]()
  implicit val waypointRepository = new WaypointRepositoryImpl[IO]()

  implicit val busService = new BusServiceImpl[IO]()
  implicit val userService = new UserServiceImpl[IO]()
  implicit val routingService = new RoutingServiceImpl[IO]()

  implicit val actorSystem = ActorSystem()
  implicit val actorMaterializer = ActorMaterializer()

  implicit val userController = new UserController[IO]()

  val server = new Server()
}
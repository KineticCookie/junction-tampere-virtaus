package io.kineticcookie.junction2018.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.stream.ActorMaterializer
import io.kineticcookie.junction2018.config.HttpConfig
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import com.typesafe.scalalogging.LazyLogging
import io.circe.syntax._
import io.kineticcookie.junction2018.api.exceptions.NotFound

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

class Server[F[_]]()(
  implicit actorSystem: ActorSystem,
  httpConfig: HttpConfig,
  executionContext: ExecutionContext,
  userController: UserController[F]
) extends LazyLogging {
  implicit val mat = ActorMaterializer()

  Http().bindAndHandle(routes, "0.0.0.0", httpConfig.port)

  def routes = handleExceptions(commonExceptionHandler) {
    pathPrefix("api") {
      pathPrefix("v1") {
        userController.routes
      }
    }
  }

  def commonExceptionHandler = ExceptionHandler {
    case p: NotFound =>
      complete(
        HttpResponse(
          StatusCodes.NotFound,
          entity = Map(
            "errorMsg" -> Option(p.getMessage).getOrElse("Oops")
          ).asJson.noSpaces
        )
      )
    case p: NotImplementedError =>
      complete(
        HttpResponse(
          StatusCodes.NotImplemented,
          entity = Map(
            "errorMsg" -> "The API endpoint is not yet implemented"
          ).asJson.noSpaces
        )
      )
    case NonFatal(p) =>
      logger.error(p.getMessage, p)
      complete(
        HttpResponse(
          StatusCodes.InternalServerError,
          entity = Map(
            "errorMsg" -> Option(p.getMessage).getOrElse("Oops")
          ).asJson.noSpaces
        )
      )
  }
}
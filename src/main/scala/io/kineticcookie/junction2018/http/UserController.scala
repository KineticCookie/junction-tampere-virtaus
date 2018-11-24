package io.kineticcookie.junction2018.http

import cats.effect.Effect
import cats.effect.syntax.effect._
import akka.http.scaladsl.server.Directives._
import io.circe.generic.auto._
import io.kineticcookie.junction2018.repository.{User, Waypoint}
import io.kineticcookie.junction2018.service.UserService

class UserController[F[_]: Effect]()(
  implicit userService: UserService[F]
) extends ApiController {

  val routes = pathPrefix("user") {
    pathEndOrSingleSlash {
      get {
        complete {
          userService.listUsers().toIO.unsafeToFuture()
        }
      } ~
      post {
        entity(as[User]) { user =>
          complete {
            userService.createUser(user).toIO.unsafeToFuture()
          }
        }
      }
    } ~
    pathPrefix(Segment / "waypoint") { userId =>
      get {
        complete {
          userService.getWaypoint(userId).toIO.unsafeToFuture()
        }
      } ~
      post {
        entity(as[Waypoint]) { waypoint =>
          complete {
            userService.addWaypoint(waypoint).toIO.unsafeToFuture()
          }
        }
      }
    }
  }
}

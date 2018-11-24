package io.kineticcookie.junction2018.service

import cats.syntax.functor._
import cats.syntax.flatMap._
import io.kineticcookie.junction2018.api.exceptions.NotFound
import io.kineticcookie.junction2018.repository.{User, UserRepository, Waypoint, WaypointRepository}
import io.kineticcookie.junction2018.util.Aliases.ThrowableMonad

trait UserService[F[_]] {
  def getUser(name: String): F[User]
  def listUsers(): F[Seq[User]]
  def createUser(user: User): F[Int]

  def getWaypoint(userId: String): F[Waypoint]
  def addWaypoint(waypoint: Waypoint): F[Int]
}

class UserServiceImpl[F[_]: ThrowableMonad]()(
  implicit userRepository: UserRepository[F],
  waypointRepository: WaypointRepository[F]
) extends UserService[F] {
  override def getUser(name: String): F[User] = {
    for {
      maybeUser <- userRepository.search(name)
      user <- ThrowableMonad[F].fromEither(maybeUser.toRight(new NotFound(classOf[User], name)))
    } yield user
  }

  def getUserById(id: String): F[User] = {
    for {
      maybeUser <- userRepository.get(id)
      user <- ThrowableMonad[F].fromEither(maybeUser.toRight(new NotFound(classOf[User], id)))
    } yield user
  }

  override def getWaypoint(userId: String): F[Waypoint] = {
    for {
      _ <- getUserById(userId)
      r <- waypointRepository.get(userId)
      rr <- ThrowableMonad[F].fromEither(r.toRight(new NotFound(classOf[Waypoint], userId)))
    } yield rr
  }

  override def listUsers(): F[Seq[User]] = {
    userRepository.list()
  }

  override def createUser(user: User): F[Int] = {
    userRepository.add(user)
  }

  override def addWaypoint(waypoint: Waypoint): F[Int] = {
    waypointRepository.add(waypoint)
  }
}
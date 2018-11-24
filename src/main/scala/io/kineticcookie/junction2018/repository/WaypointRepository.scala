package io.kineticcookie.junction2018.repository

import cats.Monad
import doobie._
import doobie.implicits._

case class Waypoint(userId: String, x: Double, y: Double)

trait WaypointRepository[F[_]] {
  def add(waypoint: Waypoint): F[Int]
  def get(userId: String): F[Option[Waypoint]]
  def delete(userId: String): F[Int]
}

class WaypointRepositoryImpl[F[_]: Monad]()(
  implicit tx: Transactor[F]
) extends WaypointRepository[F] {
  override def add(waypoint: Waypoint): F[Int] = {
    val q = sql"INSERT INTO waypoints(user_id, x, y) VALUES(${waypoint.userId}, ${waypoint.x}, ${waypoint.y})".update
    q.run.transact(tx)
  }

  override def get(userId: String): F[Option[Waypoint]] = {
    val q = sql"SELECT user_id, x, y FROM waypoints WHERE user_id = $userId".query[Waypoint].option
    q.transact(tx)
  }

  override def delete(userId: String): F[Int] = {
    val q = sql"DELETE FROM waypoints WHERE user_id = $userId".update
    q.run.transact(tx)
  }
}
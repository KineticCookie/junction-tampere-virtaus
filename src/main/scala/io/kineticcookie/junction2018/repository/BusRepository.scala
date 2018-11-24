package io.kineticcookie.junction2018.repository

import cats.Monad
import doobie._
import doobie.implicits._

case class Bus(id: String, x: Double, y: Double)

trait BusRepository[F[_]] {
  def get(id: String): F[Option[Bus]]
  def add(bus: Bus): F[Int]
  def update(id: String, bus: Bus): F[Int]
}

class BusRepositoryImpl[F[_]: Monad]()(
  implicit tx: Transactor[F]
) extends BusRepository[F] {
  override def get(id: String): F[Option[Bus]] = {
    val q = sql"SELECT id, x, y FROM buses WHERE id = $id".query[Bus].option
    q.transact(tx)
  }

  override def add(bus: Bus): F[Int] = {
    val q = sql"INSERT INTO buses(id, x, y) VALUES(${bus.id}, ${bus.x}), ${bus.y}".update
    q.run.transact(tx)
  }

  override def update(id: String, bus: Bus): F[Int] = {
    val q = sql"UPDATE buses set x = ${bus.x}, y = ${bus.y} WHERE id = ${bus.id}".update
    q.run.transact(tx)
  }
}
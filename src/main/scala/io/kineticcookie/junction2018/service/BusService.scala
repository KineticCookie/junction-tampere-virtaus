package io.kineticcookie.junction2018.service

import java.util.UUID
import io.kineticcookie.junction2018.repository.{Bus, BusRepository}

trait BusService[F[_]] {
  def listBuses(): F[Seq[Bus]]
  def moveBus(id: UUID, x: Long, y: Long): F[Unit]
}

class BusServiceImpl[F[_]]()(
  implicit val busRepository: BusRepository[F]
) {
  def moveBus(id: UUID, x: Long, y: Long) = {
    busRepository.update(id.toString, Bus(id.toString, x, y))
  }
}
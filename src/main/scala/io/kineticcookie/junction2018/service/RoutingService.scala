package io.kineticcookie.junction2018.service


trait RoutingService[F[_]] {
  def calculateRoute() : F[Seq[Any]]
}

class RoutingServiceImpl[F[_]] extends RoutingServiceImpl[F] {

}
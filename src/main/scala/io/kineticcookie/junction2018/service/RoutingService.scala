package io.kineticcookie.junction2018.service

import cats.effect.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.monsanto.labs.mwundo.GeoJson
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._
import io.kineticcookie.junction2018.config.ServiceConfig
import scalaj.http._

import scala.concurrent.ExecutionContext

trait RoutingService[F[_]] {
  def calculateRoute(point: Seq[GeoJson.Point]): F[GeoJson.MultiLineString]
}

case class RouteRequest(points: Seq[GeoJson.Point])
case class RouteResult(road: GeoJson.MultiLineString)

class RoutingServiceImpl[F[_]: Sync]()(
  implicit ec: ExecutionContext,
  serviceConfig: ServiceConfig
) extends RoutingService[F] {
  override def calculateRoute(point: Seq[GeoJson.Point]): F[GeoJson.MultiLineString] = {
    for {
      request <- Sync[F].pure(RouteRequest(point).asJson.noSpaces)
      rawResponse <- Sync[F].delay(Http(serviceConfig.router).postData(request).asString.body)
      result <- Sync[F].fromEither(decode[RouteResult](rawResponse))
    } yield result.road
  }
}
package io.kineticcookie.junction2018.repository

import cats.Monad
import doobie._
import doobie.implicits._

case class User(id: String, name: String)

trait UserRepository[F[_]] {
  def add(user: User): F[Int]
  def get(id: String): F[Option[User]]
  def search(name: String): F[Option[User]]
  def list(): F[Seq[User]]
}

class UserRepositoryImpl[F[_]: Monad]()(
  implicit tx: Transactor[F]
) extends UserRepository[F] {
  override def add(user: User): F[Int] = {
    val q = sql"INSERT INTO users(id, name) VALUES(${user.id}, ${user.name})".update
    q.run.transact(tx)
  }


  override def get(id: String): F[Option[User]] = {
    val q = sql"SELECT id, name FROM users WHERE id = $id".query[User].option
    q.transact(tx)
  }

  override def search(name: String): F[Option[User]] = {
    val q = sql"SELECT id, name FROM users WHERE name = $name".query[User].option
    q.transact(tx)
  }

  override def list(): F[Seq[User]] = {
    val q = sql"SELECT id, name FROM users".query[User].to[Seq]
    q.transact(tx)
  }
}
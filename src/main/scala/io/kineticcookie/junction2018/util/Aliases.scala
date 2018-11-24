package io.kineticcookie.junction2018.util

import cats.{ApplicativeError, MonadError}

object Aliases {
  type ThrowableMonad[T[_]] = MonadError[T, Throwable]
  def ThrowableMonad[T[_]: ThrowableMonad] = MonadError[T, Throwable]
  type ThrowableApplicative[T[_]] = ApplicativeError[T, Throwable]
  def ThrowableApplicative[T[_]: ThrowableApplicative] = ApplicativeError[T, Throwable]
}
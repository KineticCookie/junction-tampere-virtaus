package io.kineticcookie.junction2018.api

object exceptions {

  class NotFound(cls: Class[_], id: String) extends RuntimeException {
    override def getMessage: String = s"Can't find ${cls.getSimpleName} with id $id"
  }

}

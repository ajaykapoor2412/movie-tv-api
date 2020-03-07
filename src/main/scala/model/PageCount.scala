package model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol


case class PageCount(pages: Int, typeId: Int)

object PageCountProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val pageCountJsonFormat = jsonFormat2(PageCount.apply)
}


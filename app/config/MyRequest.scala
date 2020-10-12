package config

import play.api.mvc.{Request, RequestHeader}

trait MyRequest {

  implicit class MyRequest[T](request: Request[T]) {

    def userId:Int = {
      request.session.get("id").get.toInt
    }

    def userName:String = {
      request.session.get("name").get
    }

    def post : String = {
      request.session.get("post").get
    }

  }

  implicit class MyRequestHeader(request: RequestHeader) {

    def userId:Int = {
      request.session.get("id").get.toInt
    }

    def userName:String = {
      request.session.get("name").get
    }

    def post : String = {
      request.session.get("post").get
    }
  }
}

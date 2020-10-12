package controllers

import java.nio.file.Files

import config.{MyFile, MyRequest}
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.{CreatePdf, Global, pdfToPng}

import scala.concurrent.ExecutionContext

class SignatureController @Inject()(cc: ControllerComponents)
                                   (implicit exec: ExecutionContext) extends AbstractController(cc) with MyRequest with MyFile {

  def uploadSignPage = Action { implicit request =>
    if (request.post == "课题组负责人" || request.post == "接收部门负责人" || request.post == "项目负责人" || request.post == "课题组负责人和项目负责人") {
      Ok(views.html.signature.uploadSignPage())
    } else {
      Redirect(routes.UserController.loginPage())
    }
  }


  def uploadSign = Action(parse.multipartFormData) { implicit request =>
    try {
      val dir = s"${Global.path}/data/${request.userId}"
      dir.mkdirs
      println(request.body.file("file"))
      request.body.file("file").get.ref.moveTo(s"$dir/sign.jpg".toFile, replace = true)

      CreatePdf.createSignPdf(request.userId, request.post)
      val path = s"${Global.path}/data/${request.userId}"
      pdfToPng.pdf2Png(s"$path/sign.pdf", s"$path/signPdf.png")

      Ok(Json.obj("code" -> 200))
    } catch {
      case e: Exception => Ok(Json.obj("code" -> 400, "error" -> e.getMessage))
    }
  }


  def getImageByPath = Action { implicit request =>
    val ifModifiedSinceStr = request.headers.get(IF_MODIFIED_SINCE)
    val pathUser = s"${Global.path}/data/${request.userId}/signPdf.png"
    val path = if (pathUser.toFile.exists()) {
      pathUser
    } else {
      s"${Global.path}/data/noSign.png"
    }
    val file = path.toFile
    val lastModifiedStr = file.lastModified().toString
    val MimeType = if (path.contains("png")) "image/png" else "image/jpg"
    val byteArray = Files.readAllBytes(file.toPath)
    if (ifModifiedSinceStr.isDefined && ifModifiedSinceStr.get == lastModifiedStr) {
      NotModified
    } else {
      Ok(byteArray).as(MimeType).withHeaders(LAST_MODIFIED -> file.lastModified().toString)
    }
  }


}

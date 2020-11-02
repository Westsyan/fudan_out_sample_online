package controllers

import play.api.data.Form
import play.api.data.Forms._

object FormUtils {

  case class PhoneData(phone: String)

  val PhoneForm: Form[PhoneData] = Form(
    mapping(
      "phone" -> text
    )(PhoneData.apply)(PhoneData.unapply)
  )

  case class PwdLoginData(phone: String, pwd: String)

  val PwdLoginForm: Form[PwdLoginData] = Form(
    mapping(
      "phone" -> text,
      "pwd" -> text
    )(PwdLoginData.apply)(PwdLoginData.unapply)
  )

  case class MsgLoginData(phone: String, code: String)

  val MsgLoginForm: Form[MsgLoginData] = Form(
    mapping(
      "phone" -> text,
      "code" -> text
    )(MsgLoginData.apply)(MsgLoginData.unapply)
  )


  case class RegisterData(phone: String, pwd: String, name: String, post: String, project: String,
                          department: Option[String], team: Option[String], code: String)

  val RegisterForm: Form[RegisterData] = Form(
    mapping(
      "phone" -> text,
      "pwd" -> text,
      "name" -> text,
      "post" -> text,
      "project" -> text,
      "department" -> optional(text),
      "team" -> optional(text),
      "code" -> text
    )(RegisterData.apply)(RegisterData.unapply)
  )

  case class ApplyReportData(project_name:String,project_code:String,sample_name: String, sample_type: String, inactivation: String, inactivation_other: Option[String],
                             verified_inactivation: String, sample_code: String, out_nums: String, application: String, position: String
                            )

  val ApplyReportForm = Form(
    mapping(
      "project_name" -> text,
      "project_code" -> text,
      "sample_name" -> text,
      "sample_type" -> text,
      "inactivation" -> text,
      "inactivation_other" -> optional(text),
      "verified_inactivation" -> text,
      "sample_code" -> text,
      "out_nums" -> text,
      "application" -> text,
      "position" -> text
    )(ApplyReportData.apply)(ApplyReportData.unapply)
  )


}

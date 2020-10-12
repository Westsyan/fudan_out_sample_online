package models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.MySQLProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import com.github.tototoshi.slick.MySQLJodaSupport._
  import org.joda.time.DateTime
  import slick.model.ForeignKeyAction
  import slick.collection.heterogeneous._
  import slick.collection.heterogeneous.syntax._
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Admin.schema ++ AliyunKey.schema ++ Applyreport.schema ++ User.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Admin
   *  @param id Database column id SqlType(INT), AutoInc
   *  @param phone Database column phone SqlType(VARCHAR), Length(255,true)
   *  @param pwd Database column pwd SqlType(TEXT)
   *  @param name Database column name SqlType(TEXT) */
  case class AdminRow(id: Int, phone: String, pwd: String, name: String)
  /** GetResult implicit for fetching AdminRow objects using plain SQL queries */
  implicit def GetResultAdminRow(implicit e0: GR[Int], e1: GR[String]): GR[AdminRow] = GR{
    prs => import prs._
    AdminRow.tupled((<<[Int], <<[String], <<[String], <<[String]))
  }
  /** Table description of table admin. Objects of this class serve as prototypes for rows in queries. */
  class Admin(_tableTag: Tag) extends profile.api.Table[AdminRow](_tableTag, Some("fudan_out_sample_online"), "admin") {
    def * = (id, phone, pwd, name) <> (AdminRow.tupled, AdminRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(phone), Rep.Some(pwd), Rep.Some(name))).shaped.<>({r=>import r._; _1.map(_=> AdminRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc */
    val id: Rep[Int] = column[Int]("id", O.AutoInc)
    /** Database column phone SqlType(VARCHAR), Length(255,true) */
    val phone: Rep[String] = column[String]("phone", O.Length(255,varying=true))
    /** Database column pwd SqlType(TEXT) */
    val pwd: Rep[String] = column[String]("pwd")
    /** Database column name SqlType(TEXT) */
    val name: Rep[String] = column[String]("name")

    /** Primary key of Admin (database name admin_PK) */
    val pk = primaryKey("admin_PK", (id, phone))
  }
  /** Collection-like TableQuery object for table Admin */
  lazy val Admin = new TableQuery(tag => new Admin(tag))

  /** Entity class storing rows of table AliyunKey
   *  @param keyId Database column key_id SqlType(VARCHAR), Length(255,true)
   *  @param secret Database column secret SqlType(VARCHAR), Length(255,true)
   *  @param id Database column id SqlType(INT) */
  case class AliyunKeyRow(keyId: String, secret: String, id: Int)
  /** GetResult implicit for fetching AliyunKeyRow objects using plain SQL queries */
  implicit def GetResultAliyunKeyRow(implicit e0: GR[String], e1: GR[Int]): GR[AliyunKeyRow] = GR{
    prs => import prs._
    AliyunKeyRow.tupled((<<[String], <<[String], <<[Int]))
  }
  /** Table description of table aliyun_key. Objects of this class serve as prototypes for rows in queries. */
  class AliyunKey(_tableTag: Tag) extends profile.api.Table[AliyunKeyRow](_tableTag, Some("fudan_out_sample_online"), "aliyun_key") {
    def * = (keyId, secret, id) <> (AliyunKeyRow.tupled, AliyunKeyRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(keyId), Rep.Some(secret), Rep.Some(id))).shaped.<>({r=>import r._; _1.map(_=> AliyunKeyRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column key_id SqlType(VARCHAR), Length(255,true) */
    val keyId: Rep[String] = column[String]("key_id", O.Length(255,varying=true))
    /** Database column secret SqlType(VARCHAR), Length(255,true) */
    val secret: Rep[String] = column[String]("secret", O.Length(255,varying=true))
    /** Database column id SqlType(INT) */
    val id: Rep[Int] = column[Int]("id")

    /** Primary key of AliyunKey (database name aliyun_key_PK) */
    val pk = primaryKey("aliyun_key_PK", (keyId, secret, id))
  }
  /** Collection-like TableQuery object for table AliyunKey */
  lazy val AliyunKey = new TableQuery(tag => new AliyunKey(tag))

  /** Entity class storing rows of table Applyreport
   *  @param id Database column id SqlType(INT), AutoInc
   *  @param userid Database column userid SqlType(INT)
   *  @param projectName Database column project_name SqlType(TEXT)
   *  @param projectCode Database column project_code SqlType(TEXT)
   *  @param times Database column times SqlType(TEXT)
   *  @param applyCode Database column apply_code SqlType(TEXT)
   *  @param sampleName Database column sample_name SqlType(TEXT)
   *  @param sampleType Database column sample_type SqlType(TEXT)
   *  @param inactivation Database column inactivation SqlType(TEXT)
   *  @param verifiedInactivation Database column verified_inactivation SqlType(TEXT)
   *  @param sampleCode Database column sample_code SqlType(TEXT)
   *  @param outNums Database column out_nums SqlType(TEXT)
   *  @param application Database column application SqlType(TEXT)
   *  @param position Database column position SqlType(TEXT)
   *  @param team Database column team SqlType(TEXT)
   *  @param department Database column department SqlType(TEXT)
   *  @param project Database column project SqlType(TEXT)
   *  @param teamAudit Database column team_audit SqlType(ENUM), Length(2,false)
   *  @param departmentAudit Database column department_audit SqlType(ENUM), Length(2,false)
   *  @param projectAudit Database column project_audit SqlType(ENUM), Length(2,false)
   *  @param teamTime Database column team_time SqlType(TEXT)
   *  @param departmentTime Database column department_time SqlType(TEXT)
   *  @param projectTime Database column project_time SqlType(TEXT)
   *  @param teamSign Database column team_sign SqlType(ENUM), Length(2,false)
   *  @param departmentSign Database column department_sign SqlType(ENUM), Length(2,false)
   *  @param projectSign Database column project_sign SqlType(ENUM), Length(2,false) */
  case class ApplyreportRow(id: Int, userid: Int, projectName: String, projectCode: String, times: String, applyCode: String, sampleName: String, sampleType: String, inactivation: String, verifiedInactivation: String, sampleCode: String, outNums: String, application: String, position: String, team: String, department: String, project: String, teamAudit: String, departmentAudit: String, projectAudit: String, teamTime: String, departmentTime: String, projectTime: String, teamSign: String, departmentSign: String, projectSign: String)
  /** GetResult implicit for fetching ApplyreportRow objects using plain SQL queries */
  implicit def GetResultApplyreportRow(implicit e0: GR[Int], e1: GR[String]): GR[ApplyreportRow] = GR{
    prs => import prs._
    ApplyreportRow(<<[Int], <<[Int], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String])
  }
  /** Table description of table applyreport. Objects of this class serve as prototypes for rows in queries. */
  class Applyreport(_tableTag: Tag) extends profile.api.Table[ApplyreportRow](_tableTag, Some("fudan_out_sample_online"), "applyreport") {
    def * = (id :: userid :: projectName :: projectCode :: times :: applyCode :: sampleName :: sampleType :: inactivation :: verifiedInactivation :: sampleCode :: outNums :: application :: position :: team :: department :: project :: teamAudit :: departmentAudit :: projectAudit :: teamTime :: departmentTime :: projectTime :: teamSign :: departmentSign :: projectSign :: HNil).mapTo[ApplyreportRow]
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id) :: Rep.Some(userid) :: Rep.Some(projectName) :: Rep.Some(projectCode) :: Rep.Some(times) :: Rep.Some(applyCode) :: Rep.Some(sampleName) :: Rep.Some(sampleType) :: Rep.Some(inactivation) :: Rep.Some(verifiedInactivation) :: Rep.Some(sampleCode) :: Rep.Some(outNums) :: Rep.Some(application) :: Rep.Some(position) :: Rep.Some(team) :: Rep.Some(department) :: Rep.Some(project) :: Rep.Some(teamAudit) :: Rep.Some(departmentAudit) :: Rep.Some(projectAudit) :: Rep.Some(teamTime) :: Rep.Some(departmentTime) :: Rep.Some(projectTime) :: Rep.Some(teamSign) :: Rep.Some(departmentSign) :: Rep.Some(projectSign) :: HNil).shaped.<>(r => ApplyreportRow(r(0).asInstanceOf[Option[Int]].get, r(1).asInstanceOf[Option[Int]].get, r(2).asInstanceOf[Option[String]].get, r(3).asInstanceOf[Option[String]].get, r(4).asInstanceOf[Option[String]].get, r(5).asInstanceOf[Option[String]].get, r(6).asInstanceOf[Option[String]].get, r(7).asInstanceOf[Option[String]].get, r(8).asInstanceOf[Option[String]].get, r(9).asInstanceOf[Option[String]].get, r(10).asInstanceOf[Option[String]].get, r(11).asInstanceOf[Option[String]].get, r(12).asInstanceOf[Option[String]].get, r(13).asInstanceOf[Option[String]].get, r(14).asInstanceOf[Option[String]].get, r(15).asInstanceOf[Option[String]].get, r(16).asInstanceOf[Option[String]].get, r(17).asInstanceOf[Option[String]].get, r(18).asInstanceOf[Option[String]].get, r(19).asInstanceOf[Option[String]].get, r(20).asInstanceOf[Option[String]].get, r(21).asInstanceOf[Option[String]].get, r(22).asInstanceOf[Option[String]].get, r(23).asInstanceOf[Option[String]].get, r(24).asInstanceOf[Option[String]].get, r(25).asInstanceOf[Option[String]].get), (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc */
    val id: Rep[Int] = column[Int]("id", O.AutoInc)
    /** Database column userid SqlType(INT) */
    val userid: Rep[Int] = column[Int]("userid")
    /** Database column project_name SqlType(TEXT) */
    val projectName: Rep[String] = column[String]("project_name")
    /** Database column project_code SqlType(TEXT) */
    val projectCode: Rep[String] = column[String]("project_code")
    /** Database column times SqlType(TEXT) */
    val times: Rep[String] = column[String]("times")
    /** Database column apply_code SqlType(TEXT) */
    val applyCode: Rep[String] = column[String]("apply_code")
    /** Database column sample_name SqlType(TEXT) */
    val sampleName: Rep[String] = column[String]("sample_name")
    /** Database column sample_type SqlType(TEXT) */
    val sampleType: Rep[String] = column[String]("sample_type")
    /** Database column inactivation SqlType(TEXT) */
    val inactivation: Rep[String] = column[String]("inactivation")
    /** Database column verified_inactivation SqlType(TEXT) */
    val verifiedInactivation: Rep[String] = column[String]("verified_inactivation")
    /** Database column sample_code SqlType(TEXT) */
    val sampleCode: Rep[String] = column[String]("sample_code")
    /** Database column out_nums SqlType(TEXT) */
    val outNums: Rep[String] = column[String]("out_nums")
    /** Database column application SqlType(TEXT) */
    val application: Rep[String] = column[String]("application")
    /** Database column position SqlType(TEXT) */
    val position: Rep[String] = column[String]("position")
    /** Database column team SqlType(TEXT) */
    val team: Rep[String] = column[String]("team")
    /** Database column department SqlType(TEXT) */
    val department: Rep[String] = column[String]("department")
    /** Database column project SqlType(TEXT) */
    val project: Rep[String] = column[String]("project")
    /** Database column team_audit SqlType(ENUM), Length(2,false) */
    val teamAudit: Rep[String] = column[String]("team_audit", O.Length(2,varying=false))
    /** Database column department_audit SqlType(ENUM), Length(2,false) */
    val departmentAudit: Rep[String] = column[String]("department_audit", O.Length(2,varying=false))
    /** Database column project_audit SqlType(ENUM), Length(2,false) */
    val projectAudit: Rep[String] = column[String]("project_audit", O.Length(2,varying=false))
    /** Database column team_time SqlType(TEXT) */
    val teamTime: Rep[String] = column[String]("team_time")
    /** Database column department_time SqlType(TEXT) */
    val departmentTime: Rep[String] = column[String]("department_time")
    /** Database column project_time SqlType(TEXT) */
    val projectTime: Rep[String] = column[String]("project_time")
    /** Database column team_sign SqlType(ENUM), Length(2,false) */
    val teamSign: Rep[String] = column[String]("team_sign", O.Length(2,varying=false))
    /** Database column department_sign SqlType(ENUM), Length(2,false) */
    val departmentSign: Rep[String] = column[String]("department_sign", O.Length(2,varying=false))
    /** Database column project_sign SqlType(ENUM), Length(2,false) */
    val projectSign: Rep[String] = column[String]("project_sign", O.Length(2,varying=false))

    /** Primary key of Applyreport (database name applyreport_PK) */
    val pk = primaryKey("applyreport_PK", id :: userid :: HNil)
  }
  /** Collection-like TableQuery object for table Applyreport */
  lazy val Applyreport = new TableQuery(tag => new Applyreport(tag))

  /** Entity class storing rows of table User
   *  @param id Database column id SqlType(INT), AutoInc
   *  @param phone Database column phone SqlType(VARCHAR), Length(255,true)
   *  @param pwd Database column pwd SqlType(TEXT)
   *  @param name Database column name SqlType(TEXT)
   *  @param post Database column post SqlType(ENUM), Length(7,false) */
  case class UserRow(id: Int, phone: String, pwd: String, name: String, post: String)
  /** GetResult implicit for fetching UserRow objects using plain SQL queries */
  implicit def GetResultUserRow(implicit e0: GR[Int], e1: GR[String]): GR[UserRow] = GR{
    prs => import prs._
    UserRow.tupled((<<[Int], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table user. Objects of this class serve as prototypes for rows in queries. */
  class User(_tableTag: Tag) extends profile.api.Table[UserRow](_tableTag, Some("fudan_out_sample_online"), "user") {
    def * = (id, phone, pwd, name, post) <> (UserRow.tupled, UserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(phone), Rep.Some(pwd), Rep.Some(name), Rep.Some(post))).shaped.<>({r=>import r._; _1.map(_=> UserRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc */
    val id: Rep[Int] = column[Int]("id", O.AutoInc)
    /** Database column phone SqlType(VARCHAR), Length(255,true) */
    val phone: Rep[String] = column[String]("phone", O.Length(255,varying=true))
    /** Database column pwd SqlType(TEXT) */
    val pwd: Rep[String] = column[String]("pwd")
    /** Database column name SqlType(TEXT) */
    val name: Rep[String] = column[String]("name")
    /** Database column post SqlType(ENUM), Length(7,false) */
    val post: Rep[String] = column[String]("post", O.Length(7,varying=false))

    /** Primary key of User (database name user_PK) */
    val pk = primaryKey("user_PK", (id, phone))
  }
  /** Collection-like TableQuery object for table User */
  lazy val User = new TableQuery(tag => new User(tag))
}

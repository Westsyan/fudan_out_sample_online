package utils

import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.{PdfDocument, PdfWriter}
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.property.TextAlignment
import config.MyFile
import models.Tables.ApplyreportRow

object CreatePdf extends MyFile {

  val BLACK = new DeviceRgb(34, 24, 21) //黑色

  def createPdf(outpath: String, row: ApplyreportRow, team: String, department: String, project: String, gd: Int) = {

    val font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H", false)
    val writer = new PdfWriter(outpath)
    val pdfDoc = new PdfDocument(writer)
    pdfDoc.addNewPage()
    val canvas = new PdfCanvas(pdfDoc.getFirstPage)
    val document = new Document(pdfDoc)

    val pageSize = pdfDoc.getPage(1).getPageSize
    val pageWidth = pageSize.getWidth
    val pageHeight = pageSize.getHeight


    Itext7.addLeftText(document, "归档编号：", 10, 55f, pageHeight - 45f, 100f, font)

    if (gd != 0) {
      val gdCode = "GD-" + "0" * (6 - gd.toString.length) + gd
      Itext7.addLeftText(document, gdCode, 10, 105f, pageHeight - 45f, 100f, font)
    }

    Itext7.addRectangleWithoutBackgroundColor(canvas, 50f, 50f, pageWidth - 100f, pageHeight - 100f, 1f, new DeviceRgb(62, 62, 63))

    val title = new Paragraph("带出样本申请表").setFont(font).setFontSize(20).setFontColor(BLACK).
      setTextAlignment(TextAlignment.CENTER).setFixedPosition(1, 0f, pageHeight - 110f, pageWidth)
    document.add(title)

    val verifiedInactivation = row.verifiedInactivation match {
      case "on" =>"是"
      case _ => "否"
    }

    val data = Map("项目名称" -> row.projectName, "项目编号" -> row.projectCode, "申请时间" -> row.times,
      "申请编号" -> row.applyCode, "病原微生物名称" -> row.sampleName,
      "样品名称/类型" -> row.sampleType, "灭活方式" -> row.inactivation, "灭活方式是否经过验证" -> verifiedInactivation,
      "样品编号" -> row.sampleCode, "带出数量" -> row.outNums, "用途" -> row.application, "带出后保存地点" -> row.position)

    val name = Array(Map("name" -> "项目编号", "height" -> "153"),
      Map("name" -> "申请编号", "height" -> "183"),
      Map("name" -> "病原微生物名称", "height" -> "213"), Map("name" -> "样品名称/类型", "height" -> "243"),
      Map("name" -> "灭活方式", "height" -> "273"), Map("name" -> "灭活方式是否经过验证", "height" -> "303"),
      Map("name" -> "样品编号", "height" -> "333"), Map("name" -> "带出数量", "height" -> "363"),
      Map("name" -> "用途", "height" -> "393"), Map("name" -> "带出后保存地点", "height" -> "423"),
      Map("name" -> "带出人承诺书及签字", "height" -> "500"), Map("name" -> "课题组负责人审核", "height" -> "590"),
      Map("name" -> "接收部门审核", "height" -> "650"), Map("name" -> "项目负责人审核", "height" -> "710"),
      Map("name" -> "BSL-3实验室审核", "height" -> "770"))

    //PDF列名和值的插入
    name.foreach { x =>
      val bottom = pageHeight - x("height").toFloat
      Itext7.addText(document, x("name"), bottom, font)
      Itext7.addLeftText(document, data.getOrElse(x("name"), ""), 10, 215f, bottom, pageWidth - 225f, font)
    }

    //项目名称和申请时间的边框线与字段插入
    Itext7.addSolidYLine(canvas, 300f, pageHeight - 130f, pageHeight - 190f, BLACK)
    Itext7.addSolidYLine(canvas, 400f, pageHeight - 130f, pageHeight - 190f, BLACK)

    Itext7.addText2(document, "项目名称", pageHeight - 153f, font)
    if(row.projectName.length <=10 ){
      Itext7.addLeftText(document, row.projectName, 10, 415f, pageHeight - 153f, pageWidth - 225f, font)
    }else if(row.projectName.length <= 17){
      Itext7.addLeftText(document, row.projectName, 8, 410f, pageHeight - 151f,200f, font)
    }else{
      Itext7.addLeftText(document, row.projectName, 8, 410f, pageHeight - 158f,130f, font)
    }

    Itext7.addText2(document, "申请时间", pageHeight - 183f, font)
    Itext7.addLeftText(document, row.times, 10, 415f, pageHeight - 183f, pageWidth - 225f, font)

    //本人承诺插入
    Itext7.addLeftBlodText(document, "本人承诺：", 10, 205f, pageHeight - 450f, 150f, font)
    Itext7.addLeftTextIndex(document, "1. 所带样品均经过彻底得灭活处理且灭活方法均经过验证，所有样品表面均经过彻底的消毒。",
      10, 205f, pageHeight - 483f, pageWidth - 255f, font)
    Itext7.addLeftTextIndex(document, "2. 填写的所有内容均属实，如不属实，一切后果自负。",
      10, 205f, pageHeight - 499f, pageWidth - 260f, font)
    Itext7.addLeftTextIndex(document, "3. 所有带出样本均严格按照接收实验室的规程操作。",
      10, 205f, pageHeight - 517f, pageWidth - 260f, font)

    Itext7.addJustifiedBlodText(document, "承诺人：", 10, 300f, pageHeight - 540f, 45f, font)
    Itext7.addJustifiedBlodText(document, "日期：", 10, 420f, pageHeight - 540f, 40f, font)

    def auditCN(audit: String) = {
      audit match {
        case "1" => "同意"
        case "2" => "不同意"
        case _ => ""
      }
    }


    //实验室负责人字段名插入
    Itext7.addJustifiedText(document, "BSL-3项目负责人签字：", 10, 205f, pageHeight - 750f, 105f, font)
    Itext7.addJustifiedText(document, "BSL-3实验室主任签字：", 10, 380f, pageHeight - 750f, 105f, font)

    //每行底部边框线
    val xLine = Array(130f, 160f, 190f, 220f, 250f, 280f, 310f, 340f, 370f, 400f, 430f, 550f, 610f, 670f, 730f)
    xLine.foreach { x =>
      Itext7.addSolidXLine(canvas, 50f, pageWidth - 50f, pageHeight - x, BLACK)
    }

    //审核人员签字部分内部底部边框线
    Itext7.addSolidXLine(canvas, 200f, pageWidth - 50f, pageHeight - 580f, BLACK)
    Itext7.addSolidXLine(canvas, 200f, pageWidth - 50f, pageHeight - 640f, BLACK)
    Itext7.addSolidXLine(canvas, 200f, pageWidth - 50f, pageHeight - 700f, BLACK)

    Itext7.addSolidYLine(canvas, 200f, pageHeight - 130f, 50f, BLACK)


    //审核人员框内的竖线
    val average = (pageWidth - 200f - 50f) / 4

    Itext7.addSolidYLine(canvas, 200f + average, pageHeight - 730f, 292f, BLACK)
    Itext7.addSolidYLine(canvas, 200f + average * 2, pageHeight - 730f, 292f, BLACK)
    Itext7.addSolidYLine(canvas, 200f + average * 3, pageHeight - 730f, 292f, BLACK)

    //填充负责人内部表字段名
    (0 to 2).foreach { x =>
      Itext7.addLeftText(document, "审核结果：",
        10, 223f, pageHeight - 573f - x * 60f, pageWidth - 310f, font)

      Itext7.addLeftText(document, "审核时间：",
        10, 223f, pageHeight - 603f - x * 60f, pageWidth - 310f, font)

      Itext7.addLeftText(document, "审核人：",
        10, 230f + average * 2, pageHeight - 573f - x * 60f, pageWidth - 310f, font)

      Itext7.addLeftText(document, "审核人签字：",
        10, 220f + average * 2, pageHeight - 603f - x * 60f, pageWidth - 310f, font)
    }

    //填充负责人名称
    Itext7.addJustifiedText(document, team,
      10, 222f + average * 3, pageHeight - 573f, 40f, font)

    Itext7.addJustifiedText(document, department,
      10, 222f + average * 3, pageHeight - 633f, 40f, font)

    Itext7.addJustifiedText(document, project,
      10, 222f + average * 3, pageHeight - 693f, 40f, font)

    //填充审核结果
    Itext7.addJustifiedText(document, auditCN(row.teamAudit),
      10, 222f + average, pageHeight - 573f, 40f, font)

    Itext7.addJustifiedText(document, auditCN(row.departmentAudit),
      10, 222f + average, pageHeight - 633f, 40f, font)

    Itext7.addJustifiedText(document, auditCN(row.projectAudit),
      10, 222f + average, pageHeight - 693f, 40f, font)

    Itext7.addLeftText(document, row.teamTime,
      9, 208f + average, pageHeight - 603f, 80f, font)

    Itext7.addLeftText(document, row.departmentTime,
      9, 208f + average, pageHeight - 663f, 80f, font)

    Itext7.addLeftText(document, row.projectTime,
      9, 208f + average, pageHeight - 723f, 80f, font)

    //填充电子签名
    if (row.teamAudit == "1" && row.teamSign == "1") {
      val sign = s"${Global.path}/data/${row.team}/sign.jpg"
      Itext7.addImageToPdfByHeight(canvas, sign, 210f + average * 3, pageHeight - 608f, 25f)
    }

    if (row.departmentAudit == "1" && row.departmentSign == "1") {
      val sign = s"${Global.path}/data/${row.department}/sign.jpg"
      Itext7.addImageToPdfByHeight(canvas, sign, 210f + average * 3, pageHeight - 668f, 25f)
    }

    if (row.projectAudit == "1" && row.projectSign == "1") {
      val sign = s"${Global.path}/data/${row.project}/sign.jpg"
      Itext7.addImageToPdfByHeight(canvas, sign, 210f + average * 3, pageHeight - 728f, 25f)
    }


    document.close()
    writer.close()
    pdfDoc.close()
  }

  def createSignPdf(id: Int, post: String) = {

    val path = s"${Global.path}/data/$id"
    val sign = s"$path/sign.jpg"
    val outpath = s"$path/sign.pdf"

    val font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H", false)
    val writer = new PdfWriter(outpath)
    val pdfDoc = new PdfDocument(writer)
    pdfDoc.addNewPage()
    val canvas = new PdfCanvas(pdfDoc.getFirstPage)
    val document = new Document(pdfDoc)

    val pageSize = pdfDoc.getPage(1).getPageSize
    val pageWidth = pageSize.getWidth
    val pageHeight = pageSize.getHeight


    Itext7.addLeftText(document, "归档编号：", 10, 55f, pageHeight - 45f, 100f, font)

    Itext7.addRectangleWithoutBackgroundColor(canvas, 50f, 50f, pageWidth - 100f, pageHeight - 100f, 1f, new DeviceRgb(62, 62, 63))

    val title = new Paragraph("带出样本申请表").setFont(font).setFontSize(20).setFontColor(BLACK).
      setTextAlignment(TextAlignment.CENTER).setFixedPosition(1, 0f, pageHeight - 110f, pageWidth)
    document.add(title)

    val name = Array(Map("name" -> "项目编号", "height" -> "153"),
      Map("name" -> "申请编号", "height" -> "183"),
      Map("name" -> "病原微生物名称", "height" -> "213"), Map("name" -> "样品名称/类型", "height" -> "243"),
      Map("name" -> "灭活方式", "height" -> "273"), Map("name" -> "灭活方式是否经过验证", "height" -> "303"),
      Map("name" -> "样品编号", "height" -> "333"), Map("name" -> "带出数量", "height" -> "363"),
      Map("name" -> "用途", "height" -> "393"), Map("name" -> "带出后保存地点", "height" -> "423"),
      Map("name" -> "带出人承诺书及签字", "height" -> "500"), Map("name" -> "课题组负责人审核", "height" -> "590"),
      Map("name" -> "接收部门审核", "height" -> "650"), Map("name" -> "项目负责人审核", "height" -> "710"),
      Map("name" -> "BSL-3实验室审核", "height" -> "770"))

    name.foreach { x =>
      Itext7.addText(document, x("name"), pageHeight - x("height").toFloat, font)
    }

    //项目名称和申请时间的边框线与字段插入
    Itext7.addSolidYLine(canvas, 300f, pageHeight - 130f, pageHeight - 190f, BLACK)
    Itext7.addSolidYLine(canvas, 400f, pageHeight - 130f, pageHeight - 190f, BLACK)

    Itext7.addText2(document, "项目名称", pageHeight - 153f, font)
    Itext7.addText2(document, "申请时间", pageHeight - 183f, font)


    Itext7.addJustifiedText(document, "BSL-3项目负责人签字：", 10, 205f, pageHeight - 750f, 105f, font)
    Itext7.addJustifiedText(document, "BSL-3实验室主任签字：", 10, 380f, pageHeight - 750f, 105f, font)

    val xLine = Array(130f, 160f, 190f, 220f, 250f, 280f, 310f, 340f, 370f, 400f, 430f, 550f, 610f, 670f, 730f)
    xLine.foreach { x =>
      Itext7.addSolidXLine(canvas, 50f, pageWidth - 50f, pageHeight - x, BLACK)
    }

    Itext7.addSolidXLine(canvas, 200f, pageWidth - 50f, pageHeight - 580f, BLACK)
    Itext7.addSolidXLine(canvas, 200f, pageWidth - 50f, pageHeight - 640f, BLACK)
    Itext7.addSolidXLine(canvas, 200f, pageWidth - 50f, pageHeight - 700f, BLACK)

    Itext7.addSolidYLine(canvas, 200f, pageHeight - 130f, 50f, BLACK)

    //审核人员框内的竖线
    val average = (pageWidth - 200f - 50f) / 4
    Itext7.addSolidYLine(canvas, 200f + average, pageHeight - 730f, 292f, BLACK)
    Itext7.addSolidYLine(canvas, 200f + average * 2, pageHeight - 730f, 292f, BLACK)
    Itext7.addSolidYLine(canvas, 200f + average * 3, pageHeight - 730f, 292f, BLACK)

    (0 to 2).foreach { x =>
      Itext7.addLeftText(document, "审核结果",
        10, 223f, pageHeight - 573f - x * 60f, pageWidth - 310f, font)

      Itext7.addLeftText(document, "审核时间",
        10, 223f, pageHeight - 603f - x * 60f, pageWidth - 310f, font)

      Itext7.addLeftText(document, "审核人",
        10, 230f + average * 2, pageHeight - 573f - x * 60f, pageWidth - 310f, font)

      Itext7.addLeftText(document, "审核人签字",
        10, 220f + average * 2, pageHeight - 603f - x * 60f, pageWidth - 310f, font)
    }


    post match {
      case "课题组负责人" => Itext7.addImageToPdfByHeight(canvas, sign, 210f + average * 3, pageHeight - 608f, 25f)
      case "接收部门负责人" => Itext7.addImageToPdfByHeight(canvas, sign, 210f + average * 3, pageHeight - 668f, 25f)
      case "项目负责人" => Itext7.addImageToPdfByHeight(canvas, sign, 210f + average * 3, pageHeight - 728f, 25f)
      case "课题组负责人和项目负责人" =>
        Itext7.addImageToPdfByHeight(canvas, sign, 210f + average * 3, pageHeight - 608f, 25f)
        Itext7.addImageToPdfByHeight(canvas, sign, 210f + average * 3, pageHeight - 728f, 25f)
    }

    document.close()
    writer.close()
    pdfDoc.close()
  }


}

package utils

import java.io.File

import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.{PdfDocument, PdfReader, PdfWriter}
import com.itextpdf.kernel.utils.PdfMerger
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.property.TextAlignment


object Itext7 {


  val BLACK = new DeviceRgb(34, 24, 21) //黑色


  //合并pdf
  def mergePdfFiles(files: Array[File], savepath: String): Unit = {
    try{
      val pdfDoc = new PdfDocument(new PdfWriter(savepath))
      val merger = new PdfMerger(pdfDoc)

      for(i <- files.indices){
        val pdf = new PdfDocument(new PdfReader(files(i)))
        merger.merge(pdf,1,pdf.getNumberOfPages)
        pdf.close()
      }

      merger.close()
      pdfDoc.close()
    }catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }

  //无背景色矩形框
  def addRectangleWithoutBackgroundColor(canvas: PdfCanvas, x: Float, y: Float, w: Float, h: Float, width: Float, color: DeviceRgb) = {
    canvas.setStrokeColor(color)
    canvas.setLineDash(1f)
    canvas.setLineWidth(width)
    canvas.rectangle(x, y, w, h)
    canvas.stroke()
  }

  //有背景色圆角矩形框
  def addRoundRectangleWithBackgroundColor(canvas: PdfCanvas, x: Float, y: Float, w: Float, h: Float,r: Float, color: DeviceRgb) = {
    canvas.roundRectangle(x,y,w,h,r)
    canvas.setFillColor(color)
    canvas.setStrokeColor(color)
    canvas.fillStroke()
  }

  //虚线
  def addDashLine(canvas: PdfCanvas, headX: Float, lastX: Float, y: Float, color: DeviceRgb): Unit = {
    canvas.setStrokeColor(color)
    canvas.setLineDash(2f, 0.1f)
    canvas.setLineWidth(0.3f)
    canvas.moveTo(headX, y)
    canvas.lineTo(lastX, y)
    canvas.stroke()
  }

  //实线横线
  def addSolidXLine(canvas: PdfCanvas, headX: Float, lastX: Float, y: Float, color: DeviceRgb): Unit = {
    canvas.setStrokeColor(color)
    canvas.setLineWidth(0.5f)
    canvas.moveTo(headX, y)
    canvas.lineTo(lastX, y)
    canvas.stroke()
  }

  //实线竖线
  def addSolidYLine(canvas: PdfCanvas, x: Float, headY: Float, lastY: Float, color: DeviceRgb): Unit = {
    canvas.setStrokeColor(color)
    canvas.setLineWidth(0.5f)
    canvas.moveTo(x, headY)
    canvas.lineTo(x, lastY)
    canvas.stroke()
  }

  def addText(document: Document,inText:String,bottom:Float,font:PdfFont) = {
    val text = new Paragraph(inText).setFont(font).setFontSize(10).setFontColor(BLACK).
      setTextAlignment(TextAlignment.CENTER).setFixedPosition(1, 50f,bottom, 150f)
    document.add(text)
  }

  def addText2(document: Document,inText:String,bottom:Float,font:PdfFont) = {
    val text = new Paragraph(inText).setFont(font).setFontSize(10).setFontColor(BLACK).
      setTextAlignment(TextAlignment.CENTER).setFixedPosition(1, 300f,bottom, 100f)
    document.add(text)
  }


  def addLeftTextIndex(document: Document,inText:String,fontSize:Int,left:Float,bottom:Float,width:Float,font:PdfFont) = {
    val text = new Paragraph(inText).setFont(font).setFontSize(fontSize).setFontColor(BLACK).
      setTextAlignment(TextAlignment.LEFT).setBold().setFirstLineIndent(15f).setFixedPosition(1, left,bottom, width)
    document.add(text)
  }

  def addLeftText(document: Document,inText:String,fontSize:Int,left:Float,bottom:Float,width:Float,font:PdfFont) = {
    val text = new Paragraph(inText).setFont(font).setFontSize(fontSize).setFontColor(BLACK).
      setTextAlignment(TextAlignment.LEFT).setFixedPosition(1, left,bottom, width)
    document.add(text)
  }



  def addLeftBlodText(document: Document,inText:String,fontSize:Int,left:Float,bottom:Float,width:Float,font:PdfFont) = {
    val text = new Paragraph(inText).setFont(font).setFontSize(fontSize).setFontColor(BLACK).
      setTextAlignment(TextAlignment.LEFT).setBold().setFixedPosition(1, left,bottom, width)
    document.add(text)
  }

  def addJustifiedBlodText(document: Document,inText:String,fontSize:Int,left:Float,bottom:Float,width:Float,font:PdfFont) = {
    val text = new Paragraph(inText).setFont(font).setFontSize(fontSize).setFontColor(BLACK).
      setTextAlignment(TextAlignment.JUSTIFIED_ALL).setBold().setFixedPosition(1, left,bottom, width)
    document.add(text)
  }

  def addJustifiedText(document: Document,inText:String,fontSize:Int,left:Float,bottom:Float,width:Float,font:PdfFont) = {
    val text = new Paragraph(inText).setFont(font).setFontSize(fontSize).setFontColor(BLACK).
      setTextAlignment(TextAlignment.JUSTIFIED_ALL).setFixedPosition(1, left,bottom, width)
    document.add(text)
  }


  /**
   * 有背景色矩形
   * @param canvas ：pdf画布，已坐下角为起点画坐标轴，（x,y）为起点向上画h高度和向右画w宽度的矩形
   * @param x : x轴坐标
   * @param y ：y轴坐标
   * @param w : 宽度
   * @param h ：高度
   * @param color ：背景色
   * @return
   */
  def addRectangleWithBackgroundColor(canvas: PdfCanvas, x: Float, y: Float, w: Float, h: Float, color: DeviceRgb) = {
    canvas.rectangle(x, y, w, h)
    canvas.setFillColor(color)
    canvas.setStrokeColor(color)
    canvas.fillStroke()
  }

  //图片
  def addImageToPdf(canvas: PdfCanvas, picturePath: String, x: Float, y: Float, width: Float): Unit = {
    val image = ImageDataFactory.create(picturePath)
    canvas.addImage(image,x,y,width,true)
    canvas.stroke()
  }

  //图片
  def addImageToPdfByHeight(canvas: PdfCanvas, picturePath: String, x: Float, y: Float, height: Float): Unit = {
    val image = ImageDataFactory.create(picturePath)
    canvas.addImage(image,x,y, height,true,true)
    canvas.stroke()
  }


  //图片
  def addImageByRectToPdf(canvas: PdfCanvas, picturePath: String, x: Float, y: Float, width: Float,height:Float): Unit = {
    val image = ImageDataFactory.create(picturePath)
    val rect= new Rectangle(x,y,width,height)
    canvas.addImage(image,rect,true)
    canvas.stroke()
  }

}

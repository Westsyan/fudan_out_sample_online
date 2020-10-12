package utils

import java.io.File

import javax.imageio.ImageIO

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer

object pdfToPng {
  /*
      * pdf 转 图片
      */
  def pdf2Png(pdfPath: String,pngPath:String): Unit = {
    //转换路径格式，便于识别
    val pdfFiles = new File(pdfPath)
    val outFile = new File(pngPath)
    val document = PDDocument.load(pdfFiles)
    val renderer = new PDFRenderer(document)
    ImageIO.write(renderer.renderImage(0, 3), "png", outFile)
    document.close()
  }


}

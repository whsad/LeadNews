package com.heima.tess4j;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class Application {

    public static void main(String[] args) {
        //获取图片
        File file = new File("C:\\Users\\admin\\Pictures\\Saved Pictures\\审核图片.png");
        //创建Tesseract对象
        ITesseract tesseract = new Tesseract();
        //设置字体库路径
        tesseract.setDatapath("D:\\ITEMS\\LEADNEWS\\tessdata");
        //中文识别
        tesseract.setLanguage("chi_sim");
        //执行ocr识别
        String result = null;
        try {
            result = tesseract.doOCR(file);
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        //替换回车和tal键 使结果为一行
        result = result.replaceAll("\\r|\\n", "-").replaceAll(" ", "");
        System.out.println("识别结果为：" + result);

    }
}

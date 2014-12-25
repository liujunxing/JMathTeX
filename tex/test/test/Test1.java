package test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import com.sun.image.codec.jpeg.*;

import be.ugent.caagt.jmathtex.*;
import be.ugent.caagt.jmathtex.box.Box;

@SuppressWarnings("unused")
public class Test1 {

	private static void test1() {
		TestDefaultTeXFont tf = new TestDefaultTeXFont();
		tf.test();
	}
	
	private static void test2() {
		// 神奇公式:
		// s.style = 2 * (style / 4) + 4 + (style % 2);
		for (int style = 0; style < 8; ++style) {
			int s_style = 2 * (style / 4) + 4 + (style % 2);
			System.out.println("style=" + style + ", s_style=" + s_style);
		}
	}
	
	private static void test3() {
		System.out.println("TeXFormula.class = " + TeXFormula.class);
		for (int i = 0; i < TeXFormula.symbolMappings.length; ++i) {
			if (TeXFormula.symbolMappings[i] != null)
				System.out.println("symbolMappings[" + (char)i + "(" + i + ")]=" + 
					TeXFormula.symbolMappings[i]);
		}
	}
	
	private static void tf2xml(TeXFormula tf) throws Exception {
		SimpleXmlWriter sxw = new SimpleXmlWriter();
		sxw.simpleHeader();
		sxw.appendRaw("\n");
		tf.root.toXml(sxw, null);
		String result = sxw.toString();
		
		// 写入文件.
		java.io.FileWriter fw = new java.io.FileWriter("C:/temp/aaa.xml");
		fw.write(result);
		fw.close();
	}
	
	private static void box2xml(Box b) throws Exception {
		SimpleXmlWriter sxw = new SimpleXmlWriter();
		sxw.simpleHeader();
		sxw.appendRaw("\n");
		b.toXml(sxw, null);
		String result = sxw.toString();
		
		// 写入文件.
		java.io.FileWriter fw = new java.io.FileWriter("D:/temp/aaa2.xml");
		fw.write(result);
		fw.close();
	}
	
	public static final String TEST_FORMULA_1 = "a+b"; // 简单的测试
	public static final String TEST_FORMULA_2 = "\\frac{3}{2}"; // 二分之三
	
	/**
	 * 测试主程序.
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		System.out.println("Hello world!");
		//test2();
		
		Class c = TeXFormula.class; // 导致加载 TeXForumla? 失败的想法, 不会加载!
		System.out.println("TeXFormula.class = " + c);
		
		TeXFormula.debug_stop = true;
		//String str = "\\sqrt{x+2}";
		String str = "x";
		TeXFormula tf = new TeXFormula(str); // 测试...
		tf.dump();
		//tf2xml(tf);
		
		float size = 150.0f; // 这个大小很重要, 要仔细调整.
		int style = TeXConstants.STYLE_DISPLAY;
		
		// 分几个步骤完成.
		DefaultTeXFont tfont = new DefaultTeXFont(size);
		TeXEnvironment env = new TeXEnvironment(style, tfont);
		Box box = tf.createBox(env);
		box.dump(); 
		box2xml(box);
		//System.exit(1); // 下面暂不测试, 中途退出.
			// TODO: box.dump(); box.saveAsXML(), saveAsHTML(), saveAsDVI(), saveAsPDF() ...
		
		// 研究 TeXIcon 是什么.
		TeXIcon icon = new TeXIcon(box, size);	
			// TODO: icon.saveAsSVG(), saveAsPNG(), asAsJPG() ......
		int w = icon.getIconWidth();
		int h = icon.getIconHeight();
		int x = 0, y = 0;
		
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);
		icon.paintIcon(Color.BLACK, g, x, y);
		
		OutputStream out = new FileOutputStream("D:/temp/aaa2.jpg");
		// create image
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(img);
        param.setQuality(1.0f, false);
        encoder.setJPEGEncodeParam(param);
        try {
        	encoder.encode(img);
        } catch (IOException e) {
        	// 	TODO Auto-generated catch block
        	e.printStackTrace();
        }
        out.flush();
        out.close();

		
		//icon.paintIcon(c, g, x, y)
		
		//TeXIcon txi = tf.createTeXIcon(0, 10);
		System.out.print("icon = " + icon);
	}

}

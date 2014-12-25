package test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class TestImg {
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("Hello world!");
		
		// create image
		int width = 200;
		int height = 160;
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		// paint on image
		Graphics g = img.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.BLACK);
		g.drawString("Hello World", 100, 80);
		g.dispose();
		
		// output
		OutputStream out = new FileOutputStream("C:/temp/aaa2.jpg");
		
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
	}
}

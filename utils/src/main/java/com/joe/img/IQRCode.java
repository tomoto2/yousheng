package com.joe.img;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import net.glxn.qrgen.QRCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * 二维码工具类
 * 
 * @author joe
 *
 */
public class IQRCode {
	private static final Logger logger = LoggerFactory.getLogger(IQRCode.class);

	/**
	 * 将指定数据生成二维码并保存至指定文件
	 * 
	 * @param data
	 *            二维码数据
	 * @param fileName
	 *            文件名（全名，包含路径）
	 * @param width
	 *            图片的宽
	 * @param height
	 *            图片的高
	 * @throws FileNotFoundException
	 *             找不到指定文件
	 */
	public static void create(String data, String fileName, int width, int height) throws FileNotFoundException {
		create(data, new FileOutputStream(fileName), width, height);
		logger.debug("二维码保存位置为：{}", fileName);
	}

	/**
	 * 将指定数据生成二维码并写入指定输出流
	 * 
	 * @param data
	 *            二维码数据
	 * @param out
	 *            输出流
	 * @param width
	 *            图片的宽
	 * @param height
	 *            图片的高
	 */
	public static void create(String data, OutputStream out, int width, int height) {
		logger.debug("生成二维码，要生成的图片的宽为{}，高为{}", width, height);
		QRCode code = QRCode.from(data);
		code.withCharset("UTF8");
		code.withSize(width, height);
		code.writeTo(out);
		logger.debug("二维码生成成功");
	}

	/**
	 * 从本地文件读取二维码内容
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 二维码中的信息
	 * @throws IOException
	 *             IO异常
	 * @throws NotFoundException
	 *             当图像文件中没有二维码信息（图像）时抛出该异常
	 */
	public static String read(String filePath) throws IOException, NotFoundException {
		BufferedImage image = ImageIO.read(new File(filePath));
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		Binarizer binarizer = new HybridBinarizer(source);
		BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
		Result result = new MultiFormatReader().decode(binaryBitmap);
		return result.getText();
	}

	
	/**
	 * 从流中读取二维码内容
	 * 
	 * @param input
	 *            二维码的输入流
	 * @return 二维码中的信息
	 * @throws IOException
	 *             IO异常
	 * @throws NotFoundException
	 *             当图像文件中没有二维码信息（图像）时抛出该异常
	 */
	public static String read(InputStream input) throws IOException, NotFoundException {
		BufferedImage image = ImageIO.read(input);
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		Binarizer binarizer = new HybridBinarizer(source);
		BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
		Result result = new MultiFormatReader().decode(binaryBitmap);
		return result.getText();
	}
}

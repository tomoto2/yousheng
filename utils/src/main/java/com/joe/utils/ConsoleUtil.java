package com.joe.utils;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;

/**
 * 控制台程序工具类，主要用于控制台测试使用
 * 
 * @author joe
 *
 */
public class ConsoleUtil {
	/**
	 * 从控制台获取命令并且处理
	 * 
	 * @param consumer
	 *            处理控制台获取的命令，当输入的是exit时系统退出
	 */
	public static void command(Consumer<String> consumer) {
		try {
			Console console = System.console();
			CustomReader reader;

			if (console != null) {
				System.out.println("Console对象存在，使用Console对象");
				reader = () -> {
					return console.readLine();
				};
			} else {
				System.out.println("Console对象不存在，使用Reader");
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
				reader = () -> {
					return bufferedReader.readLine();
				};
			}
			work(reader, consumer);
		} catch (Exception e) {
			System.err.println(e);
			System.err.println("系统异常，即将退出");
			System.exit(1);
		}
	}

	private static void work(CustomReader reader, Consumer<String> consumer) throws IOException{
		while (true) {
			String data = reader.readLine();
			if (data.equals("exit")) {
				System.out.println("系统即将退出");
				System.exit(0);
			}
			consumer.accept(data);
		}
	}
	private interface CustomReader{
		String readLine() throws IOException;
	}
}
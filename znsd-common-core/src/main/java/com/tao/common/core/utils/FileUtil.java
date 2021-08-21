package com.tao.common.core.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.tao.common.core.common.exception.ExceptionWapper;
import com.tao.common.core.common.file.FileWrap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;


/**
 * 文件相关操作工具类
 * 
 * @author ying.han
 *
 */
public class FileUtil {

	/**
	 * 生成下载文件名,如果目录里有重复,则加上数字后缀
	 * 
	 * @param fileName
	 * @return
	 */
	public static String genDownloadFileName(String parentDir, String fileName) {
		if (!StringUtil.isEmptyOrNull(parentDir)) {
			String fileNameToUse = fileName;
			String suffix = "";
			int suffixIndex = fileName.lastIndexOf(".");

			if (suffixIndex > -1) {
				suffix = fileName.substring(suffixIndex);
				fileNameToUse = fileName.replaceAll(suffix, "");
				fileName = fileNameToUse;
			}

			int i = 1;
			while (new File(parentDir + File.separator + fileNameToUse + suffix).exists()) {
				fileNameToUse = fileName + "(" + i++ + ")";
			}

			return parentDir + File.separator + fileNameToUse + suffix;
		}

		return fileName;
	}

	/**
	 * 字节数组->流
	 * 
	 * @param bytes
	 * @return
	 */
	public static InputStream byteArray2InputStream(byte[] bytes) {
		return new ByteArrayInputStream(bytes);
	}

	/**
	 * 字节数组->文件
	 * 
	 * @param bytes
	 * @param file
	 */
	public static void write(byte[] bytes, File file) {
		try {
			FileUtils.writeByteArrayToFile(file, bytes);
		} catch (IOException e) {
			throw ExceptionWapper.createBapException(e);
		}
	}

	/**
	 * 字节数组->文件
	 * 
	 * @param bytes
	 * @param filePath
	 */
	public static void write(byte[] bytes, String filePath) {
		write(bytes, new File(filePath));
	}

	/**
	 * 复制文件夹
	 * 
	 * @param sourceDir
	 * @param targetDir
	 */
	public static void copyDirectory(File sourceDir, File targetDir) {
		try {
			if (!sourceDir.exists()) {
				return;
			}
			FileUtils.copyDirectory(sourceDir, targetDir);
		} catch (IOException e) {
			throw ExceptionWapper.createBapException(e);
		}
	}

	/**
	 * 复制文件夹
	 * 
	 * @param sourceDirPath
	 * @param targetDirPath
	 */
	public static void copyDirectory(String sourceDirPath, String targetDirPath) {
		copyDirectory(new File(sourceDirPath), new File(targetDirPath));
	}

	/**
	 * 将源文件夹的文件拷贝并拉平到目标文件夹（只有一级目录）
	 * 
	 * @param sourceDirPath
	 * @param targetDirPath
	 */
	public static void copyDirectoryFile(String sourceDirPath, String targetDirPath) {
		File sourceFile = new File(sourceDirPath);
		if (sourceFile.isFile()) {
			File targetFile = new File(genFilePath(targetDirPath, sourceFile.getName()));
			copyFile(sourceFile, targetFile);
		} else if (sourceFile.isDirectory()) {
			File[] files = sourceFile.listFiles();
			for (File file : files) {
				copyDirectoryFile(file.getAbsolutePath(), targetDirPath);
			}
		}
	}

	public static String genFilePath(String... paths) {
		StringBuffer pathBuffer = new StringBuffer();

		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];

			if (StringUtil.isEmptyOrNull(path)) {
				continue;
			}

			if (path.equals(File.separator)) {
				continue;
			}

			pathBuffer.append(path);

			if (i < paths.length - 1) {
				pathBuffer.append(File.separator);
			}
		}

		return pathBuffer.toString();
	}

	/**
	 * 复制文件
	 * 
	 * @param sourceFile
	 * @param targetFile
	 * @throws IOException
	 */
	public static void copyFile(File sourceFile, File targetFile) {
		if (!sourceFile.exists()) {
			return;
		}
		try {
			FileUtils.copyFile(sourceFile, targetFile);
		} catch (IOException e) {
			throw ExceptionWapper.createBapException(e);
		}
	}

	/**
	 * 复制文件
	 * 
	 * @param sourceFilePath
	 * @param targetFilePath
	 * @throws IOException
	 */
	public static void copyFile(String sourceFilePath, String targetFilePath) {
		copyFile(new File(sourceFilePath), new File(targetFilePath));
	}

	/**
	 * 删除文件或文件夹
	 * 
	 * @param file
	 */
	public static void deleteFile(File file) {
		if (!file.exists()) {
			return;
		}

		try {
			if (file.isDirectory()) {
				FileUtils.deleteDirectory(file);
			} else {
				if(!file.delete()){
					throw  	ExceptionWapper.createBapException(new IOException("deleteFile Failed"));
				}
			}
		} catch (IOException e) {
			throw ExceptionWapper.createBapException(e);
		}
	}

	/**
	 * 删除文件或文件夹
	 * 
	 * @param filePath
	 */
	public static void deleteFile(String filePath) {
		deleteFile(new File(filePath));
	}

	/**
	 * 读文件
	 * 
	 * @param file
	 * @param encoding
	 * @return
	 */
	public static String file2String(File file, String encoding) {
		try {
			return FileUtils.readFileToString(file, encoding);
		} catch (IOException e) {
			throw ExceptionWapper.createBapException(e);
		}
	}

	/**
	 * 读文件
	 * 
	 * @param filePath
	 * @param encoding
	 * @return
	 */
	public static String file2String(String filePath, String encoding) {
		return file2String(new File(filePath), encoding);
	}

	/**
	 * 递归搜索指定目录下的所有文件
	 * 
	 * @param dirPath
	 *            搜索文件目录
	 * @return
	 */
	public static List<File> findFiles(String dirPath) {
		return findFiles(null, dirPath, true);
	}

	/**
	 * 搜索指定目录下的所有文件
	 * 
	 * @param dirPath
	 *            搜索文件目录
	 * @param isRecursion
	 *            是否递归搜索
	 * @return
	 */
	public static List<File> findFiles(String dirPath, boolean isRecursion) {
		return findFiles(null, dirPath, isRecursion);
	}

	public static List<File> findFiles(String dirPath, FileFilter filter, boolean isRecursion) {
		List<File> fileList = new ArrayList<File>();
		findFiles(dirPath, filter, fileList, isRecursion);
		return fileList;
	}

	/**
	 * 递归搜索指定目录下，具有指定后缀名的所有文件。
	 * 
	 * @param filenameSuffix
	 *            文件后缀名
	 * @param dirPath
	 *            搜索文件目录
	 * @return
	 */
	public static List<File> findFiles(String filenameSuffix, String dirPath) {
		return findFiles(filenameSuffix, dirPath, true);
	}

	/**
	 * 搜索指定目录下，具有指定后缀名的所有文件。
	 * 
	 * @param filenameSuffix
	 *            文件后缀名
	 * @param dirPath
	 *            搜索文件目录
	 * @param isRecursion
	 *            是否递归搜索
	 */
	public static List<File> findFiles(final String filenameSuffix, String dirPath, boolean isRecursion) {
		FileFilter filter = null;
		if (!StringUtil.isEmptyOrNull(filenameSuffix)) {
			filter = new FileFilter() {

				@Override
				public boolean accept(File file) {
					return file.isDirectory() || file.getName().endsWith(filenameSuffix);
				}
			};
		}

		return findFiles(dirPath, filter, isRecursion);
	}

	/**
	 * 将流保存到文件中
	 * 
	 * @param in
	 * @param file
	 */
	public static void write(InputStream in, File file) {
		write(in, file, 1024);
	}

	/**
	 * 生成一个临时文件，将流写入,缓冲区为1024字节
	 * 
	 * @param in
	 * @return
	 */
	public static File writeToTempFile(InputStream in) {
		return writeToTempFile(in, UUIDUtil.generate(), ".bs2");
	}

	/**
	 * 生成一个临时文件，将流写入
	 * 
	 * @param in
	 * @param buffer
	 *            缓冲字节
	 * @return
	 */
	public static File writeToTempFile(InputStream in, int buffer) {
		return writeToTempFile(in, buffer, UUIDUtil.generate(), ".bs2");
	}

	/**
	 * 生成一个临时文件，将流写入,缓冲区为1024字节
	 * 
	 * @param in
	 * @param prefix
	 *            文件名
	 * @param suffix
	 *            文件后缀
	 * @return
	 */
	public static File writeToTempFile(InputStream in, String prefix, String suffix) {
		return writeToTempFile(in, 1024, prefix, suffix);
	}

	/**
	 * 生成一个临时文件，将流写入
	 * 
	 * @param in
	 * @param buffer
	 *            缓冲字节
	 * @param prefix
	 *            文件名
	 * @param suffix
	 *            文件后缀
	 * @return
	 */
	public static File writeToTempFile(InputStream in, int buffer, String prefix, String suffix) {

		try {
			File temp = File.createTempFile(prefix, suffix);
			temp.deleteOnExit();

			write(in, temp, buffer);

			return temp;
		} catch (IOException e) {
			throw ExceptionWapper.createBapException(e);
		}

	}

	/**
	 * 将流保存到文件中
	 * 
	 * @param in
	 * @param file
	 * @param buffer
	 *            缓冲字节
	 */
	public static void write(InputStream in, File file, int buffer) {

		try (FileOutputStream out = new FileOutputStream(file)){
			int n = 0;
			byte[] bytes = new byte[buffer];
			while ((n = in.read(bytes)) != -1) {
				out.write(bytes, 0, n);
				out.flush();
			}
		} catch (IOException e) {
			throw ExceptionWapper.createBapException(e);
		}

	}

	/**
	 * 将流保存到文件中
	 * 
	 * @param in
	 * @param filePath
	 */
	public static void write(InputStream in, String filePath) {
		write(in, new File(filePath));
	}

	/**
	 * 流->字节数组
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static byte[] inputStream2ByteArray(InputStream input) throws IOException {
		return IOUtils.toByteArray(input);
	}

	/**
	 * 
	 * @param path
	 *            文件的路径
	 * @return
	 */
	public static FileWrap openFile(String path) {
		return openFile(new File(path));
	}

	public static FileWrap openFile(File file) {
		FileWrap fileWrap = null;
		try {
			fileWrap = new FileWrap(file.getName(), file.getPath(), new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw ExceptionWapper.createBapException(e);
		}
		return fileWrap;
	}

	/**
	 * 读取文件
	 * 
	 * @param file
	 * @param encoding
	 * @return
	 */
	public static String readFileToString(File file, String encoding) {
		try {
			return FileUtils.readFileToString(file, encoding);
		} catch (IOException e) {
			throw ExceptionWapper.createBapException(e);
		}
	}

	/**
	 * 读取文件首行文件内容
	 * 
	 * @throws IOException
	 */
	public static String readFirstLine(File file) {
		List<String> readLines = readLines(file, null);

		return readLines.size() > 0 ? readLines.get(0) : "";
	}

	/**
	 * 逐行读取
	 * 
	 * @param file
	 * @param encoding
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<String> readLines(File file, String encoding) {
		try {
			return FileUtils.readLines(file, encoding);
		} catch (IOException e) {
			throw ExceptionWapper.createBapException(e);
		}
	}

	/**
	 * 查找文件名符合正则表达式的文件
	 * 
	 * @param dir
	 * @param pattern
	 * @return
	 */
	public static File[] seachFiles(File dir, String pattern) {
		List<File> fs = new ArrayList<File>();
		seachFiles(dir, pattern, fs);

		return fs.toArray(new File[] {});
	}

	/**
	 * 写文件
	 * 
	 * @param context
	 * @param targetFile
	 * @param encoding
	 */
	public static void write(String context, File targetFile, String encoding) {
		try {
			FileUtils.writeStringToFile(targetFile, context, encoding);
		} catch (IOException e) {
			throw ExceptionWapper.createBapException(e);
		}
	}

	/**
	 * 写文件
	 * 
	 * @param context
	 * @param targetFile
	 * @throws IOException
	 */
	public static void write(String context, File targetFile) {
		write(context, targetFile, null);
	}

	/**
	 * 写文件
	 * 
	 * @param content
	 * @param file
	 * @param append
	 */
	public static void write(String content, File file, boolean append) {
		try {
			FileUtils.writeStringToFile(file, content);
		} catch (IOException e) {
			throw ExceptionWapper.createBapException(e);
		}
	}

	/**
	 * 逐行写入，默认追加
	 * 
	 * @param file
	 * @param contexts
	 */
	public static void writeLines(List<String> lines, File file) {
		try {
			FileUtils.writeLines(file, lines);
		} catch (IOException e) {
			throw ExceptionWapper.createBapException(e);
		}
	}

	private static void findFiles(String dirPath, FileFilter filter, List<File> fileList, boolean isRecursion) {
		File dir = new File(dirPath);
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}

		for (File file : dir.listFiles(filter)) {
			if (file.isDirectory() && isRecursion) {
				/**
				 * 如果目录则递归继续遍历
				 */
				findFiles(file.getAbsolutePath(), filter, fileList, isRecursion);
			} else {
				fileList.add(file);
			}
		}
	}

	private static void seachFiles(File file, String pattern, List<File> fs) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				seachFiles(f, pattern, fs);
			}
		} else {
			if (Pattern.matches(pattern, file.getName())) {
				fs.add(file);
			}
		}
	}
}

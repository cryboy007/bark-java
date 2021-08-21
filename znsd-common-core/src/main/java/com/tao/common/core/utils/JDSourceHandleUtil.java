package com.tao.common.core.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class JDSourceHandleUtil {
	private static final String regex = "\\/\\*[\\p{ASCII}]*?\\*\\/\\S?";

	public static void handleSourceFile(String directory) throws IOException {
		File folder = new File(directory);
		directory = folder.getCanonicalPath();

		File parentFolder = folder.getParentFile();
		String newFirectory = parentFolder.getCanonicalPath() + "\\" + folder.getName() + System.currentTimeMillis()
				+ "\\";
		File[] files = folder.listFiles();
		loopFiles(directory, newFirectory, files);

	}

	private static void loopFiles(String directory, String newFirectory, File[] files) throws IOException {
		for (File f : files) {
			if (f.isFile()) {
				if (f.getName().endsWith(".java")) {
					removeContent(directory, newFirectory, f, true);
				} else {
					removeContent(directory, newFirectory, f, false);
				}
			} else {
				loopFiles(directory, newFirectory, f.listFiles());
			}
		}
	}

	private static void removeContent(String oldDir, String newDir, File file, boolean remove) throws IOException {
		List<String> lines = FileUtils.readLines(file);
		String relocationString = relocationLineNumber(lines);
		String newContent = remove ? relocationString.replaceAll(regex, "") : relocationString;
		// File newFile = new File(file.getCanonicalPath().replaceAll(oldDir,
		// newDir));
		FileUtils.writeStringToFile(file, relocationString);
	}

	private static String relocationLineNumber(List<String> lines) {
		int currentLine = 0;

		StringBuilder content = new StringBuilder();
		for (String line : lines) {
			currentLine++;
			int num = readLineNumber(line);
			if (num == -2) {
				content.delete(content.length() - IOUtils.LINE_SEPARATOR.length(), content.length());
				content.append("{");
				content.append(line).append(IOUtils.LINE_SEPARATOR);
				currentLine--;
			} else {
				if (num != -1) {
					while (currentLine < num) {
						currentLine++;
						content.append(IOUtils.LINE_SEPARATOR);
					}
				}
				content.append(line).append(IOUtils.LINE_SEPARATOR);
			}

		}
		return content.toString();
	}

	private static int readLineNumber(String line) {
		int start = line.indexOf("/*");
		int end = line.indexOf("*/");
		if (start > -1 && end > start) {
			String left = line.substring(end + 2).trim();
			if (left.startsWith("@")) {
				return -1;
			}
			if (left.trim().equals("{")) {
				return -2;
			}

			String lineNum = line.substring(start + 2, end).trim();
			lineNum = lineNum.substring(lineNum.indexOf(":") + 1).trim();
			try {
				return Integer.parseInt(lineNum);
			} catch (NumberFormatException e) {
				return -1;
			}
		}
		return -1;
	}

}

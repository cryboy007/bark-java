package com.tao.common.core.common.file;


import com.tao.common.core.utils.UUIDUtil;

import java.io.InputStream;

public class FileWrap {
	private String id;
	private String fileName;
	private InputStream inputStream;
	private String clientFilePath;

	public FileWrap(String fileName, InputStream inputStream) {
		this(fileName, null, inputStream);
	}

	public FileWrap(String fileName, String clientFilePath, InputStream inputStream) {
		this.fileName = fileName;
		this.clientFilePath = clientFilePath;
		this.inputStream = inputStream;
	}

	public String getId() {
		if (id == null) {
			this.genId();
		}

		return id;
	}

	private synchronized void genId() {
		if (this.id == null) {
			this.id = UUIDUtil.generate();
		}
	}

	public String getFileName() {
		return fileName;
	}

	public String getClientFilePath() {
		return clientFilePath;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

}

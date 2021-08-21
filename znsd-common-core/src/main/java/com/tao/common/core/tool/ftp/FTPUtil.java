package com.tao.common.core.tool.ftp;

import com.tao.common.core.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;

@Component
@Slf4j
public class FTPUtil {
	
	@Autowired
	private FtpConfig ftpConfig;
	
	public String getNginxUrl() {
		return ftpConfig.getNginxUrl();
	}

	public FTPClient getFtpClient() {
		FTPClient ftpClient = new FTPClient();
		// 设置连接超时时间
		ftpClient.setConnectTimeout(ftpConfig.getConnectTimeout());
		// 设置ftp字符集
		ftpClient.setControlEncoding(ftpConfig.getEncoding());
		try {
			ftpClient.connect(ftpConfig.getHost(), ftpConfig.getPort());
			ftpClient.login(ftpConfig.getUsername(), ftpConfig.getPassword());
			// 设置文件传输模式为二进制，可以保证传输的内容不会被改变
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			// 设置被动模式，文件传输端口设置
			ftpClient.enterLocalPassiveMode();
			int replyCode = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(replyCode)) {
				ftpClient.logout();
				ftpClient.disconnect();
				throw new BizException("FtpError", "未连接到FTP，用户名或密码错误!");
			} else {
				if (ftpConfig.isDebug()) {
					log.info("FTP连接成功!");
					log.info("【initFtpClient】: 成功登录服务器,被动模式主机：" + ftpClient.getPassiveHost() + ":"
							+ ftpClient.getPassivePort() + ":" + ftpClient.getDataConnectionMode());
					log.info("【initFtpClient】: 成功登录服务器，远程主机：" + ftpClient.getRemoteAddress() + ":"
							+ ftpClient.getRemotePort());
					log.info("【initFtpClient】: 成功登录服务器,本地主机：" + ftpClient.getLocalAddress() + ":"
							+ ftpClient.getLocalPort());
					log.info("【initFtpClient】: 成功登录服务器,返回代码：" + ftpClient.getReplyCode() + ",显示状态"
							+ ftpClient.getStatus());
				}
				return ftpClient;
			}
		} catch (Exception e) {
			throw new BizException("FtpError", "FTP连接失败");
		}
	}

	public boolean uploadFile(String pathname, String filename, InputStream inputStream) {
		FTPClient ftpClient = null;
		try {
			ftpClient = getFtpClient();
			createDir(ftpClient, pathname);
			// 保存文件
			if (!ftpClient.storeFile(filename, inputStream)) {
				log.error("{}---》上传失败！", filename);
				return false;
			} else {
				log.info("{}---》上传成功！", filename);
				return true;
			}
		} catch (Exception e) {
			log.error(String.format("{%s}---》上传失败！", filename), e);
			return false;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error("close inputStream fail ------->>>{}", e);
				}
			}
			closeFtpClient(ftpClient);
		}
	}

	public boolean deleteFile(String filename) {
		FTPClient ftpClient = null;
		try {
			ftpClient = getFtpClient();
			if (!ftpClient.deleteFile(filename)) {
				log.error("{}---》删除失败！", filename);
				return false;
			} else {
				log.info("{}---》删除成功！", filename);
				return true;
			}
		} catch (Exception e) {
			log.error("{}---》删除失败！", filename);
			return false;
		} finally {
			closeFtpClient(ftpClient);
		}
	}

	private void closeFtpClient(FTPClient ftpClient) {
		if (ftpClient != null && ftpClient.isConnected()) {
			try {
				ftpClient.logout();
				ftpClient.disconnect();
			} catch (IOException e) {
				log.error("disconnect fail ------->>>{}", e);
			}
		}
	}

	private void createDir(FTPClient client, String path) throws IOException {
		String[] dirs = path.split("/");
		for (String dir : dirs) {
			if (StringUtils.isEmpty(dir)) {
				continue;
			}
			if (!client.changeWorkingDirectory(dir)) {
				if (client.makeDirectory(dir)) {
					client.changeWorkingDirectory(new String(dir.getBytes("UTF-8"), "iso-8859-1"));
				} else {
					log.error("{}---》文件夹创建失败！", dir);
				}
			}
		}
	}

}
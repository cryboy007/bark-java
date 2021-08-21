package com.tao.common.core.tool.ftp;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author william
 */
@Component
@ConfigurationProperties("ftp")
@Data
public class FtpConfig {
	private String host;
	private int port = 21;
	private String username;
	private String password;
	private String encoding = "UTF-8";
	private int connectTimeout = 1000 * 30;
	private String nginxUrl;
	private String csvUploadPath = "/csv/upload/";
	private boolean passiveMode = true;
	private boolean debug = true;
}

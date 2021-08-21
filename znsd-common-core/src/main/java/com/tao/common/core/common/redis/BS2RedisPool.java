package com.tao.common.core.common.redis;

import com.tao.common.core.common.exception.ExceptionWapper;
import com.tao.common.core.common.other.ServiceUtils;
import com.tao.common.core.utils.StringUtil;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class BS2RedisPool {

	private static BS2RedisPool defaultPool;

	public static void setDeaultPool(BS2RedisPool pool) {
		defaultPool = pool;
	}

	public static BS2RedisPool getDeaultPool() {
		if (defaultPool == null) {
			defaultPool = ServiceUtils.getService(BS2RedisPool.class);
		}
		return defaultPool;
	}

	private String host;
	private int port;
	private String password;

	private JedisPool pool;

	private JedisCluster cluster;

	public BS2RedisPool(int maxIdle, int maxTotal, String[] host, int[] port, int timeout) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(maxIdle);
		config.setMaxTotal(maxTotal);
		config.setMaxWaitMillis(timeout);
		config.setTestOnBorrow(true);
		config.setTestOnReturn(false);
		Set<HostAndPort> nodes = new LinkedHashSet<HostAndPort>();
		for (int i = 0; i < host.length; i++) {
			nodes.add(new HostAndPort(host[i], port[i]));
		}

		cluster = new JedisCluster(nodes, config);
	}

	public BS2RedisPool(int maxIdle, int maxTotal, String host, String password, int port, int timeout) {
		this(maxIdle, maxTotal, host, password, port, timeout, 0);
	}

	public BS2RedisPool(int maxIdle, int maxTotal, String host, String password, int port, int timeout, int db) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(maxIdle);
		config.setMaxTotal(maxTotal);
		config.setTestOnBorrow(false);
		config.setTestOnReturn(false);

		this.host = host;
		this.port = port;
		this.password = !StringUtil.isEmptyOrNull(password) ? password : null;

		pool = new JedisPool(config, this.host, this.port, timeout, this.password, db);
	}

	public JedisCommands getSource() {
		try {
			if (cluster != null) {
				return cluster;
			} else {
				return pool.getResource();
			}
		} catch (JedisConnectionException e) {
			throw ExceptionWapper.createBapException(e, host, port);
		} catch (Exception e) {
			throw ExceptionWapper.createBapException(e);
		}
	}

	public void destroy() throws IOException {
		if (cluster != null) {
			cluster.close();
		}

		if (pool != null) {
			pool.destroy();
		}
	}

	/**
	 * 显示关闭
	 * @param client
	 */
	public void close(JedisCommands client){
		if (client != null && client instanceof Jedis) {
			((Jedis) client).close();
		}
	}
}

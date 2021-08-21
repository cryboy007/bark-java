package com.tao.common.core.common.redis.util;

import com.tao.common.core.common.exception.ExceptionWapper;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.exceptions.JedisDataException;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Redis hash 相关的命令
 * 
 * @author hangw
 *
 */
public class RedisHashUtils {
	/**
	 * 将哈希表 key 中的域 field 的值设为 value 。
	 * 
	 * 如果 key 不存在，一个新的哈希表被创建并进行 HSET 操作。
	 * 
	 * 如果域 field 已经存在于哈希表中，旧值将被覆盖。
	 * 
	 * 可用版本： >= 2.0.0 时间复杂度： O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @param field
	 * @param value
	 * @return 如果 field 是哈希表中的一个新建域，并且值设置成功，返回true 。 如果哈希表中域 field 已经存在且旧值已被新值覆盖，返回
	 *         false 。
	 */
	public static boolean setValue(JedisCommands jedis, String key, String field, String value) {
		return jedis.hset(key, field, value) == 1;
	}

	/**
	 * 将哈希表 key 中的域 field 的值设置为 value ，当且仅当域 field 不存在。
	 * 
	 * 若域 field 已经存在，该操作无效。
	 * 
	 * 如果 key 不存在，一个新哈希表被创建并执行 HSETNX 命令。
	 * 
	 * @param jedis
	 * @param key
	 * @param field
	 * @param value
	 * @return 设置成功，返回true 。 如果给定域已经存在且没有操作被执行，返回 false 。
	 */
	public static boolean putValue(JedisCommands jedis, String key, String field, String value) {
		return jedis.hsetnx(key, field, value) == 1;
	}

	/**
	 * 删除哈希表中的一个或多个指定域，不存在的域将被忽略。
	 * 
	 * （在Redis2.4以下的版本里，HDEL每次只能删除单个域，如果你需要在一个原子时间内删除多个域，请将命令包含在MULTI/EXEC块内。）
	 * 
	 * 可用版本： >= 2.0.0 时间复杂度： O(N),N为要删除的域的数量
	 * 
	 * @param jedis
	 * @param key
	 * @param fields
	 * @return 被成功移除的域的数量，不包括被忽略的域。
	 */
	public static Long deleteFields(JedisCommands jedis, String key, String... fields) {
		return jedis.hdel(key, fields);
	}

	/**
	 * 查看哈希表key中，给定域field是否存在。
	 * 
	 * 可用版本： >= 2.0.0 时间复杂度： O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @param field
	 * @return 如果哈希表含有给定域，返回true。如果哈希表不含有给定域，或key不存在，返回false。
	 */
	public static boolean existsField(JedisCommands jedis, String key, String field) {
		return jedis.hexists(key, field);
	}

	/**
	 * 返回哈希表key中给定域field的值。
	 * 
	 * 可用版本： >= 2.0.0 时间复杂度： O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @param field
	 * @return 给定域的值。当给定域不存在或是给定key不存在时，返回null。
	 */
	public static String getValue(JedisCommands jedis, String key, String field) {
		return jedis.hget(key, field);
	}

	/**
	 * 返回哈希表key中，所有的域和值。
	 * 
	 * 在返回值里，紧跟每个域名（field name）之后是域的值（value），所以返回值的长度是哈希表大小的两倍。
	 * 
	 * 可用版本： >= 2.0.0 时间复杂度： O(N)，N为哈希表的大小。
	 * 
	 * @param jedis
	 * @param key
	 * @return 以列表形式返回哈希表的域和域的值。若key不存在，返回空列表。
	 */
	public static Map<String, String> getAllValue(JedisCommands jedis, String key) {
		return jedis.hgetAll(key);
	}

	/**
	 * 为哈希表key中的域field的值加上增量increment。
	 * 
	 * 增量也可以为负数，相当于对给定域进行减法操作。
	 * 
	 * 如果key不存在，一个新的哈希表被创建并执行HINCRBY命令。如果域field不存在，那么在执行命令前，域的值被初始化为0。
	 * 
	 * 对一个储存字符串值得域field执行HINCRBY命令将造成一个错误。本操作的值被限制在64位（bit）有效符号数字表示之内。
	 * 
	 * 可用版本： >= 2.0.0 时间复杂度： O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @param field
	 * @param value
	 * @return 执行HINCRBY命令之后，哈希表key中域field的值。
	 */
	public static Long incrValue(JedisCommands jedis, String key, String field, Long value) {
		return jedis.hincrBy(key, field, value);
	}

	/**
	 * 返回哈希表key中的所有域。
	 * 
	 * 可用版本： >= 2.0.0时间复杂度： O(N)，N为哈希表的大小。
	 * 
	 * @param jedis
	 * @param key
	 * @return 一个包含哈希表中所有域的表。当key不存在时，返回一个空表。
	 */
	public static Set<String> getAllFieldsByKey(JedisCommands jedis, String key) {
		return jedis.hkeys(key);
	}

	/**
	 * 返回哈希表key中域的数量。
	 * 
	 * 时间复杂度： O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @return 哈希表中域的数量。当key不存在时，返回0。
	 */
	public static Long getFieldsLengthByKey(JedisCommands jedis, String key) {
		return jedis.hlen(key);
	}

	/**
	 * 返回哈希表key中，一个或多个给定域的值。
	 * 
	 * 如果给定的域不存在于哈希表中，那么返回一个null值。
	 * 
	 * 因为不存在的key被当作一个空哈希表来处理，所以对一个不存在的key进行HMGET操作将返回一个只带有null值的表。
	 * 
	 * 可用版本：>=2.0.0时间复杂度： O(N)，N为给定域的数量。
	 * 
	 * @param jedis
	 * @param key
	 * @param fields
	 * @return 一个包含多个给定域的关联值的表，表值的排列顺序和给定域参数的请求顺序一样。
	 */
	public static List<String> getValuesByfields(JedisCommands jedis, String key, String... fields) {
		return jedis.hmget(key, fields);
	}

	/**
	 * 同时将多个field-value（域-值）对设置到哈希表key中
	 * 
	 * 此命令会覆盖哈希表中已存在的域。如果key不存在，一个空哈希表被创建并执行HMSET操作。
	 * 
	 * 可用版本：>=2.0.0时间复杂度： O(N)，N为field-value对的数量。
	 * 
	 * @param jedis
	 * @param key
	 * @param hash
	 * @return 如果命令执行成功，返回true，当key不是哈希表类型时，返回false
	 */
	public static boolean setFieldsValues(JedisCommands jedis, String key, Map<String, String> hash) {
		try {
			String result = jedis.hmset(key, hash);
			if ("OK".equals(result)) {
				return true;
			}
		} catch (JedisDataException e) {
			throw ExceptionWapper.createBapException(e);
		}
		return false;
	}

	/**
	 * 返回哈希表key中所有域的值。
	 * 
	 * 可用版本：>=2.0.0时间复杂度： O(N)，N为哈希表的大小。
	 * 
	 * @param jedis
	 * @param key
	 * @return 一个包含哈希表中所有值的表。当key不存在时，返回一个空表。
	 */
	public static List<String> getValues(JedisCommands jedis, String key) {
		return jedis.hvals(key);
	}

	/**
	 * 迭代哈希键中的键值对。
	 * 
	 * @param jedis
	 * @param key
	 * @param cursor
	 */
	public static ScanResult<Entry<String, String>> iterateHashkeyValue(JedisCommands jedis, String key,
																		String cursor) {
		return jedis.hscan(key, cursor);
	}

}

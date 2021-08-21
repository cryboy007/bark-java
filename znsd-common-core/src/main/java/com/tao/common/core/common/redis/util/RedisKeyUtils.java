package com.tao.common.core.common.redis.util;

import com.tao.common.core.common.exception.ExceptionWapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.exceptions.JedisDataException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Redis Key 相关的命令
 * 
 * @author hangw
 *
 */
public class RedisKeyUtils {
	/**
	 * 删除给定的一个或多个 key。
	 * 
	 * @see #delete(Jedis, String...)
	 * 
	 * @param jedis
	 * @param key
	 * @return 如果key存在并删除返回true，否则false
	 */
	public static boolean deleteKey(Jedis jedis, String key) {
		return deleteKeys(jedis, key) == 1;
	}

	/**
	 * 删除给定的一个或多个 key 。
	 * 
	 * 不存在的 key 会被忽略。
	 * 
	 * 可用版本： >= 1.0.0 时间复杂度： O(N)， N 为被删除的 key 的数量。 删除单个字符串类型的 key ，时间复杂度为O(1)。
	 * 删除单个列表、集合、有序集合或哈希表类型的 key ，时间复杂度为O(M)， M 为以上数据结构内的元素数量。
	 * 
	 * @param jedis
	 * @param keys
	 * @return 返回成功删除的key的数量
	 */
	public static Long deleteKeys(Jedis jedis, String... keys) {
		return jedis.del(keys);
	}
	
	/**
	 * 序列化给定 key ，并返回被序列化的值。
	 * 
	 * 序列化生成的值有以下几个特点：
	 *   它带有 64 位的校验和，用于检测错误， RESTORE 在进行反序列化之前会先检查校验和。
	 *   值的编码格式和 RDB 文件保持一致。
	 *   RDB 版本会被编码在序列化值当中，如果因为 Redis 的版本不同造成 RDB 格式不兼容，那么 Redis 会拒绝对这个值进行反序列化操作。
	 *   序列化的值不包括任何生存时间信息。
	 *   
	 * 可用版本：>= 2.6.0
	 * 
	 * 时间复杂度：查找给定键的复杂度为 O(1) ，对键进行序列化的复杂度为 O(N*M) ，其中 N 是构成 key 的 Redis 对象的数量，而 M 则是这些对象的平均大小。
	 * 如果序列化的对象是比较小的字符串，那么复杂度为 O(1) 。
	 * 
	 * @param jedis
	 * @param key
	 * @return 如果 key 不存在，那么返回 null 。否则，返回序列化之后的值。
	 */
	public static byte[] serializeValue(Jedis jedis, String key){
		return jedis.dump(key);		
	}
	
	/**
	 * 反序列化给定的序列化值，并将它和给定的 key 关联。
	 * 
	 * 在执行反序列化之前会先对序列化值的 RDB 版本和数据校验和进行检查，如果 RDB 版本不相同或者数据不完整的话，
	 * 那么 RESTORE 会拒绝进行反序列化，并返回一个错误。
	 * 
	 * 可用版本：>= 2.6.0
	 * 
	 * 时间复杂度：查找给定键的复杂度为 O(1) ，对键进行反序列化的复杂度为 O(N*M) ，其中 N 是构成 key 的 Redis 对象的数量，而 M 则是这些对象的平均大小。
	 * 有序集合(sorted set)的反序列化复杂度为 O(N*M*log(N)) ，因为有序集合每次插入的复杂度为 O(log(N)) 。
	 * 如果反序列化的对象是比较小的字符串，那么复杂度为 O(1) 。
	 * 
	 * @param jedis
	 * @param key
	 *        反序列化值后需要关联的key,此key不能与jedis中已存在的key相同，否则会报错：BUSYKEY Target key name already exists.
	 * @param ttl
	 *        以毫秒为单位为 key 设置生存时间；如果 ttl 为 0 ，那么不设置生存时间。
	 * @param serializedValue
	 *        需要反序列化的值
	 * @return 如果反序列化成功那么返回反序列化的值 ，否则返回null。
	 */
	public static String deSerializeValue(Jedis jedis, String key, int ttl, byte[] serializedValue) {
		String restoreValue = null;
		try {
			restoreValue = jedis.restore(key, ttl, serializedValue);
			if (restoreValue.equals("OK")) {
				return jedis.get(key);
			}
		} catch (JedisDataException jde) {
			throw ExceptionWapper.createBapException(jde);
		} catch (NullPointerException e) {
			throw ExceptionWapper.createBapException(e);
		}

		return restoreValue;
	}
	
	/**
	 * 检查给定 key 是否存在。
	 * 可用版本：>= 1.0.0
	 * 时间复杂度：O(1) 若 key 存在，返回 1 ，否则返回 0 。
	 * 
	 * @param jedis
	 * @param key
	 * @return 若 key 存在 ,返回true，否则返回false
	 */
	public static boolean isExistsKey(Jedis jedis, String key) {
		return jedis.exists(key);
	}
	
	/**
	 * 为给定 key 设置生存时间
	 * 
	 * @param jedis
	 * @param key
	 * @return 如果设置成功返回true，否则false
	 */
	public static boolean setExpireKey(Jedis jedis, String key, int seconds) {
		return expireKey(jedis, key, seconds) == 1;
	}
	
	/**
	 * 为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除。
	 * 
	 * 在 Redis 中，带有生存时间的 key 被称为『易失的』(volatile)。
	 * 生存时间可以通过使用 DEL 命令来删除整个 key 来移除，或者被 SET 和 GETSET 命令覆写(overwrite)，
	 * 这意味着，如果一个命令只是修改(alter)一个带生存时间的 key 的值而不是用一个新的 key 值来代替(replace)它的话，
	 * 那么生存时间不会被改变。
	 * 比如说，对一个 key 执行 INCR 命令，对一个列表进行 LPUSH 命令，
	 * 或者对一个哈希表执行 HSET 命令，这类操作都不会修改 key 本身的生存时间。
	 * 另一方面，如果使用 RENAME 对一个 key 进行改名，那么改名后的 key 的生存时间和改名前一样。
	 * 使用 PERSIST 命令可以在不删除 key 的情况下，移除 key 的生存时间，让 key 重新成为一个『持久的』(persistent) key 。
	 * 
	 * 更新生存时间: 可以对一个已经带有生存时间的 key 执行 EXPIRE 命令，新指定的生存时间会取代旧的生存时间。
	 * 
	 * 过期时间的精确度: 在 Redis 2.4 版本中，过期时间的延迟在 1 秒钟之内 —— 也即是，就算 key 已经过期，
	 * 但它还是可能在过期之后一秒钟之内被访问到，而在新的 Redis 2.6 版本中，延迟被降低到 1 毫秒之内。
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度：O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @param seconds
	 * @return 设置成功返回 1 。当 key 不存在或者不能为 key 设置生存时间时(比如在低于 2.1.3 版本的 Redis 中你尝试更新 key 的生存时间)，返回 0 。
	 */
	public static Long expireKey(Jedis jedis, String key, int seconds){		
		return jedis.expire(key, seconds);		
	}
	
	/**
	 *  作用和 setExpireKey 类似，都用于为 key 设置生存时间,但是它以毫秒为单位设置 key 的生存时间
	 *  
	 *  可用版本：>= 2.6.0
	 *  
	 *  时间复杂度：O(1)
	 *  
	 *  返回值：设置成功，返回 1;key 不存在或设置失败，返回 0
	 *  
	 * @param jedis
	 * @param key
	 * @param milliseconds 毫秒数
	 * @return 如果生存时间设置成功，返回true。否则返回false
	 */
	public static boolean setPexpireKey(Jedis jedis, String key, long milliseconds){
		return jedis.pexpire(key, milliseconds) == 1;
	}
	
	/**
	 *  作用和 setExpireKey 类似，都用于为 key 设置生存时间。
	 *  
	 *  不同在于 此方法接受的时间参数是 UNIX 时间戳(unix timestamp)。
	 *  
	 * @param jedis
	 * @param key
	 * @param unixTime
	 * @return 如果生存时间设置成功，返回true。否则返回false
	 */
	public static boolean setExpireAtKey(Jedis jedis, String key, long unixTime){	
		return jedis.expireAt(key, unixTime) == 1;
	}
	
	/**
	 *  作用和 setExpireAtKey 类似，都用于为 key 设置生存时间。
	 *  
	 *  不同在于 此方法接受的时间参数是 UNIX 时间戳(unix timestamp)。
	 *  
	 *  可用版本：>= 2.6.0
	 *  
	 *  时间复杂度：O(1)
	 *  
	 *  命令返回值：如果生存时间设置成功，返回 1 。当 key 不存在或没办法设置生存时间时，返回 0 。
	 *  
	 * @param jedis
	 * @param key
	 * @param millisecondstimestamp 以毫秒为单位的时间戳
	 * @return 如果生存时间设置成功，返回true。否则返回false
	 */
	public static boolean setPExpireAtKey(Jedis jedis, String key, long millisecondstimestamp){
		return jedis.pexpireAt(key, millisecondstimestamp) == 1;
	}
	
	/**
	 *  移除给定 key 的生存时间，将这个 key 从『易失的』(带生存时间 key )转换成『持久的』(一个不带生存时间、永不过期的 key )。
	 *  
	 *  可用版本：>= 2.2.0
	 *  
	 *  时间复杂度：O(1)
	 *  
	 *  返回值：当生存时间移除成功时，返回 1 .如果 key 不存在或 key 没有设置生存时间，返回 0 。
	 *  
	 * @param jedis
	 * @param key
	 * @return 如果生存时间移除成功，返回true。否则返回false
	 */
	public static boolean removeSurvivalTime(Jedis jedis, String key){	
		return jedis.persist(key) == 1;
	}	
	
	/**
	 * 查找所有符合给定模式 pattern 的 key 。
	 * 
	 * KEYS 的速度非常快，但在一个大的数据库中使用它仍然可能造成性能问题，
	 * 如果你需要从一个数据集中查找特定的 key ，你最好还是用 Redis 的集合结构(set)来代替。
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度：O(N)， N 为数据库中 key 的数量。
	 * 
	 * @param jedis
	 * @param pattern
	 *        需要匹配的正则表达式, 特殊符号用 \ 隔开
	 * @return 返回所有符合给定模式的 key 列表。
	 */
	public static List<String> getKeys(Jedis jedis, String pattern) {
		Set<String> keys = jedis.keys(pattern);
		return new ArrayList<String>(keys);
	}
	
	/**
	 * 将 key 原子性地从当前实例传送到目标实例的指定数据库上，一旦传送成功， key 保证会出现在目标实例上，而当前实例上的 key 会被删除。
	 * 
	 * 这个命令是一个原子操作，它在执行的时候会阻塞进行迁移的两个实例，直到以下任意结果发生：迁移成功，迁移失败，等到超时。
	 * 
	 * 命令的内部实现是这样的：它在当前实例对给定 key 执行 DUMP 命令 ，将它序列化，然后传送到目标实例，
	 * 目标实例再使用 RESTORE 对数据进行反序列化，并将反序列化所得的数据添加到数据库中；
	 * 当前实例就像目标实例的客户端那样，只要看到 RESTORE 命令返回 OK ，它就会调用 DEL 删除自己数据库上的 key 。
	 * 
	 * MIGRATE命令需要在给定的时间规定内完成 IO 操作。如果在传送数据时发生 IO 错误，或者达到了超时时间，
	 * 那么MIGRATE命令会停止执行，并返回一个特殊的错误： IOERR 。
	 * 当 IOERR 出现时，有以下两种可能：1,key可能存在于两个实例; 2,key可能只存在于当前实例
	 * 
	 * 可用版本：>= 2.6.0
	 * 
	 * 时间复杂度：这个命令在源实例上实际执行 DUMP 命令和 DEL 命令，在目标实例执行 RESTORE 命令，查看以上命令的文档可以看到详细的复杂度说明。
	 * key 数据在两个实例之间传输的复杂度为 O(N) 。
	 * 
	 * @param jedis
	 * @param key
	 * @param host
	 *        目标实例地址
	 * @param port
	 *        目标实例端口	 
	 * @param destinationDb
	 *        目标数据库dbIndex（redis默认使用数据库 0）
	 * @param timeout
	 *         参数以毫秒为格式 ,指定当前实例和目标实例进行沟通的最大间隔时间。
	 *         这说明操作并不一定要在 timeout 毫秒内完成，只是说数据传送的时间不能超过这个 timeout 数。
	 * @return
	 *       获取结果值为 OK表示迁移成功，返回true; 否则返回false
	 */
	public static boolean migrateKey(Jedis jedis, String key, String host, int port, int destinationDb, int timeout){
		return "OK".equals(jedis.migrate(host, port, key, destinationDb, timeout));	
	}
	
	/**
	 * 将当前数据库的 key 移动到给定的数据库 db 当中。
	 * 
	 * 如果当前数据库(源数据库)和给定数据库(目标数据库)有相同名字的给定 key ，或者 key 不存在于当前数据库，那么 MOVE 没有任何效果。
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度：O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @param dbIndex
	 * @return
	 *       移动成功返回 true ，失败则返回false
	 */
	public static boolean moveKey(Jedis jedis, String key, int dbIndex){
		return jedis.move(key, dbIndex) == 1;
	}
	
	/**
	 * OBJECT 命令允许从内部察看给定 key 的 Redis 对象。它通常用在除错(debugging)或者了解为了节省空间而对 key 使用特殊编码的情况。
	 * 当将Redis用作缓存程序时，你也可以通过 OBJECT 命令中的信息，决定 key 的驱逐策略(eviction policies)。
	 *
	 * OBJECT 命令有多个子命令：
	 *  OBJECT REFCOUNT <key> 返回给定 key 引用所储存的值的次数。此命令主要用于除错。
	 *  OBJECT ENCODING <key> 返回给定 key 锁储存的值所使用的内部表示(representation)[相应的编码类型]。
	 *  OBJECT IDLETIME <key> 返回给定 key 自储存以来的空转时间(idle， 没有被读取也没有被写入)，以秒为单位。
	 * 
	 * 对象可以以多种方式编码：
	 *  字符串可以被编码为 raw (一般字符串)或 int (用字符串表示64位数字是为了节约空间)。
	 *  列表可以被编码为 ziplist 或 linkedlist 。 ziplist 是为节约大小较小的列表空间而作的特殊表示。
	 *  集合可以被编码为 intset 或者 hashtable 。 intset 是只储存数字的小集合的特殊表示。
	 *  哈希表可以编码为 zipmap 或者 hashtable 。 zipmap 是小哈希表的特殊表示。
	 *  有序集合可以被编码为 ziplist 或者 skiplist 格式。 ziplist 用于表示小的有序集合，而 skiplist 则用于表示任何大小的有序集合。
	 *  
	 *  假如你做了什么让 Redis 没办法再使用节省空间的编码时(比如将一个只有 1 个元素的集合扩展为一个有 100 万个元素的集合)，
	 *  特殊编码类型(specially encoded types)会自动转换成通用类型(general type)。
	 *  
	 * 可用版本：>= 2.2.3
	 *  
	 * 时间复杂度：O(1)
	 *  
	 * @param jedis
	 * @param key
	 * @return
	 *       返回给定 key 引用所储存的值的次数
	 */
	public static Long objectRefcount(Jedis jedis, String key){
		return jedis.objectRefcount(key);	
	}
	
	/**
	 * @param jedis
	 * @param key
	 * @return
	 *       返回给定 key 自储存以来的空转时间(idle， 没有被读取也没有被写入)，以秒为单位。
	 */
	public static Long objectIdletime(Jedis jedis, String key){
		return jedis.objectIdletime(key);
	}
	
	/**
	 * @param jedis
	 * @param key
	 * @return
	 *        返回相应的编码类型。
	 */
	public static String objectEncoding(Jedis jedis, String key){
		return jedis.objectEncoding(key);
	}
	
	/**
	 * 以毫秒为单位返回 key 的剩余生存时间
	 * 
	 * 可用版本：>= 2.6.0
	 * 
	 * 复杂度：O(1)
	 * 
	 * 返回值：当 key 不存在时，返回 -2 。当 key 存在但没有设置剩余生存时间时，返回 -1 。否则，以毫秒为单位，返回 key 的剩余生存时间。
	 * 
	 * 注：在 Redis 2.8 以前，当 key 不存在，或者 key 没有设置剩余生存时间时，命令都返回 -1 。
	 * 
	 * @param jedis
	 * @param key
	 * @return
	 *        以毫秒为单位，返回 key 的剩余生存时间。0表示没有剩余生存时间
	 */
	public static Long getRemainingTimeToMilliseconds(Jedis jedis, String key){
		Long time = jedis.pttl(key);
		if(time == -2 || time == -1){
			time = 0L;
		}
		return time;
	}
	
	/**
	 * 以秒为单位返回 key 的剩余生存时间
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 复杂度：O(1)
	 * 
	 * 返回值：当 key 不存在时，返回 -2 。当 key 存在但没有设置剩余生存时间时，返回 -1 。否则，以秒为单位，返回 key 的剩余生存时间。
	 * 
	 * 注：在 Redis 2.8 以前，当 key 不存在，或者 key 没有设置剩余生存时间时，命令都返回 -1 。
	 * 
	 * @param jedis
	 * @param key
	 * @return
	 *        以秒为单位，返回 key 的剩余生存时间。0表示没有剩余生存时间
	 */
	public static Long getRemainingTimeToSeconds(Jedis jedis, String key){
		Long time = jedis.ttl(key);
		if(time == -2 || time == -1){
			time = 0L;
		}
		return time;
	}
	
	/**
	 * 从当前数据库中随机返回(不删除)一个 key 。
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度：O(1)
	 * 
	 * @param jedis
	 * @return
	 *       当数据库不为空时，返回一个 key 。当数据库为空时，返回 null 。   
	 */
	public static String randomKey(Jedis jedis){
		return jedis.randomKey();
	}
	
	/**
	 * 将 oldkey 改名为 newkey 。当 oldkey 和 newkey 相同，或者 oldkey 不存在时，返回一个错误。
	 * 当 newkey 已经存在时， RENAME 命令将覆盖旧值。
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度：O(1)
	 * 
	 * 命令返回值：改名成功时提示 OK ，失败时候返回一个错误。
	 * 
	 * @param jedis
	 * @return
	 *       改名成功返回true;否则为false
	 */
	public static boolean renameKey(Jedis jedis, String oldkey, String newkey) {
		try {
			String value = jedis.rename(oldkey, newkey);
			if (value.equals("OK")) {
				return true;
			}
		} catch (JedisDataException e) {
			throw ExceptionWapper.createBapException(e);
		}
		
		return false;
	}
	
	/**
	 * 当且仅当 newkey 不存在时，将 oldkey 改名为 newkey ，或者 oldkey 不存在时，返回一个错误。
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度：O(1)
	 * 
	 * 命令返回值：修改成功时，返回 1 。如果 newkey 已经存在，返回 0 。
	 * 
	 * @param jedis
	 * @return
	 *       改名成功返回true;否则为false
	 */
	public static boolean renamenxKey(Jedis jedis, String oldkey, String newkey) {
		try {
			Long value = jedis.renamenx(oldkey, newkey);
			if (value == 1) {
				return true;
			}
		} catch (JedisDataException e) {
			throw ExceptionWapper.createBapException(e);
		}
		
		return false;
	}
	
	/**
	 * 返回给定列表、集合、有序集合 key 中经过排序的元素。
	 * 
	 * 排序默认以数字作为对象，值被解释为双精度浮点数，然后进行比较。
	 * 
	 * 当需要对字符串进行排序时， 需要显式地在 SORT 命令之后添加 ALPHA 修饰符，即需要调用sortKey(jedis, key, sortingParameters)方法
	 * 
	 * @param jedis
	 * @param key
	 * @return
	 */
	public static List<String> sortKey(Jedis jedis, String key){
		return jedis.sort(key);
	}
	
	/**
	 * 通过设置的排序方式，返回给定列表、集合、有序集合 key 中经过排序的元素。
	 * 
	 * @param jedis
	 * @param key
	 * @param sortingParameters
	 * @return
	 */
	public static List<String> sortKey(Jedis jedis, String key, SortingParams sortingParameters){
		return jedis.sort(key, sortingParameters);
	}
	
	/**
	 * 保存：按默认排序方式排序的结果
	 * 
	 * 默认情况下， SORT 操作只是简单地返回排序结果，并不进行任何保存操作。通过给 STORE 选项指定一个 key 参数，可以将排序结果保存到给定的键上。
	 * 如果被指定的 key 已存在，那么原有的值将被排序结果覆盖。
	 * 
	 * @param jedis
	 * @param key
	 * @param newkey  
	 *        将排序结果保存到给定的键
	 * @return
	 *        返回排序结果的元素数量
	 */
	public static Long sortKey(Jedis jedis, String key, String newkey){
		return jedis.sort(key, newkey);
	}
	
	/**
	 * 保存：带有排序参数的排序结果
	 * 
	 * 默认情况下， SORT 操作只是简单地返回排序结果，并不进行任何保存操作。通过给 STORE 选项指定一个 key 参数，可以将排序结果保存到给定的键上。
	 * 如果被指定的 key 已存在，那么原有的值将被排序结果覆盖。
	 * 
	 * @param jedis
	 * @param key
	 * @param sortingParameters
	 *        排序方式参数
	 * @param newkey  
	 *        将排序结果保存到给定的键
	 * @return
	 *        返回排序结果的元素数量
	 */
	public static Long sortKey(Jedis jedis, String key, SortingParams sortingParameters, String newkey){
		return jedis.sort(key, sortingParameters,newkey);
	}
	
	/**
	 * 返回 key 所储存的值的类型。
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度：O(1)
	 * 	  
	 * @param jedis
	 * @param key
	 * @return
	 *       none (key不存在);string (字符串);list (列表);set (集合);zset (有序集);hash (哈希表)
	 */
	public static String getValueType(Jedis jedis, String key){
		return jedis.type(key);
	}
	
	
	/**
	 * 获取完整遍历后的所有key集合
	 * 
	 * 以 0 作为游标开始一次新的迭代， 一直调用 SCAN 命令， 直到命令返回游标 0 ， 我们称这个过程为一次完整遍历
	 * 
	 * @return
	 */
	public static List<String> getKeysByFullIterator(Jedis jedis){
		List<String> keys = new ArrayList<String>();		
		iteratorByCursor(jedis, "0", keys);		
		return keys;
	}
	
	/**
	 * SCAN 命令是一个基于游标的迭代器，用于增量式迭代当前数据库中的数据库键
	 * 
	 * 命令每次被调用之后， 都会向用户返回一个新的游标， 用户在下次迭代时需要使用这个新游标作为 SCAN 命令的游标参数，
	 *  以此来延续之前的迭代过程。
	 *  
	 * 当 SCAN 命令的游标参数被设置为 0 时， 服务器将开始一次新的迭代， 而当服务器向用户返回值为 0 的游标时， 表示迭代已结束。
	 *  
	 * @param jedis
	 * @param cursor
	 * @param keys       
	 * @return
	 */
	public static void iteratorByCursor(Jedis jedis, String cursor, List<String> keys){
		ScanResult<String> scanResult = jedis.scan(cursor);
		keys.addAll(scanResult.getResult());		
		if(!scanResult.getStringCursor().equals("0")){
			iteratorByCursor(jedis, scanResult.getStringCursor(), keys);		
		}
	}

}

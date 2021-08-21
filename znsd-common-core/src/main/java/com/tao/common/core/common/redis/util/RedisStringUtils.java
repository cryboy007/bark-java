package com.tao.common.core.common.redis.util;

import redis.clients.jedis.BitOP;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * Redis String 相关的命令
 * 
 * @author chenhui.hu
 *
 */
public class RedisStringUtils {
	/**
	 * 如果key已经存在并且是一个字符串，APPEND命令将value追加到key原来的值的末尾。
	 * 
	 * 如果key不存在，APPEND就简单地将给定key设为value。
	 * 
	 * 可用版本： >= 2.0.0 时间复杂度：平摊 O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @param value
	 * @return 追加value之后，key中字符串的长度。
	 */
	public static Long appendValue(Jedis jedis, String key, String value) {
		return jedis.append(key, value);
	}

	/**
	 * 计算给定字符串中，被设置为1的比特位的数量。
	 * 
	 * 不存在的key被当成是空字符串来处理，因此对一个不存在的key进行BITCOUNT操作，结果为0。
	 * 
	 * 可用版本： >= 2.6.0 时间复杂度： O(N)
	 * 
	 * @param jedis
	 * @param key
	 * @return 被设置为1的位的数量。
	 */
	public static Long countBitAmount(Jedis jedis, String key) {
		return jedis.bitcount(key);
	}

	/**
	 * 计算给定字符串中，被设置为1的比特位的数量。
	 * 
	 * 一般情况下，给定的整个字符串都会被进行计数，通过指定额外的start或end参数，可以让计数只在特定的位上进行。
	 * 
	 * start和end参数的设置和GETRANGE命令类似，都可以使用负数值：比如-1表示最后一个位，而-2表示倒数第二个位，以此类推。
	 * 
	 * 不存在的key被当成是空字符串来处理，因此对一个不存在的key进行BITCOUNT操作，结果为0。
	 * 
	 * 可用版本： >= 2.6.0 时间复杂度： O(N)
	 * 
	 * @param jedis
	 * @param key
	 * @return 被设置为1的位的数量。
	 */
	public static Long countBitAmount(Jedis jedis, String key, long start, long end) {
		return jedis.bitcount(key, start, end);
	}

	/**
	 * 对一个或多个保存二进制位的字符串key进行位元操作，并将结果保存到destkey上。
	 * 
	 * operation可以是AND、OR、NOT、XOR这四种操作中的任意一种
	 * 
	 * 除了NOT操作之外，其他操作都可以接受一个或多个key作为输入。
	 * 
	 * 当BITOP处理不同长度的字符串时，较短的那个字符串所缺少的部分会被看作0。
	 * 
	 * 空的key也被看作是包含0的字符串序列。
	 * 
	 * 可用版本： >= 2.6.0 时间复杂度： O(N)，当处理大型矩阵（matrix）或者进行大数据量的统计时，最好将任务指派到附属节点（slave）进行，
	 * 避免阻塞主节点。
	 * 
	 * @param jedis
	 * @param op
	 * @param destKey
	 * @param srcKeys
	 * @return 保存到destkey的字符串的长度，和输入key中最长的字符串长度相等。
	 */
	public static Long operationKeyAndSaveToDestkey(Jedis jedis, BitOP op, String destKey, String... srcKeys) {
		return jedis.bitop(op, destKey, srcKeys);
	}

	/**
	 * 将key中储存的数字值减一。
	 * 
	 * 如果key不存在，那么key的值会先被初始化为0，然后再执行DECR操作。
	 * 
	 * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
	 * 
	 * 本操作的值限制在64位（bit）有符号数字表示之内。
	 * 
	 * 可用版本： >= 1.0.0 时间复杂度： O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @return 执行DECR命令之后key的值。
	 */
	public static Long decrKeyByOne(Jedis jedis, String key) {
		return jedis.decr(key);
	}

	/**
	 * 将key所储存的值减去减量decrement。
	 * 
	 * 如果key不存在，那么key的值会先被初始化为0，然后再执行DECRBY操作。
	 * 
	 * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
	 * 
	 * 本操作的值限制在64位（bit）有符号数字表示之内。
	 * 
	 * 可用版本： >= 1.0.0 时间复杂度： O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @param integer
	 * @return 减去decrement之后，key的值。
	 */
	public static Long decrKeyByDecrement(Jedis jedis, String key, long integer) {
		return jedis.decrBy(key, integer);
	}

	/**
	 * 返回key所关联的字符串值。如果key不存在那么返回特殊值null。
	 * 
	 * 假如key储存的值不是字符串类型，返回一个错误，因为GET只能用于处理字符串值。
	 * 
	 * 可用版本： >= 1.0.0 时间复杂度： O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @return 当key不存在时，返回null，否则，返回key的值。如果key不是字符串类型，那么返回一个错误。
	 */
	public static String getStringByKey(Jedis jedis, String key) {
		return jedis.get(key);
	}

	/**
	 * 对key所储存的字符串值，获取指定偏移量上的位（bit）。
	 * 
	 * 可用版本： >= 2.2.0 时间复杂度： O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @param offset
	 * @return 当offset比字符串值的长度大，或者key不存在时，返回false。
	 */
	public static boolean getBit(Jedis jedis, String key, long offset) {
		return jedis.getbit(key, offset);
	}

	/**
	 * 返回key中字符串值的子字符串，字符串的截取范围由start和end两个偏移量决定（包括start和end在内）。
	 * 
	 * 负数偏移量表示从字符串最后开始计数，-1表示最后一个字符，-2表示倒数第二个，以此类推。
	 * 
	 * GETRANGE通过保证子字符串的值域（range）不超过实际字符串的值域来处理超出范围的值域请求。
	 * 
	 * 可用版本： >= 2.4.0 时间复杂度： O(N),N为要返回的字符串的长度。
	 * 复杂度最终由字符串的返回值长度决定，但因为从已有字符串中取出子字符串的操作非常廉价，所以对于长度不大的字符串， 该操作的复杂度也可看作O(1)。
	 * 
	 * @param jedis
	 * @param key
	 * @param startOffset
	 * @param endOffset
	 * @return 截取得出的子字符串。
	 */
	public static String getStringRange(Jedis jedis, String key, long startOffset, long endOffset) {
		return jedis.getrange(key, startOffset, endOffset);
	}

	/**
	 * 将给定key的值设为value，并返回key的旧值。
	 * 
	 * 当key存在但不是字符串类型时，返回一个错误。
	 * 
	 * 可用版本： >= 1.0.0 时间复杂度： O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @param value
	 * @return 返回给定key的旧值。当key没有旧值时，也即是，key不存在时，返回null。
	 */
	public static String getSetValue(Jedis jedis, String key, String value) {
		return jedis.getSet(key, value);
	}

	/**
	 * 将key中储存的数字值增一。
	 * 
	 * 如果key不存在，那么key的值会先被初始化为0，然后再执行INCR操作。
	 * 
	 * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
	 * 
	 * 本操作的值限制在64位（bit）有符号数字表示之内。
	 * 
	 * 可用版本： >= 1.0.0 时间复杂度： O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @return 执行INCR命令之后key的值。
	 */
	public static Long incrKeyByOne(Jedis jedis, String key) {
		return jedis.incr(key);
	}

	/**
	 * 将key所储存的值加上增量increment。
	 * 
	 * 如果key不存在，那么key的值会先被初始化为0，然后再执行INCRBY操作。
	 * 
	 * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
	 * 
	 * 本操作的值限制在64位（bit）有符号数字表示之内。
	 * 
	 * 可用版本： >= 1.0.0 时间复杂度： O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @param integer
	 * @return 加上increment之后，key的值。
	 */
	public static Long incrKeyByIncrement(Jedis jedis, String key, long integer) {
		return jedis.incrBy(key, integer);
	}

	/**
	 * 将key所储存的值加上浮点数增量increment。
	 * 
	 * 如果key不存在，那么INCRBYFLOAT会先将key的值设为0，然后再执行加法操作。
	 * 
	 * 如果命令执行成功，那么key的值会被更新为（执行加法之后的）新值，并且新值会以字符串的形式返回给调用者。
	 * 
	 * 当以下任意一个条件发生时，返回一个错误： •域field的值不是字符串类型（因为redis中的数字和浮点数都以字符串的形式保存，所以它们都属于字符串类型）
	 * •域field当前的值或给定的增量increment不能解释（parse）为双精度浮点数
	 * 
	 * 可用版本： >= 1.0.0 时间复杂度： O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @param value
	 * @return 执行命令之后key的值。
	 */
	public static Double incrKeyByFloat(Jedis jedis, String key, Double value) {
		return jedis.incrByFloat(key, value);
	}

	/**
	 * 返回所有（一个或多个）给定key的值。
	 * 
	 * 如果给定的key里面，有某个key不存在，那么这个key返回特殊值null。因此，该命令永不失败。
	 * 
	 * 可用版本：>=1.0.0时间复杂度： O(N)，N为给定key的数量。
	 * 
	 * @param jedis
	 * @param keys
	 * @return 一个包含所有给定key的值得列表。
	 */
	public static List<String> getValuesByKeys(Jedis jedis, String... keys) {
		return jedis.mget(keys);
	}

	/**
	 * 同时设置一个或多个key-value对。
	 * 
	 * 如果某个给定key已经存在，那么MSET会用新值覆盖原来的旧值。
	 * 
	 * MSET是一个原子性操作，所有给定key都会在同一时间内被设置，某些给定key被更新而另一些给定key没有改变的情况，不可能发生。
	 * 
	 * 可用版本：>=1.0.1 时间复杂度： O(N)，N为要设置的key数量。
	 * 
	 * @param jedis
	 * @param keysvalues
	 * @return 总是返回OK，因为MSET不可能失败
	 */
	public static boolean setKeysValues(Jedis jedis, String... keysvalues) {
		return "OK".equals(jedis.mset(keysvalues));
	}

	/**
	 * 同时设置一个或多个key-value对，当且仅当所有给定key都不存在。
	 * 
	 * 即使只有一个给定key已存在，MSETNX也会拒绝执行所有给定key的设置操作。
	 * 
	 * MSETNX是原子性的，因此它可以用作设置多个不同key表示不同字段的唯一性逻辑对象，所有字段要么全被设置，要么全不被设置。
	 * 
	 * 可用版本：>=1.0.1 时间复杂度： O(N)，N为要设置的key数量。
	 * 
	 * @param jedis
	 * @param keysvalues
	 * @return 当所有key都成功设置，返回true。如果所有给定key都设置失败，说明至少有一个key已经存在，那么返回false。
	 */
	public static boolean putKeysValues(Jedis jedis, String... keysvalues) {
		return jedis.msetnx(keysvalues) == 1;
	}

	/**
	 * 将值value关联到key，并将key的生存时间设为seconds（以秒为单位）。
	 * 
	 * 如果key已经存在，SETEX命令将覆写旧值。
	 * 
	 * SETEX是一个原子性操作，关联值和设置生存时间两个动作会在同一时间内完成，该命令在Redis用作缓存时，非常实用。
	 * 
	 * 可用版本：>=2.0.0 时间复杂度： O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @param seconds
	 * @param value
	 * @return 设置成功时返回OK。当seconds参数不合法时，返回一个错误。
	 */
	public static boolean setKeySecondsValue(Jedis jedis, String key, int seconds, String value) {
		return "OK".equals(jedis.setex(key, seconds, value));
	}

	/**
	 * 将值value关联到key，并将key的生存时间设为milliseconds（以毫秒为单位）。
	 * 
	 * 如果key已经存在，PSETEX命令将覆写旧值。
	 * 
	 * PSETEX是一个原子性操作，关联值和设置生存时间两个动作会在同一时间内完成，该命令在Redis用作缓存时，非常实用。
	 * 
	 * 可用版本：>=2.6.0 时间复杂度： O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @param milliseconds
	 * @param value
	 * @return 设置成功时返回true。当milliseconds参数不合法时，返回false。
	 */
	public static boolean setKeyMillisecondsValue(Jedis jedis, String key, long milliseconds, String value) {
		return "OK".equals(jedis.psetex(key, milliseconds, value));
	}

	/**
	 * 将值value关联到key。
	 * 
	 * 如果key已经持有其他值，SET就覆写旧值，无视类型。
	 * 
	 * 对于某个原本带有生存时间（TTL）的键来说，当SET命令成功在这个键上执行时，这个键原有的TTL将被清除。
	 * 
	 * 可用版本：>=1.0.0 时间复杂度： O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @param value
	 * @return 在Redis2.6.12版本以前，SET命令总是返回true。
	 *         从Redis2.6.12版本开始，SET在设置操作成功完成时，才返回true。
	 */
	public static boolean setKeyValue(Jedis jedis, String key, String value) {
		return "OK".equals(jedis.set(key, value));
	}

	/**
	 * 将值value关联到key。
	 * 
	 * 如果key已经持有其他值，SET就覆写旧值，无视类型。
	 * 
	 * 对于某个原本带有生存时间（TTL）的键来说，当SET命令成功在这个键上执行时，这个键原有的TTL将被清除。
	 * 
	 * 可选参数：从Redis2.6.12版本开始，SET命令可以通过一系列参数来改变 •EX second，等同于SETEX key second。 •PX
	 * millisecond，等同于PSETEX key millisecond value。 •NX，等同于SETEX key value。
	 * •XX，只有在键已经存在时，才对键进行设置操作。
	 * 
	 * 可用版本：>=1.0.0 时间复杂度： O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @param value
	 * @param nxxx
	 * @return 在Redis2.6.12版本以前，SET命令总是返回true。
	 *         从Redis2.6.12版本开始，SET在设置操作成功完成时，才返回true。
	 */
	public static boolean setKeyValue(Jedis jedis, String key, String value, String nxxx) {
		return "OK".equals(jedis.set(key, value, nxxx));
	}

	/**
	 * 对key所储存的字符串值，设置或清除指定偏移量上的位（bit）。
	 * 
	 * 位的设置或清除取决于value参数，可以是0也可以是1。当key不存在时，自动生成一个新的字符串值。
	 * 
	 * 字符串会进行伸展以确保它可以将value保存在指定的偏移量上。当字符串值进行伸展时，空白位置以0填充。
	 * 
	 * offset参数必须大于或等于0，小于2^32（bit映射被限制在512MB之内）。
	 * 
	 * 可用版本：>=2.2.0 时间复杂度： O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @param offset
	 * @param value
	 * @return 设置成功返回true。
	 */
	public static boolean setKeyValueOffsetBit(Jedis jedis, String key, long offset, String value) {
		return jedis.setbit(key, offset, value);
	}

	public static boolean setKeyValueOffsetBit(Jedis jedis, String key, long offset, boolean value) {
		return jedis.setbit(key, offset, value);
	}

	/**
	 * 将key的值设为value，当且仅当key不存在。
	 * 
	 * 若给定的key已经存在，则SETNX不做任何动作。
	 * 
	 * 可用版本：>=1.0.0 时间复杂度： O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @param value
	 * @return 设置成功时返回true。设置失败返回false。
	 */
	public static boolean putKeyValue(Jedis jedis, String key, String value) {
		return jedis.setnx(key, value) == 1;
	}

	/**
	 * 用value参数覆写给定key所储存的字符串值，从偏移量offset开始。
	 * 
	 * 不存在的key当作空白字符串处理。
	 * 
	 * SETRANGE命令会确保字符串足够长，以便将value设置在指定的偏移量上，如果给定key原来储存的字符串长度比偏移量小，
	 * 那么原字符和偏移量之间的空白将用零字节来填充。
	 * 
	 * 你能使用的最大偏移量是2^29-1（536870911），如果需要使用更大的空间，可以使用多个key。
	 * 
	 * 可用版本：>=2.2.0
	 * 
	 * 时间复杂度：对小（small）的字符串，平摊复杂度O(1)。否则为O(M)，M为value参数的长度。
	 * 
	 * @param jedis
	 * @param key
	 * @param offset
	 * @param value
	 * @return 被SETRANGE修改之后，字符串的长度。
	 */
	public static Long setRangeKeyValue(Jedis jedis, String key, long offset, String value) {
		return jedis.setrange(key, offset, value);
	}

	/**
	 * 返回key所储存的字符串值的长度。
	 * 
	 * 当key储存的不是字符串时，返回一个错误。
	 * 
	 * 可用版本：>=2.2.0 时间复杂度：O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @return 字符串值得长度。当key不存在时，返回0。
	 */
	public static Long getKeyStringLength(Jedis jedis, String key) {
		return jedis.strlen(key);
	}
}

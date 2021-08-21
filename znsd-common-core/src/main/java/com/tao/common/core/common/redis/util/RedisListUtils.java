package com.tao.common.core.common.redis.util;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.Jedis;

import java.util.List;


/**
 * Redis List 相关的命令
 *  
 * @author ying.han
 *
 */
public class RedisListUtils {	
	/**
	 * 当给定多个 key 参数时，按参数 key 的先后顺序依次检查各个列表，弹出第一个非空列表的头元素。
	 * 
	 * BLPOP 是列表的阻塞式(blocking)弹出原语。
	 * 它是 LPOP 命令的阻塞版本，当给定列表内没有任何元素可供弹出的时候，连接将被 BLPOP 命令阻塞，直到等待超时或发现可弹出元素为止。
	 * 
	 * 非阻塞行为：
	 *    当 BLPOP 被调用时，如果给定 key 内至少有一个非空列表，那么弹出遇到的第一个非空列表的头元素，
	 *    并和被弹出元素所属的列表的名字一起，组成结果返回给调用者。
	 *    
	 *    当存在多个给定 key 时， BLPOP 按给定 key 参数排列的先后顺序，依次检查各个列表。
	 *    假设现在有 job 、 command 和 request 三个列表，其中 job 不存在， command 和 request 都持有非空列表。
	 * 
	 * 考虑以下命令：
	 *    BLPOP job command request 0
	 *    BLPOP 保证返回的元素来自 command ，因为它是按”查找 job -> 查找 command -> 查找 request “这样的顺序，第一个找到的非空列表。 
	 *    
	 * 可用版本：>= 2.0.0
	 * 
	 * 时间复杂度：O(1)
	 * 
	 * @param jedis
	 * @param keys
	 * @return 返回一个含有两个元素的列表，第一个元素是被弹出元素所属的 key ，第二个元素是被弹出元素的值。如果列表为空，返回null
	 */
	public static List<String> getTopMember(Jedis jedis, String... keys){
		return jedis.blpop(keys);
	}
	
	/**
	 * 当给定多个 key 参数时，按参数 key 的先后顺序依次检查各个列表，弹出第一个非空列表的头元素。
	 * 
	 * BLPOP 是列表的阻塞式(blocking)弹出原语。
	 * 它是 LPOP 命令的阻塞版本，当给定列表内没有任何元素可供弹出的时候，连接将被 BLPOP 命令阻塞，直到等待超时或发现可弹出元素为止。
	 * 
	 * 阻塞行为：
	 *    如果所有给定 key 都不存在或包含空列表，那么 BLPOP 命令将阻塞连接，直到等待超时，
	 *    或有另一个客户端对给定 key 的任意一个执行 LPUSH 或 RPUSH 命令为止。
	 *  
	 *    超时参数 timeout 接受一个以秒为单位的数字作为值。超时参数设为 0 表示阻塞时间可以无限期延长(block indefinitely) 。
	 *         
	 * 可用版本：>= 2.0.0
	 * 
	 * 时间复杂度：O(1)
	 * 
	 * @param jedis
	 * @param timeout 超时参数， 以秒为单位
	 * @param keys
	 * @return 返回一个含有两个元素的列表，第一个元素是被弹出元素所属的 key ，第二个元素是被弹出元素的值。如果列表为空，返回null
	 */
	public static List<String> getTopMember(Jedis jedis, int timeout, String... keys){
		return jedis.blpop(timeout, keys);
	}

	/**
	 * 当给定多个 key 参数时，按参数 key 的先后顺序依次检查各个列表，弹出第一个非空列表的尾部元素。
	 * 
	 *当给定列表内没有任何元素可供弹出的时候，连接将被 BRPOP 命令阻塞，直到等待超时或发现可弹出元素为止。
	 * 
	 * 非阻塞行为：
	 *    当 BLPOP 被调用时，如果给定 key 内至少有一个非空列表，那么弹出遇到的第一个非空列表的尾部元素，
	 *    并和被弹出元素所属的列表的名字一起，组成结果返回给调用者。
	 *       
	 * 可用版本：>= 2.0.0
	 * 
	 * 时间复杂度：O(1)
	 * 
	 * @param jedis
	 * @param keys
	 * @return 返回一个含有两个元素的列表，第一个元素是被弹出元素所属的 key ，第二个元素是被弹出元素的值。如果列表为空，返回null
	 */
	public static List<String> getFootMember(Jedis jedis, String... keys){
		return jedis.brpop(keys);
	}

	
	/**
	 * 当给定多个 key 参数时，按参数 key 的先后顺序依次检查各个列表，弹出第一个非空列表的尾部元素。
	 * 
	 * 当给定列表内没有任何元素可供弹出的时候，连接将被 BRPOP 命令阻塞，直到等待超时或发现可弹出元素为止。
	 * 
	 * 阻塞行为：
	 *    如果所有给定 key 都不存在或包含空列表，那么 BRPOP 命令将阻塞连接，直到等待超时，
	 *  
	 *    超时参数 timeout 接受一个以秒为单位的数字作为值。超时参数设为 0 表示阻塞时间可以无限期延长(block indefinitely) 。
	 *         
	 * 可用版本：>= 2.0.0
	 * 
	 * 时间复杂度：O(1)
	 * 
	 * @param jedis
	 * @param timeout 超时参数， 以秒为单位
	 * @param keys
	 * @return 
	 *       返回一个含有两个元素的列表，第一个元素是被弹出元素所属的 key ，第二个元素是被弹出元素的值。
	 *       如果列表为空，返回null,假如在指定时间内没有任何元素被弹出，则返回一个 null 和等待时长。
	 */
	public static List<String> getFootMember(Jedis jedis, int timeout, String... keys){
		return jedis.brpop(timeout, keys);
	}
	
	/**
	 * 命令  在一个原子时间内，执行以下两个动作：
	 *   将列表 source 中的最后一个元素(尾元素)弹出，并返回给客户端。
	 *   将 source 弹出的元素插入到列表 target ，作为 target 列表的的头元素。
	 * 
	 * BRPOPLPUSH 是 RPOPLPUSH 的阻塞版本，当给定列表 source 不为空时， BRPOPLPUSH 的表现和 RPOPLPUSH 一样。
	 * 当列表 source 为空时， BRPOPLPUSH 命令将阻塞连接，直到等待超时，或有另一个客户端对 source 执行 LPUSH 或 RPUSH 命令为止。
	 * 超时参数 timeout 接受一个以秒为单位的数字作为值。超时参数设为 0 表示阻塞时间可以无限期延长(block indefinitely) 。
	 
	 * 可用版本：>= 2.0.0
	 * 
	 * 时间复杂度：O(1)
	 * 
	 * @param jedis
	 * @param timeout
	 * @param source
	 * @param target
	 * @return
	 *       假如在指定时间内没有任何元素被弹出，则返回一个 null;反之，返回被弹出元素的值
	 */
	public static String getFootMember(Jedis jedis, int timeout, String source, String target){
		return jedis.brpoplpush(source, target, timeout);
	}
	
	/**
	 * 返回列表 key 中，下标为 index 的元素。
	 * 
	 * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
	 * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
	 * 如果 key 不是列表类型，返回一个错误。
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度：O(N)， N 为到达下标 index 过程中经过的元素数量。因此，对列表的头元素和尾元素执行 LINDEX 命令，复杂度为O(1)。
	 * 
	 * @param jedis
	 * @param key
	 * @param index
	 * @return 列表中下标为 index 的元素。如果 index 参数的值不在列表的区间范围内(out of range)，返回 null 。
	 */
	public static String getMember(Jedis jedis, String key, long index){
		return jedis.lindex(key, index);
	}
	
	/**
	 * 将值 value 插入到列表 key 当中，位于值 pivot 之前或之后。
	 * 当 pivot 不存在于列表 key 时，不执行任何操作。
	 * 当 key 不存在时， key 被视为空列表，不执行任何操作。
	 * 如果 key 不是列表类型，返回一个错误。
	 * 
	 * 可用版本：>= 2.2.0
	 * 
	 * 时间复杂度:O(N)， N 为寻找 pivot 过程中经过的元素数量。
	 * 
	 * 返回值:如果命令执行成功，返回插入操作完成之后，列表的长度。
	 *        如果没有找到 pivot ，返回 -1 。如果 key 不存在或为空列表，返回 0 。
	 *        
	 * @param jedis
	 * @param key
	 * @param where
	 * @param pivot
	 * @param value
	 * @return  插入成功，返回true; 否则为false
	 */
	public static boolean insert(Jedis jedis, String key, LIST_POSITION where, String pivot, String value) {
		Long result = jedis.linsert(key, where, pivot, value);
		return result != 0 && result != -1;
	}
	
	/**
	 * 获取列表 key 的长度。
	 * 
	 * 如果 key 不存在，则 key 被解释为一个空列表，返回 0 .
	 * 如果 key 不是列表类型，返回一个错误。
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度：O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @return 列表 key 的长度。
	 */
	public static Long getLength(Jedis jedis, String key){
		return jedis.llen(key);
	}
	
	/**
	 * 移除并返回列表 key 的头元素。
	 *
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度：O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @return 列表的头元素。当 key 不存在时，返回 null 。
	 */
	public static String getTopMemberWithRemoved(Jedis jedis, String key){
		return jedis.lpop(key);
	}
	
	/**
	 * 将一个或多个值 value 插入到列表 key 的表头
	 * 
	 * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表头：
	 *  比如说，对空列表 mylist 执行命令 LPUSH mylist a b c ，列表的值将是 c b a ，
	 *  这等同于原子性地执行 LPUSH mylist a 、 LPUSH mylist b 和 LPUSH mylist c 三个命令。
	 *  
	 *  如果 key 不存在，一个空列表会被创建并执行 LPUSH 操作。
	 *  当 key 存在但不是列表类型时，返回一个错误。
	 *  
	 * 注： 在Redis 2.4版本以前的 LPUSH 命令，都只接受单个 value 值。
	 *
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度：O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @param values
	 * @return  执行插入操作后，列表的最新长度。
	 */
	public static Long insertTop(Jedis jedis, String key, String... values){
		return jedis.lpush(key, values);
	}
	
	/**
	 * 将值 value 插入到列表 key 的表头，当且仅当 key 存在并且是一个列表。
	 * 
	 * 和 LPUSH 命令相反，当 key 不存在时， LPUSHX 命令什么也不做。
     *
	 * 可用版本：>=  2.2.0
	 * 
	 * 时间复杂度：O(1)
	 *  
	 * @param jedis
	 * @param key
	 * @param values
	 * @return 执行插入操作后，列表的最新长度。
	 */
	public static Long insertTopWhenKeyExist(Jedis jedis, String key, String... values){
		return jedis.lpushx(key, values);
	}
	
	/**
     *返回列表key中指定区间内的元素，区间以偏移量start和stop指定。
     *
     *下标（index）参数start和stop都以0为底，0表示列表的第一个元素，1表示列表的第二个元素，以此类推。
     *
     *也可以使用负数下标，以-1表示列表的最后一个元素，-2表示列表的倒数第二个元素，以此类推。
     *
     *超出范围的下标：超出范围的下标值不会引起错误。如果start下标比列表的最大下标end还要大，那么LRANGE返回一个空列表。
     *
     *如果stop下标比end下标还要大，Redis将stop的值设置为end。
     *
	 * 可用版本：>=  1.0.0
	 * 
	 * 时间复杂度：O(S+N)，S为偏移量start，N为指定区间内元素的数量。
	 *  
	 * @param jedis
	 * @param key
	 * @param start
	 * @param end
	 * @return 一个列表，包含指定区间内的元素。
	 */
	public static List<String> getKeyMemberStartToStop(Jedis jedis,String key,long start,long end){
		return jedis.lrange(key, start, end);
	}
	
	/**
     *根据参数count的值，移除列表中与参数value相等的元素。
     *
     *count的值可以是以下几种：count>0：从表头开始向表尾搜索，移除与value相等的元素，数量为count。
     *					count<0：从表尾开始向表头搜索，移除与value相等的元素，数量为count的绝对值。
     *					count=0：移除表中所有与value相等的值。
	 * 可用版本：>=  1.0.0
	 * 
	 * 时间复杂度：O(N)，N为列表的长度。
	 *  
	 * @param jedis
	 * @param key
	 * @param count
	 * @param value
	 * @return 被移除元素的数量。因为不存在的key被视作空表，所以当key不存在时，LREM命令总是返回0。
	 */
	public static Long removeEqualsValueByCount(Jedis jedis,String key,long count,String value){
		return jedis.lrem(key, count, value);
	}
	
	/**
	 * 将列表key下标为index的元素的值设置为value。
	 * 
	 * 当index参数超出范围，或对一个空列表（key不存在）进行LSET时，返回一个错误。
	 * 
	 * 可用版本：>=  1.0.0
	 * 
	 * 时间复杂度：对头元素或尾元素进行LSET操作，复杂度为O(1)。其他情况下，为O(N)，N为列表的长度。
	 *  
	 * @param jedis
	 * @param key
	 * @param index
	 * @param value
	 * @return 操作成功返回true，否则返回false。
	 */
	public static boolean setValueByKeyIndex(Jedis jedis,String key,long index,String value){
		return "OK".equals(jedis.lset(key, index, value));
	}
	
	/**
	 * 对一个列表进行修剪（trim），就是说，让列表只保留指定区间内的元素，不在指定区间内的元素都将被删除。
	 * 
	 * 下标（index）参数start和stop都以0为底，0表示列表的第一个元素，1表示列表的第二个元素，以此类推。
     *
     * 也可以使用负数下标，以-1表示列表的最后一个元素，-2表示列表的倒数第二个元素，以此类推。
     * 
     * 当key不是列表类型时，返回一个错误。
     * 
     * 超出范围的下标：超出范围的下标值不会引起错误。如果start下标比列表的最大下标end还要大，或者start>stop，LTRIM返回一个空列表。
     * 			  如果stop下标比end下标还要大，Redis将stop的值设置为end。
	 * 
	 * 可用版本：>=  1.0.0 时间复杂度：O(N)，N为被移除的元素的数量。
	 *  
	 * @param jedis
	 * @param key
	 * @param start
	 * @param end
	 * @return 命令执行成功时返回true。
	 */
	public static boolean trimKeyMember(Jedis jedis,String key,long start,long end){
		return "OK".equals(jedis.ltrim(key, start, end));
	}
	
	/**
	 * 移除并返回列表 key 的尾元素。
	 *
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度：O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @return 列表的尾元素。当 key 不存在时，返回 null 。
	 */
	public static String getFootMemberWithRemoved(Jedis jedis, String key){
		return jedis.lpop(key);
	}
	
	/**
	 * 命令  在一个原子时间内，执行以下两个动作：
	 *   将列表 source 中的最后一个元素(尾元素)弹出，并返回给客户端。
	 *   将 source 弹出的元素插入到列表 destination ，作为 destination 列表的的头元素。
	 * 
	 * BRPOPLPUSH 是 RPOPLPUSH 的阻塞版本，当给定列表 source 不为空时， BRPOPLPUSH 的表现和 RPOPLPUSH 一样。
	 * 当列表 source 为空时， BRPOPLPUSH 命令将阻塞连接，直到等待超时，或有另一个客户端对 source 执行 LPUSH 或 RPUSH 命令为止。
	 * 超时参数 timeout 接受一个以秒为单位的数字作为值。超时参数设为 0 表示阻塞时间可以无限期延长(block indefinitely) 。
	 * 
	 * 如果source不存在，值null被返回，并且不执行其他动作。
	 * 
	 * 如果source和destination相同，则列表中的表尾元素被移动到表头，并返回该元素。
	 * 
	 * 可用版本：>= 1.2.0
	 * 
	 * 时间复杂度：O(1)
	 * 
	 * @param jedis
	 * @param srckey
	 * @param dstkey
	 * @return 被弹出的元素。
	 */
	public static String getFootMember(Jedis jedis,String srckey, String dstkey){
		return jedis.rpoplpush(srckey, dstkey);
	}
	
	/**
	 * 将一个或多个值 value 插入到列表 key 的表尾。
	 * 
	 * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表尾：
	 *  比如说，对空列表 mylist 执行命令 RPUSH mylist a b c ，列表的值将是 a b c ，
	 *  这等同于原子性地执行 RPUSH mylist a 、 RPUSH mylist b 和 RPUSH mylist c 三个命令。
	 *  
	 *  如果 key 不存在，一个空列表会被创建并执行 RPUSH 操作。
	 *  当 key 存在但不是列表类型时，返回一个错误。
	 *  
	 * 注： 在Redis 2.4版本以前的 RPUSH 命令，都只接受单个 value 值。
	 *
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度：O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @param strings
	 * @return  执行RPUSH操作后，列表的最新长度。
	 */
	public static Long insertValueToFoot(Jedis jedis, String key, String... strings){
		return jedis.rpush(key, strings);
	}
	
	/**
	 * 将值 value 插入到列表 key 的表尾，当且仅当 key 存在并且是一个列表。
	 * 
	 * 和 RPUSH 命令相反，当 key 不存在时， RPUSHX 命令什么也不做。
     *
	 * 可用版本：>=  2.2.0
	 * 
	 * 时间复杂度：O(1)
	 *  
	 * @param jedis
	 * @param key
	 * @param string
	 * @return RPUSHX命令执行之后，表的长度。
	 */
	public static Long insertFootWhenKeyExist(Jedis jedis, String key, String... string){
		return jedis.rpushx(key, string);
	}
	
}

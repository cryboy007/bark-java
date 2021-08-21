package com.tao.common.core.common.redis.util;

import com.tao.common.core.common.exception.ExceptionWapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.exceptions.JedisDataException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Redis Set 相关的命令
 *  
 * @author ying.han
 *
 */
public class RedisSetUtils {
	
	/**
	 * 将 member 元素加入到集合 key 当中，已经存在于集合的 ，member 元素将被忽略。
	 * 
	 * 假如 key 不存在，则创建一个只包含 member 元素作成员的集合。
	 * 当 key 不是集合类型时，返回一个错误。
	 * 
	 * @param jedis
	 * @param key
	 * @param member
	 * @return
	 */
	public static void addMember(Jedis jedis, String key, String member){
		 addMembers(jedis, key, member);
	} 
	
	/**
	 * 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略。
	 * 假如 key 不存在，则创建一个只包含 member 元素作成员的集合。
	 * 当 key 不是集合类型时，返回一个错误。
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度:O(N)， N 是被添加的元素的数量。
	 * 
	 * 注：在Redis2.4版本以前， SADD 只接受单个 member 值。
	 * 
	 * @param jedis
	 * @param key
	 * @param members
	 * @return
	 *       被添加到集合key中的新元素的数量，不包括被忽略的元素。
	 */
	public static Long addMembers(Jedis jedis, String key, String... members){
		return jedis.sadd(key, members);
	} 
	
	/**
	 * 返回集合 key 的基数(集合中元素的数量)。
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度:O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @return 集合的基数。当 key 不存在时，返回 0 。
	 */
	public static Long getMemberCount(Jedis jedis, String key){
		return jedis.scard(key);
	} 
	
	/**
	 * 以给定的第一个集合中所有数据为主，返回与之所有其他给定集合之间的差集。
	 * 
	 * 即：返回第一个集合中不在其他给定集合中存在的所有数据
	 * 
	 * 不存在的 key 被视为空集。
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度:O(N)， N 是所有给定集合的成员数量之和。
	 * 
	 * @param jedis
	 * @param keys
	 * @return 差集成员的列表
	 */
	public static Set<String> getDiffMembers(Jedis jedis, String... keys){
		return jedis.sdiff(keys);
	}
	
	/**
	 * 获取差集成员的数量
	 * 
	 * 这个命令的作用和 SDIFF 类似，但它将结果保存到 newkey 集合，而不是简单地返回结果集。
	 * 
	 * 即：返回第一个集合中不在其他给定集合中存在的所有元素数量
	 * 
	 * 如果 newkey 集合已经存在，则将其覆盖。newkey 可以是 key 本身。
	 * 
	 * 不存在的 key 被视为空集。
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度:O(N)， N 是所有给定集合的成员数量之和。
	 * 
	 * @param jedis
	 * @param keys
	 * @return 结果集中的元素数量。
	 */
	public static Long getDiffMembersCount(Jedis jedis, String newkey, String... keys){
		return jedis.sdiffstore(newkey,keys);
	}
	
	/**
	 * 返回一个集合的全部成员，该集合是所有给定集合的交集。
	 * 
	 * 不存在的 key 被视为空集。当给定集合当中有一个空集时，结果也为空集(根据集合运算定律)。
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度:O(N * M)， N 为给定集合当中基数最小的集合， M 为给定集合的个数。
	 * 
	 * @param jedis
	 * @param keys
	 * @return 交集成员的列表
	 */
	public static Set<String> getInterMembers(Jedis jedis, String... keys){
		return jedis.sinter(keys);
	}
	
	/**
	 * 获取交集成员的数量
	 * 
	 * 这个命令的作用和 SINTER 类似，但它将结果保存到 newkey 集合，而不是简单地返回结果集。
	 * 
	 * 如果 newkey 集合已经存在，则将其覆盖。newkey 可以是 key 本身。
	 * 	
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度:O(N * M)， N 为给定集合当中基数最小的集合， M 为给定集合的个数。
	 * 
	 * @param jedis
	 * @param keys
	 * @return 结果集中的成员数量。
	 */
	public static Long getInterMembersCount(Jedis jedis, String newkey, String... keys){
		return jedis.sinterstore(newkey,keys);
	}
	
	/**
	 * 判断 member 元素是否集合 key 的成员。
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度:O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @param member
	 * @return
	 */
	public static boolean isMember(Jedis jedis, String key, String member){
		return jedis.sismember(key, member);
	}
	
	/**
	 * 返回集合 key 中的所有成员。不存在的 key 被视为空集合。
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度:O(N)， N 为集合的基数。

	 * @param jedis
	 * @param key
	 * @return
	 */
	public static Set<String> getMembers(Jedis jedis, String key){
		return jedis.smembers(key);
	}
	
	/**
	 * 将 member 元素从 sourcekey 集合移动到 targetkey 集合。
	 * 
	 * SMOVE 是原子性操作。
	 * 
	 * 如果 sourcekey 集合不存在或不包含指定的 member 元素，则 SMOVE 命令不执行任何操作，仅返回 0 。
	 * 否则， member 元素从 sourcekey 集合中被移除，并添加到 targetkey 集合中去。
	 * 当 targetkey 集合已经包含 member 元素时， SMOVE 命令只是简单地将 sourcekey 集合中的 member 元素删除。
	 * 当 sourcekey 或 targetkey 不是集合类型时，返回一个错误。
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度:O(1)
	 * 
	 * 返回值：如果 member 元素被成功移除，返回 1 。如果 member 元素不是 sourcekey 集合的成员，并且没有任何操作对 targetkey 集合执行，那么返回 0 。
	 * 
	 * @param jedis
	 * @param sourcekey
	 * @param targetkey
	 * @param member
	 * @return
	 *       成功移动member返回true;否则返回false
	 */
	public static boolean moveMember(Jedis jedis, String sourcekey, String targetkey, String member) {
		try {
			Long value = jedis.smove(sourcekey, targetkey, member);
			if (value == 1) {
				return true;
			}
		} catch (JedisDataException e) {
			throw ExceptionWapper.createBapException(e);
		}

		return false;
	}
	
	/**
	 * 
	 * 移除集合中的一个随机元素并返回该随机元素。
	 * 
	 * 如果只想获取一个随机元素，但不想该元素从集合中被移除的话，可以使用 SRANDMEMBER 命令。
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度:O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @return 返回被移除的随机元素。当 key 不存在或 key 是空集时，返回 null 。
	 */
	public static String getRandomMemberByMoved(Jedis jedis, String key){
		return jedis.spop(key);
	}

	/** 
	 * 返回集合中的一个随机元素。
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度: O(1) 
	 * 
	 * @param jedis
	 * @param key
	 * @return 返回一个随机元素；如果集合为空，返回 null 。
	 */
	public static String getRandomMember(Jedis jedis, String key){
		return jedis.srandmember(key);
	}
	
	/** 
	 * 返回集合中count个数的随机元素数组。
	 * 
	 * 从 Redis 2.6 版本开始， SRANDMEMBER 命令接受可选的 count 参数：
	 *    如果 count 为正数，且小于集合基数，那么命令返回一个包含 count 个元素的数组，数组中的元素各不相同。如果 count 大于等于集合基数，那么返回整个集合。
	 *    如果 count 为负数，那么命令返回一个数组，数组中的元素可能会重复出现多次，而数组的长度为 count 的绝对值。
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度: O(N) ，N 为返回数组的元素个数。
	 * 
	 * @param jedis
	 * @param key
	 * @return 返回一个数组；如果集合为空，返回空数组。
	 */
	public static List<String> getRandomMember(Jedis jedis, String key, int count){
		return jedis.srandmember(key, count);
	}
	
	/**
	 * 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略。
	 * 当 key 不是集合类型，返回一个错误。
	 * 
	 * 在 Redis 2.4 版本以前， SREM 只接受单个 member 值。
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度:O(N)， N 为给定 member 元素的数量。
	 * 
	 * @param jedis
	 * @param key
	 * @param members
	 * @return
	 *       被成功移除的元素的数量，不包括被忽略的元素。
	 */
	public static Long removeMembers(Jedis jedis, String key, String... members){
		return jedis.srem(key, members);
	}
	
	/**
	 * 移除集合 key 中的一个member 元素，不存在 member 元素会被忽略。
	 * 当 key 不是集合类型，返回一个错误。
	 * 
	 * @param jedis
	 * @param key
	 * @param member
	 * @return
	 *       被成功移除的元素的数量，不包括被忽略的元素。
	 */
	public static boolean removeMember(Jedis jedis, String key, String member) {
		return removeMembers(jedis, key, member) == 1;
	}
	
	/**
	 * 返回一个集合的全部成员，该集合是所有给定集合的并集。
	 * 
	 * 不存在的 key 被视为空集。
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度:O(N)， N 是所有给定集合的成员数量之和。
	 * 
	 * @param jedis
	 * @param keys
	 * @return 并集成员的列表。
	 */
	public static Set<String> getUnionMembers(Jedis jedis, String... keys){
		return jedis.sunion(keys);
	}
	
	/**
	 * 这个命令类似于 SUNION 命令，但它将结果保存到 targetkey 集合，而不是简单地返回结果集。
	 * 
	 * 如果 targetkey 已经存在，则将其覆盖。targetkey 可以是 key 本身。
	 * 
	 * 可用版本：>= 1.0.0
	 * 
	 * 时间复杂度:O(N)， N 是所有给定集合的成员数量之和。
	 * 
	 * @param jedis
	 * @param keys
	 * @return 结果集中的元素数量。
	 */
	public static Long getUnionMembersCount(Jedis jedis, String targetkey, String... keys){
		return jedis.sunionstore(targetkey,keys);
	}
	
	/**
	 * 获取完整遍历后的所有key中的元素集合
	 * 
	 * 以 0 作为游标开始一次新的迭代， 一直调用  SSCAN 命令， 直到命令返回游标 0 ， 我们称这个过程为一次完整遍历
	 *
	 * @param jedis
	 * @param key
	 * @return
	 */
	public static List<String> getMembersByFullIterator(Jedis jedis, String key){
		List<String> members = new ArrayList<String>();
		iteratorMemberByCursor(jedis, key, "0", members);		
		return members;
	}
	
	/**
	 * SSCAN 命令是一个基于游标的迭代器，用于增量式迭代当前数据库中的数据库键中的元素
	 * 
	 * SSCAN 命令的第一个参数总是一个数据库键
	 * 
	 * 命令每次被调用之后， 都会向用户返回一个新的游标， 用户在下次迭代时需要使用这个新游标作为 SSCAN 命令的游标参数，
	 *  以此来延续之前的迭代过程。
	 *  
	 * 当SSCAN 命令的游标参数被设置为 0 时， 服务器将开始一次新的迭代， 而当服务器向用户返回值为 0 的游标时， 表示迭代已结束。
	 *  
	 * @param jedis
	 * @param key
	 * @param cursor
	 * @param members       
	 * @return
	 */
	public static void iteratorMemberByCursor(Jedis jedis, String key, String cursor, List<String> members) {
		ScanResult<String> scanResult = jedis.sscan(key, cursor);
		members.addAll(scanResult.getResult());
		if (!scanResult.getStringCursor().equals("0")) {
			iteratorMemberByCursor(jedis, key, scanResult.getStringCursor(), members);
		}
	}
}

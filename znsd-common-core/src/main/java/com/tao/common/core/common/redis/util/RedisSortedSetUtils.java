package com.tao.common.core.common.redis.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis SortedSet 相关的命令
 * 
 * @author ying.han
 *
 */
public class RedisSortedSetUtils {

	/**
	 * 将 member 元素及其 score 值加入到有序集合 key 当中， 如果 member 已经是有序集的成员，那么更新这个 member 的
	 * score 值，并通过重新插入这个 member 元素，来保证该 member 在正确的位置上。 score 值可以是整数值或双精度浮点数。 如果 key
	 * 不存在，则创建一个空的有序集并执行 ZADD 操作。 当 key 存在但不是有序集类型时，返回一个错误。
	 * 
	 * 在 Redis 2.4 版本以前， ZADD 每次只能添加一个元素。
	 * 
	 * 可用版本：>= 1.2.0
	 * 
	 * 时间复杂度:O(M*log(N))， N 是有序集的基数， M 为成功添加的新成员的数量。
	 * 
	 * 返回值:被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
	 * 
	 * @param jedis
	 * @param key
	 * @param score
	 * @param member
	 * @return
	 */
	public static void addMember(Jedis jedis, String key, double score, String member) {
		jedis.zadd(key, score, member);
	}

	/**
	 * 将一个或多个 member 元素及其 score 值加入到有序集 key 当中。 如果某个 member 已经是有序集的成员，那么更新这个 member
	 * 的 score 值，并通过重新插入这个 member 元素，来保证该 member 在正确的位置上。 score 值可以是整数值或双精度浮点数。 如果
	 * key 不存在，则创建一个空的有序集并执行 ZADD 操作。 当 key 存在但不是有序集类型时，返回一个错误。
	 * 
	 * 
	 * @param jedis
	 * @param key
	 * @param scoreMembers
	 * @return 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
	 */
	public static Long addMembers(Jedis jedis, String key, Map<String, Double> scoreMembers) {
		return jedis.zadd(key, scoreMembers);
	}

	/**
	 * 返回有序集 key的成员个数。
	 * 
	 * 可用版本：>= 1.2.0
	 * 
	 * 时间复杂度:O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @return 当 key 存在且是有序集类型时，返回有序集的成员个数。当 key 不存在时，返回 0 。
	 */
	public static Long getMembersCount(Jedis jedis, String key) {
		return jedis.zcard(key);
	}

	/**
	 * 返回有序集 key 中， score 值在 min 和 max 之间(默认包括 score 值等于 min 或 max )的成员的数量。
	 * 
	 * 可用版本：>= 2.0.0
	 * 
	 * 时间复杂度:O(log(N)+M)， N 为有序集的基数， M 为值在 min 和 max 之间的元素的数量。
	 * 
	 * @param jedis
	 * @param key
	 * @param min
	 * @param max
	 * @return score 值在 min 和 max 之间的成员的数量。
	 */
	public static Long getMembersCountByScoreRange(Jedis jedis, String key, String min, String max) {
		return jedis.zcount(key, min, max);
	}

	/**
	 * 返回有序集 key 中， score 值在 min 和 max 之间(默认包括 score 值等于 min 或 max )的成员的数量。
	 * 
	 * 可用版本：>= 2.0.0
	 * 
	 * 时间复杂度:O(log(N)+M)， N 为有序集的基数， M 为值在 min 和 max 之间的元素的数量。
	 * 
	 * @param jedis
	 * @param key
	 * @param min
	 * @param max
	 * @return score 值在 min 和 max 之间的成员的数量。
	 */
	public static Long getMembersCountByScoreRange(Jedis jedis, String key, double min, double max) {
		return jedis.zcount(key, min, max);
	}

	/**
	 * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
	 * 
	 * 有序集成员按 score 值递增(从小到大)次序排列。
	 * 
	 * 具有相同 score 值的成员按字典序(lexicographical order)来排列(该属性是有序集提供的，不需要额外的计算)。
	 * 
	 * 可用版本：>= 1.0.5
	 * 
	 * 时间复杂度:O(log(N)+M)， N 为有序集的基数， M 为值在 min 和 max 之间的元素的数量。
	 * 
	 * @param jedis
	 * @param key
	 * @param min
	 * @param max
	 * @return 指定区间内，带有 score 值的有序集成员的列表。
	 */
	public static Set<String> getMembersByScoreRangeArc(Jedis jedis, String key, String min, String max) {
		return jedis.zrangeByScore(key, min, max);
	}

	/**
	 * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
	 * 
	 * 有序集成员按 score 值递增(从小到大)次序排列。
	 * 
	 * 具有相同 score 值的成员按字典序(lexicographical order)来排列(该属性是有序集提供的，不需要额外的计算)。
	 * 
	 * 可用版本：>= 1.0.5
	 * 
	 * 时间复杂度:O(log(N)+M)， N 为有序集的基数， M 为值在 min 和 max 之间的元素的数量。
	 * 
	 * @param jedis
	 * @param key
	 * @param min
	 * @param max
	 * @return 指定区间内，带有 score 值的有序集成员的列表。
	 */
	public static Set<String> getMembersByScoreRangeArc(Jedis jedis, String key, double min, double max) {
		return jedis.zrangeByScore(key, min, max);
	}

	/**
	 * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
	 * 
	 * 有序集成员按 score 值递减(从大到小)的次序排列。
	 * 
	 * 具有相同 score 值的成员按字典序(lexicographical order)来排列(该属性是有序集提供的，不需要额外的计算)。
	 * 
	 * 可用版本：>= 2.2.0
	 * 
	 * 时间复杂度:O(log(N)+M)， N 为有序集的基数， M 为值在 min 和 max 之间的元素的数量。
	 * 
	 * @param jedis
	 * @param key
	 * @param min
	 * @param max
	 * @return 指定区间内，带有 score 值的有序集成员的列表。
	 */
	public static Set<String> getMembersByScoreRangeDesc(Jedis jedis, String key, String min, String max) {
		return jedis.zrevrangeByScore(key, min, max);
	}

	/**
	 * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
	 * 
	 * 有序集成员按 score 值递减(从大到小)的次序排列。
	 * 
	 * 具有相同 score 值的成员按字典序(lexicographical order)来排列(该属性是有序集提供的，不需要额外的计算)。
	 * 
	 * 可用版本：>= 1.0.5
	 * 
	 * 时间复杂度:O(log(N)+M)， N 为有序集的基数， M 为值在 min 和 max 之间的元素的数量。
	 * 
	 * @param jedis
	 * @param key
	 * @param min
	 * @param max
	 * @return 指定区间内，带有 score 值的有序集成员的列表。
	 */
	public static Set<String> getMembersByScoreRangeDesc(Jedis jedis, String key, double min, double max) {
		return jedis.zrevrangeByScore(key, min, max);
	}

	/**
	 * 为有序集 key 的成员 member 的 score 值加上增量 increment，并返回增量后的新score值。
	 * 
	 * 可以通过传递一个负数值 increment ，让 score 减去相应的值，比如 ZINCRBY key -5 member ，就是让 member 的
	 * score 值减去 5 。 当 key 不存在，或 member 不是 key 的成员时， ZINCRBY key increment member
	 * 等同于 ZADD key increment member 。 当 key 不是有序集类型时，返回一个错误。 score 值可以是整数值或双精度浮点数。
	 * 
	 * 可用版本：>= 1.2.0
	 * 
	 * 时间复杂度:O(log(N))
	 * 
	 * @param jedis
	 * @param key
	 * @param incrScore
	 *            增量score值
	 * @param member
	 * @return member成员的新 score 值
	 */
	public static Double incrScoreForMember(Jedis jedis, String key, double incrScore, String member) {
		return jedis.zincrby(key, incrScore, member);
	}

	/**
	 * 返回有序集 key 中，指定区间内的成员。
	 * 
	 * 其中成员的位置按 score 值递增(从小到大)来排序。
	 * 
	 * 具有相同 score 值的成员按字典序(lexicographical order )来排列。
	 * 
	 * 如果你需要成员按 score 值递减(从大到小)来排列，请使用 ZREVRANGE 命令。
	 * 
	 * 下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。
	 * 你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推。 超出范围的下标并不会引起错误。 比如说，当 start
	 * 的值比有序集的最大下标还要大，或是 start > stop 时， ZRANGE 命令只是简单地返回一个空列表。 另一方面，假如 stop
	 * 参数的值比有序集的最大下标还要大，那么 Redis 将 stop 当作最大下标来处理。 可以通过使用 WITHSCORES 选项，来让成员和它的
	 * score 值一并返回，返回列表以 value1,score1, ..., valueN,scoreN 的格式表示。
	 * 
	 * 可用版本：>= 1.2.0
	 * 
	 * 时间复杂度:O(log(N)+M)， N 为有序集的基数，而 M 为结果集的基数。
	 * 
	 * @param jedis
	 * @param key
	 * @param start
	 * @param end
	 * @return 指定区间内，带有 score 值的有序集成员的列表
	 */
	public static Set<String> getRangeMembersByArc(Jedis jedis, String key, long start, long end) {
		return jedis.zrange(key, start, end);
	}

	/**
	 * 返回有序集 key 中，指定区间内的成员。
	 * 
	 * 其中成员的位置按 score 值递减(从大到小)来排列。
	 * 
	 * 具有相同 score 值的成员按字典序(lexicographical order )来排列。
	 * 
	 * 下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。
	 * 你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推。 超出范围的下标并不会引起错误。 比如说，当 start
	 * 的值比有序集的最大下标还要大，或是 start > stop 时， ZRANGE 命令只是简单地返回一个空列表。 另一方面，假如 stop
	 * 参数的值比有序集的最大下标还要大，那么 Redis 将 stop 当作最大下标来处理。 可以通过使用 WITHSCORES 选项，来让成员和它的
	 * score 值一并返回，返回列表以 value1,score1, ..., valueN,scoreN 的格式表示。
	 * 
	 * 可用版本：>= 1.2.0
	 * 
	 * 时间复杂度:O(log(N)+M)， N 为有序集的基数，而 M 为结果集的基数。
	 * 
	 * @param jedis
	 * @param key
	 * @param start
	 * @param end
	 * @return 指定区间内，带有 score 值的有序集成员的列表
	 */
	public static Set<String> getRangeMembersByDesc(Jedis jedis, String key, long start, long end) {
		return jedis.zrevrange(key, start, end);
	}

	/**
	 * 返回有序集 key 中，指定区间内的成员。
	 * 
	 * 成员和它的 score 值一并返回，返回列表以 value1,score1, ..., valueN,scoreN 的格式表示。
	 * 
	 * 可用版本：>= 1.2.0
	 * 
	 * 时间复杂度:O(log(N)+M)， N 为有序集的基数，而 M 为结果集的基数。
	 * 
	 * @param jedis
	 * @param key
	 * @param start
	 * @param end
	 * @return 指定区间内，带有 member和score 值的有序集成员的Map集合
	 */
	public static Map<String, Double> getRangeMembersWithScores(Jedis jedis, String key, long start, long end) {
		Map<String, Double> membersWithScores = new HashMap<String, Double>();

		Set<Tuple> tuples = jedis.zrangeWithScores(key, start, end);
		for (Tuple tuple : tuples) {
			membersWithScores.put(tuple.getElement(), tuple.getScore());
		}
		return membersWithScores;
	}

	/**
	 * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score
	 * 值递增(从小到大)次序排列。 具有相同 score 值的成员按字典序(lexicographical
	 * order)来排列(该属性是有序集提供的，不需要额外的计算)。
	 * 
	 * 可用版本：>= 1.0.5
	 * 
	 * 时间复杂度:O(log(N)+M)， N 为有序集的基数， M 为值在 min 和 max 之间的元素的数量。
	 * 
	 * @param jedis
	 * @param key
	 * @param min
	 * @param max
	 * @return 指定score值区间内，带有 member和 score 值的有序集成员的列表。
	 */
	public static Map<String, Double> getMembersWithScoresByScoreRange(Jedis jedis, String key, String min,
			String max) {
		Map<String, Double> membersWithScores = new HashMap<String, Double>();
		Set<Tuple> tuples = jedis.zrangeByScoreWithScores(key, min, max);
		for (Tuple tuple : tuples) {
			membersWithScores.put(tuple.getElement(), tuple.getScore());
		}
		return membersWithScores;
	}

	/**
	 * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score
	 * 值递增(从小到大)次序排列。 具有相同 score 值的成员按字典序(lexicographical
	 * order)来排列(该属性是有序集提供的，不需要额外的计算)。
	 * 
	 * 可用版本：>= 1.0.5
	 * 
	 * 时间复杂度:O(log(N)+M)， N 为有序集的基数， M 为值在 min 和 max 之间的元素的数量。
	 * 
	 * @param jedis
	 * @param key
	 * @param min
	 * @param max
	 * @return 指定score值区间内，带有 member和 score 值的有序集成员的列表。
	 */
	public static Map<String, Double> getMembersWithScoresByScoreRange(Jedis jedis, String key, double min,
			double max) {
		Map<String, Double> membersWithScores = new HashMap<String, Double>();
		Set<Tuple> tuples = jedis.zrangeByScoreWithScores(key, min, max);
		for (Tuple tuple : tuples) {
			membersWithScores.put(tuple.getElement(), tuple.getScore());
		}
		return membersWithScores;
	}

	/**
	 * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递增(从小到大)顺序排列。
	 * 
	 * 排名以 0 为底，也就是说， score 值最小的成员排名为 0 。
	 * 
	 * 可用版本：>= 2.0.0
	 * 
	 * 时间复杂度:O(log(N))
	 * 
	 * @param jedis
	 * @param key
	 * @param member
	 * @return 如果 member 是有序集 key 的成员，返回 member 的排名。 如果 member 不是有序集 key 的成员，返回 null
	 *         。
	 */
	public static Long getMemberRankByArc(Jedis jedis, String key, String member) {
		return jedis.zrank(key, member);
	}

	/**
	 * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递减(从大到小)排序。
	 * 
	 * 排名以 0 为底，也就是说， score 值最大的成员排名为 0 。
	 * 
	 * 可用版本：>= 2.0.0
	 * 
	 * 时间复杂度:O(log(N))
	 * 
	 * @param jedis
	 * @param key
	 * @param member
	 * @return 如果 member 是有序集 key 的成员，返回 member 的排名。 如果 member 不是有序集 key 的成员，返回 null
	 *         。
	 */
	public static Long getMemberRankByDesc(Jedis jedis, String key, String member) {
		return jedis.zrevrank(key, member);
	}

	/**
	 * 移除有序集 key 中的一个成员，不存在将被忽略。
	 * 
	 * @param jedis
	 * @param key
	 * @param member
	 */
	public static void removeMember(Jedis jedis, String key, String member) {
		removeMembers(jedis, key, member);
	}

	/**
	 * 移除有序集 key 中的一个或多个成员，不存在的成员将被忽略。
	 * 
	 * 当 key 存在但不是有序集类型时，返回一个错误。
	 * 
	 * 可用版本：>= 1.2.0
	 * 
	 * 时间复杂度:O(M*log(N))， N 为有序集的基数， M 为被成功移除的成员的数量。
	 * 
	 * 在 Redis 2.4 版本以前， ZREM 每次只能删除一个元素。
	 * 
	 * @param jedis
	 * @param key
	 * @param members
	 * @return 被成功移除的成员的数量，不包括被忽略的成员。
	 */
	public static Long removeMembers(Jedis jedis, String key, String... members) {
		return jedis.zrem(key, members);
	}

	/**
	 * 移除有序集 key 中，指定排名(rank)区间内的所有成员。
	 * 
	 * 区间分别以下标参数 start 和 stop 指出，包含 start 和 stop 在内。
	 * 
	 * 下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。
	 * 你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推。
	 * 
	 * 可用版本：>= 2.0.0
	 * 
	 * 时间复杂度:O(log(N)+M)， N 为有序集的基数，而 M 为被移除成员的数量。
	 * 
	 * @param jedis
	 * @param key
	 * @param start
	 * @param end
	 * @return 被移除成员的数量。
	 */
	public static Long removeMembersByRank(Jedis jedis, String key, long start, long end) {
		return jedis.zremrangeByRank(key, start, end);
	}

	/**
	 * 移除有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。 自版本2.1.6开始， score
	 * 值等于 min 或 max 的成员也可以不包括在内，详情请参见 ZRANGEBYSCORE 命令。
	 * 
	 * 可用版本：>= 1.2.0
	 * 
	 * 时间复杂度:O(log(N)+M)， N 为有序集的基数，而 M 为被移除成员的数量。
	 * 
	 * @param jedis
	 * @param key
	 * @param start
	 * @param end
	 * @return 被移除成员的数量。
	 */
	public static Long removeMembersByScoreRange(Jedis jedis, String key, String start, String end) {
		return jedis.zremrangeByScore(key, start, end);
	}

	/**
	 * 移除有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。 自版本2.1.6开始， score
	 * 值等于 min 或 max 的成员也可以不包括在内，详情请参见 ZRANGEBYSCORE 命令。
	 * 
	 * 可用版本：>= 1.2.0
	 * 
	 * 时间复杂度:O(log(N)+M)， N 为有序集的基数，而 M 为被移除成员的数量。
	 * 
	 * @param jedis
	 * @param key
	 * @param start
	 * @param end
	 * @return 被移除成员的数量。
	 */
	public static Long removeMembersByScoreRange(Jedis jedis, String key, double start, double end) {
		return jedis.zremrangeByScore(key, start, end);
	}

	/**
	 * 返回有序集 key 中，成员 member 的 score 值。
	 * 
	 * 如果 member 元素不是有序集 key 的成员，或 key 不存在，返回 null 。
	 * 
	 * 可用版本：>= 1.2.0
	 * 
	 * 时间复杂度:O(1)
	 * 
	 * @param jedis
	 * @param key
	 * @param member
	 * @return member 成员的 score 值 ,如果key 不存在，返回 null
	 */
	public static Double getScore(Jedis jedis, String key, String member) {
		return jedis.zscore(key, member);
	}

	/**
	 * 计算给定的一个或多个有序集的并集，并将该并集(结果集)储存到 targetkey 。 默认情况下，结果集中某个成员的 score 值是所有给定集下该成员
	 * score 值之 和 。
	 * 
	 * 可用版本：>= 2.0.0
	 * 
	 * 时间复杂度:O(N)+O(M log(M))， N 为给定有序集基数的总和， M 为结果集的基数。
	 * 
	 * @param jedis
	 * @param targetkey
	 * @param keys
	 * @return 保存到 targetkey集合 的结果集的个数。
	 */
	public static Long getUnionsStoreCount(Jedis jedis, String targetkey, String... keys) {
		return jedis.zunionstore(targetkey, keys);
	}

	/**
	 * 计算给定的一个或多个有序集的并集，并将该并集(结果集)储存到 targetkey 。
	 * 
	 * 带参数选项ZParams: 使用 WEIGHTS 选项，你可以为 每个 给定有序集 分别 指定一个乘法因子(multiplication factor)，
	 * 每个给定有序集的所有成员的 score 值在传递给聚合函数(aggregation function)之前都要先乘以该有序集的因子。
	 * 
	 * 使用 AGGREGATE 选项，你可以指定并集的结果集的聚合方式。 默认使用的参数 SUM ，可以将所有集合中某个成员的 score 值之 和
	 * 作为结果集中该成员的 score 值；使用参数 MIN ， 可以将所有集合中某个成员的 最小 score 值作为结果集中该成员的 score 值； 而参数
	 * MAX 则是将所有集合中某个成员的 最大 score 值作为结果集中该成员的 score 值。
	 * 
	 * 默认情况下，结果集中某个成员的 score 值是所有给定集下该成员 score 值之 和 。
	 * 
	 * 可用版本：>= 2.0.0
	 * 
	 * 时间复杂度:O(N)+O(M log(M))， N 为给定有序集基数的总和， M 为结果集的基数。
	 * 
	 * @param jedis
	 * @param targetkey
	 * @param params
	 * @param keys
	 * @return 保存到 targetkey集合 的结果集的个数。
	 */
	public static Long getUnionsStoreCount(Jedis jedis, String targetkey, ZParams params, String... keys) {
		return jedis.zunionstore(targetkey, params, keys);
	}

	/**
	 * 计算给定的一个或多个有序集的并集，并将该并集(结果集)储存到 targetkey 。 默认情况下，结果集中某个成员的 score 值是所有给定集下该成员
	 * score 值之 和 。
	 * 
	 * 可用版本：>= 2.0.0
	 * 
	 * 时间复杂度:O(N)+O(M log(M))， N 为给定有序集基数的总和， M 为结果集的基数。
	 * 
	 * @param jedis
	 * @param targetkey
	 * @param keys
	 * @return targetkey集合 的结果集。
	 */
	public static Set<String> getUnionsStore(Jedis jedis, String targetkey, String... keys) {
		getUnionsStoreCount(jedis, targetkey, keys);
		return getRangeMembersByArc(jedis, targetkey, 0, -1);
	}

	/**
	 * 计算给定的一个或多个有序集的并集，并将该并集(结果集)储存到 targetkey 。
	 * 
	 * 带参数选项ZParams: 使用 WEIGHTS 选项，你可以为 每个 给定有序集 分别 指定一个乘法因子(multiplication factor)，
	 * 每个给定有序集的所有成员的 score 值在传递给聚合函数(aggregation function)之前都要先乘以该有序集的因子。
	 * 
	 * 使用 AGGREGATE 选项，你可以指定并集的结果集的聚合方式。 默认使用的参数 SUM ，可以将所有集合中某个成员的 score 值之 和
	 * 作为结果集中该成员的 score 值；使用参数 MIN ， 可以将所有集合中某个成员的 最小 score 值作为结果集中该成员的 score 值； 而参数
	 * MAX 则是将所有集合中某个成员的 最大 score 值作为结果集中该成员的 score 值。
	 * 
	 * 默认情况下，结果集中某个成员的 score 值是所有给定集下该成员 score 值之 和 。
	 * 
	 * 可用版本：>= 2.0.0
	 * 
	 * 时间复杂度:O(N)+O(M log(M))， N 为给定有序集基数的总和， M 为结果集的基数。
	 * 
	 * @param jedis
	 * @param targetkey
	 * @param params
	 * @param keys
	 * @return targetkey集合 的结果集。
	 */
	public static Set<String> getUnionsStore(Jedis jedis, String targetkey, ZParams params, String... keys) {
		getUnionsStoreCount(jedis, targetkey, params, keys);
		return getRangeMembersByArc(jedis, targetkey, 0, -1);
	}

	/**
	 * 计算给定的一个或多个有序集的交集，并将该交集(结果集)储存到 targetkey 。 默认情况下，结果集中某个成员的 score 值是所有给定集下该成员
	 * score 值之 和 。
	 * 
	 * 可用版本：>= 2.0.0
	 * 
	 * 时间复杂度:O(N*K)+O(M*log(M))， N 为给定 key 中基数最小的有序集， K 为给定有序集的数量， M 为结果集的基数。
	 * 
	 * @param jedis
	 * @param targetkey
	 * @param keys
	 * @return targetkey集合 的结果集。
	 */
	public static Set<String> getIntersStore(Jedis jedis, String targetkey, String... keys) {
		getIntersStoreCount(jedis, targetkey, keys);
		return getRangeMembersByArc(jedis, targetkey, 0, -1);
	}

	/**
	 * 计算给定的一个或多个有序集的交集，并将该交集(结果集)储存到 targetkey 。
	 * 
	 * 带参数选项ZParams: 使用 WEIGHTS 选项，你可以为 每个 给定有序集 分别 指定一个乘法因子(multiplication factor)，
	 * 每个给定有序集的所有成员的 score 值在传递给聚合函数(aggregation function)之前都要先乘以该有序集的因子。
	 * 
	 * 使用 AGGREGATE 选项，你可以指定并集的结果集的聚合方式。 默认使用的参数 SUM ，可以将所有集合中某个成员的 score 值之 和
	 * 作为结果集中该成员的 score 值；使用参数 MIN ， 可以将所有集合中某个成员的 最小 score 值作为结果集中该成员的 score 值； 而参数
	 * MAX 则是将所有集合中某个成员的 最大 score 值作为结果集中该成员的 score 值。
	 * 
	 * 默认情况下，结果集中某个成员的 score 值是所有给定集下该成员 score 值之 和 。
	 * 
	 * 可用版本：>= 2.0.0
	 * 
	 * 时间复杂度:O(N*K)+O(M*log(M))， N 为给定 key 中基数最小的有序集， K 为给定有序集的数量， M 为结果集的基数。
	 * 
	 * @param jedis
	 * @param targetkey
	 * @param params
	 * @param keys
	 * @return targetkey集合 的结果集。
	 */
	public static Set<String> getIntersStore(Jedis jedis, String targetkey, ZParams params, String... keys) {
		getIntersStoreCount(jedis, targetkey, params, keys);
		return getRangeMembersByArc(jedis, targetkey, 0, -1);
	}

	/**
	 * 计算给定的一个或多个有序集的交集，并将该交集(结果集)储存到 targetkey 。 默认情况下，结果集中某个成员的 score 值是所有给定集下该成员
	 * score 值之 和 。
	 * 
	 * 可用版本：>= 2.0.0
	 * 
	 * 时间复杂度:O(N*K)+O(M*log(M))， N 为给定 key 中基数最小的有序集， K 为给定有序集的数量， M 为结果集的基数。
	 * 
	 * @param jedis
	 * @param targetkey
	 * @param keys
	 * @return 保存到 targetkey集合 的结果集的个数。
	 */
	public static Long getIntersStoreCount(Jedis jedis, String targetkey, String... keys) {
		return jedis.zinterstore(targetkey, keys);
	}

	/**
	 * 计算给定的一个或多个有序集的交集，并将该交集(结果集)储存到 targetkey 。
	 * 
	 * 带参数选项ZParams: 使用 WEIGHTS 选项，你可以为 每个 给定有序集 分别 指定一个乘法因子(multiplication factor)，
	 * 每个给定有序集的所有成员的 score 值在传递给聚合函数(aggregation function)之前都要先乘以该有序集的因子。
	 * 
	 * 使用 AGGREGATE 选项，你可以指定并集的结果集的聚合方式。 默认使用的参数 SUM ，可以将所有集合中某个成员的 score 值之 和
	 * 作为结果集中该成员的 score 值；使用参数 MIN ， 可以将所有集合中某个成员的 最小 score 值作为结果集中该成员的 score 值； 而参数
	 * MAX 则是将所有集合中某个成员的 最大 score 值作为结果集中该成员的 score 值。
	 * 
	 * 默认情况下，结果集中某个成员的 score 值是所有给定集下该成员 score 值之 和 。
	 * 
	 * 可用版本：>= 2.0.0
	 * 
	 * 时间复杂度:O(N*K)+O(M*log(M))， N 为给定 key 中基数最小的有序集， K 为给定有序集的数量， M 为结果集的基数。
	 * 
	 * @param jedis
	 * @param targetkey
	 * @param params
	 * @param keys
	 * @return 保存到 targetkey集合 的结果集的个数。
	 */
	public static Long getIntersStoreCount(Jedis jedis, String targetkey, ZParams params, String... keys) {
		return jedis.zinterstore(targetkey, params, keys);
	}

	/**
	 * 获取完整遍历后的所有key中的元素(包括元素成员和元素分值)集合
	 * 
	 * 以 0 作为游标开始一次新的迭代， 一直调用 ZSCAN 命令， 直到命令返回游标 0 ， 我们称这个过程为一次完整遍历
	 *
	 * @param jedis
	 * @param key
	 * @return
	 */
	public static Map<String, Double> getMembersWithScoresByFullIterator(Jedis jedis, String key) {
		Map<String, Double> membersWithScores = new HashMap<String, Double>();
		iteratorMemberByCursor(jedis, key, "0", membersWithScores);
		return membersWithScores;
	}

	/**
	 * ZSCAN 命令是一个基于游标的迭代器，用于增量式迭代有序集合中的元素（包括元素成员和元素分值）。
	 * 
	 * ZSCAN 命令的第一个参数总是一个数据库键
	 * 
	 * 命令每次被调用之后， 都会向用户返回一个新的游标， 用户在下次迭代时需要使用这个新游标作为 ZSCAN 命令的游标参数， 以此来延续之前的迭代过程。
	 * 
	 * 当ZSCAN 命令的游标参数被设置为 0 时， 服务器将开始一次新的迭代， 而当服务器向用户返回值为 0 的游标时， 表示迭代已结束。
	 * 
	 * ZSCAN 命令返回的每个元素都是一个有序集合元素，一个有序集合元素由一个成员（member）和一个分值（score）组成。
	 * 
	 * @param jedis
	 * @param key
	 * @param cursor
	 * @param membersWithScores
	 * @return
	 */
	public static void iteratorMemberByCursor(Jedis jedis, String key, String cursor,
			Map<String, Double> membersWithScores) {
		ScanResult<Tuple> scanResult = jedis.zscan(key, cursor);
		List<Tuple> tuples = scanResult.getResult();
		for (Tuple tuple : tuples) {
			membersWithScores.put(tuple.getElement(), tuple.getScore());
		}
		if (!scanResult.getStringCursor().equals("0")) {
			iteratorMemberByCursor(jedis, key, scanResult.getStringCursor(), membersWithScores);
		}
	}

}

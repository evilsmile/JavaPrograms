/**
 * 不带虚拟结点的一致性Hash算法
 */


import java.util.TreeMap;
import java.util.SortedMap;

public class ConsistentHashingWithoutVirtualNode
{
	/*
	 *待添加到Hash环的服务器列表
	 */
	private static String[] servers = {"192.168.0.0:111", "192.168.0.1:111", "192.168.0.2:111", "192.168.0.3:111", "192.168.0.4:111"}; 

	/*
	 * key表示服务器的hash值，value表示服务器的名称
	 */
	private static SortedMap<Integer, String> sortedMap = new TreeMap<Integer, String>();

	/*
	 * 程序初始化，将所有的服务加入sortedMap中
	 */
	static 
	{
		for (int i = 0; i < servers.length; i++) {
			int hash = getHash(servers[i]);
			System.out.println("[" + servers[i] + "]加入集合中，其hash值为" + hash);
			sortedMap.put(hash, servers[i]);
		}
		System.out.println();
	}

	/*
	 * 使用FNV1_32_HASH算法计算服务器的Hash值
	 */
	private static int getHash(String str)
	{
		final int p = 16777619;
		int hash = (int)2166136261L;
		for (int i = 0; i < str.length(); i++) {
			hash = (hash ^ str.charAt(i)) * p;
		}

		hash += hash << 13;
		hash ^= hash >> 7;
		hash += hash << 3;
		hash ^= hash >> 17;
		hash += hash << 5;

		if (hash < 0) 
			hash = Math.abs(hash);
		return hash;
	}

	/*
	 * 得到应当路由到的结点
	 */
	private static String getServer(String node)
	{
		// 得到带路由的结点的Hash值
		int hash = getHash(node);
		// 得到大于该Hash值的所有Map
		SortedMap<Integer, String> subMap = 
			sortedMap.tailMap(hash);
		// 第一个key就是顺时针过去离node最近的那个结点
		Integer i = subMap.firstKey();
		//返回对应的服务名称
		return subMap.get(i);
	}

	public static void main(String[] args)
	{
		String[] nodes = {"127.0.0.1:1111", "221.226.0.1:2222", "10.211.0.1:3333"};
		for (int i = 0; i < nodes.length; i++) {
			System.out.println("[" + nodes[i] + "]的hash值为" +
					getHash(nodes[i]) + ", 被路由到结点[" + getServer(nodes[i]) + "]");
		}
	}
}

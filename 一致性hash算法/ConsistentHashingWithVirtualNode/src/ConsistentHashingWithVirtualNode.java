/**
 * 带虚拟结点的一致性Hash算法
 */

import java.util.TreeMap;
import java.util.SortedMap;
import java.util.LinkedList;
import java.util.List;

public class ConsistentHashingWithVirtualNode
{
	private static String[] servers = {"192.168.0.0:111", "192.168.0.1:111", "192.168.0.2:111",
		 "192.168.0.3:111", "192.168.0.4:111"};

	/*
	* 真实结点列表,考虑到服务器上线、下线的场景，即添加、删除的场景会比较频繁，这里使用LinkedList会更好
	*/
	private static List<String> realNodes = new LinkedList<String>();

	/*
	 *  虚拟节点，key表示虚拟节点的hash值，value表示虚拟节点的名称
	 */
	private static SortedMap<Integer, String> virtualNodes = new TreeMap<Integer, String>();

	private static final int VIRTUAL_NODES = 5;

	static
	{
		for (int i = 0; i < servers.length; i++) {
			realNodes.add(servers[i]);
		}

		for (String str : realNodes) {
			for (int i = 0; i < VIRTUAL_NODES; i++) {
				String virtualNodeName = str +"&&VN" + String.valueOf(i);
				int hash = getHash(virtualNodeName);
				System.out.println("虚拟结点[" + virtualNodeName + "]被添加, hash值为" + hash);
				virtualNodes.put(hash, virtualNodeName);
			}
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
			virtualNodes.tailMap(hash);
		// 第一个key就是顺时针过去离node最近的那个结点
		Integer i = subMap.firstKey();
		String virtualNode  = subMap.get(i);
		//返回对应的服务名称
		return virtualNode.substring(0, virtualNode.indexOf("&&"));
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

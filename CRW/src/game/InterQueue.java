package game;

import java.util.Hashtable;
import java.util.PriorityQueue;

public class InterQueue {

	boolean enabled;

	private PriorityQueue<String> queue;

	private Hashtable<String,Integer> ptrs = new Hashtable<String,Integer>();
	private Hashtable<String,Integer> params = new Hashtable<String,Integer>();

	public String[] INames = { "COLLISION",
			"WALL",
			"DAMAGE",
			"SHIELD",
			"TOP",
			"BOTTOM",
			"LEFT",
			"RIGHT",
			"RADAR",
			"RANGE",
			"TEAMMATES",
			"ROBOTS",
			"SIGNAL",
			"CHRONON"
	};

	public InterQueue()
	{
		enabled = false;
		queue = new PriorityQueue<String>();
		params.put("DAMAGE",150);
		params.put("SHIELD",25);
		params.put("TOP",20);
		params.put("BOTTOM",980);
		params.put("LEFT",20);
		params.put("RIGHT",980);
		params.put("RADAR",1000);
		params.put("RANGE",1000);
		params.put("CHRONON",0);

		for (String name:INames) {
			ptrs.put(name, -1);
		}

	}




	public int getParam(String name)
	{
		return params.get(name);
	}

	public void setParam(String name, int value) 
	{
		params.put(name, value);
	}

	public int getPtr(String name)
	{
		return ptrs.get(name);
	}

	public void setPtr(String name, int value)
	{
		ptrs.put(name, value);
	}

	public void add(String name) 
	{
		queue.add(name);
	}

	public void flush()
	{
		queue.clear();
	}

	public boolean hasNext() 
	{
		if (queue.size() == 0)
		{
			return false;
		}
		// Use this call to prune any interrupts that point to address -1.
		while (queue.size() > 0) {
			String next = queue.peek();
			if (ptrs.get(next) == -1)
				queue.remove(next); // Remove it!
			else
				return true;
		}
		return false;  // I guess they all pointed to -1...
	}

	public String next() {
		if (queue.size() > 0)
			return queue.poll();
		else
			throw new Error("Internal error: Tried getting next interrupt signal but none queued");
	}

}

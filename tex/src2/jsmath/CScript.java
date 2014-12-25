package jsmath;

import java.util.Queue;

/**
 * Implement loading of remote scripts using XMLHttpRequest, if
 *  possible, otherwise use a hidden IFRAME and fake it.  That
 *  method runs asynchronously, which causes lots of headaches.
 *  Solve these using Push command, which queues actions
 *  until files have loaded.
 *
 * 我们可能不需要这个东西......但是也得知道它到底在干什么...
 */
public class CScript {
	/** 事件队列. */
	private Queue<QueueItem> queue = new java.util.LinkedList<QueueItem>(); 

	/** true when an asynchronous action is being performed */
	private boolean blocking = false;
	
	/**
	 * 放入 queue 队列中的项目.
	 */
	private static class QueueItem {
		public ScriptCallable object;
		public String method;
		public Object data;
		
		public QueueItem(ScriptCallable object, String method, Object data) {
			this.object = object;
			this.method = method;
			this.data = data;
		}
		
		public void doCall() {
			object.scriptCall(method, data);
		}
	}

	/**
	 * Create the XMLHttpRequest object, if we can.
     *  Otherwise, use the iframe-based fallback method.
	 */
	public void Init() {
		// TODO: 我们因为用 java 不需要 xmlRequest, 所以这里也就不创建了.
	}

	/**
	 * 将要执行的函数放到队列中.
     *  Queue a function to be processed.
     *  If nothing is being loaded, do the pending commands.
	 * @param object
	 * @param method
	 * @param data
	 */
	public void Push(ScriptCallable object, String method, Object data) {
		// 先添加到队列中.
		this.queue.add(new QueueItem(object, method, data));
		
		// 然后执行.
		if (this.can_process())
			this.Process();
	}
	
	/**
	 * 执行挂起在队列中的函数.
     *  Do any pending functions (stopping if a file load is started)
	 */
	private void Process() {
		while (this.queue.isEmpty() == false && this.blocking == false) {
			// 取出队列中的第一个.
			QueueItem item = this.queue.poll();
			Queue<QueueItem> savedQueue = this.SaveQueue();
			// object=call[0]=item.object, method=call[1]=item.method, data=call[2]
			item.doCall();  // js 中允许 object 或直接 method, 转换为 java 我们还是必须要给出 object 吧.
			this.RestoreQueue(savedQueue);
		}
	}
	
	/**
	 * 返回 true 表示可以执行 Process(), 返回 false 表示不能执行 Process()
	 * @return
	 */
	private boolean can_process() {
		// 阻塞住则不能执行.
		if (this.blocking) return false;
		
		// 如果需要 body 而没有, 则不能执行.
		// TODO: if (this.needsBody && jsMath.document.body == null) return false;
		
		// 可以执行.
		return true;
	}

	/**
	 * 创建一个新的空的 queue 取代原来的 queue, 然后返回原来的 queue.
	 * 允许 push 发生在队列的前面.
	 *  Allows pushes to occur at the FRONT of the queue
     *  (so a command acts as a single unit, including anything
     *  that it pushes on to the command stack)
	 * @return
	 */
	private Queue<QueueItem> SaveQueue() {
		Queue<QueueItem> queue = this.queue;
		this.queue = new java.util.LinkedList<QueueItem>();
		return queue;
	}
	
	private void RestoreQueue(Queue<QueueItem> old_queue) {
		// 将所有原有的 old_queue 中所有数据添加到现有 queue 的后面(合并).
		this.queue.addAll(old_queue);
	}
	
	
}


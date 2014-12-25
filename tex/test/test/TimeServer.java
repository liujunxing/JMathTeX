package test;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Date;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class TimeServer {
	private static final int PORT = 9123;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		// 1. 用于侦听连接. listen for incoming connections.
		IoAcceptor acceptor = new NioSocketAcceptor();
		
		// 2. add a filter to the configuration
		//    This filter will log all information such as newly created sessions ......
		acceptor.getFilterChain().addLast( "logger", new LoggingFilter() );
		//    这个过滤器用于转换 数据为消息对象(或相反).
		acceptor.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName( "UTF-8" ))));
    
		// 3. define handler; 然后实现 TimeServerHandler 类.
		acceptor.setHandler(  new TimeServerHandler() );

		// 4. allow us to make socket-specific settings 
			// 配置: Input Buffer Size
		acceptor.getSessionConfig().setReadBufferSize( 2048 );
			// 配置: check for idle session.
	    acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, 10 );

	    // 5. Last: define listen address.
	    acceptor.bind( new InetSocketAddress(PORT) );

	}

}

/**
 * 定义 handler 用于服务于客户端请求 (client request for current time)
 *
 * 派生自 IoHandlerAdapter (使用 Adapter 模式)
 */
class TimeServerHandler extends IoHandlerAdapter {
	// 派生的.
	
	public void sessionOpened(IoSession session) throws java.lang.Exception {
		System.out.println("session opened: " + session.toString());
	}
	
	public void sessionClosed(IoSession session) throws java.lang.Exception {
		System.out.println("session closed: " + session.toString());
	}
	
	// 这个函数应该总是定义, 以处理出现异常的情况. 一般行为是记录/打印错误信息, 然后关闭 session.
	@Override
    public void exceptionCaught( IoSession session, Throwable cause ) throws Exception
    {
        cause.printStackTrace();
    }

	// 接受来自客户端的数据, 然后给客户端以响应.
	// 根据解码器不同, message 对象类型也不一定相同
	@Override
    public void messageReceived( IoSession session, Object message ) throws Exception
    {
        String str = message.toString();
        System.out.println("recv msg: " + str);
        // 如果来自客户端的消息为 "quit", 则关闭 session.
        //  the message received from the client is the word "quit", then the session will be closed.
        if( str.trim().equalsIgnoreCase("quit") ) {
            session.close(false);
            return;
        }

        // 向客户端写入时间, 也即数据.
        Date date = new Date();
        session.write( date.toString() + "\r\n" );
        System.out.println("Message written...: " + date.toString());
    }
	
	// 在配置的时间内没有消息, 则产生 idle 调用.
    @Override
    public void sessionIdle( IoSession session, IdleStatus status ) throws Exception
    {
        //System.out.println( "IDLE " + session.getIdleCount( status ));
        if (session.getIdleCount(status) > 10)
        	session.close(false);
    }

}
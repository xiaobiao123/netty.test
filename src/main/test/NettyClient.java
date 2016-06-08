import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {

	/*
	 * 服务器端口号
	 */
	private int port;

	/*
	 * 服务器IP
	 */
	private String host;

	public NettyClient(int port, String host) throws InterruptedException {
		this.port = port;
		this.host = host;
		start();
	}

	private void start() throws InterruptedException {
		// 配置客户端NIO线程组
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

		try {

			Bootstrap bootstrap = new Bootstrap();
			bootstrap.channel(NioSocketChannel.class);
			bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
			bootstrap.group(eventLoopGroup);
			bootstrap.remoteAddress(host, port);
			bootstrap.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel socketChannel) throws Exception {
					socketChannel.pipeline().addLast(new NettyClientHandler());
				}
			});
			// 发起异步连接操作
			ChannelFuture future = bootstrap.connect(host, port).sync();
			if (future.isSuccess()) {
				// Returns a channel where the I/O operation associated with
				// this  future takes place.
				SocketChannel socketChannel = (SocketChannel) future.channel();
				System.out.println("----------------connect server success----------------");
			}
			// 当代客户端链路关闭
			future.channel().closeFuture().sync();
		} finally {
			// 优雅退出，释放NIO线程组
			eventLoopGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws InterruptedException {

		NettyClient client = new NettyClient(9999, "localhost");

	}
}
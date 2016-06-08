import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.apache.log4j.Logger;

public class NettyServerBootstrap {

	private static Logger logger = Logger.getLogger(NettyServerBootstrap.class);

	private int port;

	public NettyServerBootstrap(int port) {
		this.port = port;
		bind();
	}

	private void bind() {
		// 配置服务端的NIO线程组
		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup();

		try {

			ServerBootstrap bootstrap = new ServerBootstrap();

			bootstrap.group(boss, worker);// 用于处理所有的事件和IO
			bootstrap.channel(NioServerSocketChannel.class);// 用于创建Channel实例
			bootstrap.option(ChannelOption.SO_BACKLOG, 1024); // 连接数
			bootstrap.option(ChannelOption.TCP_NODELAY, true); // 不延迟，消息立即发送
			bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true); // 长连接
			bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel socketChannel) throws Exception {
					ChannelPipeline p = socketChannel.pipeline();// ChannelPipeline通道
					p.addLast(new NettyServerHandler());// Inserts a {@link
														// ChannelHandler}s at
														// the last position of
														// this pipeline
					// ChannelPipeline
					// 在Netty中，每个Channel被创建的时候都需要被关联一个对应的pipeline（通道），这种关联关系是永久的（整个程序运行的生命周期中）。
					// ChannelPipeline可以理解成一个消息（
					// 或消息事件，ChanelEvent）流转的通道，在这个通道中可以被附上许多用来处理消息的handler，
					// 当消息在这个通道中流转的时候，如果有与这个消息类型相对应的handler，就会触发这个handler去执行相应的动作。它实现了Intercepting
					// Filter模式（个人理解与Filter Chain模式类似）。
				}

			});
			// 绑定端口，同步等待成功
			ChannelFuture f = bootstrap.bind(port).sync();
			if (f.isSuccess()) {
				logger.debug("启动Netty服务成功，端口号：" + this.port);
				System.out.println("启动Netty服务成功，端口号：" + this.port);
			}
			// 等待服务端监听端口关闭
			f.channel().closeFuture().sync();

		} catch (Exception e) {
			logger.error("启动Netty服务异常，异常信息：" + e.getMessage());
			System.out.println("启动Netty服务异常，异常信息：" + e.getMessage());
			e.printStackTrace();
		} finally {
			// 优雅退出，释放线程池资源
			boss.shutdownGracefully();
			worker.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws InterruptedException {

		NettyServerBootstrap server = new NettyServerBootstrap(9999);

	}

}
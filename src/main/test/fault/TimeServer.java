/*
 * Copyright 2013-2018 Lilinfeng.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fault;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author lilinfeng
 * @date 2014年2月14日
 * @version 1.0
 */
public class TimeServer {

	public void bind(int port) throws Exception {
		// 配置服务端的NIO线程组
		EventLoopGroup bossGroup = new NioEventLoopGroup();//服务端接收客户端的连接
		EventLoopGroup workerGroup = new NioEventLoopGroup();//另一个进行socketChannel的网络读写
		try {
			ServerBootstrap b = new ServerBootstrap();//netty 启动服务端的辅助类，目的是降低服务端的开发难度
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)//设置创建的channel为
			                .option(ChannelOption.SO_BACKLOG, 1024)//配置NioServerSocketChannel的TCP参数
			                .childHandler(new ChildChannelHandler());//绑定I/O事件处理类，日志记录对消息进行编码解码
			// 绑定端口，同步等待成功
			ChannelFuture f = b.bind(port).sync();//绑定端口监听端口，调用同步阻塞方法-----ChannelFuture f异步操作的通知回调
			// 等待服务端监听端口关闭
			f.channel().closeFuture().sync();//方法进行阻塞，等服务端连路关闭后main函数才推出
		} finally {
			// 优雅退出，释放线程池资源
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
		@Override
		protected void initChannel(SocketChannel arg0) throws Exception {
			//解决tcp粘包/拆包问题
			arg0.pipeline().addLast(new LineBasedFrameDecoder(1024));
			arg0.pipeline().addLast(new StringDecoder());
			arg0.pipeline().addLast(new TimeServerHandler());
		}

	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		int port = 8081;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				// 采用默认值
			}
		}
		new TimeServer().bind(port);
	}
}

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.UnsupportedEncodingException;

public class NettyServerHandler extends ChannelHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {

		ByteBuf buf = (ByteBuf) msg;
		// 接收客户端请求消息
		String recieved = getMessage(buf);
		System.out.println("服务器接收到消息：" + recieved);

		try {
			// 想服务端响应的消息返回给客户端
			ctx.writeAndFlush(getSendByteBuf("服务器返回消息"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 从ByteBuf中获取信息 使用UTF-8编码返回
	 */
	private String getMessage(ByteBuf buf) {

		byte[] con = new byte[buf.readableBytes()];
		buf.readBytes(con);
		try {
			return new String(con, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	private ByteBuf getSendByteBuf(String message) throws UnsupportedEncodingException {

		byte[] req = message.getBytes("UTF-8");
		ByteBuf pingMessage = Unpooled.buffer();// 创建一个新的Java堆大端法和相当小的初始容量缓冲区--无限地扩展其能力的需求
		pingMessage.writeBytes(req);

		return pingMessage;
	}
}
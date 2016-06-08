import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.UnsupportedEncodingException;

public class NettyClientHandler extends ChannelHandlerAdapter {
	private ByteBuf firstMessage;

	// 向服务器发送消息
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		byte[] data = "服务器，给我一个APPLE".getBytes();
		firstMessage = Unpooled.buffer();// 创建一个新的Java堆大端法和相当小的初始容量缓冲区--无限地扩展其能力的需求
		firstMessage.writeBytes(data);//传输源数组中指定的数据在当前这个缓冲区开始   并增加了传输的字节数
		ctx.writeAndFlush(firstMessage);
	}

	// 接收服务器返回消息
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		String rev = getMessage(buf);
		System.out.println("客户端收到服务器数据:" + rev);
	}

	private String getMessage(ByteBuf buf) {
		byte[] con = new byte[buf.readableBytes()];
		buf.readBytes(con);
		try {
			return new String(con, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
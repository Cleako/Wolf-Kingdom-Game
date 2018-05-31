package com.wk.server.net;

import com.wk.server.model.entity.player.Player;
import com.wk.server.net.rsc.LoginPacketHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;

/**
 * 
 * @author Imposter
 * 
 */
public class RSCConnectionHandler extends ChannelInboundHandlerAdapter {
	public static final AttributeKey<ConnectionAttachment> attachment = AttributeKey.valueOf("conn-attachment");
	public LoginPacketHandler loginHandler = new LoginPacketHandler();

	@Override
	public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
		final Channel channel = ctx.channel();
		channel.close();
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, final Object message) throws Exception {
		try {
			final Channel channel = ctx.channel();

			if (message instanceof Packet) {
				final Packet packet = (Packet) message;
				Player player = null;
				ConnectionAttachment att = channel.attr(attachment).get();
				if (att != null) {
					player = att.player.get();
				}
				if (player == null) {
					loginHandler.processLogin(packet, channel);
				} else {
					if (loginHandler != null) {
						loginHandler = null;
					}
					player.addToPacketQueue(packet);
				}
			}
		} finally {
			ReferenceCountUtil.release(message);
		}
	}
	
	@Override
	public void channelRegistered(final ChannelHandlerContext ctx) throws Exception {
		//final ConnectionAttachment att = new ConnectionAttachment();
		//ctx.channel().attr(attachment).set(att);
	}

	@Override
	public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
		final Channel channel = ctx.channel();
		final ConnectionAttachment conn_attachment = channel.attr(attachment).get();

		Player player = null;
		if (conn_attachment != null) {
			player = conn_attachment.player.get();
		}
		if (player != null) {
			player.unregister(false, "Channel closed");
		}
	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable e) throws Exception {
		if (ctx.channel().isActive())
			ctx.channel().close();
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}
}

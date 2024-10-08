package io.micronaut.docs.netty

import io.micronaut.core.annotation.NonNull
import io.netty.buffer.ByteBuf
import io.netty.buffer.CompositeByteBuf
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.channel.ChannelPromise
import io.netty.channel.embedded.EmbeddedChannel

import java.nio.channels.ClosedChannelException

class EmbeddedTestUtil {
    static void advance(EmbeddedChannel... channels) {
        boolean advanced
        do {
            advanced = false
            for (EmbeddedChannel channel : channels) {
                if (channel.hasPendingTasks()) {
                    advanced = true
                    channel.runPendingTasks()
                }
                channel.checkException()
            }
        } while (advanced);
    }

    static void connect(EmbeddedChannel server, EmbeddedChannel client) {
        new ConnectionDirection(server, client).register()
        def csDir = new ConnectionDirection(client, server)
        csDir.register()
        // PipeliningServerHandler fires a read() before this method is called, so we don't see it.
        csDir.readPending = true
    }

    private static class ConnectionDirection {
        final EmbeddedChannel source
        final EmbeddedChannel dest
        CompositeByteBuf sourceQueue
        List<ChannelPromise> sourceQueueFutures = new ArrayList<>();
        final Queue<ByteBuf> destQueue = new ArrayDeque<>()
        boolean readPending

        ConnectionDirection(EmbeddedChannel source, EmbeddedChannel dest) {
            this.source = source
            this.dest = dest
        }

        private void forwardNow(ByteBuf msg) {
            if (!dest.isOpen()) {
                msg.release()
                return
            }
            ByteBuf copy = msg.copy()
            msg.release()
            dest.writeOneInbound(copy)
            dest.pipeline().fireChannelReadComplete()
        }

        void register() {
            source.pipeline().addFirst(new ChannelOutboundHandlerAdapter() {
                boolean flushing = false

                @Override
                void write(ChannelHandlerContext ctx_, Object msg, ChannelPromise promise) throws Exception {
                    if (!(msg instanceof ByteBuf)) {
                        throw new IllegalArgumentException("Can only forward bytes, got " + msg)
                    }
                    if (!dest.isActive()) {
                        msg.release()
                        promise.tryFailure(new ClosedChannelException())
                        return
                    }
                    if (!msg.isReadable()) {
                        // no data
                        msg.release()
                        promise.setSuccess()
                        return
                    }

                    if (sourceQueue == null) {
                        sourceQueue = ((ByteBuf) msg).alloc().compositeBuffer()
                    }
                    sourceQueue.addComponent(true, (ByteBuf) msg)
                    if (!promise.isVoid()) {
                        sourceQueueFutures.add(promise)
                    }
                }

                @Override
                void flush(ChannelHandlerContext ctx_) throws Exception {
                    if (flushing) {
                        return // avoid reentrancy
                    }
                    flushing = true
                    while (sourceQueue != null) {
                        ByteBuf packet = sourceQueue
                        sourceQueue = null

                        def sqf = sourceQueueFutures
                        sourceQueueFutures = new ArrayList<>()
                        for (ChannelPromise promise : sqf) {
                            promise.trySuccess()
                        }

                        if (readPending || dest.config().isAutoRead()) {
                            dest.eventLoop().execute(() -> forwardNow(packet))
                            readPending = false
                        } else {
                            destQueue.add(packet)
                        }
                    }
                    flushing = false
                }

                @Override
                void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                    if (sourceQueue != null) {
                        sourceQueue.release()
                        sourceQueue = null
                    }
                }
            })
            dest.pipeline().addFirst(new ChannelDuplexHandler() {
                @Override
                void read(ChannelHandlerContext ctx) throws Exception {
                    if (destQueue.isEmpty()) {
                        readPending = true
                    } else {
                        ByteBuf msg = destQueue.poll()
                        ctx.channel().eventLoop().execute(() -> forwardNow(msg))
                    }
                }

                @Override
                void channelInactive(@NonNull ChannelHandlerContext ctx) throws Exception {
                    for (ChannelPromise f : sourceQueueFutures) {
                        f.tryFailure(new ClosedChannelException())
                    }
                    super.channelInactive(ctx)
                }

                @Override
                void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                    while (true) {
                        def buf = destQueue.poll()
                        if (buf == null) {
                            break
                        }
                        buf.release()
                    }
                }
            })
        }
    }
}


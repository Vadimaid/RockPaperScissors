package handler;

import data.PlayerData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import queue.CustomNonBlockingQueue;
import storage.PlayerChoiceStorage;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

public class ProcessingHandler extends ChannelInboundHandlerAdapter {

    private int readsCount = 0;
    private UUID playerId;

    private final CustomNonBlockingQueue<PlayerData> playerQueue;
    private final PlayerChoiceStorage playerChoiceStorage;

    public ProcessingHandler(
            CustomNonBlockingQueue<PlayerData> queue,
             PlayerChoiceStorage playerChoiceStorage
    ) {
        super();
        this.playerQueue = queue;
        this.playerChoiceStorage = playerChoiceStorage;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("New player connected from host: " + ctx.channel().remoteAddress().toString());
        this.playerId = UUID.randomUUID();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String data = this.getMessage(msg);
        if (readsCount > 0) {
            if (!Arrays.asList("rock", "paper", "scissors").contains(data.trim().toLowerCase())) {
                ctx.channel().writeAndFlush("Incorrect choice, please try again: ");
            } else {
                if (!this.playerChoiceStorage.playersChoice.containsKey(this.playerId)) {
                    this.playerChoiceStorage.playersChoice.put(this.playerId, data);
                }
            }
        } else {
            this.playerQueue.customQueue.offer(new PlayerData(playerId, data, ctx.channel()));
            readsCount++;
            ctx.channel().writeAndFlush("Waiting for opponent...\n");
        }
    }

    private String getMessage(Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        return buf.toString(StandardCharsets.UTF_8);
    }


}

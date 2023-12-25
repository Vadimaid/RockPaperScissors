package queue;

import data.GamePair;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import service.GameService;
import storage.PlayerChoiceStorage;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GamePairListener extends Thread {

    private final CustomNonBlockingQueue<GamePair> gamePairQueue;
    private final PlayerChoiceStorage playerChoiceStorage;

    private final ExecutorService pairExecutorService = Executors.newFixedThreadPool(5);
    private final ExecutorService decisionExecutorService = Executors.newFixedThreadPool(10);

    public GamePairListener(
            CustomNonBlockingQueue<GamePair> gamePairQueue,
            PlayerChoiceStorage playerChoiceStorage
    ) {
        this.gamePairQueue = gamePairQueue;
        this.playerChoiceStorage = playerChoiceStorage;
    }

    @Override
    public void run() {
        System.out.println("Game pair listener initialized!");
        while (true) {
            if (!this.gamePairQueue.customQueue.isEmpty()) {
                GamePair gamePair = this.gamePairQueue.customQueue.poll();
                Future<String> pl1Choice = this.getPlayerDecision(gamePair.getPlayer1().getPlayerId());
                Future<String> pl2Choice = this.getPlayerDecision(gamePair.getPlayer2().getPlayerId());
                Channel pl1Channel = gamePair.getPlayer1().getChannel();
                Channel pl2Channel = gamePair.getPlayer2().getChannel();
                this.pairExecutorService.submit(() -> {
                    while (!(pl1Choice.isDone() && pl2Choice.isDone())) {
                        Thread.yield();
                    }
                    try {
                        String pl1 = pl1Choice.get();
                        String pl2 = pl2Choice.get();

                        int result = new GameService().makeDecision(pl1, pl2);
                        if (result == 0) {
                            pl1Channel.writeAndFlush("Draw!\n");
                            pl1Channel.writeAndFlush("Enter your choice[rock, paper or scissors]: ");

                            pl2Channel.writeAndFlush("Draw!\n");
                            pl2Channel.writeAndFlush("Enter your choice[rock, paper or scissors]: ");

                            this.gamePairQueue.customQueue.offer(gamePair);
                        }
                        if (result == -1) {
                            pl1Channel.writeAndFlush("You win!!!").addListener(ChannelFutureListener.CLOSE);
                            pl2Channel.writeAndFlush("You lose...").addListener(ChannelFutureListener.CLOSE);
                        }
                        if (result == 1) {
                            pl2Channel.writeAndFlush("You win!!!").addListener(ChannelFutureListener.CLOSE);
                            pl1Channel.writeAndFlush("You lose...").addListener(ChannelFutureListener.CLOSE);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    private Future<String> getPlayerDecision(UUID playerId) {
        return this.decisionExecutorService.submit(() -> {
            String result = null;
            while (Objects.isNull(result)) {
                if (this.playerChoiceStorage.playersChoice.containsKey(playerId)) {
                    result = this.playerChoiceStorage.playersChoice.get(playerId);
                    this.playerChoiceStorage.playersChoice.remove(playerId);
                }
            }
            return result;
        });
    }
}

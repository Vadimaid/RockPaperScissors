package queue;

import data.GamePair;
import data.PlayerData;

import java.util.Objects;

public class LoggedPlayersListener extends Thread {

    private final CustomNonBlockingQueue<PlayerData> playerQueue;
    private final CustomNonBlockingQueue<GamePair> gamePairQueue;

    public LoggedPlayersListener(
            CustomNonBlockingQueue<PlayerData> playerQueue,
            CustomNonBlockingQueue<GamePair> gamePairQueue
    ) {
        this.playerQueue = playerQueue;
        this.gamePairQueue = gamePairQueue;
    }

    @Override
    public void run() {
        GamePair gamePair = new GamePair();
        PlayerData currentPlayer;
        System.out.println("Players queue listener initialized!");
        while (true) {
            if (Objects.nonNull(gamePair.getPlayer1()) && Objects.nonNull(gamePair.getPlayer2())) {
                gamePair.getPlayer1().getChannel().writeAndFlush("Opponent found: " + gamePair.getPlayer2().getLogin());
                gamePair.getPlayer1().getChannel().writeAndFlush("Enter your choice[rock, paper or scissors]: ");

                gamePair.getPlayer2().getChannel().writeAndFlush("Opponent found: " + gamePair.getPlayer1().getLogin());
                gamePair.getPlayer2().getChannel().writeAndFlush("Enter your choice[rock, paper or scissors]: ");

                this.gamePairQueue.customQueue.offer(gamePair);
                gamePair = new GamePair();
            }
            if (!this.playerQueue.customQueue.isEmpty()) {
                currentPlayer = this.playerQueue.customQueue.poll();
                if (Objects.isNull(gamePair.getPlayer1())) {
                    gamePair.setPlayer1(currentPlayer);
                } else {
                    gamePair.setPlayer2(currentPlayer);
                }
            }
        }
    }
}

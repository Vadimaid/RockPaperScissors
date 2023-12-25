import data.GamePair;
import data.PlayerData;
import queue.CustomNonBlockingQueue;
import queue.GamePairListener;
import queue.LoggedPlayersListener;
import server.NettyServer;
import storage.PlayerChoiceStorage;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Application initialization...");
        CustomNonBlockingQueue<PlayerData> playerQueue = new CustomNonBlockingQueue<>();
        CustomNonBlockingQueue<GamePair> gamePairQueue = new CustomNonBlockingQueue<>();
        PlayerChoiceStorage playerChoiceStorage = new PlayerChoiceStorage();
        new LoggedPlayersListener(playerQueue, gamePairQueue).start();
        new GamePairListener(gamePairQueue, playerChoiceStorage).start();
        new NettyServer(playerQueue, playerChoiceStorage).run();
    }
}

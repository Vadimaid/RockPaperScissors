package data;

public class GamePair {

    private PlayerData player1;
    private PlayerData player2;

    public GamePair() {
    }

    public PlayerData getPlayer1() {
        return player1;
    }

    public GamePair setPlayer1(PlayerData player1) {
        this.player1 = player1;
        return this;
    }

    public PlayerData getPlayer2() {
        return player2;
    }

    public GamePair setPlayer2(PlayerData player2) {
        this.player2 = player2;
        return this;
    }
}

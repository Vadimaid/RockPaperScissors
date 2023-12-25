package storage;


import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerChoiceStorage {

    public ConcurrentHashMap<UUID, String> playersChoice;

    public PlayerChoiceStorage() {
        this.playersChoice = new ConcurrentHashMap<>();
    }

}

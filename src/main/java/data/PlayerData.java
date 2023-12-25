package data;

import io.netty.channel.Channel;

import java.util.Objects;
import java.util.UUID;


public class PlayerData {

    private UUID playerId;
    private String login;
    private Channel channel;


    public PlayerData() {
    }

    public PlayerData(UUID playerId, String login, Channel channel) {
        this.playerId = playerId;
        this.login = login;
        this.channel = channel;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public PlayerData setPlayerId(UUID playerId) {
        this.playerId = playerId;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public PlayerData setLogin(String login) {
        this.login = login;
        return this;
    }

    public Channel getChannel() {
        return channel;
    }

    public PlayerData setChannel(Channel channel) {
        this.channel = channel;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.playerId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PlayerData o)) {
            return false;
        }
        return this.playerId.equals(o.getPlayerId());
    }
}

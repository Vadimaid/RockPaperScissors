package service;

public class GameService {

    public int makeDecision(String pl1Choice, String pl2Choice) {
        if (pl1Choice.equalsIgnoreCase(pl2Choice)) {
            return 0;
        }
        if ((pl1Choice.equalsIgnoreCase("rock") && pl2Choice.equalsIgnoreCase("scissors")) ||
                (pl1Choice.equalsIgnoreCase("scissors") && pl2Choice.equalsIgnoreCase("paper")) ||
                (pl1Choice.equalsIgnoreCase("paper") && pl2Choice.equalsIgnoreCase("rock"))) {
            return -1;
        }

        return 1;
    }
}

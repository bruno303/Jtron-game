package com.example.jtron.model.player;

import java.io.Serializable;

public class PlayerImages implements Serializable {

    private final String currentPlayer;
    private final String currentPlayerPath;
    private final String background;
    private final String stop;

    public PlayerImages(String currentPlayer, String currentPlayerPath,
                        String background, String stop) {
        this.currentPlayer = currentPlayer;
        this.currentPlayerPath = currentPlayerPath;
        this.background = background;
        this.stop = stop;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public String getCurrentPlayerPath() {
        return currentPlayerPath;
    }

    public String getBackground() {
        return background;
    }

    public String getStop() {
        return stop;
    }

    public static PlayerImagesBuilder builder() {
        return new PlayerImagesBuilder();
    }

    public static class PlayerImagesBuilder {
        private String currentPlayer;
        private String currentPlayerPath;
        private String background = "fundo.png";
        private String stop = "gameover.png";

        public PlayerImagesBuilder withCurrentPlayer(String currentPlayer) {
            this.currentPlayer = currentPlayer;
            return this;
        }

        public PlayerImagesBuilder withCurrentPlayerPath(String currentPlayerPath) {
            this.currentPlayerPath = currentPlayerPath;
            return this;
        }

        public PlayerImagesBuilder withBackground(String background) {
            this.background = background;
            return this;
        }

        public PlayerImagesBuilder withStop(String stop) {
            this.stop = stop;
            return this;
        }

        public PlayerImages build() {
            return new PlayerImages(currentPlayer, currentPlayerPath, background, stop);
        }
    }
}

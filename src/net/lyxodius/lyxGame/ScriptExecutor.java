package net.lyxodius.lyxGame;

/**
 * Created by Lyxodius on 19.06.2017.
 */
class ScriptExecutor {
    private final LyxGame lyxGame;

    ScriptExecutor(LyxGame lyxGame) {
        this.lyxGame = lyxGame;
    }

    void executeScript(String script) {
        if (script == null || script.isEmpty()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        String action = null;
        String[] params;
        char currentChar;
        for (int i = 0; i < script.length(); i++) {
            currentChar = script.charAt(i);
            if (currentChar == '(') {
                action = sb.toString();
                sb = new StringBuilder();
            } else if (currentChar == ')') {
                params = sb.toString().split(",");
                executeAction(action, params);
            } else {
                sb.append(currentChar);
            }
        }
    }

    private void executeAction(String action, String[] args) {
        if (action.equals("teleport")) {
            lyxGame.teleportPlayer(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        }
    }
}

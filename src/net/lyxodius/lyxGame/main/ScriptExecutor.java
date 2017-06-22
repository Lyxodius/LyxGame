package net.lyxodius.lyxGame.main;

import java.util.ArrayList;

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
        ArrayList<String> params = new ArrayList<>();
        char currentChar;
        boolean stringMode = false;
        for (int i = 0; i < script.length(); i++) {
            currentChar = script.charAt(i);
            if (currentChar == '"') {
                if (stringMode) {
                    sb = new StringBuilder(sb.toString().substring(0, sb.length()));
                }
                stringMode = !stringMode;
            } else if (!stringMode) {
                if (currentChar == ',') {
                    params.add(sb.toString());
                    sb = new StringBuilder();
                } else if (currentChar == '(') {
                    action = sb.toString();
                    sb = new StringBuilder();
                } else if (currentChar == ')') {
                    params.add(sb.toString());
                    sb = new StringBuilder();
                    executeAction(action, params);
                } else {
                    sb.append(currentChar);
                }
            } else {
                sb.append(currentChar);
            }
        }
    }

    private void executeAction(String action, ArrayList<String> args) {
        if (action.equals("teleport")) {
            lyxGame.teleportPlayer(args.get(0), Integer.parseInt(args.get(1)), Integer.parseInt(args.get(2)));
        } else if (action.equals("message")) {
            lyxGame.movementStopped = true;
            lyxGame.getMessageBox().showMessage(args.get(0));
        }
    }
}

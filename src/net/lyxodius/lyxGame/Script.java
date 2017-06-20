package net.lyxodius.lyxGame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Lyxodius on 18.06.2017.
 */
public class Script {
    public final String name;
    private final String script;

    private Script(String name, String script) {
        this.name = name;
        this.script = script;
    }

    public static Script loadFromFile(String name) {
        Script script = null;

        if (name != null && !name.isEmpty() && !name.equals("null")) {
            try {
                String fileContent = new String(Files.readAllBytes(Paths.get("script/" + name + ".script")));
                script = new Script(name, fileContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return script;
    }

    String getScript() {
        return script;
    }

    @Override
    public String toString() {
        return name;
    }
}

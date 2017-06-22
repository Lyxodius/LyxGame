package net.lyxodius.lyxGame.main;

/**
 * Created by Lyxodius on 18.06.2017.
 */
public enum Behavior {
    NOTHING(null),
    RANDOM_MOVEMENT(new Timer(1000, 3000));

    final Timer timer;

    Behavior(Timer timer) {
        this.timer = timer;
    }
}

package net.lyxodius.lyxGame.main;

import java.util.Comparator;

/**
 * Created by Lyxodius on 17.06.2017.
 */
class EntityComparator implements Comparator<Entity> {

    @Override
    public int compare(Entity entity1, Entity entity2) {
        if (entity1.position.z > entity2.position.z) {
            return 1;
        } else if (entity1.position.z < entity2.position.z) {
            return -1;
        } else if (entity1.position.y > entity2.position.y) {
            return 1;
        } else if (entity1.position.y < entity2.position.y) {
            return -1;
        }
        return 0;
    }
}

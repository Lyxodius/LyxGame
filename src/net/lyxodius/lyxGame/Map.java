package net.lyxodius.lyxGame;

import java.io.*;
import java.util.ArrayList;

public class Map {
    private final ArrayList<Entity> entityList;
    public String name;
    public int[][][] tiles;
    public boolean[][] collisionTiles;
    private int width;
    private int height;

    public Map() {
        this("untitled", 16, 9);
    }

    private Map(String name, int width, int height) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.resize(width, height);

        entityList = new ArrayList<>();
    }

    public static Map readFromFile() {
        return readFromFile(new File("map/0"));
    }

    static Map readFromFile(String name) {
        return readFromFile(new File("map/" + name));
    }

    public static Map readFromFile(File file) {
        Map map = null;

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            try (InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream)) {
                try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    int width = Integer.parseInt(bufferedReader.readLine());
                    int height = Integer.parseInt(bufferedReader.readLine());
                    int[][][] tiles = new int[3][height][width];
                    boolean[][] collisionTiles = new boolean[height][width];

                    bufferedReader.readLine();

                    for (int z = 0; z < 4; z++) {
                        for (int y = 0; y < height; y++) {
                            String row = bufferedReader.readLine();
                            String[] columns = row.split("\\.");
                            for (int x = 0; x < width; x++) {
                                String input = columns[x];
                                if (z < 3) {
                                    tiles[z][y][x] = Integer.parseInt(input);
                                } else {
                                    boolean collisionTile = false;
                                    if (columns[x].equals("x")) {
                                        collisionTile = true;
                                    }
                                    collisionTiles[y][x] = collisionTile;
                                }
                            }
                        }

                        bufferedReader.readLine();
                    }

                    map = new Map(file.getName(), width, height);
                    map.tiles = tiles;
                    map.collisionTiles = collisionTiles;

                    String line = bufferedReader.readLine();
                    while (line != null) {
                        String name = line;
                        String[] coordinates = bufferedReader.readLine().split(" ");
                        int x = Integer.parseInt(coordinates[0]);
                        int y = Integer.parseInt(coordinates[1]);
                        int z = Integer.parseInt(coordinates[2]);
                        Vector3D position = new Vector3D(x, y, z);
                        Direction direction = Direction.valueOf(bufferedReader.readLine());
                        String imageName = bufferedReader.readLine();
                        Entity entity = new Entity(name, position, imageName);
                        entity.direction = direction;
                        entity.behavior = Behavior.valueOf(bufferedReader.readLine());
                        for (Event event : Event.values()) {
                            entity.events.put(event, Script.loadFromFile(bufferedReader.readLine()));
                        }

                        map.entityList.add(entity);

                        bufferedReader.readLine();
                        line = bufferedReader.readLine();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ArrayList<Entity> getEntityList() {
        return entityList;
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;

        if (width < 16 && height < 9) {
            throw new IllegalArgumentException("A map's size must be at least 16x9.");
        }

        if (tiles == null) {
            tiles = new int[3][height][width];
            collisionTiles = new boolean[height][width];
        } else {
            int[][][] newTiles = new int[3][height][width];
            boolean[][] newCollisionFields = new boolean[height][width];

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < 3; z++) {
                        newTiles[z][y][x] = tiles[z][y][x];
                    }
                    newCollisionFields[y][x] = collisionTiles[y][x];
                }
            }

            tiles = newTiles;
            collisionTiles = newCollisionFields;
        }
    }

    public void saveToFile() {
        String path = "map/" + name;

        try (FileWriter fileWriter = new FileWriter(path)) {
            try (PrintWriter printWriter = new PrintWriter(fileWriter)) {
                printWriter.println(Integer.toString(width));
                printWriter.println(Integer.toString(height));

                for (int z = 0; z < 4; z++) {
                    printWriter.println();
                    for (int y = 0; y < tiles[0].length; y++) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int x = 0; x < tiles[0][0].length; x++) {
                            String output;

                            if (z < 3) {
                                output = Integer.toString(tiles[z][y][x]);
                            } else {
                                boolean collisionTile = collisionTiles[y][x];
                                if (collisionTile) {
                                    output = "x";
                                } else {
                                    output = "o";
                                }
                            }

                            stringBuilder.append(output).append(".");
                        }
                        printWriter.println(stringBuilder.substring(0, stringBuilder.length() - 1));
                    }
                }

                for (Entity entity : entityList) {
                    printWriter.println();
                    printWriter.println(entity.name);
                    printWriter.println(String.format("%d %d %d",
                            entity.position.x, entity.position.y, entity.position.z));
                    printWriter.println(entity.direction);
                    printWriter.println(entity.imageName);
                    printWriter.println(entity.behavior);
                    for (Event event : Event.values()) {
                        printWriter.println(entity.events.get(event));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Entity getEntityAt(int x, int y) {
        for (Entity entity : entityList) {
            if (entity.position.x == x && entity.position.y == y) {
                return entity;
            }
        }
        return null;
    }

    public Entity getEntity(String name) {
        for (Entity entity : entityList) {
            if (entity.name.equals(name)) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}

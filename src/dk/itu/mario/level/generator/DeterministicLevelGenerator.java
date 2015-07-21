package dk.itu.mario.level.generator;

import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelInterface;
import dk.itu.mario.level.MyLevel;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 * Creates an already saved map
 */
public class DeterministicLevelGenerator extends CustomizedLevelGenerator {
    public LevelInterface generateLevel(GamePlay playerMetrics) {
        LevelInterface level = readLevel("map1.txt");
        return level;
    }

    @Override
    public LevelInterface generateLevel(String detailedInfo) {
        // TODO Auto-generated method stub
        return null;
    }

    private MyLevel readLevel(String filename) {
        MyLevel level = null;
        try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
            level =  (MyLevel) ois.readObject();
        } catch (Exception e) {
            System.out.println("Can't load map file!");
        }
        System.err.println(level.getMap());
        return level;
    }

}

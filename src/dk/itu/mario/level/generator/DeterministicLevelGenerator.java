package dk.itu.mario.level.generator;

import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelInterface;
import dk.itu.mario.level.CustomizedLevel;
import dk.itu.mario.level.RandomLevel;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Random;

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

    private RandomLevel readLevel(String filename) {
        RandomLevel level = null;
        try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
            level =  (RandomLevel) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return level;
    }
}

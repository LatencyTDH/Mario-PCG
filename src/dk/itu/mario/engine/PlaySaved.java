package dk.itu.mario.engine;

import javax.swing.*;
import java.awt.*;

/**
 * Created by admin on 7/20/2015.
 */
public class PlaySaved {
    public static void main(String[] args)
    {

        String txtFileToUse = "player.txt";

        if (args.length!=0){
            txtFileToUse = args[0]+".txt";
        }

        JFrame frame = new JFrame("Mario Experience Showcase");

        MarioComponent mario = new MarioComponent(640, 480, true, txtFileToUse);
        mario.setSaved(true);

        frame.setContentPane(mario);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((screenSize.width - frame.getWidth()) / 2, (screenSize.height -frame.getHeight())/2);

        frame.setVisible(true);


        mario.start();
    }

}

package main;

import blocks.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class PlayManager {

    //play area
    final int WIDTH = 360;
    final int HEIGHT = 600;

    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    WholeBlocks currentBlock;
    final int BLOCK_START_X;
    final int BLOCK_START_Y;
    WholeBlocks nextBlock;
    final int NEXT_BLOCK_X;
    final int NEXT_BLOCK_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();

    public static int dropInterval = 16;
    public boolean gameOver;

    int level = 1;
    int score, lines;

    public PlayManager(){
        left_x = (GamePanel.WIDTH/2) - (WIDTH/2);
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        BLOCK_START_X = left_x + (WIDTH/2) - Block.SIZE;
        BLOCK_START_Y = top_y + Block.SIZE;
        NEXT_BLOCK_X = right_x +175;
        NEXT_BLOCK_Y = top_y + 500;

        currentBlock = pickRandomBlock();
        currentBlock.setXY(BLOCK_START_X, BLOCK_START_Y);
        nextBlock = pickRandomBlock();
        nextBlock.setXY(NEXT_BLOCK_X, NEXT_BLOCK_Y);
    }

    private WholeBlocks pickRandomBlock(){
        WholeBlocks wholeBlocks = null;
        switch(new Random().nextInt(7)){
            case 0: wholeBlocks = new T(); break;
            case 1: wholeBlocks = new L1(); break;
            case 2: wholeBlocks = new L2(); break;
            case 3: wholeBlocks = new Square(); break;
            case 4: wholeBlocks = new Z1(); break;
            case 5: wholeBlocks = new Z2(); break;
            case 6: wholeBlocks = new I(); break;
        }
        return wholeBlocks;
    }

    public void update(){
        if(!currentBlock.active){
            staticBlocks.add(currentBlock.b[0]);
            staticBlocks.add(currentBlock.b[1]);
            staticBlocks.add(currentBlock.b[2]);
            staticBlocks.add(currentBlock.b[3]);

            //check if game over
            if(currentBlock.b[0].x == BLOCK_START_X && currentBlock.b[0].y == BLOCK_START_Y){
                gameOver = true;
            }

            currentBlock.deactivating = false;

            currentBlock = nextBlock;
            currentBlock.setXY(BLOCK_START_X, BLOCK_START_Y);
            nextBlock = pickRandomBlock();
            nextBlock.setXY(NEXT_BLOCK_X, NEXT_BLOCK_Y);

            //check if we have a full line
            checkDelete();
        }
        else{
            currentBlock.update();
        }
    }
    public void checkDelete(){
        int x = left_x, y = top_y, blockCount = 0, lineCount = 0;

        while(x < right_x && y < bottom_y){
            for (Block staticBlock : staticBlocks) {
                if (staticBlock.x == x && staticBlock.y == y) {
                    blockCount++;
                }
            }
            x+= Block.SIZE;
            if(x == right_x){
                if(blockCount == 12){
                    lines++;
                    lineCount++;
                    //increase level
                    if(lines%8 == 0){
                        level++;
                        if(PlayManager.dropInterval > 2){
                            PlayManager.dropInterval -= 3;
                        }
                    }


                    for(int i = staticBlocks.size()-1; i > -1; i--){
                        if(staticBlocks.get(i).y == y){
                            staticBlocks.remove(i);
                        }
                    }
                    for (Block staticBlock : staticBlocks) {
                        if (staticBlock.y < y) {
                            staticBlock.y += Block.SIZE;
                        }
                    }
                }
                blockCount = 0;
                x = left_x;
                y += Block.SIZE;
            }
        }
        //score
        if(lineCount > 0){
            int singleLineScore = 8*level*lines;
            score += singleLineScore;
        }
    }
    public void draw(Graphics2D g2){
        //main play area
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x-4, top_y-4, WIDTH+8, HEIGHT+8);

        //next tetris block frame
        int x = right_x + 100;
        int y = bottom_y - 200;
        g2.drawRect(x, y, 200,200);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT", x+60, y+60);

        //draw current block
        if(currentBlock != null){
            currentBlock.draw(g2);
        }
        //draw next block
        nextBlock.draw(g2);

        //draw dead blocks
        for(Block sb : staticBlocks)
            sb.draw(g2);

        switch(level){
            case 1: g2.setColor(Color.blue);    break;
            case 2: g2.setColor(Color.green);   break;
            case 3: g2.setColor(Color.red);     break;
            case 4: g2.setColor(Color.white);   break;
            case 5: g2.setColor(Color.gray);    break;
            case 6: g2.setColor(Color.magenta); break;
            case 7: g2.setColor(Color.orange);  break;
            case 8: g2.setColor(Color.yellow);  break;
            case 9: g2.setColor(Color.CYAN);    break;
            case 10: g2.setColor(Color.PINK);   break;
            default: g2.setColor(new Color(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255)));
                break;
        }
        g2.drawRect(x, top_y, 250,300);
        x+= 40;
        y = top_y + 90;


        g2.setColor(Color.WHITE);
        g2.drawString("Lvl: " + level, x, y);
        y += 70;
        g2.drawString("Lines: " + lines, x, y);
        y += 70;
        g2.drawString("Score: " + score, x, y);

        //PAUSE SCREEN 
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(50f));
        if(gameOver){
            g2.setColor(Color.red);
            x = 55;
            y = top_y + 400;
            g2.drawString("GAME OVER ", x, y);
        }
        else if(KeyHandler.pausePressed){
            x = left_x + 80;
            y = top_y + 320;
            g2.drawString("PAUSE", x, y);
        }
        //draw the siatka
        x = left_x;
        y = top_y;
        g2.setColor(Color.lightGray);
        g2.setStroke(new BasicStroke(0.1f));
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g2.setComposite(alphaComposite);
        while(x < right_x){
            g2.drawLine(x, top_y, x, bottom_y);
            x += Block.SIZE;
        }
        while(y < bottom_y){
            g2.drawLine(left_x, y, right_x, y);
            y += Block.SIZE;
        }
        g2.setStroke(new BasicStroke());
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

        //draw the game title
        x = 45;
        y = top_y + 320;
        g2.setColor(Color.white);
        g2.setFont(new Font("Times New Roman", Font.ITALIC, 72));
        g2.drawString("Tetris :)", x, y);
    }
}

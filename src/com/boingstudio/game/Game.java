package com.boingstudio.game;

import IO.Input;
import com.boingstudio.display.Display;
import com.boingstudio.utils.Time;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Game implements Runnable{


    public static final int WIDTH =             800;
    public static final int HIGHT =             600;
    public static final String TITLE =          "TANKS";
    public static final int CLEAR_COLOR =       0xff000000;
    public static final int NUM_BUFFERS =       3;

    public static final float UPDATE_RATE =     60.0f;
    public static final float UPDATE_INTERVAL = Time.SECOND / UPDATE_RATE;
    public static final long IDLE_TIME =        1;

    private boolean     running = false;
    private Thread      gameThread;
    private Graphics2D graphics;
    private Input   input;


    public static int run = 0;

    //temp
        float x = 350;
        float y = 250;
        float delta = 0;
        float radius = 50;
        float speed = 3;
    //tempend
    public Game(){

        running = false;
        Display.create(WIDTH,HIGHT,TITLE,CLEAR_COLOR,NUM_BUFFERS);
        graphics = Display.getGrafics();
        input = new Input();
        Display.addInputListener(input);
    }

    public synchronized void start(){
        if(running)
            return;

        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public synchronized void stop(){
        if(!running)
            return;

        running = false;

        try {
            gameThread.join();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }

        cleanUp();

    }

    private void update(){
        if(input.getKey(KeyEvent.VK_UP))
            y -= speed;
        if(input.getKey(KeyEvent.VK_DOWN))
            y += speed;
        if(input.getKey(KeyEvent.VK_LEFT))
            x -= speed;
        if(input.getKey(KeyEvent.VK_RIGHT))
            x += speed;
    }

    private void render(){
        Display.clear();
        graphics.setColor(Color.white);
        graphics.fillOval((int)  (x + (Math.sin(delta)*200)),(int)y,(int)radius *2,(int)radius *2 );
        Display.swapBuffers();
    }

    public void run(){


        System.out.println("running: " + running);

        int fps = 0;
        int upd = 0;
        int updLoops = 0;

        long count = 0;

        float delta = 0;

        long lastTime = Time.get();
        while (running){

        //    System.out.println("RUN: " + run);
          //  run++;

            long now = Time.get();
            long elapsTime = now - lastTime;
            lastTime = now;

            count += elapsTime;



            boolean render = false;
            delta += (elapsTime / UPDATE_INTERVAL);
          //  System.out.println("delta: " + delta);

            while (delta > 1){
               // System.out.println("while2");
              //  System.out.println("delta2: " + delta);
                update();
                upd++;
                delta=delta-1;
                if(render){
                    updLoops++;
                }
                else{
                    render = true;
                }

            }
            if(render){
               // System.out.println("render");
                render();
                fps++;
            }else {
               // System.out.println("render else");
                try {
                    Thread.sleep(IDLE_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
          //  System.out.println("count: " + count);
            if(count >= Time.SECOND)
            {
                //System.out.println("FPS: " + fps);
                Display.setTitle(TITLE + " || Fps: " + fps +" | Upd: " + upd + " UpdLoops: " + updLoops);
                upd = 0;
                fps =0;
                updLoops=0;
                count=0;
            }

        }

        System.out.println("running2: " + running);

    }

    private void cleanUp()
    {
        Display.destroy();
    }
}

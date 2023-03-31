package cn.wr.examination;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author : WangRui
 * @date : 2023/3/31
 */

public class SnakeGame extends JPanel implements Runnable, KeyListener{

    private static final long serialVersionUID = 1L;

    // 游戏界面大小
    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;

    // 贪吃蛇的每个格子大小
    private static final int SIZE = 20;

    // 贪吃蛇的初始长度
    private static final int INITIAL_LENGTH = 3;

    // 贪吃蛇的移动速度
    private static final int SPEED = 100;

    // 贪吃蛇的移动方向
    private static final int DIRECTION_UP = 0;
    private static final int DIRECTION_DOWN = 1;
    private static final int DIRECTION_LEFT = 2;
    private static final int DIRECTION_RIGHT = 3;

    // 贪吃蛇的位置和移动方向
    private LinkedList<Point> snake;
    private int direction;

    // 食物的位置
    private Point food;

    // 游戏是否结束
    private boolean gameOver;
    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);

        initGame();
    }

    private void initGame() {
        snake = new LinkedList<Point>();
        direction = DIRECTION_RIGHT;

        // 初始化贪吃蛇的位置
        for (int i = 0; i < INITIAL_LENGTH; i++) {
            snake.add(new Point(i, 0));
        }

        // 随机生成食物的位置
        Random rand = new Random();
        int x = rand.nextInt(WIDTH / SIZE);
        int y = rand.nextInt(HEIGHT / SIZE);
        food = new Point(x, y);

        gameOver = false;
    }
    private void moveSnake() {
        // 获取贪吃蛇头部的位置
        Point head = snake.getFirst();

        // 根据移动方向计算贪吃蛇的下一个位置
        int x = head.x;
        int y = head.y;
        if (direction == DIRECTION_UP) {
            y--;
        } else if (direction == DIRECTION_DOWN) {
            y++;
        } else if (direction == DIRECTION_LEFT) {
            x--;
        } else if (direction == DIRECTION_RIGHT) {
            x++;
        }
        Point next = new Point(x, y);

        // 检查下一个位置是否合法
        if (next.x < 0 || next.x >= WIDTH / SIZE || next.y < 0 || next.y >= HEIGHT / SIZE) {
            gameOver = true;
            return;
        }
        if (snake.contains(next)) {
            gameOver = true;
            return;
        }

        // 在头部添加下一个位置
        snake.addFirst(next);

        // 如果吃到了食物，增加贪吃蛇的长度并重新生成食物
        if (next.equals(food)) {
        int x1, y1;
        do {
        x1 = new Random().nextInt(WIDTH / SIZE);
        y1 = new Random().nextInt(HEIGHT / SIZE);
        } while (snake.contains(new Point(x1, y1)));
        food.setLocation(x1, y1);
        } else {
        // 如果没有吃到食物，删除尾部位置
        snake.removeLast();
        }
        }

    @Override
    public void run() {
        while (!gameOver) {
            moveSnake();
            repaint();
            try {
                Thread.sleep(SPEED);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 绘制贪吃蛇和食物
        g.setColor(Color.GREEN);
        for (Point p : snake) {
            g.fillRect(p.x * SIZE, p.y * SIZE, SIZE, SIZE);
        }
        g.setColor(Color.RED);
        g.fillRect(food.x * SIZE, food.y * SIZE, SIZE, SIZE);

        // 绘制游戏结束提示
        if (gameOver) {
            g.setColor(Color.BLACK);
            g.drawString("Game Over!", WIDTH / 2 - 30, HEIGHT / 2);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // 根据按键修改移动方向
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_UP && direction != DIRECTION_DOWN) {
            direction = DIRECTION_UP;
        } else if (keyCode == KeyEvent.VK_DOWN && direction != DIRECTION_UP) {
            direction = DIRECTION_DOWN;
        } else if (keyCode == KeyEvent.VK_LEFT && direction != DIRECTION_RIGHT) {
            direction = DIRECTION_LEFT;
        } else if (keyCode == KeyEvent.VK_RIGHT && direction != DIRECTION_LEFT) {
            direction = DIRECTION_RIGHT;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        SnakeGame game = new SnakeGame();
        frame.getContentPane().add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        new Thread(game).start();
    }

}

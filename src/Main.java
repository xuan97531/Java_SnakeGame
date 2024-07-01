import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends JPanel implements KeyListener {

    public static final int CELL_SIZE = 20;
    public static int width = 400;
    public static int height = 400;
    public static int row = height / CELL_SIZE;
    public static int column = width / CELL_SIZE;
    private Snake snake;
    private Fruit fruit;
    private Timer t;
    private int speed = 100;
    private static String direction;
    private boolean allowKeyPress;
    private int score;
    private int highest_score;
    String desktop = System.getProperty("user.home") + "/Desktop/";
    String myfile = desktop + "filename.txt";

    public Main(){
        read_highest_score();
        reset();
        addKeyListener(this);
    }

    private void setTimer(){
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run(){
                repaint();
            }
        }, 0, speed);
    }

    private void reset(){
        score = 0;
        if(snake != null){
            snake.getSnakeBody().clear();;
        }
        allowKeyPress = true;
        direction = "Right";
        snake = new Snake();
        fruit = new Fruit();
        setTimer();
    }

    @Override
    public void paintComponent(Graphics g){
        // check if the snake bites itself
        ArrayList<Node> snake_body = snake.getSnakeBody();
        Node head = snake_body.get(0);
        for(int i=1; i<snake_body.size(); i++){
            if(snake_body.get(i).x == head.x && snake_body.get(i).y == head.y){
                allowKeyPress = false;

                t.cancel();
                t.purge();

                int response = JOptionPane.showOptionDialog(this, "Game Over!!!" + "Your score is " + score + ".Your highest score is "+ highest_score + ". Would you like to start over?", "Game Over!!!", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, JOptionPane.YES_OPTION);

                write_a_file(score);

                switch (response){
                    case JOptionPane.CLOSED_OPTION:
                        System.exit(0);
                        break;
                    case JOptionPane.NO_OPTION:
                        System.exit(0);
                        break;
                    case JOptionPane.YES_OPTION:
                        reset();
                        return;

                }
            }
        }

        // System.out.println("We are calling paint component...");
        // draw a black background
        g.fillRect(0, 0, width, height);

        fruit.drawFruit(g);
        snake.drawSnake(g);

        // remove snake tail and put it in head
        int snakeX = snake.getSnakeBody().get(0).x;
        int snakeY = snake.getSnakeBody().get(0).y;

        // Right, x += cell_size / Left,  x -= cell_size
        // Down,  y += cell_size / Up,    y -= cell_size
        if(direction.equals("Right")){
            snakeX += CELL_SIZE;
        } else if (direction.equals("Left")) {
            snakeX -= CELL_SIZE;
        } else if (direction.equals("Down")) {
            snakeY += CELL_SIZE;
        } else if (direction.equals("Up")) {
            snakeY -= CELL_SIZE;
        }

        Node newHead = new Node(snakeX, snakeY);

        // check if the snake eats the fruit
        if(snake.getSnakeBody().get(0).x == fruit.getX() && snake.getSnakeBody().get(0).y == fruit.getY()){
            // 1. set fruit to a new location
            fruit.setNewLocation(snake);
            // 2. drawFruit
            fruit.drawFruit(g);
            // 3. score++
            score++;
        } else {
            snake.getSnakeBody().remove(snake.getSnakeBody().size() - 1);
        }

        snake.getSnakeBody().add(0, newHead);

        requestFocusInWindow();

        allowKeyPress = true;
    }

    @Override
    public Dimension getPreferredSize(){
        return new Dimension(width, height);
    }

    public static void main(String[] args) {
        // 創建名為window的Jframe對象，標題為Snake Game (JFrame 用來顯示窗口)
        JFrame window = new JFrame("Snake Game");
        // 設置窗口的默認關閉操作，當窗口關閉十，應用程序也會退出
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setContentPane(new Main());
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.setResizable(false);
    }

    @Override
    public void keyTyped(KeyEvent e){}

    @Override
    public void keyPressed(KeyEvent e){
        if(allowKeyPress){
            if(e.getKeyCode() == KeyEvent.VK_LEFT && !direction.equals("Right")){
                direction = "Left";
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && !direction.equals("Left")) {
                direction = "Right";
            } else if (e.getKeyCode() == KeyEvent.VK_UP && !direction.equals("Down")) {
                direction = "Up";
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && !direction.equals("Up")) {
                direction = "Down";
            }
            allowKeyPress = false;
        }
    }

    @Override
    public void keyReleased(KeyEvent e){}

    public void read_highest_score(){
        try{
            File myObj = new File(myfile);
            Scanner myReader = new Scanner(myObj);
            highest_score = myReader.nextInt();
            myReader.close();
        } catch (FileNotFoundException e){
            highest_score = 0;
            try{
                File myObj = new File(myfile);
                if(myObj.createNewFile()){
                    System.out.println("File Created: " + myObj.getName());
                }
                FileWriter myWriter = new FileWriter(myObj.getName());
                myWriter.write("" + 0);
                myWriter.close();
            } catch (IOException err) {
                System.out.println("An error occurred");
                err.printStackTrace();
            }
        }
    }

    public void write_a_file(int score){
        try{
            FileWriter myWriter = new FileWriter(myfile);
            if(score > highest_score){
                myWriter.write("" + score);
                highest_score = score;
            } else {
                myWriter.write("" + highest_score);
            }
            myWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}

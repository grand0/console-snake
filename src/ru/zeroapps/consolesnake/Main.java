package ru.zeroapps.consolesnake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayDeque;
import java.util.Arrays;

public class Main {
    Color bgColor = Color.BLACK;
    Color fgColor = Color.GREEN;
    Font font = new Font("Consolas", Font.PLAIN, 18);
    int areaWidth = 30;
    int areaHeight = 20;
    int fps = 10;
    int windowWidth = 800;
    int windowHeight = 800;

    JFrame frame;
    JTextArea gameField;
    int[][] field = new int[areaWidth][areaHeight];
    volatile Direction lastDirection = Direction.NONE;
    ArrayDeque<Direction> directionsQueue = new ArrayDeque<>();
    int startLength = 3;

    long timeLastFrame = System.currentTimeMillis();

    boolean makeBigger = false;
    boolean gameOver = false;
    int score = 0;

    public static void main(String[] args) {
        Main main = new Main();

        main.setupGUI();
        main.setupGame();
        main.runGame();
    }

    private void setupGUI() {
        frame = new JFrame("Snake");
        gameField = new JTextArea();
        gameField.setFont(font);
        gameField.setEditable(false);
        gameField.setBackground(bgColor);
        gameField.setForeground(fgColor);
        gameField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                e.consume();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (!getLastDirection().isIllegalMove(Direction.UP))
                            offerDirectionToQueue(Direction.UP);
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (!getLastDirection().isIllegalMove(Direction.RIGHT))
                            offerDirectionToQueue(Direction.RIGHT);
                        break;
                    case KeyEvent.VK_DOWN:
                        if (!getLastDirection().isIllegalMove(Direction.DOWN))
                            offerDirectionToQueue(Direction.DOWN);
                        break;
                    case KeyEvent.VK_LEFT:
                        if (!getLastDirection().isIllegalMove(Direction.LEFT))
                            offerDirectionToQueue(Direction.LEFT);
                        break;
                    case KeyEvent.VK_ESCAPE:
                        exitProgram();
                        break;
                }
                e.consume();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                e.consume();
            }
        });
        frame.getContentPane().add(gameField);
        frame.setSize(windowWidth, windowHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        gameField.requestFocus();
    }

    private Direction getLastDirection() {
        if (directionsQueue.isEmpty()) return lastDirection;
        else return directionsQueue.getLast();
    }

    private Direction getCurrentDirection() {
        if (directionsQueue.isEmpty()) return lastDirection;
        else return directionsQueue.poll();
    }

    private void offerDirectionToQueue(Direction dir) {
        if (directionsQueue.size() < 2) directionsQueue.add(dir);
    }

    private void setupGame() {
        for (int i = 0; i < areaWidth; i++) {
            Arrays.fill(field[i], 0);
        }
        // draw a snake with length 3
        field[2][2] = 1;
        field[3][2] = 2;
        field[4][2] = 3;
    }

    private void runGame() {
        spawnFood();
        while (true) {
            if (System.currentTimeMillis() - timeLastFrame >= 1000L / fps) {
                updateField();
                if (gameOver) {
                    gameField.append("G A M E   O V E R !\n");
                    gameField.append("You're so bad, please never play this game again.\n");
                    gameField.append("Press ESC to quit and never come back.");
                    break;
                }
                drawFrame();
                timeLastFrame = System.currentTimeMillis();
            }
        }
    }

    private void drawFrame() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < areaWidth + 2; i++) {
            sb.append("#");
        }
        sb.append("\n");
        for (int i = 0; i < areaHeight; i++) {
            sb.append("#");
            for (int j = 0; j < areaWidth; j++) {
                if (field[j][i] > 0) {
                    if (field[j][i] > (startLength + score) * 0.75) sb.append("█");
                    else if (field[j][i] > (startLength + score) * 0.5) sb.append("▓");
                    else if (field[j][i] > (startLength + score) * 0.25) sb.append("▒");
                    else sb.append("░");
                }
                else if (field[j][i] == -1) sb.append("●");
                else sb.append(" ");
            }
            sb.append("#\n");
        }
        for (int i = 0; i < areaWidth + 2; i++) {
            sb.append("#");
        }
        sb.append("\nScore: ").append(score).append("\n");
        gameField.setText(sb.toString());
    }

    private void updateField() {
        if (getLastDirection() == Direction.NONE) return;
        int max = -1;
        int headX = -1;
        int headY = -1;
        for (int y = 0; y < areaHeight; y++) {
            for (int x = 0; x < areaWidth; x++) {
                if (field[x][y] > 0 && !makeBigger) field[x][y]--;
                if (field[x][y] > max) {
                    max = field[x][y];
                    headX = x;
                    headY = y;
                }
            }
        }
        makeBigger = false;
        lastDirection = getCurrentDirection();
        switch (lastDirection) {
            case RIGHT:
                if (headX == areaWidth - 1) { // crash in border
                    gameOver = true;
                    return;
                }
                if (field[headX + 1][headY] > 0) { // crash in tail
                    gameOver = true;
                    return;
                }
                if (field[headX + 1][headY] == -1) makeBigger = true;
                field[headX + 1][headY] = max + 1;
                break;
            case UP:
                if (headY == 0) { // crash in border
                    gameOver = true;
                    return;
                }
                if (field[headX][headY - 1] > 0) { // crash in tail
                    gameOver = true;
                    return;
                }
                if (field[headX][headY - 1] == -1) makeBigger = true;
                field[headX][headY - 1] = max + 1;
                break;
            case LEFT:
                if (headX == 0) { // crash in border
                    gameOver = true;
                    return;
                }
                if (field[headX - 1][headY] > 0) { // crash in tail
                    gameOver = true;
                    return;
                }
                if (field[headX - 1][headY] == -1) makeBigger = true;
                field[headX - 1][headY] = max + 1;
                break;
            case DOWN:
                if (headY == areaHeight - 1) { // crash in border
                    gameOver = true;
                    return;
                }
                if (field[headX][headY + 1] > 0) { // crash in tail
                    gameOver = true;
                    return;
                }
                if (field[headX][headY + 1] == -1) makeBigger = true;
                field[headX][headY + 1] = max + 1;
                break;
            default:
                break;
        }
        if (makeBigger) {
            spawnFood();
            score++;
        }
    }

    private void spawnFood() {
        int x = (int) (Math.random() * areaWidth);
        int y = (int) (Math.random() * areaHeight);
        while (field[x][y] != 0) {
            x = (int) (Math.random() * areaWidth);
            y = (int) (Math.random() * areaHeight);
        }
        field[x][y] = -1;
    }

    private void exitProgram() {
        System.exit(0);
    }
}

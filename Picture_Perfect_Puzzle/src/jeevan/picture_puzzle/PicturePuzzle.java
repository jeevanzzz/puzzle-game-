package jeevan.picture_puzzle;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

class MyButton extends JButton {//Represents each puzzle piece as a button.

    private static final long serialVersionUID = 1L;
    boolean isLastButton;

    public MyButton(boolean isLastButton) {
        this.isLastButton = isLastButton;
        setBorder(null);
        init();
    }

    public MyButton(Image iconImage, boolean isLastButton) {
        setIcon(new ImageIcon(iconImage));
        this.isLastButton = isLastButton;
        setBorder(null);
        init();
    }

    private void init() {
        if (!isLastButton) {
            setBorder(BorderFactory.createLineBorder(Color.RED));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    setBorder(BorderFactory.createLineBorder(Color.YELLOW));
                }

                public void mouseExited(MouseEvent e) {
                    setBorder(BorderFactory.createLineBorder(Color.RED));
                }
            });
        } else {
            setBorder(BorderFactory.createLineBorder(Color.CYAN));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    setBorder(BorderFactory.createLineBorder(Color.GREEN));
                }

                public void mouseExited(MouseEvent e) {
                    setBorder(BorderFactory.createLineBorder(Color.CYAN));
                }
            });
        }
    }
}

public class PicturePuzzle extends JFrame implements ActionListener {//Handles game logic and UI interactions
    private static final long serialVersionUID = 1L;
    JPanel northWrapper, centerWrapper, southWrapper;
    JLabel topLabel, starImageLabel;
    JButton mainMenu, solutionImage, timeButton, clickButton;
    BufferedImage sourceImage, resizedImage;
    Image createdImage;
    int width, height;
    long beforeTime;
    String timeTaken;
    boolean timeFlag;
    int clicks;
    Scanner sc = new Scanner(System.in);
    Image iconImage = Toolkit.getDefaultToolkit().getImage("src\\resources\\puzzle-icon.png").getScaledInstance(100, 100, Image.SCALE_SMOOTH);
    int pictureFlag;
    List<MyButton> buttons;
    List<Point> solution;
    final int NUMBER_OF_BUTTONS = 9;
    final int DESIRED_WIDTH = 500;

    public PicturePuzzle() {
        System.out.println("Which picture puzzle do you want to play? \n1. DOREMON \n2. OGGY AND THE COCKROACHES \n3. TOM AND JERRY ");
        int numberChosen = sc.nextInt();
        switch (numberChosen) {
            case 1: pictureFlag = 1; break;
            case 2: pictureFlag = 2; break;
            case 3: pictureFlag = 3; break;
            default: pictureFlag = 1;
        }

        solution = new ArrayList<>();
        // Solution points for 3x3 grid
        solution.add(new Point(0, 0));
        solution.add(new Point(0, 1));
        solution.add(new Point(0, 2));
        solution.add(new Point(1, 0));
        solution.add(new Point(1, 1));
        solution.add(new Point(1, 2));
        solution.add(new Point(2, 0));
        solution.add(new Point(2, 1));
        solution.add(new Point(2, 2));

        buttons = new ArrayList<>();
        if (centerWrapper != null) {
            centerWrapper.removeAll();
        }
        centerWrapper = new JPanel();
        centerWrapper.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        centerWrapper.setLayout(new GridLayout(3, 3, 0, 0)); // 3x3 grid

        try {
            sourceImage = loadImage();
            int h = getNewHeight(sourceImage.getWidth(), sourceImage.getHeight());
            resizedImage = resizeImage(sourceImage, DESIRED_WIDTH, h, BufferedImage.TYPE_INT_ARGB);
        } catch (IOException e) {
            e.printStackTrace();
        }

        width = resizedImage.getWidth(null);
        height = resizedImage.getHeight(null);
        MyButton lastButton = null;

        for (int i = 0; i < 3; i++) { // 3 rows
            for (int j = 0; j < 3; j++) { // 3 columns
                createdImage = createImage(new FilteredImageSource(resizedImage.getSource(),
                        new CropImageFilter(j * width / 3, i * height / 3, width / 3, height / 3))); // Split into 3x3
                if (i == 2 && j == 2) { // Last button at (2,2)
                    lastButton = new MyButton(createdImage, true);
                    lastButton.putClientProperty("position", new Point(i, j));
                } else {
                    MyButton button = new MyButton(createdImage, false);
                    button.putClientProperty("position", new Point(i, j));
                    buttons.add(button);
                }
            }
        }

        Collections.shuffle(buttons);
        buttons.add(lastButton); // Add last button after shuffling
        for (int i = 0; i < NUMBER_OF_BUTTONS; i++) {
            JButton button = buttons.get(i);
            centerWrapper.add(butto2n);
            button.addActionListener(new ClickAction());
        }
        add(centerWrapper, BorderLayout.CENTER);

        Image starImage = Toolkit.getDefaultToolkit().getImage("src\\resources\\star-icon.png").getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        topLabel = new JLabel("Star icon swaps with its neighboring icon");
        starImageLabel = new JLabel(new ImageIcon(starImage));
        northWrapper = new JPanel();
        northWrapper.add(topLabel);
        northWrapper.add(starImageLabel);

        mainMenu = new JButton("Menu");
        mainMenu.addActionListener(this);
        solutionImage = new JButton("Solution");
        solutionImage.addActionListener(this);
        timeButton = new JButton("00 : 00");
        timeButton.setToolTipText("Time Taken");
        clickButton = new JButton("0");
        clickButton.setToolTipText("Total Clicks");
        southWrapper = new JPanel();
        southWrapper.add(mainMenu);
        southWrapper.add(solutionImage);
        southWrapper.add(timeButton);
        southWrapper.add(clickButton);

        add(northWrapper, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);
        add(southWrapper, BorderLayout.SOUTH);

        pack();
        setTitle("PICTURE PERFECT PUZZLE");
        setLayout(new BorderLayout());
        setIconImage(iconImage);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PicturePuzzle());
    }

    private BufferedImage loadImage() throws IOException {
        BufferedImage sourceImage = null;
        if (pictureFlag == 1)
            sourceImage = ImageIO.read(new File("src\\resources\\DOREMON.jpg"));
        else if (pictureFlag == 2)
            sourceImage = ImageIO.read(new File("src\\resources\\OGGY AND THE COCKROACHES.jpg"));
        else if (pictureFlag == 3)
            sourceImage = ImageIO.read(new File("src\\resources\\TOM AND JERRY.jpg"));
        return sourceImage;
    }

    private int getNewHeight(int width, int height) {
        double ratio = DESIRED_WIDTH / (double) width;
        return (int) (height * ratio);
    }

    private BufferedImage resizeImage(BufferedImage sourceImage, int width, int height, int type) {
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics g = resizedImage.createGraphics();
        g.drawImage(sourceImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    class ClickAction extends AbstractAction { //Handles puzzle tile swapping and win condition.

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!timeFlag) {
                beforeTime = System.currentTimeMillis();
                timeFlag = true;
                countdownTime();
            }
            clicks++;
            clickButton.setText("" + clicks);
            checkButton(e);
            checkSolution();
        }

        private void checkButton(ActionEvent e) {
            int lidx = 0;
            for (MyButton button : buttons) {
                if (button.isLastButton) {
                    lidx = buttons.indexOf(button);
                }
            }
            JButton button = (JButton) e.getSource();
            int bidx = buttons.indexOf(button);
            if ((bidx - 1 == lidx) || (bidx + 1 == lidx) || (bidx - 3 == lidx) || (bidx + 3 == lidx)) {
                Collections.swap(buttons, bidx, lidx);
                updateButtons();
            }
        }

        private void updateButtons() {
            centerWrapper.removeAll();
            for (MyButton button : buttons) {
                centerWrapper.add(button);
            }
            centerWrapper.validate();
        }

        private void checkSolution() {
            List<Point> current = new ArrayList<>();
            for (MyButton button : buttons) {
                current.add((Point) button.getClientProperty("position"));
            }
            if (compareList(solution, current)) {
                timeFlag = false;
                String message = "";
                if (pictureFlag == 1) message = "Successfully completed the Doraemon puzzle and won the game!!!🎉";
                else if (pictureFlag == 2) message = "Successfully completed the OGGY AND THE COCKROACHES puzzle and won the game!!!🎉";
                else if (pictureFlag == 3) message = "Successfully completed the TOM AND JERRY puzzle and won the game!!!";
                JOptionPane.showMessageDialog(centerWrapper, "Congratulations!\n" + message,
                        "You Won! Time Taken: " + timeTaken + " Clicks: " + clicks, JOptionPane.INFORMATION_MESSAGE);
                clicks = 0;
                timeTaken = null;
            }
        }

        public boolean compareList(List<Point> list1, List<Point> list2) {
            return list1.toString().contentEquals(list2.toString());
        }
    }

    public void countdownTime()//Runs a timer in a separate thread.
    {
        Thread t = new Thread(() -> {
            while (timeFlag) {
                long currentTime = System.currentTimeMillis();
                long runningTime = currentTime - beforeTime;
                Duration duration = Duration.ofMillis(runningTime);
                long minutes = duration.toMinutes();
                duration = duration.minusMinutes(minutes);
                long seconds = duration.getSeconds();
                DecimalFormat formatter = new DecimalFormat("00");
                timeTaken = formatter.format(minutes) + " : " + formatter.format(seconds);
                timeButton.setText(timeTaken);
            }
        });
        t.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mainMenu) {
            int reply = JOptionPane.showConfirmDialog(this, "Progress will be lost. Continue?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                timeFlag = false;
                clicks = 0;
                dispose();
                new PicturePuzzle();
            }
        }
        if (e.getSource() == solutionImage) {
            JDialog dialog = new JDialog();
            dialog.setTitle("Solution");
            Image dialogIcon = Toolkit.getDefaultToolkit().getImage("src\\resources\\correct-mark-icon.png");
            dialog.setIconImage(dialogIcon);
            try {
                Image solutionImage = loadImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
                JLabel label = new JLabel(new ImageIcon(solutionImage));
                dialog.add(label);
                dialog.pack();
                dialog.setVisible(true);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
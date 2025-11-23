import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class MyClock {
    private static volatile boolean running = false; // For timer control

    public static void main(String[] rk) {
        JFrame f = new JFrame("My Clock");
        f.setSize(700, 600);
        f.setLocationRelativeTo(null);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane jtp = new JTabbedPane();
        f.add(jtp);

        // ================== WORLD CLOCK TAB ==================
        JPanel p1 = new JPanel(new GridLayout(4, 1, 10, 10));
        p1.setBackground(Color.ORANGE);
        jtp.addTab("World Clock", p1);

        JLabel india = new JLabel("", SwingConstants.CENTER);
        JLabel usa = new JLabel("", SwingConstants.CENTER);
        JLabel london = new JLabel("", SwingConstants.CENTER);
        JLabel tokyo = new JLabel("", SwingConstants.CENTER);

        india.setFont(new Font("", Font.BOLD, 30));
        usa.setFont(new Font("", Font.BOLD, 30));
        london.setFont(new Font("", Font.BOLD, 30));
        tokyo.setFont(new Font("", Font.BOLD, 30));

        p1.add(india);
        p1.add(usa);
        p1.add(london);
        p1.add(tokyo);

        // Thread for World Clock
        Thread worldClock = new Thread(() -> {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");
            while (true) {
                india.setText("India: " + ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).format(fmt));
                usa.setText("New York: " + ZonedDateTime.now(ZoneId.of("America/New_York")).format(fmt));
                london.setText("London: " + ZonedDateTime.now(ZoneId.of("Europe/London")).format(fmt));
                tokyo.setText("Tokyo: " + ZonedDateTime.now(ZoneId.of("Asia/Tokyo")).format(fmt));
                try { Thread.sleep(1000); } catch (Exception e) {}
            }
        });
        worldClock.start();

        // ================== CLOCK TAB ==================
        JPanel p2 = new JPanel();
        p2.setBackground(Color.CYAN);
        jtp.addTab("Clock", p2);

        JLabel l = new JLabel();
        l.setFont(new Font("", Font.BOLD, 40));
        p2.add(l);

        Thread clock = new Thread(() -> {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");
            while (true) {
                LocalTime t = LocalTime.now();
                l.setText("Current Time: " + t.format(fmt));
                try { Thread.sleep(1000); } catch (Exception e) {}
            }
        });
        clock.start();

        // ================== TIMER TAB ==================
        JPanel p3 = new JPanel();
        p3.setBackground(Color.YELLOW);
        p3.setLayout(new GridLayout(3, 1, 10, 10));
        jtp.addTab("Timer", p3);

        // ===== Row 1: Input =====
        JPanel inputPanel = new JPanel(new FlowLayout());
        JTextField hourBox = new JTextField(2);
        JTextField minBox = new JTextField(2);
        JTextField secBox = new JTextField(2);

        inputPanel.add(new JLabel("Hours:"));   inputPanel.add(hourBox);
        inputPanel.add(new JLabel("Minutes:")); inputPanel.add(minBox);
        inputPanel.add(new JLabel("Seconds:")); inputPanel.add(secBox);
        p3.add(inputPanel);

        // ===== Row 2: Buttons =====
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton startBtn = new JButton("Start");
        JButton stopBtn = new JButton("Stop");
        JButton resetBtn = new JButton("Reset");
        buttonPanel.add(startBtn);
        buttonPanel.add(stopBtn);
        buttonPanel.add(resetBtn);
        p3.add(buttonPanel);

        // ===== Row 3: Display + Notification =====
        JPanel displayPanel = new JPanel(new GridLayout(2, 1));
        JLabel timerLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("", Font.BOLD, 40));
        displayPanel.add(timerLabel);

        JLabel notificationLabel = new JLabel("", SwingConstants.CENTER);
        notificationLabel.setFont(new Font("", Font.BOLD, 20));
        notificationLabel.setForeground(Color.RED);
        displayPanel.add(notificationLabel);

        p3.add(displayPanel);

        // ================== TIMER LOGIC ==================
        final Thread[] timerThread = {null};

        startBtn.addActionListener(e -> {
            if (running) return; // Prevent multiple starts
            running = true;
            notificationLabel.setText("");

            int h = 0, m = 0, s = 0;
            try {
                h = Integer.parseInt(hourBox.getText().trim().isEmpty() ? "0" : hourBox.getText().trim());
                m = Integer.parseInt(minBox.getText().trim().isEmpty() ? "0" : minBox.getText().trim());
                s = Integer.parseInt(secBox.getText().trim().isEmpty() ? "0" : secBox.getText().trim());
            } catch (Exception ex) {
                notificationLabel.setText("Invalid input!");
                running = false;
                return;
            }

            int totalSeconds = h * 3600 + m * 60 + s;
            int finalTotal = totalSeconds;

            timerThread[0] = new Thread(() -> {
                int timeLeft = finalTotal;
                while (timeLeft >= 0 && running) {
                    int hh = timeLeft / 3600;
                    int mm = (timeLeft % 3600) / 60;
                    int ss = timeLeft % 60;
                    timerLabel.setText(String.format("%02d:%02d:%02d", hh, mm, ss));

                    if (timeLeft == 0) {
                        notificationLabel.setText("⏰ Time's Up!");
                        Toolkit.getDefaultToolkit().beep();
                        running = false;
                        break;
                    }

                    try { Thread.sleep(1000); } catch (Exception ex) {}
                    timeLeft--;
                }
            });
            timerThread[0].start();
        });

        stopBtn.addActionListener(e -> {
            running = false;
            notificationLabel.setText("⏸ Stopped");
        });

        resetBtn.addActionListener(e -> {
            running = false;
            timerLabel.setText("00:00:00");
            notificationLabel.setText("");
            hourBox.setText("");
            minBox.setText("");
            secBox.setText("");
        });

        f.setVisible(true);
    }
}

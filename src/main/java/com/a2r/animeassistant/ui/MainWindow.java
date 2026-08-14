package com.a2r.animeassistant.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import com.a2r.animeassistant.core.AssistantCore;
import com.a2r.animeassistant.theme.Theme;

public class MainWindow extends JFrame {

    private JLabel waifuLabel;
    private ImageIcon[] mouthFrames;
    private JPanel chatBox;
    private JTextField inputField;
    private Timer mouthTimer;
    private int frameIndex = 0;
    private final AssistantCore core = new AssistantCore();
    private JPanel typingBubble = null;

    public MainWindow() {
        setTitle(core.getWaifuName() + " - Waifu Companion");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        setLocationRelativeTo(null);

        // save chat when closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                core.shutdown();
            }
        });

        // top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Theme.BG);
        topBar.setBorder(new EmptyBorder(8, 15, 0, 15));

        String personality = core.getConfig().getPersonality();
        JLabel titleLabel = new JLabel(core.getWaifuName() + " (" + personality + ") ♡");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Theme.ACCENT);
        topBar.add(titleLabel, BorderLayout.WEST);

        JButton settingsBtn = new JButton("Settings");
        settingsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        settingsBtn.setBackground(Theme.CARD);
        settingsBtn.setForeground(Theme.TEXT);
        settingsBtn.setFocusPainted(false);
        settingsBtn.setBorderPainted(false);
        settingsBtn.addActionListener(e -> showSettings());
        topBar.add(settingsBtn, BorderLayout.EAST);

        // waifu image
        JPanel waifuPanel = new JPanel(new GridBagLayout());
        waifuPanel.setBackground(new Color(30, 30, 30));
        waifuPanel.setPreferredSize(new Dimension(420, 0));

        loadWaifuImages();
        waifuLabel = new JLabel(mouthFrames[0]);
        waifuPanel.add(waifuLabel);

        // chat area
        chatBox = new JPanel();
        chatBox.setLayout(new BoxLayout(chatBox, BoxLayout.Y_AXIS));
        chatBox.setBackground(new Color(245, 240, 250));
        chatBox.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scrollPane = new JScrollPane(chatBox);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBorder(new EmptyBorder(8, 15, 15, 15));
        chatPanel.add(scrollPane, BorderLayout.CENTER);

        // input area
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBorder(new EmptyBorder(10, 15, 15, 15));

        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sendButton.setBackground(Theme.WAIFU_BUBBLE);
        sendButton.setFocusPainted(false);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // layout
        add(topBar, BorderLayout.NORTH);
        add(waifuPanel, BorderLayout.WEST);
        add(chatPanel, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        mouthTimer = new Timer(180, e -> animateMouth());

        // greeting after window opens
        SwingUtilities.invokeLater(this::showGreeting);
    }

    // load waifu images - custom from config or default from resources
    // supports 2 images for mouth animation, or 1 image (no animation)
    private void loadWaifuImages() {
        var config = core.getConfig();

        if (config.hasCustomImage()) {
            File closedFile = new File(config.getWaifuImageClosed());

            if (closedFile.exists()) {
                ImageIcon closedImg = scaleImageFromFile(closedFile.getAbsolutePath(), 380, 520);

                // check if user also provided the open mouth image
                if (config.hasCustomAnimation()) {
                    File openFile = new File(config.getWaifuImageOpen());
                    if (openFile.exists()) {
                        // both images - full mouth animation!
                        ImageIcon openImg = scaleImageFromFile(openFile.getAbsolutePath(), 380, 520);
                        mouthFrames = new ImageIcon[]{ closedImg, openImg };
                        return;
                    }
                }

                // only closed image - no animation, just static
                mouthFrames = new ImageIcon[]{ closedImg, closedImg };
                return;
            }
            System.err.println("custom image not found: " + config.getWaifuImageClosed());
        }

        // default bundled images
        mouthFrames = new ImageIcon[]{
            scaleImage("/waifu_closed.png", 380, 520),
            scaleImage("/waifu_open.png", 380, 520)
        };
    }

    // scale from classpath (default images)
    private ImageIcon scaleImage(String path, int maxW, int maxH) {
        java.net.URL res = getClass().getResource(path);
        if (res == null) return new ImageIcon();
        return scaleIcon(new ImageIcon(res), maxW, maxH);
    }

    // scale from filesystem (custom images)
    private ImageIcon scaleImageFromFile(String path, int maxW, int maxH) {
        ImageIcon icon = new ImageIcon(path);
        if (icon.getIconWidth() <= 0) return new ImageIcon();
        return scaleIcon(icon, maxW, maxH);
    }

    // shared scaling
    private ImageIcon scaleIcon(ImageIcon icon, int maxW, int maxH) {
        Image img = icon.getImage();
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        if (w <= 0 || h <= 0) return icon;

        double scale = Math.min((double) maxW / w, (double) maxH / h);
        return new ImageIcon(img.getScaledInstance(
            (int)(w * scale), (int)(h * scale), Image.SCALE_SMOOTH));
    }

    private void animateMouth() {
        frameIndex = (frameIndex + 1) % mouthFrames.length;
        waifuLabel.setIcon(mouthFrames[frameIndex]);
    }

    private void showGreeting() {
        mouthTimer.start();
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return core.getGreeting();
            }

            @Override
            protected void done() {
                mouthTimer.stop();
                waifuLabel.setIcon(mouthFrames[0]);
                try {
                    addBubble(core.getWaifuName() + ": " + get(),
                        Theme.WAIFU_BUBBLE, Color.BLACK, false);
                } catch (InterruptedException | ExecutionException ex) {
                    addBubble(core.getWaifuName() + ": hey~",
                        Theme.WAIFU_BUBBLE, Color.BLACK, false);
                }
            }
        };
        worker.execute();
    }

    // settings popup - api key, personality, wipe memory, etc
    private void showSettings() {
        var config = core.getConfig();

        JTextField apiField = new JTextField(config.getApiKey(), 30);
        JTextField nameField = new JTextField(config.getUserName(), 15);
        JTextField waifuField = new JTextField(config.getWaifuName(), 15);
        JTextField imgClosedField = new JTextField(config.getWaifuImageClosed(), 25);
        JTextField imgOpenField = new JTextField(config.getWaifuImageOpen(), 25);

        // all personality types
        String[] personalities = {
            "tsundere", "yandere", "kuudere", "dandere", "deredere",
            "kamidere", "gyaru", "oneesan", "tomboy", "maid", "succubus"
        };
        JComboBox<String> personalityBox = new JComboBox<>(personalities);
        personalityBox.setSelectedItem(config.getPersonality());

        // wipe memory button - red so they know its serious
        JButton wipeBtn = new JButton("Wipe Chat Memory");
        wipeBtn.setBackground(new Color(220, 50, 50));
        wipeBtn.setForeground(Color.WHITE);
        wipeBtn.setFocusPainted(false);
        wipeBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "This will delete ALL chat history. She won't remember anything.\nAre you sure?",
                "Wipe Memory", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                core.wipeMemory();
                JOptionPane.showMessageDialog(this,
                    "Memory wiped! Restart the app for a fresh start.",
                    "Done", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        Object[] fields = {
            "API Key:", apiField,
            "Your Name:", nameField,
            "Waifu Name:", waifuField,
            "Personality:", personalityBox,
            "Image - Closed Mouth (optional):", imgClosedField,
            "Image - Open Mouth (optional):", imgOpenField,
            " ", wipeBtn
        };

        // remember old values to check if they changed
        String oldName = config.getUserName();
        String oldWaifuName = config.getWaifuName();
        String oldPersonality = config.getPersonality();

        int result = JOptionPane.showConfirmDialog(this, fields,
            "Settings", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            config.setApiKey(apiField.getText().trim());
            config.setUserName(nameField.getText().trim());
            config.setWaifuName(waifuField.getText().trim());
            config.setPersonality((String) personalityBox.getSelectedItem());
            config.setWaifuImageClosed(imgClosedField.getText().trim());
            config.setWaifuImageOpen(imgOpenField.getText().trim());
            config.save();

            // check if they changed name or personality - suggest wiping memory
            boolean changed = !oldName.equals(config.getUserName())
                || !oldWaifuName.equals(config.getWaifuName())
                || !oldPersonality.equals(config.getPersonality());

            if (changed) {
                addBubble("Settings saved! Restart the app for changes.\n"
                    + "Tip: you changed name/personality - consider wiping memory "
                    + "(Settings > Wipe Chat Memory) for the best experience!",
                    new Color(200, 200, 200), Color.DARK_GRAY, false);
            } else {
                addBubble("Settings saved! Restart the app for changes to take effect.",
                    new Color(200, 200, 200), Color.DARK_GRAY, false);
            }
        }
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        addBubble("You: " + text, Theme.USER_BUBBLE, Color.BLACK, true);
        inputField.setText("");

        showTyping();
        mouthTimer.start();

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return core.chat(text);
            }

            @Override
            protected void done() {
                mouthTimer.stop();
                waifuLabel.setIcon(mouthFrames[0]);
                hideTyping();

                try {
                    addBubble(core.getWaifuName() + ": " + get(),
                        Theme.WAIFU_BUBBLE, Color.BLACK, false);
                } catch (InterruptedException | ExecutionException ex) {
                    addBubble(core.getWaifuName() + ": ...",
                        Theme.WAIFU_BUBBLE, Color.BLACK, false);
                }
            }
        };
        worker.execute();
    }

    private void showTyping() {
        typingBubble = makeBubbleWrapper(
            core.getWaifuName() + " is typing...",
            new Color(255, 210, 220), Color.GRAY, false);
        chatBox.add(typingBubble);
        chatBox.revalidate();
        chatBox.repaint();
        scrollDown();
    }

    private void hideTyping() {
        if (typingBubble != null) {
            chatBox.remove(typingBubble);
            typingBubble = null;
            chatBox.revalidate();
            chatBox.repaint();
        }
    }

    private JPanel makeBubbleWrapper(String text, Color bg, Color fg, boolean right) {
        JPanel wrapper = new JPanel(new FlowLayout(
            right ? FlowLayout.RIGHT : FlowLayout.LEFT, 6, 0));
        wrapper.setOpaque(false);

        BubblePanel bubble = new BubblePanel(text, bg, fg);
        int maxW = Math.max(200, this.getWidth() / 3);
        bubble.setMaximumSize(new Dimension(maxW + 24, Integer.MAX_VALUE));

        wrapper.add(bubble);
        return wrapper;
    }

    private void addBubble(String text, Color bg, Color fg, boolean right) {
        chatBox.add(makeBubbleWrapper(text, bg, fg, right));
        chatBox.add(Box.createVerticalStrut(8));
        chatBox.revalidate();
        chatBox.repaint();
        scrollDown();
    }

    private void scrollDown() {
        SwingUtilities.invokeLater(() -> {
            Container p = chatBox.getParent();
            if (p instanceof JViewport vp) {
                Component grand = vp.getParent();
                if (grand instanceof JScrollPane sp) {
                    JScrollBar bar = sp.getVerticalScrollBar();
                    bar.setValue(bar.getMaximum());
                }
            }
        });
    }

    // rounded chat bubble
    class BubblePanel extends JPanel {
        private final JTextArea textArea;
        private final Color bg;

        public BubblePanel(String text, Color bg, Color fg) {
            this.bg = bg;
            setLayout(new BorderLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(8, 12, 8, 12));

            textArea = new JTextArea(text);
            textArea.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
            textArea.setForeground(fg);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEditable(false);
            textArea.setOpaque(false);
            textArea.setFocusable(false);

            add(textArea, BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public Dimension getPreferredSize() {
            int maxW = Math.max(200, MainWindow.this.getWidth() / 3);
            textArea.setSize(new Dimension(maxW, Short.MAX_VALUE));
            Dimension pref = textArea.getPreferredSize();
            return new Dimension(
                Math.min(pref.width, maxW) + 24,
                pref.height + 16
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}

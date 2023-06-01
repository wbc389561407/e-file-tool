package com.nobug;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.nobug.util.AESFile;
import com.nobug.util.FileUtil;
import com.nobug.util.PropertiesUtil;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * @author 389561407@qq.com
 * @version 1.0
 * @since 2022-11-17
 */
public class RunMain {
    private JPanel root;
    private JLabel filePath;
    private JTextField textField1;
    private JTextField textField3;
    private JLabel password;
    private JLabel passwordmode;
    private JButton fileb;
    private JButton bjia;
    private JButton bjie;
    private JComboBox comboBox1;
    private JLabel status;
    private JLabel stautsShow;
    private JLabel runState;
    private JLabel vs;
    private JLabel vsText;
    private JLabel runStateText;
    private JLabel title;

    private static boolean flag = false;


    //    private static List<String> typeList;
    private static Map<String, String> typeMap;

    static {
        typeMap = new HashMap<>();
        typeMap.put("T2T(文档加密)", "T2T");
        typeMap.put("RT2TM(加密后为MP4文件)", "RT2TM");
        typeMap.put("RT2MM(加密后为MP4文件)", "RT2MM");
        typeMap.put("R2T(时间戳)", "R2T");
        typeMap.put("R2M(MD5)", "R2M");
        typeMap.put("V2Z", "V2Z");
//        typeMap.put("T2T", "T2T(文档加密)");
//        typeMap.put("RT2TM", "RT2TM(加密后为MP4文件)");
//        typeMap.put("RT2MM", "RT2MM(加密后为MP4文件)");
//        typeMap.put("R2T", "R2T(时间戳)");
//        typeMap.put("R2M", "R2M(MD5)");
//        typeMap.put("V2Z", "V2Z");
//        T2T(文档加密)
//        RT2TM(加密后为MP4文件)
//        RT2MM(加密后为MP4文件)
//        R2T(时间戳)
//        R2M(MD5)
//        V2Z
//
//        typeList = new ArrayList();
//        typeList.add("T2T(文档加密)");
//        typeList.add("RT2TM(加密后为MP4文件");
//        typeList.add("RT2MM(加密后为MP4文件");
//        typeList.add("R2T(时间戳)");
//        typeList.add("R2M(MD5)");
//        typeList.add("V2Z");


    }


    public RunMain() {

        ComboBoxModel model = comboBox1.getModel();

        vsText.setText(PropertiesUtil.getValue("vs"));
        new DropTarget(textField1, DnDConstants.ACTION_COPY_OR_MOVE,
                new DropTargetAdapter() {
                    @Override
                    public void drop(DropTargetDropEvent dtde) {
                        try {
                            // 如果拖入的文件格式受支持
                            if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                                System.out.println("接受");
//                                // 接收拖拽来的数据
                                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                                @SuppressWarnings("unchecked")
                                List<File> list = (List<File>) (dtde.getTransferable()
                                        .getTransferData(DataFlavor.javaFileListFlavor));
                                //area.append("");
                                File[] files = new File[1];
                                files[0] = list.get(0);
                                System.out.println("添加文件：" + list.get(0).getAbsolutePath());
                                textField1.setText(list.get(0).getAbsolutePath());

                                // 指示拖拽操作已完成
                                dtde.dropComplete(true);
                                System.out.println("拖拽完成");
                            } else {
                                // 拒绝拖拽来的数据
                                dtde.rejectDrop();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });

        fileb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jFileChooser.showSaveDialog(null);
                File selectedFile = jFileChooser.getSelectedFile();
                if (selectedFile == null) {
                    return;
                }
                String path = selectedFile.getPath();
                textField1.setText(path);
            }
        });

        bjia.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!flag) {
                    flag = true;
                    runStateText.setText("运行中...");
                    System.out.println("加密");
                    ArrayList<String> fileList = new ArrayList<>();
                    String filePath = textField1.getText();
                    File file = new File(filePath);

                    if (file.isDirectory()) {
                        File[] filesList = FileUtil.getFilesList(filePath);
                        for (File file1 : filesList) {
                            fileList.add(file1.getPath());
                        }

                    } else {
                        fileList.add(filePath);
                    }

                    String password = textField3.getText();
                    String mode = Objects.requireNonNull(comboBox1.getSelectedItem()).toString();

                    System.out.println("mode:" + mode);
                    //type to type 文件类型不变 V1
                    if (model.getElementAt(0).toString().equals(mode)) {
                        System.out.println("T2T");
//                    if (typeList.get(0).equals(mode)) {
                        new Thread(() -> {
                            try {

                                for (String s : fileList) {
                                    String encrypt = AESFile.encrypt(s, password, stautsShow);
                                }

                            } catch (RuntimeException e1) {
                                JOptionPane.showMessageDialog(null, e1.getMessage());
                            }
                            flag = false;
                            runStateText.setText("未运行");
                        }).start();
                    } else {
                        System.out.println(mode);
                        new Thread(() -> {
                            try {
                                for (String s : fileList) {
                                    String encrypt = AESFile.encrypt(s, password, typeMap.get(mode), stautsShow);
                                }
                            } catch (RuntimeException e1) {
                                JOptionPane.showMessageDialog(null, e1.getMessage());
                            }

                            flag = false;
                            runStateText.setText("未运行");
                        }).start();
                    }

//                    //rename type to Time mp4 文件改为MP4文件 文件名为时间戳 解密后会恢复原有名字
//                    if (model.getElementAt(1).toString().equals(mode)) {
//                        System.out.println("RT2TM");
////                    if ("RT2TM".equals(mode)) {
//                        new Thread(() -> {
//                            try {
//                                for (String s : fileList) {
//                                    String encrypt = AESFile.encrypt(s, password, typeMap.get(mode), stautsShow);
//                                }
//                            } catch (RuntimeException e1) {
//                                JOptionPane.showMessageDialog(null, e1.getMessage());
//                            }
//                            flag = false;
//                            runStateText.setText("未运行");
//                        }).start();
//                    }
//
//                    //rename type to MD5 mp4 文件改为MP4文件 文件名为 32为 MD5 解密后会恢复原有名字
//                    if (model.getElementAt(2).toString().equals(mode)) {
//                        System.out.println("RT2MM");
////                    if ("RT2MM".equals(mode)) {
//                        new Thread(() -> {
//                            try {
//                                for (String s : fileList) {
//                                    String encrypt = AESFile.encrypt(s, password, typeMap.get(mode), stautsShow);
//                                }
//                            } catch (RuntimeException e1) {
//                                JOptionPane.showMessageDialog(null, e1.getMessage());
//                            }
//
//                            flag = false;
//                            runStateText.setText("未运行");
//                        }).start();
//                    }
//
//
//                    //rename to time 文件名改为时间戳  解密后会恢复原有名字 V3
//                    if (model.getElementAt(3).toString().equals(mode)) {
//                        System.out.println("R2T");
////                    if ("R2T".equals(mode)) {
//                        new Thread(() -> {
//                            try {
//                                for (String s : fileList) {
//                                    String encrypt = AESFile.encrypt(s, password, typeMap.get(mode), stautsShow);
//                                }
//                            } catch (RuntimeException e1) {
//                                JOptionPane.showMessageDialog(null, e1.getMessage());
//                            }
//
//                            flag = false;
//                            runStateText.setText("未运行");
//                        }).start();
//                    }
//                    //rename to MD5  文件名改为 MD5 解密后会恢复原有名字
//                    if (model.getElementAt(4).toString().equals(mode)) {
//                        System.out.println("R2M");
////                    if ("R2M".equals(mode)) {
//                        new Thread(() -> {
//                            try {
//                                for (String s : fileList) {
//                                    String encrypt = AESFile.encrypt(s, password, typeMap.get(mode), stautsShow);
//                                }
//                            } catch (RuntimeException e1) {
//                                JOptionPane.showMessageDialog(null, e1.getMessage());
//                            }
//
//                            flag = false;
//                            runStateText.setText("未运行");
//                        }).start();
//                    }
//
//                    //Video to zybfq  文件名不改，后缀改为专用播放器格式 解密后会恢复原有名字
//                    if (model.getElementAt(5).toString().equals(mode)) {
//                        System.out.println("V2Z");
////                    if ("V2Z".equals(mode)) {
//                        new Thread(() -> {
//                            try {
//                                for (String s : fileList) {
//                                    String encrypt = AESFile.encrypt(s, password, typeMap.get(mode), stautsShow);
//                                }
//                            } catch (RuntimeException e1) {
//                                JOptionPane.showMessageDialog(null, e1.getMessage());
//                            }
//
//                            flag = false;
//                            runStateText.setText("未运行");
//                        }).start();
//                    }


                } else {
                    JOptionPane.showMessageDialog(null, "程序运行中。。。");
                }


            }
        });
//        bjie.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (!flag) {
//
//                    System.out.println("解密");
//                    flag = true;
//                    runStateText.setText("运行中...");
//                    String filePath = textField1.getText();
//                    File file = new File(filePath);
//                    ArrayList<String> fileList = new ArrayList<>();
//
//                    if (file.isDirectory()) {
//                        File[] filesList = FileUtil.getFilesList(filePath);
//                        for (File file1 : filesList) {
//                            fileList.add(file1.getPath());
//                        }
//
//                    } else {
//                        fileList.add(filePath);
//                    }
//
//                    String password = textField3.getText();
//                    String mode = Objects.requireNonNull(comboBox1.getSelectedItem()).toString();
//
//                    if ("V2Z".equals(mode)) {
//                        JOptionPane.showMessageDialog(null, "请使用专用播放器播放");
//                        flag = false;
//                        return;
//                    }
//
//                    if ("T2T".equals(mode)) {
//                        new Thread(() -> {
//                            try {
//                                for (String s : fileList) {
//                                    String decrypt = AESFile.decrypt(s, password, stautsShow);
//                                }
//                            } catch (RuntimeException e1) {
//                                JOptionPane.showMessageDialog(null, e1.getMessage());
//                            }
//                            flag = false;
//                            runStateText.setText("未运行");
//                        }).start();
//                    } else {
//                        new Thread(() -> {
//                            try {
//                                for (String s : fileList) {
//                                    String decrypt = AESFile.decrypt(s, password, mode, stautsShow);
//                                }
//                            } catch (RuntimeException e1) {
//                                JOptionPane.showMessageDialog(null, e1.getMessage());
//                            }
//                            flag = false;
//                            runStateText.setText("未运行");
//                        }).start();
//                    }
//
//
//                } else {
//                    JOptionPane.showMessageDialog(null, "程序运行中。。。");
//                }
//            }
//        });

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("e-file-tool");
        frame.setContentPane(new RunMain().root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();//获取桌面像素
        int WIDTH = dim.width / 2;
        int HEIGHT = dim.height / 2;
        if (WIDTH < 700) {
            WIDTH = 700;
        }
        if (HEIGHT < 360) {
            HEIGHT = 360;
        }

        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);//居中显示
        frame.setVisible(true);

    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        root = new JPanel();
        root.setLayout(new GridLayoutManager(6, 5, new Insets(0, 0, 0, 0), -1, -1));
        root.setBackground(new Color(-11611918));
        filePath = new JLabel();
        filePath.setBackground(new Color(-857102));
        Font filePathFont = this.$$$getFont$$$(null, Font.BOLD, 18, filePath.getFont());
        if (filePathFont != null) filePath.setFont(filePathFont);
        filePath.setHorizontalAlignment(2);
        filePath.setText("文件路径：");
        root.add(filePath, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        passwordmode = new JLabel();
        Font passwordmodeFont = this.$$$getFont$$$(null, Font.BOLD, 18, passwordmode.getFont());
        if (passwordmodeFont != null) passwordmode.setFont(passwordmodeFont);
        passwordmode.setText("加密模式：");
        root.add(passwordmode, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textField1 = new JTextField();
        Font textField1Font = this.$$$getFont$$$(null, Font.BOLD, 18, textField1.getFont());
        if (textField1Font != null) textField1.setFont(textField1Font);
        textField1.setText("将文件或者文件夹拖入此处");
        root.add(textField1, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fileb = new JButton();
        Font filebFont = this.$$$getFont$$$(null, Font.BOLD, 18, fileb.getFont());
        if (filebFont != null) fileb.setFont(filebFont);
        fileb.setText("选择文件");
        root.add(fileb, new GridConstraints(2, 3, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        password = new JLabel();
        Font passwordFont = this.$$$getFont$$$(null, Font.BOLD, 20, password.getFont());
        if (passwordFont != null) password.setFont(passwordFont);
        password.setHorizontalAlignment(2);
        password.setText("密码：");
        root.add(password, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        stautsShow = new JLabel();
        Font stautsShowFont = this.$$$getFont$$$(null, Font.BOLD, 20, stautsShow.getFont());
        if (stautsShowFont != null) stautsShow.setFont(stautsShowFont);
        stautsShow.setText("");
        stautsShow.setToolTipText("");
        root.add(stautsShow, new GridConstraints(4, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        status = new JLabel();
        Font statusFont = this.$$$getFont$$$(null, Font.BOLD, 20, status.getFont());
        if (statusFont != null) status.setFont(statusFont);
        status.setHorizontalAlignment(2);
        status.setText("运行报告：");
        root.add(status, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textField3 = new JPasswordField();
        Font textField3Font = this.$$$getFont$$$(null, Font.BOLD, 18, textField3.getFont());
        if (textField3Font != null) textField3.setFont(textField3Font);
        root.add(textField3, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        vs = new JLabel();
        vs.setText("版本：");
        root.add(vs, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        vsText = new JLabel();
        vsText.setText("");
        root.add(vsText, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        runState = new JLabel();
        runState.setText("状态：");
        root.add(runState, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        runStateText = new JLabel();
        runStateText.setText("未运行");
        root.add(runStateText, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        title = new JLabel();
        Font titleFont = this.$$$getFont$$$(null, Font.BOLD, 36, title.getFont());
        if (titleFont != null) title.setFont(titleFont);
        title.setText("文件加密工具");
        root.add(title, new GridConstraints(0, 0, 2, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bjia = new JButton();
        Font bjiaFont = this.$$$getFont$$$(null, Font.BOLD, 18, bjia.getFont());
        if (bjiaFont != null) bjia.setFont(bjiaFont);
        bjia.setText("加密");
        root.add(bjia, new GridConstraints(5, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBox1 = new JComboBox();
        Font comboBox1Font = this.$$$getFont$$$(null, Font.BOLD, 18, comboBox1.getFont());
        if (comboBox1Font != null) comboBox1.setFont(comboBox1Font);
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("T2T(文档加密)");
        defaultComboBoxModel1.addElement("RT2TM(加密后为MP4文件)");
        defaultComboBoxModel1.addElement("RT2MM(加密后为MP4文件)");
        defaultComboBoxModel1.addElement("R2T(时间戳)");
        defaultComboBoxModel1.addElement("R2M(MD5)");
        defaultComboBoxModel1.addElement("V2Z");
        comboBox1.setModel(defaultComboBoxModel1);
        comboBox1.setToolTipText("");
        root.add(comboBox1, new GridConstraints(3, 3, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

}

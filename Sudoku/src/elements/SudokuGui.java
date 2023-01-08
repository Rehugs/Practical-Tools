package elements;

import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import javax.swing.border.Border;

public class SudokuGui extends JFrame implements WindowFocusListener, KeyListener, FocusListener, MouseListener
{
    private JPanel panelBody; //主体面板
    private JPanel panelRight; //右侧面板
    private JPanel panelBottom; //底部面板
    private JLabel labelLevel; //等级标签
    private JLabel labelTime; //时间标签
    private JButton buttonLabel; //标注按钮
    private JButton buttonStart; //开始/暂停按钮
    private JButton buttonRestart; //重新开始按钮
    private JTextField textFieldLevel; //等级文本框
    private JTextField textFieldTime; //时间文本框
    private JTextField textFieldMessage; //消息提醒文本框
    private JTextField[][] textFieldsCells; //单元格文本框
    
    private int gameLevel; //定义等级
    private int textLevel; //用于校验用户输入
    private int times; //定义花费的时间
    private int[][] labelData; //定义标注的数字
    private boolean isLabel; //定义当前是否处于标注状态
    private boolean isWinGame; //定义游戏是否成功
    private Timer timer; //定义时间对象
    private SudokuMap sudokuMap; //定义数独对象
    
    private final Border centernBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY);
    private final Border rightAndBottomBorder = BorderFactory.createMatteBorder(1, 1, 4, 4, Color.GRAY);
    private final Border bottomBorder = BorderFactory.createMatteBorder(1, 1, 4, 1, Color.GRAY);
    private final Border rightBorder = BorderFactory.createMatteBorder(1, 1, 1, 4, Color.GRAY);
    
    private final Color lightRed = new Color(0xFF5555); //定义显示答案处的背景色
    private final Color lightGray = new Color(0xA0A0A0); //定义不可更改处的背景色
    private final Color lightBlue = new Color(0x00AAAA); //定义相同数字处的背景色
    private final Color lightPink = new Color(0xFFAAFF); //定义标注处的背景色
    
    private final Font commonFont = new Font("Courier New", Font.BOLD, 24); //定义文字字体
    private final Font messageTextFont = new Font("FangSong", Font.BOLD, 18); //定义信息框字体
    
    
    public SudokuGui()
    {
        super("Sudoku -- by Rehug");
        this.setSize(990, 680); //设置初始窗口大小
        this.setLocationRelativeTo(null); //定义窗口居中
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //退出即终止
        
        this.initElements(); //初始化各组件
        this.layoutElements(); //布局各组件
        this.registerElements(); //注册各组件
        
        this.setVisible(true);
    }
    
    public void runSudoku()
    {
        //程序的入口点吧，将最初的数据显示出来
        this.times = 0; //初始化时间
        this.isWinGame = false; //最初定义游戏尚未成功
        this.isLabel = false; //最初定义并未处于标注状态
        this.sudokuMap = new SudokuMap();
        this.gameLevel = this.sudokuMap.getGameLevel(); //第一次两者都一样，也可以赋值为1
        this.textLevel = this.sudokuMap.getGameLevel(); //但是我乐意...
        
        //显示数据......
        this.showGameData();
    }
    
    private void initElements()
    {
        //初始化九宫格面板
        this.panelBody = new JPanel();
        this.panelBody.setBackground(this.lightGray); //设置九宫格的背景色，看起来有层次感
        
        //初始化右侧菜单栏面板
        this.panelRight = new JPanel();
        
        //初始化底部信息栏面板
        this.panelBottom = new JPanel();
        
        //初始化等级标签
        this.labelLevel = new JLabel("Level: ");
        this.labelLevel.setName("labelLevel");
        this.labelLevel.setFont(commonFont);
        this.labelLevel.setHorizontalAlignment(JLabel.RIGHT);
        
        //初始化时间标签
        this.labelTime = new JLabel("Times: ");
        this.labelTime.setName("labelTime");
        this.labelTime.setFont(commonFont);
        this.labelTime.setHorizontalAlignment(JLabel.RIGHT);
        
        //初始化等级信息框
        this.textFieldLevel = new JTextField();
        this.textFieldLevel.setName("textFieldLevel");
        this.textFieldLevel.setFont(commonFont);
        this.textFieldLevel.setHorizontalAlignment(JTextField.CENTER);
        
        //初始化时间信息框
        this.textFieldTime = new JTextField();
        this.textFieldTime.setName("textFieldTime");
        this.textFieldTime.setFont(commonFont);
        this.textFieldTime.setEditable(false); //设置时间窗口不可编辑
        this.textFieldTime.setHorizontalAlignment(JTextField.CENTER);
        
        //初始化底部信息框
        this.textFieldMessage = new JTextField();
        this.textFieldMessage.setName("textFieldMessage");
        this.textFieldMessage.setFont(messageTextFont);
        this.textFieldMessage.setEditable(false); //设置消息提示窗口不可编辑
        this.textFieldMessage.setHorizontalAlignment(JTextField.LEFT);
        
        //初始化标注按钮
        this.buttonLabel = new JButton("Label");
        this.buttonLabel.setName("buttonLabel");
        this.buttonLabel.setFont(commonFont);
        
        //初始化开始/暂停按钮
        this.buttonStart = new JButton("Start");
        this.buttonStart.setName("buttonStart");
        this.buttonStart.setFont(commonFont);
        
        //初始化重新开始游戏按钮
        this.buttonRestart = new JButton("Restart");
        this.buttonRestart.setName("buttonRestart");
        this.buttonRestart.setFont(commonFont);
        
        //初始化九宫格内各个单元格
        this.labelData = new int[9][9]; //借个东风......
        this.textFieldsCells = new JTextField[9][9];
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                textFieldsCells[i][j] = new JTextField();
                textFieldsCells[i][j].setFont(commonFont);
                textFieldsCells[i][j].setHorizontalAlignment(JTextField.CENTER);
                textFieldsCells[i][j].setName(Integer.toString(i * 9 + j));
                
                //设置每个边框的边界，看起来更加美观
                if (i == 2 && j == 2 || i == 2 && j == 5 || i == 5 && j == 2 || i == 5 && j == 5)
                {
                    textFieldsCells[i][j].setBorder(rightAndBottomBorder);
                }
                else if (j == 2 || j == 5)
                {
                    textFieldsCells[i][j].setBorder(rightBorder);
                }
                else if (i == 2 || i == 5)
                {
                    textFieldsCells[i][j].setBorder(bottomBorder);
                }
                else
                {
                    textFieldsCells[i][j].setBorder(centernBorder);
                }
                
                this.labelData[i][j] = 0; //用于记录用户做的标注信息
            }
        }
    }
    
    private void layoutElements()
    {
        //布局九宫格面板内容
        this.panelBody.setLayout(new GridBagLayout());
        GridBagConstraints body = new GridBagConstraints();
        body.fill = GridBagConstraints.BOTH;
        body.weightx = 1;
        body.weighty = 1;
        body.gridwidth = 1;
        body.gridheight = 1;
        for (int i = 0; i < 9; i++)
        {
            //注意此处的顺序，当i不变时，单元格向下移动
            body.gridy = i;
            for (int j = 0; j < 9; j++)
            {
                body.gridx = j;
                this.panelBody.add(this.textFieldsCells[i][j], body);
            }
        }
        
        //布局右侧菜单面板内容
        this.panelRight.setLayout(new GridBagLayout()); //网格包布局
        GridBagConstraints right = new GridBagConstraints();
        right.fill = GridBagConstraints.HORIZONTAL; //使组件在水平方向上填充
        right.insets = new Insets(4, 4, 4, 4);
        //布局等级标签
        right.gridx = 0;
        right.gridy = 0;
        right.weightx = 1;
        right.weighty = 2;
        right.gridwidth = 1;
        right.gridheight = 2;
        this.panelRight.add(this.labelLevel, right);
        //布局等级信息框
        right.gridx = 1;
        right.gridy = 0;
        right.weightx = 3;
        right.weighty = 2;
        right.gridwidth = 3;
        right.gridheight = 2;
        this.panelRight.add(this.textFieldLevel, right);
        //布局时间标签
        right.gridx = 0;
        right.gridy = 2;
        right.weightx = 1;
        right.weighty = 2;
        right.gridwidth = 1;
        right.gridheight = 2;
        this.panelRight.add(this.labelTime, right);
        //布局时间信息框
        right.gridx = 1;
        right.gridy = 2;
        right.weightx = 3;
        right.weighty = 2;
        right.gridwidth = 3;
        right.gridheight = 2;
        this.panelRight.add(this.textFieldTime, right);
        //布局开始/暂停按钮
        right.gridx = 0;
        right.gridy = 4;
        right.weightx = 4;
        right.weighty = 2;
        right.gridwidth = 4;
        right.gridheight = 2;
        this.panelRight.add(this.buttonStart, right);
        //布局标注/停止按钮
        right.gridx = 0;
        right.gridy = 6;
        right.weightx = 4;
        right.weighty = 2;
        right.gridwidth = 4;
        right.gridheight = 2;
        this.panelRight.add(this.buttonLabel, right);
        //布局重新开始游戏按钮
        right.gridx = 0;
        right.gridy = 8;
        right.weightx = 4;
        right.weighty = 2;
        right.gridwidth = 4;
        right.gridheight = 2;
        this.panelRight.add(this.buttonRestart, right);
        
        //布局底部信息面板内容
        this.panelBottom.setLayout(new GridBagLayout()); //网格包布局
        GridBagConstraints bottom = new GridBagConstraints();
        bottom.fill = GridBagConstraints.BOTH; //纵横填充面板
        bottom.insets = new Insets(4, 4, 4, 4);
        bottom.gridx = 0;
        bottom.gridy = 0;
        bottom.weightx = 12;
        bottom.weighty = 1;
        bottom.gridwidth = 12;
        bottom.gridheight = 1;
        this.panelBottom.add(this.textFieldMessage, bottom);
        
        //布局主体面板内容
        this.getContentPane().setLayout(new GridBagLayout()); //网格包布局
        GridBagConstraints content = new GridBagConstraints();
        content.fill = GridBagConstraints.BOTH; //纵横填充，利于缩放
        content.insets = new Insets(4, 4, 4, 4); //有点边距看起来美观
        //向主体面板加入九宫格面板
        content.gridx = 0;
        content.gridy = 0;
        content.weightx = 9;
        content.weighty = 9;
        content.gridwidth = 9;
        content.gridheight = 9;
        this.getContentPane().add(this.panelBody, content);
        //向主体面板加入右侧菜单面板
        content.gridx = 9;
        content.gridy = 0;
        content.weightx = 2;
        content.weighty = 9;
        content.gridwidth = 2;
        content.gridheight = 9;
        this.getContentPane().add(this.panelRight, content);
        //向主体面板加入底部信息菜单面板
        content.gridx = 0;
        content.gridy = 9;
        content.weightx = 11;
        content.weighty = 1;
        content.gridwidth = 11;
        content.gridheight = 1;
        this.getContentPane().add(this.panelBottom, content);
    }
    
    private void registerElements()
    {
        //注册主体组件，提供提示信息
        this.addKeyListener(this); //添加快捷键显示答案，以下如无特殊声明，皆是如此
        this.addWindowFocusListener(this); //窗口失去焦点时给出提示
        
        //注册等级标签
        this.labelLevel.addKeyListener(this);
        this.labelLevel.addMouseListener(this); //鼠标滑入滑出时在信息栏给出提示
        
        //注册时间标签
        this.labelTime.addKeyListener(this);
        this.labelTime.addMouseListener(this);
        
        //注册开始按钮
        this.buttonStart.addKeyListener(this);
        this.buttonStart.addMouseListener(this); //点击完成后切换状态，并执行任务
        
        //注册标注按钮
        this.buttonLabel.addKeyListener(this);
        this.buttonLabel.addMouseListener(this); //点击切换状态，执行任务
        
        //注册重新开始按钮
        this.buttonRestart.addKeyListener(this);
        this.buttonRestart.addMouseListener(this); //点击重新开始
        
        //注册等级信息框
        this.textFieldLevel.addKeyListener(this);
        this.textFieldLevel.addFocusListener(this);
        
        //注册时间信息框
        this.textFieldTime.addKeyListener(this);
        
        //注册底部信息框
        this.textFieldMessage.addKeyListener(this);
        
        //注册九宫格内的每个单元格
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                this.textFieldsCells[i][j].addKeyListener(this);
                this.textFieldsCells[i][j].addMouseListener(this);
                this.textFieldsCells[i][j].addFocusListener(this);
            }
        }
    }
    
    //各个监听事件的重写
    //--------------WindowFocusListener-----------
    @Override
    public void windowGainedFocus(WindowEvent e)
    {
        showMessage("", Color.BLACK);
    }
    
    @Override
    public void windowLostFocus(WindowEvent e)
    {
        showMessage("Try to press F1 for help.", Color.RED);
    }
    
    //-----------------KeyListener----------------
    @Override
    public void keyTyped(KeyEvent e) //键入
    {
        if (e.getSource() instanceof JTextField) //键入的对象如果是文本框
        {
            char keyChar = e.getKeyChar();
            if (keyChar == KeyEvent.VK_ENTER) //当键入 Enter 时
            {
                this.textFieldMessage.requestFocus(); //焦点移至信息框
            }
            else if (keyChar <= KeyEvent.VK_0 || keyChar > KeyEvent.VK_9) //当信息框的输入为非1—9时屏蔽
            {
                e.consume(); //关键，屏蔽掉非法输入
            }
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) //按下
    {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_F1)
        {
            this.showResult();
            showMessage("Please think more and finish the game alone.", Color.RED);
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) //释放
    {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_F1)
        {
            this.showUserData();
            showMessage("", Color.BLACK);
        }
    }
    
    //-----------------FocusListener--------------
    @Override
    public void focusGained(FocusEvent e) //仅仅是为了在文本框输入信息后......
    {
        
        Object object = e.getSource();
        
        if (object instanceof JTextField textField) //虽然说加上也没什么用
        {
            if (textField.getName().startsWith("textField"))
            {
                if (textField.getName().equals("textFieldLevel"))
                {
                    showMessage("Please input a number between 1 and 5, then press the key Restart.", Color.BLACK);
                }
            }
            else if (!this.isWinGame && this.buttonStart.getName().equals("buttonPause"))//因为全局只有九宫格和等级文本框加入了焦点响应事件
            {
                showMessage("Please input a number between 1 and 9.", Color.BLACK);
            }
        }
        
        if (this.textLevel != this.gameLevel) //此处是为了校验更改等级后是否按下 Restart
        {
            this.textLevel = this.gameLevel; //倘若没有的话，就将textLevel恢复成gameLevel
        }
        
    }
    
    @Override
    public void focusLost(FocusEvent e) //校验，写入数据
    {
        Object object = e.getSource();
        
        if (object instanceof JTextField textField)
        {
            
            if (!"".equals(textField.getText())) //校验输入的信息是否为空
            {
                if (textField.getName().startsWith("textField"))
                {
                    if (textField.getName().equals("textFieldLevel")) //等级文本框
                    {
                        int number = Integer.parseInt(textField.getText()); //由于所有的信息框都有过滤功能
                        if (number < 1 || number > 5)
                        {
                            this.showUserData(); //此处只是为了显示原等级
                            showMessage("The number you input is not satisfied.", Color.RED);
                        }
                        else
                        {
                            this.textLevel = number; //将文本数据赋值给 textLevel, 之后
                            showMessage("If you want the results to take effect, please press Restart next", Color.BLACK);
                        }
                    }
                }
                else if (!this.isWinGame && this.buttonStart.getName().equals("buttonPause"))//九宫格的文本框
                {
                    int number = Integer.parseInt(textField.getText()); //此处的转换唯一需要担心的是数值超限
                    if (number < 1 || number > 9)
                    {
                        this.showUserData();
                        showMessage("Please play carefully, input values must be between 1 and 9", Color.RED);
                    }
                    else
                    {
                        int row = Integer.parseInt(textField.getName()) / 9;
                        int line = Integer.parseInt(textField.getName()) % 9;
                        
                        if (SudokuMap.isSatisfied(this.sudokuMap.getUserData(), row, line, number))
                        {
                            this.labelData[row][line] = 0; //倘若关闭标注后，将labelData里的数据清零
                            SudokuMap.writeToCells(this.sudokuMap.getUserData(), row, line, number); //直接结束就好了
                            if (isLabel) //似乎是唯一一次用处......
                            {
                                this.labelData[row][line] = number; //而且是为了省事，倘若标注全对了...
                            }
                            this.judgeSudoku(); //填写进数据后校验用户填写的数据是否已经完成游戏
                        }
                        else
                        {
                            this.showUserData();
                            showMessage("Check the values carefully to make sure they comply with the rules", Color.RED);
                        }
                    }
                }
                else //当处于暂停或者胜利后
                {
                    this.showUserData(); //用户如果输入信息，可以照常清零
                }
            }
            else if (!textField.getName().equals("textFieldLevel") && this.buttonStart.getName().equals("buttonPause")) //判断该文本框是否为九宫格
            {
                //倘若是，则执行代码，将该处结果填0，以实现删除数据
                int row = Integer.parseInt(textField.getName()) / 9;
                int line = Integer.parseInt(textField.getName()) % 9;
                
                this.labelData[row][line] = 0; //标注数据也清零，判断的话带不带都是可以的
                SudokuMap.writeToCells(this.sudokuMap.getUserData(), row, line, 0);
                showUserData();
            }
            
        }
        
    }
    
    //---------------MouseListener----------------
    @Override
    public void mouseClicked(MouseEvent e) //单击
    {
        Object object = e.getSource();
        
        if (object instanceof JButton button)
        {
            if (!this.isWinGame) //倘若游戏尚未成功
            {
                switch (button.getName())
                {
                    case "buttonStart" ->
                    {
                        this.startSudoku();
                        button.setText("Pause");
                        button.setName("buttonPause");
                    }
                    case "buttonPause" ->
                    {
                        this.pauseSudoku();
                        button.setText("Start");
                        button.setName("buttonStart");
                    }
                    case "buttonLabel" ->
                    {
                        this.labelSudoku();
                        button.setText("Stop Label");
                        button.setName("buttonStopLabel");
                    }
                    case "buttonStopLabel" ->
                    {
                        this.stopLabelSudoku();
                        button.setText("Label");
                        button.setName("buttonLabel");
                    }
                    case "buttonRestart" -> this.restartSudoku();
                }
            }
            else if (button.getName().equals("buttonRestart")) //否则只能点击重新开始
            {
                this.restartSudoku();
            }
        }
    }
    
    @Override
    public void mousePressed(MouseEvent e) //按下
    {
    
    }
    
    @Override
    public void mouseReleased(MouseEvent e) //松开
    {
    
    }
    
    @Override
    public void mouseEntered(MouseEvent e) //划入 —— 主要为显示提示信息
    {
        if (!this.isWinGame)
        {
            Object object = e.getSource();
            
            if (object instanceof JTextField textField)
            {
                if (!textField.getName().startsWith("textField")) //九宫格内的文本框
                {
                    try
                    {
                        String numberString = textField.getName();
                        int row = Integer.parseInt(numberString) / 9;
                        int line = Integer.parseInt(numberString) % 9;
                        int number = this.sudokuMap.getUserData()[row][line].getNumber();
                        
                        if (number != 0) //如果该处数字不为0，将相同数字背景颜色颜色特殊化
                        {
                            for (int i = 0; i < 9; i++)
                            {
                                for (int j = 0; j < 9; j++)
                                {
                                    if (this.sudokuMap.getUserData()[i][j].getNumber() == number)
                                    {
                                        this.textFieldsCells[i][j].setBackground(lightBlue);
                                        this.textFieldsCells[i][j].setForeground(Color.BLACK);
                                    }
                                }
                            }
                        }
                    }
                    catch (NullPointerException error1)
                    {
                        System.err.println("The data is still Null.");
                    }
                    catch (Exception error2)
                    {
                        System.err.println("Here is something wrong.");
                    }
                }
            }
            else if (object instanceof JLabel label)
            {
                switch (label.getName())
                {
                    case "labelLevel" ->
                            showMessage("The difficulty level of the game, you can change them between 0 and 5.", Color.BLACK);
                    case "labelTime" ->
                            showMessage("Game running time, hope you can finish as soon as possible.", Color.BLACK);
                }
            }
            else if (object instanceof JButton button)
            {
                switch (button.getName())
                {
                    case "buttonStart" -> showMessage("Start the game and start the timer.", Color.BLACK);
                    case "buttonPause" -> showMessage("Pause the game and stop the timer.", Color.BLACK);
                    case "buttonLabel" ->
                            showMessage("Enable the annotation function to mark numbers in squares.", Color.BLACK);
                    case "buttonStopLabel" -> showMessage("Disable annotation function.", Color.BLACK);
                    case "buttonRestart" -> showMessage("Start a whole new game.", Color.BLACK);
                }
            }
        }
    }
    
    @Override
    public void mouseExited(MouseEvent e) //划出 -- 当划出时将原来划入显示的信息删除
    {
        if (!isWinGame)
        {
            Object object = e.getSource();
            
            if (object instanceof JTextField textField)
            {
                if (!textField.getName().startsWith("textField")) //九宫格内的文本框，尽管其余的文本框也没有设置鼠标监听
                {
                    try
                    {
                        this.showUserData(); //恢复用户数据原先单元格的底色
                        this.textFieldMessage.requestFocus(); //将九宫格内的焦点隐藏
                    }
                    catch (NullPointerException error1)
                    {
                        System.err.println("The data is still Null.");
                    }
                    catch (Exception error2)
                    {
                        System.err.println("Here is something wrong.");
                    }
                }
            }
            else if (object instanceof JLabel)
            {
                showMessage("", Color.BLACK);
            }
            else if (object instanceof JButton)
            {
                showMessage("", Color.BLACK);
            }
        }
    }
    //------------------------------------------------------
    
    
    //--------------------代码功能实现----------------------
    private void startTimer() //开始计时功能
    {
        this.timer = new Timer();
        TimerTask timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                if (buttonStart.getName().equals("buttonPause")) //当按下Start按钮时，开始计数
                {
                    times++; //每次调用函数，将时间加1
                    showTimes(); //调用函数显示时间
                }
            }
        };
        timer.schedule(timerTask, 1000L, 1000L); // 开始游戏时自动计时
    }
    
    private void showTimes()
    {
        int minutes = this.times / 60;
        int seconds = this.times % 60;
        
        String minutesString = Integer.toString(minutes);
        String secondsString = Integer.toString((seconds));
        
        
        if (minutes < 10) //如果分钟数小于10，加上0前缀
        {
            minutesString = "0" + minutesString;
        }
        
        if (seconds < 10) //如果秒钟数小于10，加上0前缀
        {
            secondsString = "0" + secondsString;
        }
        
        this.textFieldTime.setText(minutesString + ":" + secondsString); //拼接字符串，并输出时间信息
    }
    
    private void startSudoku()
    {
        this.startTimer(); //开始计时
        this.showUserData(); //显示用户数据，并且开启可以编辑功能
    }
    
    private void pauseSudoku() //暂停计时，并将各个文本框设置为不可编辑状态
    {
        this.timer.cancel(); //将timer的任务取消
        this.showUserData(); //内部有条件判断，按下暂停键时，该按键的昵称仍然是 buttonPause
    }
    
    private void labelSudoku()
    {
        this.isLabel = true;
    }
    
    private void stopLabelSudoku()
    {
        this.isLabel = false;
    }
    
    private void restartSudoku()
    {
        //仅仅初始化数据，开始游戏需要点击Start
        this.startTimer(); //无关紧要...
        this.timer.cancel(); //重新开始取消时间任务
        this.times = 0; //将时间初始化为0
        this.isWinGame = false; //初始化游戏进度
        this.isLabel = false; //初始化标注条件
    
        //重置开始按钮
        this.buttonStart.setText("Start");
        this.buttonStart.setName("buttonStart");
        //重置标注按钮
        this.buttonLabel.setText("Label");
        this.buttonLabel.setName("buttonLabel");
    
        this.gameLevel = this.textLevel; //将更改后的等级赋值给gameLevel即可，用于显示数据
        this.sudokuMap.createSudokuMap(this.gameLevel); //生成新的数独游戏数据
        
        this.showGameData(); //显示游戏数据
    }
    
    private void judgeSudoku() //当九宫格内的文本框失去焦点时，判断游戏是否结束
    {
        if (sudokuMap.isWined())
        {
            this.isWinGame = true;
            //此处将标注处的数据重置为0
            for (int i = 0; i < 9; i++)
            {
                for (int j = 0; j < 9; j++)
                {
                    this.labelData[i][j] = 0; //游戏结束后标注就没用了，由pauseSudoku调用showUserData函数
                }
            }
            
            //重置开始按钮
            this.buttonStart.setText("Start");
            this.buttonStart.setName("buttonStart");
            //重置标注按钮
            this.buttonLabel.setText("Label");
            this.buttonLabel.setName("buttonLabel");
            
            this.pauseSudoku(); //直接调用暂停游戏即可
            showMessage("Congratulations, you have completed this game", Color.RED);
        }
        else
        {
            this.showUserData();
            showMessage("", Color.BLACK);
        }
    }
    
    //在底部信息栏显示指定颜色的信息
    private void showMessage(String message, Color color)
    {
        this.textFieldMessage.setText(message);
        this.textFieldMessage.setForeground(color);
    }
    
    //显示游戏数据，并将单元格设置为不可编辑状态
    private void showGameData()
    {
        try
        {
            String text;
            for (int i = 0; i < 9; i++)
            {
                for (int j = 0; j < 9; j++)
                {
                    if (this.sudokuMap.getGameData()[i][j] != 0) //倘若初始游戏数据不为0
                    {
                        text = Integer.toString(this.sudokuMap.getGameData()[i][j]);
                        this.textFieldsCells[i][j].setText(text); //显示该处的数值
                        this.textFieldsCells[i][j].setForeground(Color.BLACK); //设置文本颜色为黑色
                        this.textFieldsCells[i][j].setBackground(lightGray); //设置背景色，特殊化显示
                    }
                    else
                    {
                        this.textFieldsCells[i][j].setText(""); //否则，设置文本为空
                        this.textFieldsCells[i][j].setForeground(Color.BLACK);
                        this.textFieldsCells[i][j].setBackground(Color.WHITE);
                    }
                    //将所有单元格设置为不可编辑
                    this.textFieldsCells[i][j].setEditable(false);
                }
            }
            this.showTimes(); //显示游戏的时间，该处的话，只在初始化时用到，显示的只能是零了....
            this.textFieldLevel.setText(Integer.toString(this.gameLevel)); //为了避免更改等级后，不能实时更新
        }
        catch (NullPointerException error1)
        {
            System.err.println("The data is still Null.");
        }
        catch (Exception error2)
        {
            System.err.println("Here is something wrong.");
        }
    }
    
    //显示用户的数据，并设置可编辑状态
    private void showUserData()
    {
        String text;
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                if (this.sudokuMap.getGameData()[i][j] == 0) //倘若初始游戏数据为空
                {
                    if (this.labelData[i][j] != 0) //首先判断标注处是否为0
                    {
                        text = Integer.toString(this.labelData[i][j]);
                        this.textFieldsCells[i][j].setBackground(lightPink); //当标注处存在数值时，设置其背景色
                    }
                    else if (this.sudokuMap.getUserData()[i][j].getNumber() != 0) //判断用户数据是否为0
                    {
                        text = Integer.toString(this.sudokuMap.getUserData()[i][j].getNumber());
                        this.textFieldsCells[i][j].setBackground(Color.WHITE);
                    }
                    else //当用户数据为0时不予显示
                    {
                        text = "";
                        this.textFieldsCells[i][j].setBackground(Color.WHITE);
                    }
                    
                    this.textFieldsCells[i][j].setText(text);
                    this.textFieldsCells[i][j].setForeground(Color.BLACK);
                    
                    if ((!this.isWinGame) && this.buttonStart.getName().equals("buttonPause")) //仅当游戏未结束且按下开始按钮时
                    {
                        this.textFieldsCells[i][j].setEditable(true); //才能设置设置可编辑
                    }
                }
                else //倘若初始数据不为空，则恢复其背景色
                {
                    text = Integer.toString(this.sudokuMap.getGameData()[i][j]);
                    this.textFieldsCells[i][j].setText(text); //显示该处的数值
                    this.textFieldsCells[i][j].setEditable(false); //设置其为不可编辑状态
                    this.textFieldsCells[i][j].setForeground(Color.BLACK); //设置文本颜色为黑色
                    this.textFieldsCells[i][j].setBackground(lightGray); //设置背景色，特殊化显示
                }
            }
        }
        this.textFieldLevel.setText(Integer.toString(this.gameLevel));
    }
    
    //显示数独的结果，并设置特殊颜色显示
    private void showResult()
    {
        String text;
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                if (this.sudokuMap.getGameData()[i][j] == 0) //此处仅仅是进入内部的钥匙
                {
                    text = Integer.toString(this.sudokuMap.getResult()[i][j]);
                    this.textFieldsCells[i][j].setText(text);
                    this.textFieldsCells[i][j].setEditable(false);
                    this.textFieldsCells[i][j].setBackground(lightRed);
                    this.textFieldsCells[i][j].setForeground(Color.BLACK);
                }
            }
        }
        this.textFieldLevel.setText(Integer.toString(this.gameLevel));
    }
}
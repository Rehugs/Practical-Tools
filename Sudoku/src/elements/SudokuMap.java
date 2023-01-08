package elements;

import java.util.Random;

public class SudokuMap
{
    private int gameLevel; //定义游戏等级
    private boolean DFSFlag; //定义DFS算法结束的标志
    private int[][] result; //用于储存结果
    private int[][] gameData; //用于储存图形界面显示的原始数据
    private Cell[][] userDataCells; //用于记录用户填写的数据
    
    
    //初始化数据，避免原数据的干扰
    private void initData()
    {
        this.DFSFlag = false;
        this.result = new int[9][9];
        this.gameData = new int[9][9];
        this.userDataCells = new Cell[9][9];
        
        //将每个数据赋上初始值，避免检测时报错
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                this.userDataCells[i][j] = new Cell();
            }
        }
    }
    
    //用于生成随机的数独数据，避免每次结果重复
    private void createRandomSudokuMap()
    {
        int x, y, number; //定义横纵坐标及该单元格的数据
        int count = 0; //定义计数的变量，首先用于随机生成部分数据
        
        while(count < 15) //数独唯一解的条件：至少17个已知数
        {
            //但是尝试过后发现对于唯一解的情况，某些时刻递归需要花费过长的时间
            x = new Random().nextInt(9); //随机生成横坐标
            y = new Random().nextInt(9); //随机生成纵坐标
            number = new Random().nextInt(9) + 1; //随机生成该位置的数值
            
            if (this.userDataCells[x][y].getNumber() == 0 && isSatisfied(this.userDataCells, x, y, number))
            {
                count++;
                this.userDataCells[x][y].setNumber(number);
            }
        }
    }
    
    //使用DFS算法，由随机数独穷举生成完整的数独，但是似乎效果不太理想
    private void DFS(int number)
    {
        if (number < 81)
        {
            if (this.DFSFlag) return;
            
            //求解出count对应的横纵坐标
            int row = number/9;
            int line = number%9;
            
            //判断该点数值是否为0
            if (this.userDataCells[row][line].getNumber() == 0)
            {
                //穷举法尝试填入数据
                for (int i=1; i<10; i++)
                {
                    //满足条件的话，则进入下一步，递归尝试
                    if (isSatisfied(this.userDataCells, row, line, i))
                    {
                        this.userDataCells[row][line].setNumber(i);
                        DFS(number + 1);
                        //倘若不合适，或者已成功，将原来为0的位置复原
                        this.userDataCells[row][line].setNumber(0);
                    }
                }
            }
            else
            {
                DFS(number + 1);
            }
        }
        else
        {
            this.DFSFlag = true;
            //由于最后结果会复原，此处将结果拷贝到result和gameData中
            copyToArrays(this.userDataCells, this.result);
            copyToArrays(this.userDataCells, this.gameData);
        }
    }
    
    //根据等级，随机挖空一些数值，作为游戏的数据
    private void hollow(int grade)
    {
        int row, line;
        int hollowNumber = 21+grade*8; //等级数不会超过5，确保答案具有唯一解
        
        while (hollowNumber > 0)
        {
            row = new Random().nextInt(9);
            line = new Random().nextInt(9);
            
            if (this.gameData[row][line] != 0)
            {
                hollowNumber--;
                this.gameData[row][line] = 0;
            }
        }
        
        //将挖空后的数据完全复制到用户数据中
        copyToCells(this.gameData, this.userDataCells);
    }

    
    //该类的构造函数，用于创建对象
    public SudokuMap()
    {
        this.createSudokuMap(1);
    }
    
    //本类的主要函数，用于调用其余内部函数，生成数独数据
    public void createSudokuMap(int grade)
    {
        this.gameLevel = grade;
        this.initData(); //初始化数据
        
        this.createRandomSudokuMap(); //生成随机数
        this.DFS(0); //求解完整的数独数据
        this.hollow(grade); //根据等级来挖空数据
    }
    
    //定义函数，检测用户输入的数据是否满足结束的条件
    //由于生成的数独是唯一解，此处仅比较result和userData中的数据即可
    public boolean isWined()
    {
        boolean winFlag = true;
        for (int i=0; i<9; i++)
        {
            for (int j=0; j<9; j++)
            {
                if (this.result[i][j] != this.userDataCells[i][j].getNumber())
                {
                    winFlag = false;
                    break;
                }
            }
        }
        return winFlag;
    }
    
    //定义函数，检测userData是否合法，外部也会用到（static）
    public static boolean isSatisfied(Cell[][] cells, int row, int line, int number)
    {
        //判断cells的数据是否符合规格
        boolean isSatisfiedFlag = true;
        
        //检测行列是否重复
        for (int i = 0; i < 9; i++)
        {
            //去掉该数值本身的影响
            if ((cells[row][i].getNumber() == number && i != line) || (cells[i][line].getNumber() == number && i != row))
            {
                isSatisfiedFlag = false;
                break;
            }
        }
        
        //如果每行列的数据符合规则，则检测每个宫是否重复
        if (isSatisfiedFlag)
        {
            for (int i = (row / 3 * 3); i < (row / 3 * 3 + 3); i++)
            {
                for (int j = (line / 3 * 3); j < (line / 3 * 3 + 3); j++)
                {
                    //去除数值本身的影响
                    if (cells[i][j].getNumber() == number && i != row && j != line)
                    {
                        isSatisfiedFlag = false;
                        break;
                    }
                }
            }
        }
    
        return isSatisfiedFlag;
    }
    
    //通过横纵坐标，将数值写入Cell[][]中，但似乎又没必要写一个函数
    public static void writeToCells(Cell[][] cells, int row, int line, int number)
    {
            cells[row][line].setNumber(number);
    }
    
    //将Cell[][]中的数值复制到int[][]类型中
    public static void copyToArrays(Cell[][] cells, int[][] arrays)
    {
        //正常来说应该有数值校验的，但是这里都是9，就不麻烦了
        for (int i=0; i<arrays.length; i++)
        {
            for (int j=0; j<arrays[i].length; j++)
            {
                arrays[i][j] = cells[i][j].getNumber();
            }
        }
    }
    
    //将int[][]中的数值复制到Cell[][]类型中
    public static void copyToCells(int[][] arrays, Cell[][] cells)
    {
        //正常来说应该有数值校验的，但是这里都是9，就不麻烦了
        for (int i = 0; i < arrays.length; i++)
        {
            for (int j = 0; j < arrays[i].length; j++)
            {
                cells[i][j].setNumber(arrays[i][j]);
            }
        }
    }

    //返回该对象的值，用于后续的操作
    public int[][] getResult()
    {
        return this.result;
    }
    public int[][] getGameData()
    {
        return this.gameData;
    }
    
    public Cell[][] getUserData()
    {
        return this.userDataCells;
    }
    
    public int getGameLevel()
    {
        return gameLevel;
    }
    
    //调试时使用，用于在控制台输出信息
    //该函数在程序运行时不会用到的
    public void showSudokuMap()
    {
        System.out.println("--------gameData----------");
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                System.out.printf("%2d", this.getGameData()[i][j]);
            }
            System.out.println();
        }
        System.out.println("--------userData----------");
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                System.out.printf("%2d", this.getUserData()[i][j].getNumber());
            }
            System.out.println();
        }
        System.out.println("---------result-----------");
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                System.out.printf("%2d", this.getResult()[i][j]);
            }
            System.out.println();
        }
    }
}

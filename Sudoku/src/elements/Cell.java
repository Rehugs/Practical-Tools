package elements;

public class Cell//定义最小单元格，尽管看似没必要
{
    private int number;
    
    public Cell()
    {
        this.number = 0;
    }
    
    public int getNumber()
    {
        return number;
    }
    
    public void setNumber(int number)
    {
        this.number = number;
    }
}

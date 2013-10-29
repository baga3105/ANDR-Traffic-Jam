package is.ru.TrafficJam;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import is.ru.TrafficJam.DataBase.TrafficJamSQLiteAdapter;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: AuðunVetle
 * Date: 26.10.2013
 * Time: 15:37
 * To change this template use File | Settings | File Templates.
 */
public class GameLogic
{
    ArrayList<Block> blockArray;
    int m_level;
    int[][] m_boardstatus;
    Context m_context;
    private TrafficJamSQLiteAdapter trafficJamAdapter;

    public ArrayList<Block> getBlockArray() {
        return blockArray;
    }
    GameLogic(int level,Context context){
        m_context = context;
        m_level = level;
        trafficJamAdapter = new TrafficJamSQLiteAdapter(m_context );
        loadLevel();
    }
    private void loadLevel()
    {
        blockArray = XMLParser.getLevel(m_level);
        updateBoardStatus();
    }
    public void loadNextLevel()
    {
        m_level++;
        loadLevel();
    }

    private void updateBoardStatus()
    {
        m_boardstatus = new int[6][6];
        int counter = 1;
        for(Block b : blockArray)
        {
            for(int i = 0; i<b.getLength();i++){
                if(b.isVertical()){
                    m_boardstatus[b.getPos().x][b.getPos().y+i] = counter;
                } else {
                    m_boardstatus[b.getPos().x+i][b.getPos().y] = counter;
                }
            }
            counter++;
        }
    }
    public boolean isEmpty(int x, int y, int id)
    {
        return (m_boardstatus[x][y] == 0 || m_boardstatus[x][y] == id);
    }
    public int getId(int x, int y)
    {
        return m_boardstatus[x][y];
    }
    public int getMax(Block b)
    {

        for(int i = 0; i<6; i++)
        {
            String msg = "";
            for(int j = 0; j <6; j++)
            {
                msg+= m_boardstatus[j][i];
            }
            Log.d("GameViewLOL",msg);
        }
        int result;
        if(b.isVertical())
        {
            result = b.getPos().y;
            while(isEmpty(b.getPos().x,result,getId(b.getPos().x,b.getPos().y)))
            {
                result++;
                if(result >5)
                    return 6-b.getLength();
            }
        } else
        {
            result = b.getPos().x;
            while(isEmpty(result,b.getPos().y,getId(b.getPos().x,b.getPos().y)))
            {
                result++;
                if(result >5)
                    return 6-b.getLength();
            }
        }
        return result-b.getLength();
    }
    public int getMin(Block b)
    {
        int result;
        if(b.isVertical())
        {
            result = b.getPos().y;
            while(isEmpty(b.getPos().x,result,getId(b.getPos().x,b.getPos().y)))
            {
                result--;
                if(result <0)
                    return 0;
            }
        } else
        {
            result = b.getPos().x;
            while(isEmpty(result,b.getPos().y,getId(b.getPos().x,b.getPos().y)))
            {
                result--;
                if(result <0)
                    return 0;
            }
        }
        return result+1;
    }
    public boolean isGameOver(){
        if (blockArray.get(0).getPos().equals(4,2)){
            trafficJamAdapter.markLevelAsFinished(m_level);
            return true;
        }
        else
            return false;
    }

    public void moveBlock (Point blockToMove, Point posToMoveTo){
        Block block = getBlockByPos(blockToMove);
        if (block != null){
            block.setPos(posToMoveTo);
            updateBoardStatus();
        }
        else{
            Log.d("GameViewLOL", "Block not found at point: " + blockToMove.toString());
        }
        /*if (isGameOver()){
            m_level++;
            //Get the next level
            loadLevel();
        }*/
    }
    private Block getBlockByPos(Point pos){
        for (Block b : blockArray){
            if (b.getPos().equals(pos))
                return b;
        }
        return null;
    }
}

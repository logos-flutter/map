package com.caobugs.gis.view.map;


import android.widget.Toast;

import com.caobugs.gis.algorithm.MathUtil;
import com.caobugs.gis.data.db.sql.FarmlandSQL;
import com.caobugs.gis.geometry.Coordinate;
import com.caobugs.gis.geometry.primary.Envelope;
import com.caobugs.gis.tile.CoordinateSystemManager;
import com.caobugs.gis.tile.TileInfo;
import com.caobugs.gis.tool.ApplicationContext;


/**
 * 用于存储map的当前比例等信息的类
 * @author Young-Ken
 * @version 0.1
 * @since 2015/12/11
 */
public class MapInfo
{

    public MapInfo()
    {

    }

    /**
     * 当前地图的外包络线,这个方法只能取不能设置
     */
    private Envelope currentEnvelope = new Envelope();

    /**
     * 全图外包络线，这个方法用于初始化当前的最大包络线
     */
    private Envelope fullEnvelope = new Envelope();

    public double moveX ;
    public double moveY ;
    /**
     * 设备的高
     */
    private int deviceHeight = 0;

    /**
     * 设备宽
     */
    private int deviceWidth = 0;

    private int currentLevel = 0;

    /**
     * 当前分辨率
     */
    private double currentResolution = 0;

    /**
     * 当前比例尺
     */
    private double currentScale = 0;

    private Coordinate currentCenter = new Coordinate();
    //private TileInfo tileInfo = null;

    /**
     * 初始化当前的级别和分辨率还有比例尺
     * 这里应该抛出异常，以后处理
     */
    public void calculationMapInfo()
    {
        double resolution;
        if (getFullEnvelope().isEmpty())
        {
            return;
        }

        if (getFullEnvelope().getWidth() > getFullEnvelope().getHeight())
        {
            resolution = getFullEnvelope().getWidth() / getDeviceWidth();
        } else
        {
            resolution = getFullEnvelope().getHeight() /getDeviceHeight();
        }

        TileInfo tileInfo = CoordinateSystemManager.getInstance().getCoordinateSystem().getTileInfo();
        double[] resolutions = tileInfo.getResolutions();
        for (int i = 0; i < resolutions.length - 1; i++)
        {
            if (MathUtil.between(resolution, resolutions[i], resolutions[i + 1]))
            {
                setCurrentLevel(i);
                currentResolution = resolutions[i];
                currentScale = tileInfo.getScales()[i];
                currentEnvelope = calculationEnvelope();
            }
        }
    }



    /**
     * 计算当前包络线范围
     * @return 包络线
     */
    public Envelope calculationEnvelope()
    {
        double resX = getDeviceWidth()/2 * getCurrentResolution();
        double resY = getDeviceHeight()/2 * getCurrentResolution();
        return new Envelope(getCurrentCenter().x - resX, getCurrentCenter().x + resX,
                getCurrentCenter().y - resY, getCurrentCenter().y + resY);
    }


    public double getCurrentResolution()
    {
        return currentResolution;
    }

    public double getCurrentScale()
    {
        return currentScale;
    }


    /**
     * 取得当前和屏幕范围计算得到的范围
     * @return Envelope
     */
    public Envelope getCurrentEnvelope()
    {
        return currentEnvelope;
    }


    public int getCurrentLevel()
    {
        return currentLevel;
    }


    public void setCurrentLevel(int currentLevel)
    {
        this.currentLevel = currentLevel;
        TileInfo tileInfo = CoordinateSystemManager.getInstance().getCoordinateSystem().getTileInfo();
        this.currentResolution = tileInfo.getResolutions()[currentLevel];
    }

    public void setDeviceHeight(int deviceHeight)
    {
        this.deviceHeight = deviceHeight;
    }

    public void setDeviceWidth(int deviceWidth)
    {
        this.deviceWidth = deviceWidth;
    }

    public int getDeviceHeight()
    {
        return deviceHeight;
    }

    public int getDeviceWidth()
    {
        return deviceWidth;
    }

    /**
     * 每次从新设置map全图就进行一次计算, 把Map地图编程屏幕坐标系
     * @param fullEnvelope
     */
    public void setFullEnvelope(Envelope fullEnvelope)
    {
        if(fullEnvelope.isEmpty())
        {
            this.fullEnvelope = new Envelope();
        }
        TileInfo tileInfo = CoordinateSystemManager.getInstance().getCoordinateSystem().getTileInfo();
        this.fullEnvelope = new Envelope(
                Math.abs(tileInfo.getOriginPoint().x) + fullEnvelope.getMaxX(),
                Math.abs(tileInfo.getOriginPoint().x) + fullEnvelope.getMinX(),
                Math.abs(tileInfo.getOriginPoint().y) - fullEnvelope.getMaxY(),
                Math.abs(tileInfo.getOriginPoint().y) - fullEnvelope.getMinY());
        calculationMapInfo();
    }

    public Envelope getFullEnvelope()
    {
        return fullEnvelope;
    }

    /**
     * 取得当前地图的中心点
     * @return
     */
    public Coordinate getCurrentCenter()
    {

        if(currentCenter.x != 0 && currentCenter.y != 0)
        {
           return currentCenter;
        }

        if (!getCurrentEnvelope().isEmpty())
        {
            return getCurrentEnvelope().getCentre();
        }

        if(!getFullEnvelope().isEmpty())
        {
            return getFullEnvelope().getCentre();
        }

        return null;
    }

    public void setCurrentCurrentImage(Coordinate currentCenter)
    {
        this.currentCenter = currentCenter;
    }

    public void setCurrentCurrentImageLevel(Coordinate currentCenter, int currentLevel)
    {
        TileInfo tileInfo = CoordinateSystemManager.getInstance().getCoordinateSystem().getTileInfo();
        if(currentLevel > tileInfo.getResolutions().length)
        {
            Toast.makeText(ApplicationContext.getContext(),"最大级别",Toast.LENGTH_SHORT).show();
            return;
        }

        if(currentLevel < 0)
        {
            Toast.makeText(ApplicationContext.getContext(),"最大小级别",Toast.LENGTH_SHORT).show();
            return;
        }

        this.currentCenter  = currentCenter;
        this.currentLevel = currentLevel;

        this.currentResolution = tileInfo.getResolutions()[currentLevel];
        currentEnvelope = calculationEnvelope();
    }

    /**
     * 先简单做
     * @param currentCenter
     */
    public void setCurrentCenter(Coordinate currentCenter)
    {
        TileInfo tileInfo = CoordinateSystemManager.getInstance().getCoordinateSystem().getTileInfo();
        Coordinate temp = new Coordinate(currentCenter.x + Math.abs(tileInfo.getOriginPoint().x),
                Math.abs(tileInfo.getOriginPoint().y)  - currentCenter.y);
        this.currentCenter = temp;
        currentEnvelope = calculationEnvelope();
       // Log.d("RUN",currentEnvelope+"");
    }

    public void setCurrentLevel(Coordinate currentCenter, int level)
    {
        TileInfo tileInfo = CoordinateSystemManager.getInstance().getCoordinateSystem().getTileInfo();

        Coordinate temp = new Coordinate(currentCenter.x + Math.abs(tileInfo.getOriginPoint().x),
                Math.abs(tileInfo.getOriginPoint().y)  - currentCenter.y);
        this.currentCenter = temp;
        setCurrentLevel(level);
        currentEnvelope = calculationEnvelope();

        //setCurrentCenter(currentCenter);

        //Envelope envelope = calculationEnvelope();
        //setFullEnvelope(envelope);

        //Envelope tempEn = new Envelope(envelope.getMinY(), envelope.getMaxY(), envelope.getMinX(), envelope.getMaxX());
        //currentEnvelope = envelope;
    }

    public void setCurrentResolution(double currentResolution)
    {
        this.currentResolution = currentResolution;
    }

    public void setCurrentScale(double currentScale)
    {
        this.currentScale = currentScale;
    }

    public void setCurrentEnvelope(Envelope currentEnvelope)
    {
        this.currentEnvelope = currentEnvelope;
    }

}

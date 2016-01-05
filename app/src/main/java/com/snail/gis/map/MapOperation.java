package com.snail.gis.map;
import android.view.View;

import com.snail.gis.algorithm.MathUtil;
import com.snail.gis.geometry.Coordinate;
import com.snail.gis.geometry.primary.Envelope;
import com.snail.gis.tile.TileInfo;

/**
 * 这先简单处理
 * @author Young-Ken
 * @version 0.1
 * @since 2015/12/11
 */
public class MapOperation
{

    public MapOperation()
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

        TileInfo tileInfo = MapManger.getInstance().getMap().getTileInfo();
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
    private Envelope calculationEnvelope()
    {
        double resX = getDeviceWidth()/2 * getCurrentResolution();
        double resY = getDeviceHeight()/2 * getCurrentResolution();
        return new Envelope(getCurrentCenter().getX() - resX, getCurrentCenter().getX() + resX,
                getCurrentCenter().getY() - resY, getCurrentCenter().getY() + resY);
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
     * 每次从新设置map全图就进行一次计算
     * @param fullEnvelope
     */
    public void setFullEnvelope(Envelope fullEnvelope)
    {
        this.fullEnvelope = fullEnvelope;
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
        if(!currentCenter.isEmpty())
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

    /**
     * 先简单做
     * @param currentCenter
     */
    public void setCurrentCenter(Coordinate currentCenter)
    {
        this.currentCenter = currentCenter;
        currentEnvelope = calculationEnvelope();
    }

}

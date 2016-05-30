package com.caobugs.gis.view.map.event;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.caobugs.gis.util.TAG;
import com.caobugs.gis.view.map.BaseMap;

/**
 * @author Young-Ken
 * @version 0.1
 * @since 2016/1/6
 */
public class MapOnTouchListener extends GestureDetector.SimpleOnGestureListener implements ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener
{
    private BaseMap map = null;
    private OnMapDefaultListener defaultListener = null;
    public MapOnTouchListener(BaseMap map)
    {
        this.map = map;
        defaultListener = new MapDefaultListener(map);
    }


    @Override
    public boolean onDown(MotionEvent e)
    {
        Log.e(TAG.EVENT,"onDown");
        return true;
    }

    public void onLongPress(MotionEvent e)
    {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e)
    {
        Log.e(TAG.EVENT,"onSingleTapUp");
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        Log.e(TAG.EVENT,"onFling");
        return super.onFling(e1, e2, velocityX, velocityY);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.e(TAG.EVENT," onScroll");
        return true;
    }



    private float zoomt = 0;
    @Override
    public boolean onScale(ScaleGestureDetector detector)
    {
        if(zoomt == 0)
        {
            zoomt = detector.getCurrentSpan();
        }

        if(zoomt - detector.getCurrentSpan() > 100)
        {
            zoomt = detector.getCurrentSpan();
            map.getMapController().zoomTemp(-1);
        }

        if( detector.getCurrentSpan() - zoomt> 100)
        {
            zoomt = detector.getCurrentSpan();
            map.getMapController().zoomTemp(1);
        }

        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector)
    {
        Log.e(TAG.EVENT,detector.getCurrentSpan()+" "+"onScaleBegin");
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector)
    {
        zoomt = 0;
        Log.e(TAG.EVENT,detector.getCurrentSpan()+" "+"onScaleEnd");
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if(event.getPointerCount() == 1)
        {
            defaultListener.onMapDefaultEvent(event);
        }
        return false;
    }
}

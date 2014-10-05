import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class SimpleDrag extends View {



    private final int INVALID_INDEX = -1;

    private final int mTotalItems = 5;

    private ArrayList<Rect> mItemsCollection;

    private ArrayList<Point> mActiveDragPoints;

    private ArrayList<Rect>  mActiveRects;


    private Paint mPaint;

    /**
     * @param context
     * @return of type SimpleDrag
     * Constructor function
     * @since Feb 19, 2013
     * @author rajeshcp
     */
    public SimpleDrag(Context context) {
        super(context);
        init();
    }

    /**
     * @param context
     * @param attrs
     * @return of type SimpleDrag
     * Constructor function
     * @since Feb 19, 2013
     * @author rajeshcp
     */
    public SimpleDrag(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     * @return of type SimpleDrag
     * Constructor function
     * @since Feb 19, 2013
     * @author rajeshcp
     */
    public SimpleDrag(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /* (non-Javadoc)
     * @see android.view.View#onDraw(android.graphics.Canvas)
     * @since Feb 19, 2013
     * @author rajeshcp
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLUE, PorterDuff.Mode.CLEAR);
        for( Rect rect : mItemsCollection)
        {
            canvas.drawRect(rect, mPaint);
        }
    }


    /**
     * @param of type null
     * @return of type null
     * function which will initialize the view
     * @since Feb 20, 2013
     * @author rajeshcp
     */
    private void init()
    {
        mActiveRects      = new ArrayList<Rect>(mTotalItems);
        mActiveDragPoints = new ArrayList<Point>(mTotalItems);
        mItemsCollection  = new ArrayList<Rect>();
        for( int i = 0; i < mTotalItems; i++)
        {
            Rect rect = new Rect(i * 100, i * 100, (i + 1) * 100, (i + 1) * 100);
            mItemsCollection.add(rect);
        }
        mPaint     = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
    }





    /* (non-Javadoc)
     * @see android.view.View#onTouchEvent(android.view.MotionEvent)
     * @since Feb 19, 2013
     * @author rajeshcp
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        final int action  = event.getActionMasked();
        final int pointer = event.getActionIndex();

        switch (action) {
            case MotionEvent.ACTION_DOWN :
                Point touchDown = new Point((int)event.getX(), (int)event.getY());
                lookForIntersection(touchDown);
                break;
            case MotionEvent.ACTION_UP :
            case MotionEvent.ACTION_CANCEL :
                mActiveDragPoints.removeAll(mActiveDragPoints);
                mActiveRects.removeAll(mActiveRects);
                break;
            case MotionEvent.ACTION_MOVE :
                int count = 0;
                for(Rect rect : mActiveRects)
                {
                    Point curretPoint = new Point((int)event.getX(count), (int)event.getY(count));
                    moveRect(curretPoint, mActiveDragPoints.get(count), rect);
                    count++;
                }
                Log.d(getClass().getName(), "Active Rects" + mActiveRects.size());
                Log.d(getClass().getName(), "Active Points" + mActiveDragPoints.size());
                invalidate();
                break;
            case MotionEvent.ACTION_POINTER_DOWN :
                touchDown = new Point((int)event.getX(pointer), (int)event.getY(pointer));
                lookForIntersection(touchDown);
                //Log.d(getClass().getName(), "ACTION_POINTER_DOWN" + pointer);
                break;
            case MotionEvent.ACTION_POINTER_UP :
                int index = getIntersectionRectIndex(new Point((int)event.getX(pointer), (int)event.getY(pointer)));
                if( index != INVALID_INDEX )
                {
                    Rect rect = mItemsCollection.get(index);
                    mActiveDragPoints.remove(mActiveRects.indexOf(rect));
                    mActiveRects.remove(rect);
                }

                break;

            default:
                break;
        }
        return true;
    }


    /**
     * @param touchDown of type Point
     * @return of type null
     * function which will find the
     * intersecting rect and add to the
     * active collection
     * @since Feb 20, 2013
     * @author rajeshcp
     */
    private void lookForIntersection(Point touchDown)
    {
        final int index = getIntersectionRectIndex(touchDown);

        if( index != INVALID_INDEX )
        {
            final Rect rect = mItemsCollection.get(index);
            if( mActiveRects.indexOf(rect) == INVALID_INDEX )
            {
                mActiveDragPoints.add(touchDown);
                mActiveRects.add(mItemsCollection.get(index));
            }
        }
        Log.d(getClass().getName(), "Active Rects" + mActiveRects.size());
        Log.d(getClass().getName(), "Active Points" + mActiveDragPoints.size());

    }




    /**
     * @param point of type Point
     * @return of type int
     * function which will return the index of
     * the rect contaning the given point
     * @since Feb 20, 2013
     * @author rajeshcp
     */
    private int getIntersectionRectIndex(final Point point)
    {
        int index = INVALID_INDEX;
        for(Rect rect : mItemsCollection)
        {
            if( rect.contains(point.x, point.y) )
            {
                index = mItemsCollection.indexOf(rect);
                break;
            }
        }
        return index;
    }


    /**
     * @param currentPoint of type Point
     * @param prevPoint of type Point
     * @param rect of type Rect
     * @return of type null
     * function which will move the change the
     * bounds of teh rect
     * @since Feb 20, 2013
     * @author rajeshcp
     */
    private void moveRect(Point currentPoint, Point prevPoint, final Rect rect)
    {
        int xMoved = currentPoint.x - prevPoint.x;
        int yMoved = currentPoint.y - prevPoint.y;
        rect.set(rect.left + xMoved, rect.top + yMoved, rect.right + xMoved, rect.bottom + yMoved);
        mActiveDragPoints.set(mActiveDragPoints.indexOf(prevPoint), currentPoint);
    }

}
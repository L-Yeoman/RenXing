
package com.dldzkj.app.renxing.customview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.utils.Expressions;
import com.dldzkj.app.renxing.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lyBin on 2015/6/19.
 */
public class FaceLayout extends RelativeLayout {

    private ViewPager mPager;
    private List<Object> mList;
    private ImageView[] dots;
    private String[] currentFacesName;
    private String[] facesName;
    private String[] facesName1;
    private String[] facesName2;
    private String[] facesName3;
    private String[] facesName4;
    private String[] facesName5;
    private String[] facesName6;
    private int[] faces;
    private int[] faces1;
    private int[] faces2;
    private int[] faces3;
    private int[] faces4;
    private int[] faces5;
    private int[] faces6;
    private int[] currentFaces;
    private GridView gridView;
    private GridView gridView1;
    private GridView gridView2;
    private GridView gridView3;
    private GridView gridView4;
    private GridView gridView5;
    private GridView gridView6;
    private LinearLayout mDotLayout;
    private OnFaceItem mFaceItem;
private Context c;

    public FaceLayout(Context context) {
        this(context,null);
    }

    public FaceLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.c=context;
        initView();
        initViewPager(gridView, faces, 0);
        initViewPager(gridView1, faces1,1);
        initViewPager(gridView2,faces2,2);
        initViewPager(gridView3, faces3,3);
        initViewPager(gridView4, faces4,4);
        initViewPager(gridView5, faces5,5);
        initViewPager(gridView6, faces6,6);
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(new OnpagerChange());
        addPoint();
    }

    public interface OnFaceItem{
        public void onFace(SpannableString s);
    }

    public void setOnFace( OnFaceItem faceItem){
        mFaceItem = faceItem;
    }

    public void initView(){
        LayoutInflater.from(getContext()).inflate(R.layout.face_layout,this);
        mPager=(ViewPager)findViewById(R.id.fl_viewPager);
        mDotLayout=(LinearLayout)findViewById(R.id.fl_dot_layout);
        mList=new ArrayList<Object>();
        faces= Expressions.expressionImgs;
        faces1= Expressions.expressionImgs1;
        faces2= Expressions.expressionImgs2;
        faces3= Expressions.expressionImgs3;
        faces4= Expressions.expressionImgs4;
        faces5= Expressions.expressionImgs5;
        faces6= Expressions.expressionImgs6;
        facesName=Expressions.expressionImgNames;
        facesName1=Expressions.expressionImgNames1;
        facesName2=Expressions.expressionImgNames2;
        facesName3=Expressions.expressionImgNames3;
        facesName4=Expressions.expressionImgNames4;
        facesName5=Expressions.expressionImgNames5;
        facesName6=Expressions.expressionImgNames6;
    }

    public void initViewPager(GridView gView,int[] faces,int tag){
        gView= (GridView) LayoutInflater.from(getContext()).inflate(R.layout.face_gridview,null);
        List<Map<String,Object>> mapList=new ArrayList<Map<String,Object>>();
        for(int i=0;i<faces.length;i++){
            Map<String,Object> _Map=new HashMap<String,Object>();
            _Map.put("image",faces[i]);
            mapList.add(_Map);
        }
        SimpleAdapter _Adapter=new SimpleAdapter(getContext(),mapList,R.layout.singleexpression,
                new String[]{"image"},new int[]{R.id.image});
        gView.setTag(tag);
        gView.setAdapter(_Adapter);
        gView.setOnItemClickListener(new OnClick());
        mList.add(gView);

    }

    public void addPoint(){
        int size=mList.size();
        dots = new ImageView[size];
        for(int i=0;i<size;i++){
            ImageView _View=new ImageView(getContext());
            LinearLayout.LayoutParams _Params=new LinearLayout.LayoutParams(10,
                    10);
            if(i==0){
                _View.setImageResource(R.drawable.dot_focus);
            }else{
                _View.setImageResource(R.drawable.dot_blur);
            }
            _Params.leftMargin=5;
            _Params.rightMargin=5;
            dots[i]=_View;
            mDotLayout.addView(_View,_Params);
        }
    }

    PagerAdapter mAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
          //  super.destroyItem(container, position, object);
            container.removeView((View) mList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView((View) mList.get(position));
            return mList.get(position);
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view==o;
        }
    };

    private class OnClick implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int tag = (int) parent.getTag();
            switch(tag) {
                case 0:
                    currentFaces = faces;
                    currentFacesName = facesName;
                    break;
                case 1:
                    currentFaces = faces1;
                    currentFacesName = facesName1;
                    break;
                case 3:
                    currentFaces = faces3;
                    currentFacesName = facesName3;
                    break;
                case 4:
                    currentFaces = faces4;
                    currentFacesName = facesName4;
                    break;
                case 5:
                    currentFaces = faces5;
                    currentFacesName = facesName5;
                    break;
                case 6:
                    currentFaces = faces6;
                    currentFacesName = facesName6;
                    if(position==currentFaces.length-1){
                        mFaceItem.onFace(null);
                        return;
                    }
                    break;
                case 2:
                    currentFaces = faces2;
                    currentFacesName = facesName2;
                    break;
            }
            if(position==17){
                mFaceItem.onFace(null);
                return;
            }
            SpannableString _SpanString=getFace(position);
            mFaceItem.onFace(_SpanString);
        }
    }

    private SpannableString getFace(int position){
        Bitmap _Bitmap = BitmapFactory.decodeResource(getResources(),currentFaces[position]);
       /* int w=0;
        int densityDpi= ScreenUtils.getDensityDpi((Activity) c);
        if (densityDpi>=320) {
            w=100;
        }else if (densityDpi<=160) {
            w=30;
        }else {
            w=65;
        }
        _Bitmap = Bitmap.createScaledBitmap(_Bitmap, w, w, true);*/

        ImageSpan _ImageSpan = new ImageSpan(getContext(),_Bitmap);

        SpannableString _SpanString=new SpannableString(currentFacesName[position]);
        _SpanString.setSpan(_ImageSpan,0,currentFacesName[position].length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return  _SpanString;
    }
    private class OnpagerChange implements ViewPager.OnPageChangeListener{
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }
        @Override
        public void onPageSelected(int position) {

            for( int i=0;i<dots.length;i++ ){
                if(i==position){
                    dots[i].setImageResource(R.drawable.dot_focus);
                }else{
                    dots[i].setImageResource(R.drawable.dot_blur);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }
}

package com.dldzkj.app.renxing.customview;

import android.content.Context;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.ArticleCommentModel;
import com.dldzkj.app.renxing.utils.ExpressionUtil;
import com.dldzkj.app.renxing.utils.StringUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.BitmapUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public class ArticleCommentView extends LinearLayout {
	private ArticleCommentModel mModel;
	private int mPosition;
	private Context mContext;
	public ArticleCommentView(Context context) {
		this(context,null);
	}
	public ArticleCommentView(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}
	public ArticleCommentView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext=context;
		initView();
	}
	
	 private WebView mWeb;
	 private TextView mCommentTitle;
	 private CircleImageView avater;
	 private TextView name;
	 private TextView time;
	 private TextView content;
	 private BitmapUtils mUtils;
	 private int TAG;
	public void initView()
	{
			LayoutInflater.from(getContext()).inflate(R.layout.comment_item, this);
			avater=(CircleImageView) findViewById(R.id.ci_header);
			name=(TextView) findViewById(R.id.ci_name);
			time=(TextView) findViewById(R.id.ci_time);
			content=(TextView) findViewById(R.id.ci_comment);


	//	 throw new RuntimeException("there is no type that matches the type " + type + " make sure your using types correctly");
    }
	
	public void setData(ArticleCommentModel model,int position,BitmapUtils bUtils)
	{
		 mModel=model;
		 mPosition=position;
		 mUtils=bUtils;
		 initData();
	 }

	public void initData()
	{
		name.setText(mModel.getNicName());
		//mUtils.display(avater, mModel.getHeadUri());
		Glide.with(mContext)
				.load(WebUtils.RENXING_WEB+mModel.getPortrait())
				.into(avater);
		String str= StringUtils.DecodeBase64Str(mModel.getContent());
		time.setText(mModel.getAdd_Time());
		String zhengze = "f_static_0[0-9]{2}|f_static_10[0-7]";
		SpannableString spannableString=null;
		/*try {
			 spannableString = ExpressionUtil
					.getExpressionString(getContext(), str, zhengze);
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("ArticleCommentView","表情获取失败");
		}*/
		spannableString = StringUtils.setFaceToTextView(mContext,str,content);
		content.setText(spannableString);
	}


}

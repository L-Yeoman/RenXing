package com.dldzkj.app.renxing.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.dldzkj.app.renxing.BaseActivity;
import com.dldzkj.app.renxing.PromptManager;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.adapter.PhotoAdapter;
import com.dldzkj.app.renxing.bean.BaseModel;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.bean.Picture;
import com.dldzkj.app.renxing.cropview.CropImage;
import com.dldzkj.app.renxing.cropview.InternalStorageContentProvider;
import com.dldzkj.app.renxing.customview.AppDialog;
import com.dldzkj.app.renxing.customview.AppSelectPicsDialog;
import com.dldzkj.app.renxing.utils.BmpUtils;
import com.dldzkj.app.renxing.utils.FileUtils;
import com.dldzkj.app.renxing.utils.OnRequestListener;
import com.dldzkj.app.renxing.utils.RequestHelper;
import com.dldzkj.app.renxing.utils.SPUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoSelectorActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * 我的相册
 * 
 * @author Administrator
 * 
 */
public class MyPhotoActivity extends BaseActivity implements OnRefreshListener,PhotoAdapter.PhotoClickListener {
	RecyclerView recyclerView;
	SwipeRefreshLayout swipeRefreshLayout;
	GridView mGridView;
	PhotoAdapter mAdapter;
	ImageView mDelete;
	ArrayList<Picture> photos;
	private File mFileTemp;
	public TextView deleteView;
	private List<Picture> deleteImages;
//	private ArrayList<Picture> mList;
	private boolean isDelete = false;
	private ProgressDialog mProgress;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myphoto_layout_activity);
		inits();
	}



	public void inits() {
		setTitle(R.string.photo_title);
		toolbar.setNavigationIcon(R.drawable.ic_back);
		// 这里要去下载数据
	//	adapter = new MyPhotoAdapter(MyPhotoActivity.this);
	//	mList = new ArrayList<Picture>();
		photos=new ArrayList<Picture>();
		mAdapter=new PhotoAdapter();
		/*photos.add("http://img0.imgtn.bdimg.com/it/u=495617650,507557420&fm=21&gp=0.jpg");
		photos.add("http://img1.imgtn.bdimg.com/it/u=1140279559,2930201298&fm=11&gp=0.jpg");
		photos.add("http://img2.cache.netease.com/ent/2015/5/13/2015051310364984eb7.jpg");
		photos.add("http://img5.imgtn.bdimg.com/it/u=4175149886,1024763479&fm=21&gp=0.jpg");
		photos.add("http://img2.cache.netease.com/house/2015/1/14/2015011410265825280.jpg");
		photos.add("http://pic.baike.soso.com/p/20120516/20120516232430-958992798.jpg");
		photos.add("http://img1.ph.126.net/AuJCmVLbRGdaRND4Mo8wXA==/6630502616722656415.jpg");
		photos.add("http://img0.ph.126.net/aGwps6kbK4-acRChfy9xBw==/6630272818792449738.jpg");
		photos.add("http://img0.ph.126.net/tyWNKTaNTOHLtb8QZ0BCAQ==/6630333291931980228.jpg");*/
	//	photos.add("http://img2.ph.126.net/26qaL5uEuqp0fZtpm6QWCA==/2880614911674473597.jpg");
		handler.sendEmptyMessage(2);
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
		swipeRefreshLayout.setOnRefreshListener(this);
		mGridView = (GridView)findViewById(R.id.mla_gridView);
		deleteView = (TextView)findViewById(R.id.deleteView);
		deleteView.setOnClickListener(new OnClicked());
		mAdapter.setData(photos, this,new BitmapUtils(getApplicationContext()),MyPhotoActivity.this);
		mGridView.setAdapter(mAdapter);
		Log.e("recyclerView ", recyclerView + "dsd");
		deleteImages=mAdapter.getDeleteItems();
	}

	@SuppressWarnings("deprecation")
	public void setOnScrollListen() {
		recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView,
											 int newState) {
				super.onScrollStateChanged(recyclerView, newState);
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);

			}
		});
	}



	/************** 自定义裁剪功能 *******************/
	public static final int REQUEST_CODE_GALLERY = 0x1;
	public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
	public static final int REQUEST_CODE_CROP_IMAGE = 0x3;
	public static final int REQUEST_SELECTOR = 0x04;
	
	public class OnClicked implements OnClickListener{

		@Override
		public void onClick(View v) {
			//删除选中图片
			switch(v.getId()){
			case R.id.deleteView:
				deleteView.setVisibility(View.GONE);
				if(deleteImages.size()==0){
					cancelDelete();
					Log.i("onClick","cancelDelete");
					break;
				}
				isDelete = true;
				Log.i("deleteImages.size()",deleteImages.size()+"");
				handler.sendEmptyMessage(4);
				break;
			}
		}
	}
	private void takePicture() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		try {
			Uri mImageCaptureUri = null;
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				mImageCaptureUri = Uri.fromFile(mFileTemp);
			} else {
				mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
			}
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					mImageCaptureUri);
			intent.putExtra("return-data", true);
			startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void openGallery() {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Bitmap bitmap;
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		//得到本地照片
		case REQUEST_CODE_GALLERY:
			if(data==null){
				return;
			}
			try {
				InputStream inputStream = getContentResolver().openInputStream(
						data.getData());
				FileOutputStream fileOutputStream = new FileOutputStream(
						mFileTemp);
				copyStream(inputStream, fileOutputStream);
				fileOutputStream.close();
				inputStream.close();
			//	startCropImage();
			} catch (Exception e) {
				Log.e("zxl", "Error while creating temp file", e);
			}
			break;
		case REQUEST_CODE_TAKE_PICTURE:
		//	startCropImage();
			PhotoModel _Model = new PhotoModel();
			_Model.setOriginalPath(mFileTemp.getPath());
			ArrayList<PhotoModel> array=new ArrayList<PhotoModel>();
			array.add(_Model);
			setBitmap(array);
			/*if(photos.size()>0){
				photos.add(photos.size()-1,_Picture);
			}
			updateImage(_Picture.getImgUrl());*/
			break;
		case REQUEST_CODE_CROP_IMAGE:
			if(data==null)
			{
				return;
			}
			String path = data.getStringExtra(CropImage.IMAGE_PATH);
			if (path == null) {
				return;
			}
		//	bitmap = BitmapFactory.decodeFile(mFileTemp.getPath());
			/*Picture _Picture = new Picture();
			_Picture.setImgUrl(mFileTemp.getPath());
			photos.add(_Picture);
			updateImage(_Picture.getImgUrl());*/
			break;
		//相册
		case REQUEST_SELECTOR:
			if(data!=null&&data.getExtras()!=null){
				ArrayList<PhotoModel> _Array = (ArrayList<PhotoModel>) data.getExtras().get("photos");
			/*	ArrayList<Picture> _List = new ArrayList<Picture>();
				for(int i=0;i<_Array.size();i++){
					PhotoModel _Model = _Array.get(i);
					Picture picture = new Picture();
					picture.setImgUrl(_Model.getOriginalPath());
					_List.add(picture);
				}*/
				setBitmap(_Array);
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	private void setBitmap(ArrayList<PhotoModel> list) {
		if (list.size() == 0) {
			return;
		}
		Log.i("photos.size()", photos.size() + "");
		for (int i = 0; i < list.size(); i++) {
			//	Picture _Picture = list.get(i);
			PhotoModel _Model = list.get(i);
			Picture _Picture = new Picture();
			_Picture.setImgUrl(_Model.getOriginalPath());
			boolean bool = true;
			//	mList.add(_Picture);
			Log.i("lyBin",photos.size()+"");
			if (photos.size() <= 9) {
				photos.add(0, _Picture);
				Log.i("lyBin","图片上传");
				showProgress();
				String s = _Model.getOriginalPath();
				if(i==list.size()-1){
					bool = false;
				}
				//上传
				updateImage(s,bool);
			} else {
			//	photos.clear();
				handler.sendEmptyMessage(2);
				mProgress.cancel();
				showDialog();
				Log.i("lyBin","图片超出");
				break;
			}
		}
		Log.i("lyBin","结束循环");
		/*byte[] bytes = BmpUtils.Bitmap2Bytes(bitmap);
	//	InputStream _Input = new ByteArrayInputStream(bytes);
		String data = Base64.encodeToString(bytes,Base64.DEFAULT);
		HashMap<String,Object> _Map = new HashMap<String,Object>();
		_Map.put("UserID", ConstantValue.Id);
		_Map.put("Img", data);
		// 上传图片
		onService(_Map,ConstantValue.AddMyAlbums);*/
	}
	public void showProgress(){
		if(mProgress==null){
			mProgress = new ProgressDialog(this);
		}
		mProgress.setCanceledOnTouchOutside(false);
		mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgress.setMessage("    正在上传,请稍候...");
		mProgress.show();
	}
	public void updateImage(String s,final boolean bool) {
	//	ImageLoader.getInstance().displayImage("file://" + path, menuAvart);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("UserID", SPUtils.getLoginId(this));
		String json = JSON.toJSONString(map);
		map.clear();
		map.put("Json", json);
		Bitmap bitmap = BmpUtils.getSmallBitmap(s);
		map.put("Img", FileUtils.Bitmap2Bytes(bitmap));
		bitmap.recycle();
		RequestHelper.requestDataBySoap(ConstantValue.AddMyAlbums, map, new OnRequestListener() {
			@Override
			public void onRequestStart(int tag) {

			}

			@Override
			public void onRequestFail(int errorCode, int tag, Object map) {

			}

			@Override
			public void onRequestSuccess(String result, int tag) {

				if (result == null) {
					return;
				}
					photos.clear();
					handler.sendEmptyMessage(2);
				if(bool==false){
					mProgress.cancel();
				}
				BaseModel baseModel = JSON.parseObject(
						result.toString(), BaseModel.class);
				switch (baseModel.getErrNum()) {
					case 0:
						break;
					default:
						break;
				}
			}
		}, 0x07);
	}
	private void startCropImage() {
		Intent intent = new Intent(this, CropImage.class);
		intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
		intent.putExtra(CropImage.SCALE, true);
		intent.putExtra(CropImage.OUTPUT_X, 200);
		intent.putExtra(CropImage.OUTPUT_Y, 200);
		intent.putExtra(CropImage.ASPECT_X, 1);
		intent.putExtra(CropImage.ASPECT_Y, 1);
		startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
	}
	public static void copyStream(InputStream input, OutputStream output)
			throws IOException {

		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		/*new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				handler.sendEmptyMessage(0);
			}
		}).start();*/
		handler.sendEmptyMessageDelayed(0,2000);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			HashMap<String,Object> _Map;
			switch (msg.what) {
				case 0:
					swipeRefreshLayout.setRefreshing(false);
					break;
				case 1:
					break;
				//首次加载
				case 2:
					Log.i("lyBin","GerMyAlbumsList");
					Log.i("lyBin",photos.size()+"");
					 _Map = new HashMap<String,Object>();
					_Map.put("UserID",SPUtils.getLoginId(MyPhotoActivity.this));
					onService(_Map,ConstantValue.GerMyAlbumsList);
					break;
				//删除本地图片
				case 3:
					for(int i=0;i<deleteImages.size();){
						photos.remove(deleteImages.get(i));
						deleteImages.remove(i);
					}
					mAdapter.notifyDataSetChanged();
					mAdapter.setDelete(false);
					cancelDelete();
					break;
				//删除图片
				case 4:
					Log.i("handle",getDeletePic());

					_Map = new HashMap<String,Object>();
					_Map.put("PicID",getDeletePic());
					onService(_Map,ConstantValue.DeleteMyAlbumsPic);
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
// Boolean ISDELETE=false;
 
	@Override
	public void OnClick(View view) {
		int i=(Integer) view.getTag();
	//	String s = _Picture.getImgUrl();
		//最后一张点击上传图片
				if(photos.size()==0||i==photos.size()){
					if(photos.size()>=11){
						showDialog();
						return;
					}
					dialog();
					return;
				}
				Picture _Picture=photos.get(i);
				//编辑模式下，选中删除
				if(mAdapter.isDelete()){
					CheckBox cb = (CheckBox) view.findViewById(R.id.photo_checkBox1);
					if(cb.isChecked()&&deleteImages.contains(_Picture)){
						cb.setChecked(false);
						deleteImages.remove(_Picture);
						Log.i("OnClick","取消");
						Log.i("deleteImages.size",deleteImages.size()+"");
					}else{
						cb.setChecked(true);
						deleteImages.add(_Picture);
						Log.i("OnClick", "选中");
						Log.i("deleteImages.size",deleteImages.size()+"");
					}
					return;
				}
		
		Intent _Intent=new Intent(MyPhotoActivity.this,PhotoLook.class);
		Bundle _Bundle=new Bundle();
		_Bundle.putSerializable("",photos);
		_Bundle.putInt("currentItem", i);
		_Intent.putExtras(_Bundle);
		startActivity(_Intent);
	}

	public void showDialog(){
		AppDialog _Dialog = new AppDialog(this,R.style.dialog,AppDialog.TIP_TYPE);
		_Dialog.show();
		_Dialog.setTitle(R.string.add_photo);
	}

	@Override
	public void OnLongClick(View v) {
		//长按添加选项框
	//	editPhoto();
		/*for(int i=0;i<photos.size();i++){
			//最后一张不加选项框
			if(i==photos.size()-1){
				continue;
			}
			View view=mGridView.getChildAt(i);
			CheckBox cb=(CheckBox) view.findViewById(R.id.photo_checkBox1);
			cb.setFocusable(false);
			cb.setVisibility(View.VISIBLE);
		}*/
	}

	@Override
	public void OnChickedClick(CompoundButton button, Boolean b) {
		// TODO Auto-generated method stub
		int i=(Integer) button.getTag();
		//得到图片地址
		Picture _Picture=photos.get(i);
		//若选中将给地址添加删除集合
		if(mAdapter.isDelete()&&b){
			//	photos.remove(_Picture);
			//	button.setTag(1);
				deleteImages.add(_Picture);
			}
		//若取消，将地址从集合中移除
		if(!b&&deleteImages.contains(_Picture)){
			deleteImages.remove(_Picture);
		}
	//	mAdapter.notifyDataSetChanged();
	}
	
	//弹出dialog选择照片上传方式
	public void dialog() {
		//判断SD卡是否挂载
		String state = Environment.getExternalStorageState();
		//创建图片路径,针对相片名称，起不同的名字，否则无法上传
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			mFileTemp = new File(Environment.getExternalStorageDirectory(),
					"temp_crop"+new Random().nextInt()+".jpg");
		} else {
			mFileTemp = new File(getFilesDir(), "temp_crop"+new Random().nextInt()+".jpg");
		}
		AppSelectPicsDialog dialog = new AppSelectPicsDialog(this, R.style.dialog);
		dialog.show();
		dialog.setDialogListner(new AppSelectPicsDialog.dialogListenner() {

			@Override
			public void setOnCameraLis(Dialog d, View v) {
				d.dismiss();
				takePicture();
			}
			@Override
			public void setOnGalleryLis(Dialog d, View v) {
				d.dismiss();
				Intent intent = new Intent(MyPhotoActivity.this,PhotoSelectorActivity.class);
				intent.putExtra(PhotoSelectorActivity.KEY_MAX, 5);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				MyPhotoActivity.this.startActivityForResult(intent, REQUEST_SELECTOR);
			}
			@Override
			public void setOnCancelLis(Dialog d, View v) {
				d.dismiss();
			}
		});

		/*final AppSelectPicsDialog _Dialog=new AppSelectPicsDialog(this,R.style.dialog);
		_Dialog.show();
		_Dialog.setDialogListner(new AppSelectPicsDialog.dialogListenner() {
			@Override
			public void setOnCameraLis(Dialog d, View v) {
				_Dialog.dismiss();
				takePicture();
			}

			@Override
			public void setOnGalleryLis(Dialog d, View v) {
				_Dialog.dismiss();
				openGallery();
			}

			@Override
			public void setOnCancelLis(Dialog d, View v) {
				_Dialog.dismiss();
			}*/
		/*dialog = new Dialog(MyPhotoActivity.this, R.style.dialog);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(R.layout.dialog_photo);*/
		/*Button btn_take_photo = (Button) _Dialog
				.findViewById(R.id.dialog_title);
		Button btn_pick_photo = (Button) _Dialog
				.findViewById(R.id.dialog_sure);
		Button btn_cancel = (Button) _Dialog.findViewById(R.id.dialog_cancel);*/
		/*btn_take_photo.setOnClickListener(new OnClicked());
		btn_pick_photo.setOnClickListener(new OnClicked());
		btn_cancel.setOnClickListener(new OnClicked());*/
		/*WindowManager m = ((Activity) MyPhotoActivity.this).getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = (int) (d.getWidth() * 0.99); // 宽度设置为屏幕的0.8
		params.height = (int) (d.getHeight() * 0.5); // 宽度设置为屏幕的0.8
		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
		dialog.show();*/
	}
	private final int MenuEdit=R.menu.photo_menu;
	private final int MenuCancel=R.menu.photo_cancel;
	private  int currentMenu=MenuEdit;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(currentMenu,menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
			case R.id.pm_edit:
				editPhoto();
				break;
			case R.id.pm_cancel:
				cancelDelete();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	//取消删除
	public void cancelDelete(){
		deleteView.setVisibility(View.GONE);
		mAdapter.notifyDataSetChanged();
		mAdapter.setDelete(false);
		currentMenu=MenuEdit;
		mAdapter.getDeleteItems().clear();
		deleteImages.clear();
		invalidateOptionsMenu();
	}

	public void editPhoto(){
		deleteView.setVisibility(View.VISIBLE);
		mAdapter.setDelete(true);
		mAdapter.notifyDataSetChanged();
		currentMenu=MenuCancel;
		invalidateOptionsMenu();
	}

	public void onService(HashMap<String,Object> hashMap,String method) {
		WebUtils.sendPostNoProgress(this, method, hashMap, new WebListener());
	}

	/*public void showToast(String s){
		Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
	}
*/
	public class WebListener  implements WebUtils.OnWebListenr{

		@Override
		public void onSuccess(boolean isSuccess, String msg, String data) {
			if(isSuccess){

				if(isDelete){
					handler.sendEmptyMessage(3);
					isDelete = false;
					return;
				}
				 ArrayList _List = (ArrayList<Picture>) JSON.parseArray(data, Picture.class);
				setPhotos(_List);
			}
		}

		@Override
		public void onFailed(HttpException error, String msg) {
		}
	}
	private void setPhotos(ArrayList<Picture> list){
		if(list.size()==0){
			return ;
		}
		if(photos.size()>0){
			photos.clear();
		}
		for(int i=list.size()-1;i>=0;i--){
			Picture _Picture = list.get(i);
			String s =  _Picture.getImgUrl();
			_Picture.setImgUrl(WebUtils.RENXING_WEB + s);
			photos.add(_Picture);
		}
	//	photos.add(new Picture());
		mAdapter.notifyDataSetChanged();
	}
	private String getDeletePic(){
		String deleteS="";
		if(photos.size()==0||deleteImages.size()==0){
		//	showToast("mList.size："+mList.size()+"---deleteImages.size"+deleteImages.size());
			return null;
		}
	//	Picture _Picture=null;
		for(int i=0;i<deleteImages.size();i++){
			deleteS+=(deleteImages.get(i).getID()+"#");
			/*for(int ii=0;ii<photos.size();ii++){
				Picture picture = photos.get(ii);
				if(s.equals(picture.getImgUrl())){
					deleteS+=(picture.getID()+"#");
				}
			}*/
			/*Picture picture=list.get(i);
			if(s.equals(picture.getImgUrl())){
				_Picture=picture;
				return picture;
			}*/
		}
		deleteS.substring(0, deleteS.length() - 1);
		if(deleteS.equals("")){
			cancelDelete();
		}
		return deleteS;
	}

}

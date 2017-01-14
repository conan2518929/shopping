package com.car.shopping.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.car.shopping.R;
import com.car.shopping.adapter.ResultShopListAdapter;
import com.car.shopping.adapter.ResultShopListAdapter.ResultListClickListener;
import com.car.shopping.app.AppContext;
import com.car.shopping.app.BaseActivity;
import com.car.shopping.constant.Constant;
import com.car.shopping.constant.Urls;
import com.car.shopping.entity.BaseRecommendShopInfo;
import com.car.shopping.entity.RecommendShopsInfo;
import com.car.shopping.entity.StatusInfo;
import com.car.shopping.entity.TelsInfo;
import com.car.shopping.sharepref.SharedPrefConstant;
import com.car.shopping.talk.ui.ChatActivity;
import com.car.shopping.utils.FileUtils;
import com.car.shopping.utils.Utils;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

@SuppressLint("ShowToast")
public class ResultShopLisOnetActivity extends BaseActivity {

	private LinearLayout back, ll;
	private TextView add_all, tv_send,shopnum;
	private String name = "";
	private List<RecommendShopsInfo> lists;
	private List<RecommendShopsInfo> lists_copy;
	private ListView lv;
	private Dialog dialog;
	private Display display;
	private boolean isAll = false;
	private int num = 0;
	private BaseRecommendShopInfo baseInfo;
	private String numPhone = "";
	private List<TelsInfo> tels;
	private EditText et_input;
	private ImageView add_pic;
	private String[] arr = new String[] { "拍照", "本地相册" };

	private static int CAMERA_REQUEST_CODE = 1;
	private static int GALLERY_REQUEST_CODE = 2;
	private Uri uri;

	private ResultShopListAdapter adapter;
	private boolean isXZ = false;
	private String sendContent = "";
	private String toAccids = "";
	private String imageStr = "";
	private Handler h = new Handler();
	private File file = null;

	private int isSend = 0;// 是否是拍照 是0 否1
	private Bitmap bitmap_yasuo = null;

	private String pathStr = "";
	private int typeNum = 0;

	private TextView collection, verification, video;
	private String order_by = "save_num desc";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		initViews();
		initData();
	}

	@Override
	public void initViews() {
		back = getView(R.id.back);
		add_all = getView(R.id.add_all);
		lv = getView(R.id.lv);
		ll = getView(R.id.ll);
		et_input = getView(R.id.et_input);
		add_pic = getView(R.id.add_pic);
		tv_send = getView(R.id.tv_send);

		collection = getView(R.id.collection);
		verification = getView(R.id.verification);
		video = getView(R.id.video);
		shopnum = getView(R.id.shopnum);
	}

	@Override
	public void initData() {
		name = getIntent().getExtras().getString("name");
		typeNum = getIntent().getExtras().getInt("type", 100);
		lists = new ArrayList<RecommendShopsInfo>();
		lists_copy = new ArrayList<RecommendShopsInfo>();
		tels = new ArrayList<>();
		tv_send.setOnClickListener(new OnClickListener() {// 发送文字

			@SuppressLint("ShowToast")
			@Override
			public void onClick(View v) {
				boolean isShow = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if (isShow) {
					isSend = 1;
					if (lists.size() > 0) {
						lists_copy.clear();
						toAccids = "";
						sendContent = et_input.getText().toString().trim();
						if (sendContent.length() == 0) {
							Toast.makeText(getBaseContext(), "不能发空消息...", 1000).show();
						} else {
							for (int i = 0; i < lists.size(); i++) {
								isXZ = ResultShopListAdapter.getIsSelected(ResultShopListAdapter.getIsSelected(), i);
								if (isXZ) {
									lists_copy.add(lists.get(i));
								}
							}
							int num = lists_copy.size();
							if (num == 0) {
								Toast.makeText(getBaseContext(), "请选择商家后发送消息...", 1000).show();
							} else {
								for (int m = 0; m < lists_copy.size(); m++) {
									// System.out.println("商家==" +
									// lists_copy.get(m).getShop_name());
									toAccids = toAccids + lists_copy.get(m).getIm_username() + ",";
								}
								toAccids.substring(0, toAccids.length() - 1);
								showLoadingDialog();
								sendImageOrTextData(0);
							}
						}
					}
				} else {
					Utils.showText(ResultShopLisOnetActivity.this, "请您登录后进行聊天...");
				}

			}
		});
		add_pic.setOnClickListener(new OnClickListener() {// 弹出拍照

			@Override
			public void onClick(View v) {
				boolean isShow = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if (isShow) {
					if (lists.size() > 0) {
						lists_copy.clear();
						toAccids = "";
						for (int i = 0; i < lists.size(); i++) {
							isXZ = ResultShopListAdapter.getIsSelected(ResultShopListAdapter.getIsSelected(), i);
							if (isXZ) {
								lists_copy.add(lists.get(i));
							}
						}
						int num = lists_copy.size();
						if (num == 0) {
							Toast.makeText(getBaseContext(), "请选择商家后发送图片...", 1000).show();
							return;
						} else {
							for (int m = 0; m < lists_copy.size(); m++) {
								// System.out.println("商家==" +
								// lists_copy.get(m).getShop_name());
								toAccids = toAccids + lists_copy.get(m).getIm_username() + ",";
							}
							toAccids.substring(0, toAccids.length() - 1);
						}
						AlertDialog.Builder builder2 = new Builder(ResultShopLisOnetActivity.this);
						builder2.setTitle("请您选择发送图片方式");
						builder2.setItems(arr, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int position) {
								if (position == 0) {
									isSend = 0;
									getphoto();
								} else if (position == 1) {
									isSend = 1;
									Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
									intent.setType("image/*");
									startActivityForResult(intent, GALLERY_REQUEST_CODE);
								}
							}
						});
						builder2.create().show();
					}
				} else {
					Utils.showText(ResultShopLisOnetActivity.this, "请您登录后进行聊天...");
				}
			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (lists.size() > 0) {
					if (!isAll) {
						Utils.changeColor(ResultShopLisOnetActivity.this, add_all, R.drawable.white_y_g);
						isAll = true;
						// 遍历list的长度，将MyAdapter中的map值全部设为true
						for (int i = 0; i < lists.size(); i++) {
							ResultShopListAdapter.getIsSelected().put(i, true);
						}
					} else {
						Utils.changeColor(ResultShopLisOnetActivity.this, add_all, R.drawable.white_y);
						isAll = false;
						// 遍历list的长度，将MyAdapter中的map值全部设为true
						for (int i = 0; i < lists.size(); i++) {
							if (ResultShopListAdapter.getIsSelected().get(i)) {
								ResultShopListAdapter.getIsSelected().put(i, false);
							}
						}
					}
					adapter.changeView();
				}
			}
		});
		et_input.addTextChangedListener(watcher);
		collection.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				order_by = "save_num desc";
				getResultData(order_by);
			}
		});
		verification.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				order_by = "has_tag desc";
				getResultData(order_by);
			}
		});
		video.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				order_by = "has_video desc";
				getResultData(order_by);
			}
		});
		getResultData(order_by);
	}

	private TextWatcher watcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			String str = et_input.getText().toString().trim();
			if (str.length() == 0) {
				add_pic.setVisibility(View.VISIBLE);
				tv_send.setVisibility(View.GONE);
			} else {
				add_pic.setVisibility(View.GONE);
				tv_send.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
			String str = et_input.getText().toString().trim();
			if (str.length() == 0) {
				add_pic.setVisibility(View.VISIBLE);
				tv_send.setVisibility(View.GONE);
			} else {
				add_pic.setVisibility(View.GONE);
				tv_send.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			String str = et_input.getText().toString().trim();
			if (str.length() == 0) {
				add_pic.setVisibility(View.VISIBLE);
				tv_send.setVisibility(View.GONE);
			} else {
				add_pic.setVisibility(View.GONE);
				tv_send.setVisibility(View.VISIBLE);
			}
		}
	};

	private void getphoto() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		file = FileUtils.getImageFile(this);
		uri = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, CAMERA_REQUEST_CODE);
	}

	private ResultListClickListener mListener = new ResultListClickListener() {
		@Override
		public void myOnClick(int position, View v) {

			switch (v.getId()) {
			case R.id.tv_tel:
				showDialog(position, v);// 弹出电话列表
				break;
			case R.id.ll_detail:// 商家详情
				getDetail(position);
				break;
			case R.id.tv_talk:// 聊天
				boolean isShow = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if (isShow) {
					String name = lists.get(position).getIm_username();
					if (name != null && name.length() != 0) {
						updateAlias(name, position);
					}
				} else {
					Utils.showText(ResultShopLisOnetActivity.this, "请您登录后进行聊天...");
				}
				break;
			case R.id.radioButton1:// 选中商家
				CheckBox img = (CheckBox) v.findViewById(R.id.radioButton1);
				ResultShopListAdapter.getIsSelected().put(position, img.isChecked());

				for (int i = 0; i < lists.size(); i++) {
					boolean all = ResultShopListAdapter.getIsSelected(ResultShopListAdapter.getIsSelected(), i);
					if (!all) {
						Utils.changeColor(ResultShopLisOnetActivity.this, add_all, R.drawable.white_y);
						isAll = false;
						return;
					}
					Utils.changeColor(ResultShopLisOnetActivity.this, add_all, R.drawable.white_y_g);
					isAll = true;
				}

				break;
			default:
				break;
			}
		}
	};

	private void updateAlias(String name, int position) {
		Intent intent = new Intent(ResultShopLisOnetActivity.this, ChatActivity.class);
		intent.putExtra(com.car.shopping.talk.Constant.EXTRA_USER_ID, baseInfo.getData().get(position).getIm_username());
		intent.putExtra("user_nickname", baseInfo.getData().get(position).getShop_name());
		startActivity(intent);
	}

	private void getDetail(int position) {
		String id = lists.get(position).getId();
		Bundle data = new Bundle();
		data.putString("shop_id", id);
		Utils.goOtherWithDataActivity(ResultShopLisOnetActivity.this, ShopDetailActivity.class, data);
	}

	@SuppressLint({ "InflateParams", "RtlHardcoded" })
	@SuppressWarnings("deprecation")
	private void showDialog(final int position, View v) {
		View view = LayoutInflater.from(ResultShopLisOnetActivity.this).inflate(R.layout.tel_number_txl, null);
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
		view.setMinimumWidth(display.getWidth());
		final TextView tv1 = (TextView) view.findViewById(R.id.tv_one);
		final TextView tv2 = (TextView) view.findViewById(R.id.tv_two);
		final TextView tv3 = (TextView) view.findViewById(R.id.tv_three);
		final TextView tv4 = (TextView) view.findViewById(R.id.tv_four);
		final TextView tv5 = (TextView) view.findViewById(R.id.tv_five);
		TextView tv_qx = (TextView) view.findViewById(R.id.tv_qx);
		tels = lists.get(position).getTels();
		int numTles = tels.size();
		if (numTles == 1) {
			tv1.setVisibility(View.VISIBLE);
			tv1.setText(tels.get(0).getTel());
		} else if (numTles == 2) {
			tv1.setVisibility(View.VISIBLE);
			tv1.setText(tels.get(0).getTel());
			tv2.setVisibility(View.VISIBLE);
			tv2.setText(tels.get(1).getTel());
		} else if (numTles == 3) {
			tv1.setVisibility(View.VISIBLE);
			tv1.setText(tels.get(0).getTel());
			tv2.setVisibility(View.VISIBLE);
			tv2.setText(tels.get(1).getTel());
			tv3.setVisibility(View.VISIBLE);
			tv3.setText(tels.get(2).getTel());
		} else if (numTles == 4) {
			tv1.setVisibility(View.VISIBLE);
			tv1.setText(tels.get(0).getTel());
			tv2.setVisibility(View.VISIBLE);
			tv2.setText(tels.get(1).getTel());
			tv3.setVisibility(View.VISIBLE);
			tv3.setText(tels.get(2).getTel());
			tv4.setVisibility(View.VISIBLE);
			tv4.setText(tels.get(3).getTel());
		} else if (numTles > 4) {
			tv1.setVisibility(View.VISIBLE);
			tv1.setText(tels.get(0).getTel());
			tv2.setVisibility(View.VISIBLE);
			tv2.setText(tels.get(1).getTel());
			tv3.setVisibility(View.VISIBLE);
			tv3.setText(tels.get(2).getTel());
			tv4.setVisibility(View.VISIBLE);
			tv4.setText(tels.get(3).getTel());
			tv5.setVisibility(View.VISIBLE);
			tv5.setText(tels.get(4).getTel());
		}
		tv1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				numPhone = tv1.getText().toString();
				getCallShop(position);
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numPhone));
				startActivity(intent);
				dialog.dismiss();
			}
		});

		tv2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				numPhone = tv2.getText().toString();
				getCallShop(position);
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numPhone));
				startActivity(intent);
				dialog.dismiss();
			}
		});

		tv3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				numPhone = tv3.getText().toString();
				getCallShop(position);
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numPhone));
				startActivity(intent);
				dialog.dismiss();
			}
		});
		tv4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				numPhone = tv4.getText().toString();
				getCallShop(position);
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numPhone));
				startActivity(intent);
				dialog.dismiss();
			}
		});
		tv5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				numPhone = tv5.getText().toString();
				getCallShop(position);
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numPhone));
				startActivity(intent);
				dialog.dismiss();
			}
		});
		tv_qx.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog = new Dialog(ResultShopLisOnetActivity.this, R.style.ActionSheetDialogStyle);
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.x = 0;
		lp.y = 0;
		dialogWindow.setAttributes(lp);
		dialog.show();
	}

	private void getCallShop(final int position) {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.CALL_SHOP, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				dealTelResult(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(ResultShopLisOnetActivity.this, "网络访问失败");
			}
		}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("shop_id", baseInfo.getData().get(position).getId());
				params.put("tel", numPhone);
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	private void dealTelResult(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {

		} else if (status == 300) {
			logout();
		}
	}

	public void getResultData(final String  order_by) {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.SEARCH_SHOP_ONE, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				 try {
				 JSONObject json = new JSONObject(result);
				 System.out.println("1获取搜索结果==" + json.toString());
				 } catch (JSONException e) {
				 e.printStackTrace();
				 }
				dealData(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(ResultShopLisOnetActivity.this, "网络访问失败");
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				String cityId = AppContext.imp_SharedPref.getSharePrefString(SharedPrefConstant.CHOOSE_CITY, "210100");
				params.put("city_id", cityId);
				params.put("search_content", name);
				System.out.println("name="+name);
				System.out.println("order_by="+order_by);
				params.put("order_by", order_by);
				if (typeNum == 1) {
					params.put("search_field", "am_series");
				} else if (typeNum == 2) {
					params.put("search_field", "am_series");
				} else if (typeNum == 7) {
					params.put("search_field", "am_part");
				} else if (typeNum == 8) {
					params.put("search_field", "am_accessory");
				}
				System.out.println("111city_id="+cityId+",search_content="+name+",order_by="+order_by+",typeNum="+typeNum);
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	private void dealData(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			baseInfo = gson.fromJson(result, BaseRecommendShopInfo.class);
			if (baseInfo != null) {
				lists.clear();
				lists = baseInfo.getData();
				if (lists != null) {
					num = lists.size();
					if (num > 0) {
						shopnum.setText("商家数目:"+lists.size());
						adapter = new ResultShopListAdapter(ResultShopLisOnetActivity.this, lists, mListener);
						lv.setAdapter(adapter);
					} else {
						Utils.showText(ResultShopLisOnetActivity.this, "暂无搜索结果...");
					}
				}
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		AppContext.getInstance().cancelPendingRequests(TAG);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CAMERA_REQUEST_CODE) {
			if (resultCode == RESULT_CANCELED) {
				return;
			}
			// Bundle bundle = data.getExtras();
			// if (bundle != null) {
			// Bitmap photo = (Bitmap) bundle.get("data");
			// sendImage(photo);
			// Uri uriPaiZhao = uri;
			showLoadingDialog();
			Bitmap bitmap = decodeUriAsBitmap(uri);
			imageStr = "";
			// imageStr = sendImage(bitmap);
			pathStr = getRealFilePath(ResultShopLisOnetActivity.this, uri);
			file = Utils.getCompressImg(pathStr, 60, 4, CompressFormat.JPEG);
			imageStr = Utils.getStringValue(pathStr);
		} else if (requestCode == GALLERY_REQUEST_CODE) {
			if (resultCode == RESULT_CANCELED) {

				return;
			}
			if (data == null) {
				return;
			}
			pathStr = "";
			Uri uriLocal;
			uriLocal = data.getData();
			pathStr = getRealFilePath(ResultShopLisOnetActivity.this, uriLocal);
			showLoadingDialog();
			Bitmap bitmap = decodeUriAsBitmap(uriLocal);
			imageStr = "";
			imageStr = sendImage(bitmap);
			file = new File(pathStr);
		}
		new Thread() {

			@Override
			public void run() {

				h.post(new Runnable() {

					@Override
					public void run() {
						imageStr = "data:image/jpeg;base64," + imageStr;
						sendImageOrTextData(1);
					}
				});
			}

		}.start();
	}

	private void sendImageOrTextData(final int type) {
		AppContext.getInstance().cancelPendingRequests(TAG);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.SEND_BATCH_MESSAGE, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				// try {
				// JSONObject json = new JSONObject(result);
				// System.out.println("群发消息结果==" + json.toString());
				// } catch (JSONException e) {
				// e.printStackTrace();
				// }
				dealTalkData(result, type);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(ResultShopLisOnetActivity.this, "网络访问失败");
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("toAccids", toAccids);
				if (type == 0) {
					params.put("type", "0");
					params.put("msg", sendContent);
				} else if (type == 1) {
					params.put("type", "1");
					params.put("file", imageStr);
				}
				return params;
			}
		};
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(300000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // 默认最大尝试次数
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));// 防止传递2次参数
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	private void dealTalkData(String result, int type) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			et_input.setText("");
			// if(isSend == 0){
			// Intent intent = new
			// Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			// Uri uri = Uri.fromFile(file);
			// intent.setData(uri);
			// sendBroadcast(intent);
			// }
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(et_input.getWindowToken(), 0);

			EMConversation em = null;
			EMMessage msg = null;
			List<EMMessage> msgs = null;
			for (int i = 0; i < lists_copy.size(); i++) {
				em = EMClient.getInstance().chatManager().getConversation(lists_copy.get(i).getIm_username(), EMConversationType.Chat, true);
				if (type == 0) {// 文字
					msg = EMMessage.createSendMessage(EMMessage.Type.TXT);
					msg.setReceipt(lists_copy.get(i).getIm_username());
					msg.setStatus(EMMessage.Status.SUCCESS);
					EMTextMessageBody body = new EMTextMessageBody(sendContent);
					msg.addBody(body);
					em.insertMessage(msg);
					msgs = new ArrayList<>();
					msgs.add(msg);
				} else if (type == 1) {// 图片
					msg = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
					msg.setReceipt(lists_copy.get(i).getIm_username());
					msg.setStatus(EMMessage.Status.SUCCESS);
					EMImageMessageBody body = new EMImageMessageBody(file);
					msg.addBody(body);
					em.insertMessage(msg);
					msgs = new ArrayList<>();
					msgs.add(msg);
				}
				EMClient.getInstance().chatManager().importMessages(msgs);
			}

			Utils.showText(ResultShopLisOnetActivity.this, "发送信息成功...");
		} else if (status == 300) {
			logout();
		} else {
			Utils.showText(ResultShopLisOnetActivity.this, "发送信息失败...");
		}
	}

	private String sendImage(Bitmap bm) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 30, stream);
		byte[] bytes = stream.toByteArray();
		String img = new String(Base64.encodeToString(bytes, Base64.DEFAULT));
		return img;
	}

	public static String getRealFilePath(final Context context, final Uri uri) {
		if (null == uri)
			return null;
		final String scheme = uri.getScheme();
		String data = null;
		if (scheme == null)
			data = uri.getPath();
		else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
			data = uri.getPath();
		} else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
			Cursor cursor = context.getContentResolver().query(uri, new String[] { ImageColumns.DATA }, null, null, null);
			if (null != cursor) {
				if (cursor.moveToFirst()) {
					int index = cursor.getColumnIndex(ImageColumns.DATA);
					if (index > -1) {
						data = cursor.getString(index);
					}
				}
				cursor.close();
			}
		}
		return data;
	}

	private Bitmap decodeUriAsBitmap(Uri uri) {

		try {
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inSampleSize = 4;
			bitmap_yasuo = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri), null, bitmapOptions);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap_yasuo;

	}

	private void logout() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.LOGOUT, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				dealLogoutShop(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(ResultShopLisOnetActivity.this, "网络访问失败");
			}
		});
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	private void dealLogoutShop(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			AppContext.imp_SharedPref.putSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
			Intent intent = new Intent();
			intent.setAction("com.app.action.broadcast");
			sendBroadcast(intent);
			Intent i = new Intent(ResultShopLisOnetActivity.this, LoginActivity.class);
			startActivity(i);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bitmap_yasuo != null && !bitmap_yasuo.isRecycled())
			bitmap_yasuo.recycle();
	}

}

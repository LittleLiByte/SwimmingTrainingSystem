package com.scnu.swimmingtrainingsystem.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.mobeta.android.dslv.DragSortListView;
import com.scnu.swimmingtrainingsystem.R;
import com.scnu.swimmingtrainingsystem.activity.MyApplication;
import com.scnu.swimmingtrainingsystem.adapter.ScoreListAdapter;
import com.scnu.swimmingtrainingsystem.util.CommonUtils;
import com.scnu.swimmingtrainingsystem.util.Constants;

@SuppressLint("ValidFragment")
public class EachTimeScoreFragment extends Fragment {
	private MyApplication mApplication;
	private View view;
	private List<ListView> viewList;
	private AutoCompleteTextView acTextView;
	private DragSortListView scListView;
	private DragSortListView dsListView;
	private ScoreListAdapter scoreListAdapter;
	private List<String> scores = new ArrayList<String>();
	private ArrayAdapter<String> dragAdapter;
	private List<String> dragDatas = new ArrayList<String>();
	private int position;
	private int distance;
	private String scoreString;
	private String nameString;
	private boolean firstMatch = true;
	private Toast mToast;

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			String item = dragAdapter.getItem(from);
			dragAdapter.notifyDataSetChanged();
			dragAdapter.remove(item);
			dragAdapter.insert(item, to);
		}
	};

	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			if (dragDatas.size() > 1) {
				dragAdapter.remove(dragAdapter.getItem(which));
			} else {
				CommonUtils.showToast(getActivity(), mToast, "至少要保留一个运动员");
			}
			dragAdapter.notifyDataSetChanged();
		}
	};

	private DragSortListView.DragScrollProfile ssProfile = new DragSortListView.DragScrollProfile() {
		@Override
		public float getSpeed(float w, long t) {
			if (w > 0.8f) {
				// Traverse all views in a millisecond
				return ((float) dragAdapter.getCount()) / 0.001f;
			} else {
				return 10.0f * w;
			}
		}
	};
	private DragSortListView.RemoveListener onRemove2 = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			if (scores.size() > 1) {
				scores.remove(which);
			} else {
				CommonUtils.showToast(getActivity(), mToast, "至少要保留一个成绩");
			}
			scoreListAdapter.notifyDataSetChanged();
		}
	};

	public EachTimeScoreFragment(int position, int distance, String scoreJson,
			String nameJson) {
		this.position = position;
		this.distance = distance;
		this.scoreString = scoreJson;
		this.nameString = nameJson;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_modify_scores, null);
			String[] autoStrings = new String[] { "25", "50", "75", "100",
					"125", "150", "175", "200", "225", "250", "275", "300" };
			ArrayAdapter<String> tipsAdapter = new ArrayAdapter<String>(
					getActivity(), android.R.layout.simple_dropdown_item_1line,
					autoStrings);
			acTextView = (AutoCompleteTextView) view
					.findViewById(R.id.modify_act_current_distance);
			// 设置AutoCompleteTextView的Adapter
			acTextView.setAdapter(tipsAdapter);
			acTextView.setDropDownHeight(350);
			acTextView.setThreshold(1);

			scListView = (DragSortListView) view
					.findViewById(R.id.matchscore_list);
			scListView.setRemoveListener(onRemove2);
			dsListView = (DragSortListView) view
					.findViewById(R.id.matchName_list);
			dsListView.setDropListener(onDrop);
			dsListView.setRemoveListener(onRemove);
			dsListView.setDragScrollProfile(ssProfile);
			acTextView.setText(this.distance + "");

			try {
				JSONArray jsonArray = new JSONArray(scoreString);
				for (int i = 0; i < jsonArray.length(); i++) {
					scores.add(jsonArray.get(i).toString());
				}
				JSONArray jsonArray2 = new JSONArray(nameString);
				for (int j = 0; j < jsonArray2.length(); j++) {
					dragDatas.add(jsonArray2.get(j).toString());
				}
				scoreListAdapter = new ScoreListAdapter(getActivity(), scores);
				dragAdapter = new ArrayAdapter<String>(getActivity(),
						R.layout.drag_list_item, R.id.drag_list_item_text,
						dragDatas);
				scListView.setAdapter(scoreListAdapter);
				dsListView.setAdapter(dragAdapter);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			viewList = new ArrayList<ListView>();
			viewList.add(scListView);
			viewList.add(dsListView);
			MyScrollListener mListener = new MyScrollListener();
			scListView.setOnScrollListener(mListener);
			dsListView.setOnScrollListener(mListener);
		} else {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null) {
				parent.removeView(view);
			}
		}

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mApplication = (MyApplication) getActivity().getApplication();
	}

	private class MyScrollListener implements OnScrollListener {

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			// 关键代码
			View subView = view.getChildAt(0);
			if (subView != null) {
				final int top = subView.getTop();
				for (ListView item : viewList) {
					item.setSelectionFromTop(firstVisibleItem, top);
				}
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

			// 关键代码
			if (scrollState == SCROLL_STATE_IDLE
					|| scrollState == SCROLL_STATE_TOUCH_SCROLL) {
				View subView = view.getChildAt(0);
				if (subView != null) {
					final int top = subView.getTop();
					final int position = view.getFirstVisiblePosition();
					for (ListView item : viewList) {
						item.setSelectionFromTop(position, top);
					}
				}
			}
		}
	}

	public Map<String, Object> check() {
		Map<String, Object> map = new HashMap<String, Object>();
		if (Integer.parseInt(acTextView.getText().toString()) == 0) {
			// 当前距离为0
			map.put("resCode", 1);
			map.put("position", position);
		} else if (scores.size() != dragDatas.size()) {
			// 成绩数和人数不对应
			map.put("resCode", 2);
			map.put("position", position);
		} else {
			// 匹配完成
			if (firstMatch) {
				int number = ((Integer) mApplication.getMap().get(
						Constants.COMPLETE_NUMBER)) + 1;
				firstMatch = false;
				mApplication.getMap().put(Constants.COMPLETE_NUMBER, number);
				map.put("resCode", 0);
				map.put("position", position);
				map.put("distance", acTextView.getText().toString());
				map.put("scores", scores);
				map.put("names", dragDatas);
			} else {
				map.put("resCode", -1);
				map.put("position", position);
			}

		}
		return map;
	}
}

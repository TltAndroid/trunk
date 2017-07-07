package com.sirc.tlt.ui.activity.nativeContact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.app.Activity;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.health.PidHealthStats;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.sirc.tlt.R;
import com.sirc.tlt.base.BaseActivity;
import com.sirc.tlt.feiyucloud.InCallActivity;
import com.sirc.tlt.feiyucloud.util.ToastUtil;
import com.sirc.tlt.ui.activity.LoginActivity;
import com.sirc.tlt.ui.view.SearchEditText;
import com.sirc.tlt.ui.view.TemplateTitle;
import com.sirc.tlt.utils.CommonUtil;
import com.sirc.tlt.utils.Config;
import com.sirc.tlt.utils.SetTranslucentStatus;

public class ActivityNativtContact extends BaseActivity implements SectionIndexer {
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortGroupMemberAdapter adapter;
    private SearchEditText mClearEditText;

    private LinearLayout titleLayout;
    private TextView title;
    private TextView tvNofriends;
    private AsyncQueryHandler asyncQueryHandler; // 异步查询数据库类对象
    private Map<Integer, ContactBean> contactIdMap = null;
    private List<ContactBean> list;
    private boolean isSearch = false;
    /**
     * 上次第一个可见元素，用于滚动时记录标识。
     */
    private int lastFirstVisibleItem = -1;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<ContactBean> SourceDateList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    private RelativeLayout rl_contact_kefu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SetTranslucentStatus setTranslucentStatus = new SetTranslucentStatus(this);

        setContentView(R.layout.activity_native_contact);
        rl_contact_kefu = (RelativeLayout) findViewById(R.id.rl_contact_kefu);

        rl_contact_kefu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("Flag_Incoming", false);
                intent.putExtra("CallType", Config.CALLTYPE);
                intent.setClass(ActivityNativtContact.this, InCallActivity.class);
                intent.putExtra("CallNumber",Config.KEFU_PHONE);
                startActivity(intent);
            }
        });

        initViews();
    }

    private void initViews() {

        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        titleLayout = (LinearLayout) findViewById(R.id.title_layout);
        title = (TextView) this.findViewById(R.id.title_layout_catalog);
        tvNofriends = (TextView) this
                .findViewById(R.id.title_layout_no_friends);

        asyncQueryHandler = new MyAsyncQueryHandler(getContentResolver());
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // ��ϵ��Uri��
        // 查询的字段
        String[] projection = {ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.DATA1, "sort_key",
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
                ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI};
        // 按照sort_key升序查詢
        asyncQueryHandler.startQuery(0, null, uri, projection, null, null,
                "sort_key COLLATE LOCALIZED asc");

        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                if (adapter != null){
                    int position = adapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        sortListView.setSelection(position);
                    }
                }
            }
        });

    }

    /**
     * 为ListView填充数据
     *
     * @param
     * @return
     */
    private List<ContactBean> filledData(List<ContactBean> list) {
        List<ContactBean> mSortList = new ArrayList<ContactBean>();

        for (int i = 0; i < list.size(); i++) {
            ContactBean sortModel = new ContactBean();
            sortModel.setDesplayName(list.get(i).getDesplayName());
            sortModel.setPhoneNum(list.get(i).getPhoneNum());
            sortModel.setContactId(list.get(i).getContactId());
            sortModel.setPhotoId(list.get(i).getPhotoId());
            sortModel.setPhotoUrl(list.get(i).getPhotoUrl());
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(list.get(i)
                    .getDesplayName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<ContactBean> filterDateList = new ArrayList<ContactBean>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
            tvNofriends.setVisibility(View.GONE);
        } else {
            filterDateList.clear();
            for (ContactBean sortModel : SourceDateList) {
                String name = sortModel.getDesplayName();
                if (name.indexOf(filterStr.toString()) != -1
                        || characterParser.getSelling(name).startsWith(
                        filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
        if (filterDateList.size() == 0) {
            tvNofriends.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public Object[] getSections() {
        return null;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return SourceDateList.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < SourceDateList.size(); i++) {
            String sortStr = SourceDateList.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @author Administrator
     */
    private class MyAsyncQueryHandler extends AsyncQueryHandler {

        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                contactIdMap = new HashMap<Integer, ContactBean>();
                list = new ArrayList<ContactBean>();
                cursor.moveToFirst(); // 游标移动到第一项
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    String name = cursor.getString(1);
                    String number = cursor.getString(2);
                    String sortKey = cursor.getString(3);
                    int contactId = cursor.getInt(4);
                    Long photoId = cursor.getLong(5);
                    String lookUpKey = cursor.getString(6);
                    int mThumbnailColumn;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        mThumbnailColumn = cursor.getInt(7);
                        // Otherwise, sets the thumbnail column to the _ID
                        // column
                    } else {
                        mThumbnailColumn = cursor.getInt(4);
                    }
                    String photoUrl = cursor.getString(mThumbnailColumn);
                    if (contactIdMap.containsKey(contactId)) {

                    } else {
                        // 创建联系人对象
                        ContactBean contact = new ContactBean();
                        contact.setDesplayName(name);
                        contact.setPhoneNum(number);
                        contact.setSortKey(sortKey);
                        contact.setContactId(contactId);
                        contact.setPhotoId(photoId);
                        contact.setLookUpKey(lookUpKey);
                        contact.setPhotoUrl(photoUrl);
                        list.add(contact);
                        contactIdMap.put(contactId, contact);
                    }
                }
                if (list.size() > 0) {
                    SourceDateList = filledData(list);
                    // 根据a-z进行排序源数据
                    Collections.sort(SourceDateList, pinyinComparator);
                    adapter = new SortGroupMemberAdapter(
                            ActivityNativtContact.this, SourceDateList,ActivityNativtContact.this);
                    sortListView.setAdapter(adapter);

//                    sortListView
//                            .setOnItemClickListener(new OnItemClickListener() {
//
//                                @Override
//                                public void onItemClick(AdapterView<?> parent,
//                                                        View view, int position, long id) {
//                                    // 这里要利用adapter.getItem(position)来获取当前position所对应的对象
//                                    if (CommonUtil.getIsLogin(ActivityNativtContact.this)){
//                                        int callType = 1;
//                                        Intent intent = new Intent();
//                                        intent.putExtra("Flag_Incoming", false);
//                                        intent.putExtra("CallType", callType);
//                                        intent.putExtra("CallNumber", SourceDateList.get(position)
//                                                .getPhoneNum());
//                                        intent.setClass(ActivityNativtContact.this,InCallActivity.class);
//                                        startActivity(intent);
//                                        finish();
//                                        Log.i("拨打list号码",position+"");
//                                    }else {
//                                        Intent intent = new Intent(ActivityNativtContact.this,
//                                                LoginActivity.class);
//                                        startActivity(intent);
//                                    }
//
//                                }
//                            });

                    sortListView.setOnScrollListener(new OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView view,
                                                         int scrollState) {
                        }

                        @Override
                        public void onScroll(AbsListView view,
                                             int firstVisibleItem, int visibleItemCount,
                                             int totalItemCount) {
                            int section = getSectionForPosition(firstVisibleItem);
                            int nextSection = getSectionForPosition(firstVisibleItem + 1);
                            int nextSecPosition = getPositionForSection(+nextSection);
                            if (firstVisibleItem != lastFirstVisibleItem) {
                                MarginLayoutParams params = (MarginLayoutParams) titleLayout
                                        .getLayoutParams();
                                params.topMargin = 0;
                                titleLayout.setLayoutParams(params);
                                title.setText(SourceDateList.get(
                                        getPositionForSection(section))
                                        .getSortLetters());

                            }
                            if (nextSecPosition == firstVisibleItem + 1) {
                                View childView = view.getChildAt(0);
                                if (childView != null) {
                                    int titleHeight = titleLayout.getHeight();
                                    int bottom = childView.getBottom();
                                    MarginLayoutParams params = (MarginLayoutParams) titleLayout
                                            .getLayoutParams();
                                    if (bottom < titleHeight) {
                                        float pushedDistance = bottom
                                                - titleHeight;
                                        params.topMargin = (int) pushedDistance;
                                        titleLayout.setLayoutParams(params);
                                    } else {
                                        if (params.topMargin != 0) {
                                            params.topMargin = 0;
                                            titleLayout.setLayoutParams(params);
                                        }
                                    }
                                }
                            }
                            lastFirstVisibleItem = firstVisibleItem;
                        }
                    });
                    mClearEditText = (SearchEditText) findViewById(R.id.filter_edit);

                    // 根据输入框输入值的改变来过滤搜索
                    mClearEditText.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void onTextChanged(CharSequence s, int start,
                                                  int before, int count) {
                            // 这个时候不需要挤压效果 就把他隐藏掉
                            titleLayout.setVisibility(View.GONE);
                            // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                            filterData(s.toString());
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s,
                                                      int start, int count, int after) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }
            }
            super.onQueryComplete(token, cookie, cursor);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

}

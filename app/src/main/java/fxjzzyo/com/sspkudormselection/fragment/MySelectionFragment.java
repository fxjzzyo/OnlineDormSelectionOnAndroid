package fxjzzyo.com.sspkudormselection.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import fxjzzyo.com.sspkudormselection.Constant.Global;
import fxjzzyo.com.sspkudormselection.Constant.PersonData;
import fxjzzyo.com.sspkudormselection.Constant.ResponseBean;
import fxjzzyo.com.sspkudormselection.MainActivity;
import fxjzzyo.com.sspkudormselection.R;
import fxjzzyo.com.sspkudormselection.utils.NetUtils;
import fxjzzyo.com.sspkudormselection.utils.SPFutils;
import okhttp3.Call;
import okhttp3.Response;

import static fxjzzyo.com.sspkudormselection.Constant.Global.account;
import static fxjzzyo.com.sspkudormselection.Constant.Global.vcode;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MySelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MySelectionFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.drawerIcon)
    ImageView drawerIcon;
    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.tv_stuid)
    TextView tvStuid;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_gender)
    TextView tvGender;
    @BindView(R.id.tv_vcode)
    TextView tvVcode;
    @BindView(R.id.tv_room)
    TextView tvRoom;
    @BindView(R.id.tv_building)
    TextView tvBuilding;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_grade)
    TextView tvGrade;
    Unbinder unbinder;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.tv_state)
    TextView tvState;
    @BindView(R.id.ll_build_number)
    LinearLayout llBuildNumber;
    @BindView(R.id.ll_room_number)
    LinearLayout llRoomNumber;
    @BindView(R.id.btn_to_select)
    Button btnToSelect;
    @BindView(R.id.iv_map)
    ImageView ivMap;
    private View v;

    private ActionBar actionBar;

    private String mParam1;
    private String mParam2;


    public MySelectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MySelectionFragment.
     */
    public static MySelectionFragment newInstance(String param1, String param2) {
        MySelectionFragment fragment = new MySelectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (v != null) {
            unbinder = ButterKnife.bind(this, v);
            return v;
        }
        View v = inflater.inflate(R.layout.fragment_my_selection, container, false);
        //绑定控件
        unbinder = ButterKnife.bind(this, v);
        //设置actionbar
//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolBar);
//        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        tvTitle.setText("个人信息");
        //设置打开菜单监听
        drawerIcon.setOnClickListener(this);
        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化数据
        initData();
        initEvent();

    }

    private void initEvent() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.green, R.color.blue);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //从网络获取数据
        getDataFromNet();

    }

    /**
     * //从网络获取数据
     */
    private void getDataFromNet() {
        NetUtils netUtils = NetUtils.getInstance();
        netUtils.getDataAsynFromNet(Global.GET_DETAIL + "?stuid=" + account, new NetUtils.MyNetCall() {
            @Override
            public void success(Call call, Response response) throws IOException {
                Log.i("tag", "success");
                String result = response.body().string();
                Log.i("tag", "result: " + result);
                //解析数据
                final ResponseBean responseBean = JSON.parseObject(result, ResponseBean.class);

                Log.i("tag", "responseBean: " + responseBean.toString());
                if (responseBean != null) {
                    final String data = responseBean.getData();
                    final PersonData personData = JSON.parseObject(data, PersonData.class);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            String errcode = responseBean.getErrcode();
                            Log.i("tag", "errcode: " + errcode);
                            Log.i("tag", "data: " + personData.toString());
                            if (errcode.equals("0")) {//登录成功
                                Toast.makeText(getActivity(), "获取数据成功！", Toast.LENGTH_SHORT).show();
                                //设置数据
                                setData(personData);
                                //存储个人信息数据到sharedpreference
                                saveData(personData);
                            } else {
                                Toast.makeText(getActivity(), "请求失败！错误代码： " + errcode, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
            }

            @Override
            public void failed(Call call, IOException e) {
                Log.i("tag", "failed");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(getActivity(), "请求失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 存储个人信息数据到sharedpreference
     */
    private void saveData(PersonData personData) {
        SPFutils.saveStringData(getActivity(), SPFutils.STUDID, personData.getStudentid());
        SPFutils.saveStringData(getActivity(), SPFutils.NAME, personData.getName());
        SPFutils.saveStringData(getActivity(), SPFutils.GENDER, personData.getGender());
        SPFutils.saveStringData(getActivity(), SPFutils.VCODE, personData.getVcode());
        SPFutils.saveStringData(getActivity(), SPFutils.ROOM, personData.getRoom());
        SPFutils.saveStringData(getActivity(), SPFutils.BUILDING, personData.getBuilding());
        SPFutils.saveStringData(getActivity(), SPFutils.LOCATION, personData.getBuilding());
        SPFutils.saveStringData(getActivity(), SPFutils.GRADE, personData.getGrade());
    }

    /**
     * 设置数据
     *
     * @param personData
     */
    private void setData(PersonData personData) {
        String room = personData.getRoom();
        if (room==null) {//还未选宿舍
            tvState.setText("未办理");
            llBuildNumber.setVisibility(View.GONE);
            llRoomNumber.setVisibility(View.GONE);
            ivMap.setVisibility(View.GONE);
            btnToSelect.setVisibility(View.VISIBLE);
        } else {//已办理
            tvState.setText("已办理");
            tvRoom.setText(personData.getRoom());
            tvBuilding.setText(personData.getBuilding());
            ivMap.setVisibility(View.VISIBLE);
            btnToSelect.setVisibility(View.GONE);
        }

        tvStuid.setText(personData.getStudentid());
        tvName.setText(personData.getName());
        tvGender.setText(personData.getGender());
        tvVcode.setText(personData.getVcode());
        tvLocation.setText(personData.getLocation());
        tvGrade.setText(personData.getGrade());
        //记录校验码
        vcode = personData.getVcode();
        String gender = personData.getGender();
        if (gender.equals("女")) {
            //记录性别
            Global.gender = 2;//默认为1，男生
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //解除绑定
        unbinder.unbind();
    }

    @Override
    public void onRefresh() {
        //从网络获取数据
        getDataFromNet();
    }

    /**
     * 跳转到选宿舍页
     */
    @OnClick(R.id.btn_to_select)
    public void toSelect() {
        //切换菜单按钮
        MainActivity.mainActivityInstance.mNavigationView.setCheckedItem(R.id.navigation_select);
        //跳转到SelectFragment
        MainActivity.mainActivityInstance.switchFragment(this,SelectFragment.newInstance("", ""));

    }

    public interface MySelectionFragmentListener {
        public void myselectionFragment();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.drawerIcon://只有在抽屉关闭的状态下 才能看到这个icon 故这是判断抽屉关闭的时候 点击icon时 触发怎样的事件 这个触发事件放在mainactivity中实现。
                if (getActivity() instanceof MySelectionFragmentListener) {
                    ((MySelectionFragmentListener) getActivity()).myselectionFragment();
                }
                break;

            default:
                break;
        }
    }
}

package com.zcc.android.mvpframe.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.zcc.android.mvpframe.mvpc.presenter.BasePresenter;
import com.zcc.android.mvpframe.mvpc.presenter.PresenterFactory;
import com.zcc.android.mvpframe.mvpc.view.IBaseView;
import com.zcc.android.mvpframe.util.ToastUtil;
import com.zcc.android.mvpframe.widget.CustomDialog;

import java.util.Set;

import butterknife.ButterKnife;

/**
 * @author ZCC
 * @date 2017/12/18
 * @description Activity 基类
 * 1. 封装Presenter
 * 1.1 在需要使用Presenter的Activity类上方添加注解@CreatePresenter({XXXPresenter.class})，声明所需要的Presenter
 * 1.2 实现业务逻辑的时候通过getPresenter(Class)来获取Presenter
 * 2. 封装CustomDialog
 * 2.1 需要显示时，根据需要调用showLoadingDialog() / showErrorDialog()
 * 2.2 调用disMissDialog()关闭弹窗，onDestroy()也会调用
 */

public abstract class BaseActivity extends AppCompatActivity implements IBaseView {

    protected final String TAG = this.getClass().getSimpleName();

    // 对话框
    private CustomDialog mDialog;

    /**
     * 使用List来保存Presenter
     * 解决一个界面多个Presenter的情况
     */
    private Set<BasePresenter> mPresenterSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParams(getIntent().getExtras());
        initLayout();
        initPresenter();
        initData();
    }

    /**
     * 初始化Bundle参数
     */
    protected void initParams(Bundle params) {

    }

    /**
     * 初始化布局
     * 封装成方法的目的：
     * 例如需要实现含有标题栏的BaseActivity可重写此方法
     */
    protected void initLayout() {
        setContentView(bindLayout());
        ButterKnife.bind(this);
    }

    /**
     * 获取绑定的布局ID
     *
     * @return 要进行绑定的布局ID
     */
    protected abstract int bindLayout();

    /**
     * 初始化Presenter
     */
    private void initPresenter() {
        createPresenter();
        attachView();
    }

    /**
     * 创建Presenter
     */
    private void createPresenter() {
        mPresenterSet = PresenterFactory.getPresenter(this);
    }

    /**
     * Presenter绑定V层
     */
    private void attachView() {
        // 绑定View
        for (BasePresenter e : mPresenterSet) {
            e.attachView(this);
            Log.d(TAG, e.getClass().getSimpleName() + " -- 绑定当前View");
        }
    }

    /**
     * 获取对应的Presenter
     *
     * @param clazz 对应的Presenter类
     * @param <T>
     * @return 对应的Presenter
     */
    @SuppressWarnings("unchecked")
    public <T extends BasePresenter> T getPresenter(Class<? extends BasePresenter> clazz) {
        for (BasePresenter e : mPresenterSet) {
            if (e.getClass() == clazz) {
                Log.d(TAG, e.getClass().getSimpleName() + " -- 成功获取");
                return (T) e;
            }
        }
        Log.d(TAG, clazz.getSimpleName() + " -- 找不到类");
        return null;
    }

    /**
     * 初始化数据
     */
    protected abstract void initData();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detachView();
        disMissDialog();
    }

    /**
     * 显示加载对话框
     */
    public void showLoadingDialog() {
        showCustomDialog(CustomDialog.LOADING);
    }

    /**
     * 显示错误提示框
     */
    public void showErrorDialog() {
        showCustomDialog(CustomDialog.ERROR);
    }

    /**
     * 显示对话框
     *
     * @param layoutId 对话框的类型
     */
    private void showCustomDialog(int layoutId) {
        // 考虑多次调用的情况，先将以显示的dialog关闭
        disMissDialog();
        mDialog = new CustomDialog(this, layoutId);
        mDialog.show();
        Log.d(TAG, "显示Dialog");
    }

    /**
     * 若dialog显示，则将其关闭
     */
    public void disMissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            Log.d(TAG, "关闭Dialog");
        }
    }

    /**
     * 解绑View
     */
    private void detachView() {
        for (BasePresenter e : mPresenterSet) {
            Log.d(TAG, e.getClass().getSimpleName() + " -- 解除绑定");
            e.detachView();
        }
        mPresenterSet.clear();
    }

    @Override
    public void onFailure(String s) {
        ToastUtil.showMainThreadToast(this, s);
        disMissDialog();
    }
}

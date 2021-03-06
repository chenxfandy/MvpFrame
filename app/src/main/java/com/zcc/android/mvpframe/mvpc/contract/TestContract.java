package com.zcc.android.mvpframe.mvpc.contract;

import com.zcc.android.mvpframe.mvpc.presenter.BasePresenter;
import com.zcc.android.mvpframe.mvpc.view.IBaseView;

/**
 * @author ZCC
 * @date 2017/12/29
 * @description
 */

public interface TestContract {
    interface View extends IBaseView {
        void request(String s);
    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void request(String url);
    }
}

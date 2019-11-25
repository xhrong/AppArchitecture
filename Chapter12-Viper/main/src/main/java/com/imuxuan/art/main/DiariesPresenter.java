package com.imuxuan.art.main;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import com.imuxuan.art.base.UseCase;
import com.imuxuan.art.data.DiariesRepository;
import com.imuxuan.art.main.list.DiariesAdapter;
import com.imuxuan.art.model.Diary;

import java.util.List;

public class DiariesPresenter implements DiariesContract.Presenter {

    //    private final DiariesRepository mDiariesRepository; // 数据仓库
    private final DiariesContract.View mView; // 日记列表视图
    private DiariesAdapter mListAdapter; // 日记列表适配器

    //    private GetAllDiariesUseCase mGetAllDiariesUseCase = new GetAllDiariesUseCase();
    private DiariesContract.Interactor mInteractor;
    private DiariesContract.Router mRouter;

    public DiariesPresenter(@NonNull DiariesContract.View diariesFragment,
                            DiariesContract.Interactor interactor,
                            DiariesContract.Router router) {
//        mDiariesRepository = DiariesRepository.getInstance(); // 获取数据仓库的实例
        mView = diariesFragment; // 将传入的界面复制给日记的成员变量保存
        mInteractor = interactor;
        mRouter = router;
    }

    @Override
    public void start() {
        initAdapter(); // 初始化适配器
        loadDiaries(); // 加载日记数据
    }

    @Override
    public void destroy() {

    }

    @Override
    public void loadDiaries() { // 加载日记数据
//        mDiariesRepository.getAll(new DataCallback<List<Diary>>() { // 通过数据仓库获取数据
//            @Override
//            public void onSuccess(List<Diary> diaryList) {
//                if (!mView.isActive()) { // 视图未被添加则返回
//                    return;
//                }
//                updateDiaries(diaryList); // 数据获取成功，处理数据
//            }
//
//            @Override
//            public void onError() {
//                if (!mView.isActive()) { // 视图未被添加则返回
//                    return;
//                }
//                mView.showError();  // 数据获取失败，弹出错误提示
//            }
//        });
        mInteractor.getAll().setRequestValues(DiariesRepository.getInstance())
                .setUseCaseCallback(new UseCase.UseCaseCallback<List<Diary>>() {
                    @Override
                    public void onSuccess(List<Diary> response) {
                        if (!mView.isActive()) { // 视图未被添加则返回
                            return;
                        }
                        updateDiaries(response); // 数据获取成功，处理数据
                    }

                    @Override
                    public void onError() {
                        if (!mView.isActive()) { // 视图未被添加则返回
                            return;
                        }
                        mView.showError();  // 数据获取失败，弹出错误提示
                    }
                }).run();
    }

    @Override
    public void addDiary() {
//        mView.gotoWriteDiary(); // 跳转添加日记
        mRouter.gotoWriteDiary();
    }

    @Override
    public void updateDiary(@NonNull Diary diary) {
//        mView.gotoUpdateDiary(diary.getId()); // 跳转更新日记
        mRouter.gotoUpdateDiary(diary.getId());
    }

    @Override
    public void onResult(int requestCode, int resultCode) { // 返回界面获取结果信息
        if (Activity.RESULT_OK != resultCode) { // 处理结果不是成功则返回
            return;
        }
        mView.showSuccess(); // 弹出成功提示信息
    }

    private void initAdapter() { // 初始化适配器
        mListAdapter = new DiariesAdapter(); // 创建日记列表的适配器
        // 设置列表的条目长按事件
        mListAdapter.setOnLongClickListener(new DiariesAdapter.OnLongClickListener<Diary>() {
            @Override
            public boolean onLongClick(View v, Diary data) {
                updateDiary(data); // 更新日记
                return false;
            }
        });
        mView.setListAdapter(mListAdapter);
    }

    private void updateDiaries(List<Diary> diaries) {
        mListAdapter.update(diaries); // 更新列表中的日记数据
    }
}

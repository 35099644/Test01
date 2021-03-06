package testpackage;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import junit.framework.TestCase;
import java.util.Random;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;


/**
 * created by xiaozhi
 * <p>
 * Date 2019/12/3
 */
public class WeiShitest extends UiAutomatorTestCase {


    /*app 名字*/
    private String appName = "微视";

    private int errorCount = 0;//记录异常强制启动次数  超过10次就关闭应用

    //    @Test
    public void test() throws UiObjectNotFoundException {
        // 获取设备对象
//        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        UiDevice uiDevice = UiDevice.getInstance(/*instrumentation*/);

        // 获取上下文
//        Context context = instrumentation.getContext();

//        LogUtil.e("我开始运行了");
        int count = 0;

        try {

            baseMethod(uiDevice, 0);//启动时  先关闭其他的

            //腾讯微视完全要自己特别定制方案 因为需要每次一达到目标就进行点击
            while (true) {

//                LogUtil.e("我运行了" + (count++));

                //播放按钮
                UiObject uiPlay = new UiObject(new UiSelector().resourceId("com.tencent.weishi:id/video_player_play_button"));
                //首页
                UiObject uiHome = new UiObject(new UiSelector().resourceId("com.tencent.weishi:id/bottom_bar_image_icon"));
                //心
                UiObject uiHeart = new UiObject(new UiSelector().index(5).className("android.widget.FrameLayout"));
                //任务中心
                UiObject uiTaskCenter = new UiObject(new UiSelector().resourceId("com.tencent.weishi:id/top_bar"));


                if (uiHome.exists() && uiHome.isSelected()) {//是首页

                    //微视需要手动点击收钱  有问题  解析会出现问题
                    try {
                        UiObject uiFinishNms = new UiObject(new UiSelector().index(3).className("android.widget.TextView"));
                        if (uiFinishNms.exists() && !TextUtils.isEmpty(uiFinishNms.getText()) &&
                                Integer.parseInt(uiFinishNms.getText()) >= 1) {//已经完成了一条以上
                            uiFinishNms.click();
                            continue;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Random r = new Random();
                    int number = r.nextInt(100) + 1;
                    /*随机数 进行判断 点击心或者滑动到下一个视频*/
                    if (number <= 10) {//上滑
                        uiDevice.swipe(534, 802, 400, 1200, 2);
                    } else if (number <= 95) {//下滑
                        uiDevice.swipe(400, 1200, 534, 802, 2);
                        Thread.sleep(8000);//播放 时长
                    } else {//3点击心
                        if (uiHeart.exists()) uiHeart.click();
                    }

                } else if (uiTaskCenter.exists()) {//是任务中心

                    uiDevice.click(550, 1155);//设置的点击固定  的位置

                } else {//处理异常情况  1.0 点击重播 2.0 广告滑动一下
                    UiObject uiAct = new UiObject(new UiSelector().resourceId("com.tencent.weishi:id/iv_outer_activity_close"));
                    UiObject uiCloseBtn = new UiObject(new UiSelector().resourceId("com.tencent.weishi:id/close_btn"));


                    if (uiAct.exists()) {//活动
                        uiAct.click();
                    } else if (uiCloseBtn.exists()) {//青少年保护弹框
                        uiCloseBtn.click();
                    } else {//最终的强制搞一波

                        baseMethod(uiDevice, 1);

                    }
                }

                Thread.sleep(500);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 基本的运行方法封装
     */
    public void baseMethod(UiDevice uiDevice, int flag) {
        try {
            switch (flag) {
                case 0://CLEAR_APP
                    uiDevice.pressRecentApps();
                    Thread.sleep(500);
                    UiObject clearAll = new UiObject(new UiSelector().resourceId("com.android.systemui:id/clearAnimView"));
                    if (clearAll.exists()) {
                        clearAll.click();
                        Thread.sleep(500);
                    }
                    break;
                case 1://Error_Base
                    if (errorCount > 6) {//这个强制方法走了10次  出现什么异常问题了 直接关闭应用  重新启动
                        uiDevice.pressHome();
                        Thread.sleep(500);
                        uiDevice.pressRecentApps();
                        Thread.sleep(500);
                        UiObject appClearAll =
                                new UiObject(new UiSelector().resourceId("com.android.systemui:id/clearAnimView"));
                        if (appClearAll.exists()) {
                            appClearAll.click();
                            errorCount = 0;//重置失败次数
                            Thread.sleep(500);
                        }
                    }
                    uiDevice.pressHome();
                    Thread.sleep(500);
                    uiDevice.pressRecentApps();
                    Thread.sleep(500);
                    UiObject appLaunch = new UiObject(new UiSelector().descriptionContains(appName)
                            .className("android.widget.FrameLayout"));
                    if (appLaunch.exists()) {//没有彻底挂掉
                        appLaunch.click();
                        Thread.sleep(1000);
                    } else {//彻底挂掉了  重启
                        uiDevice.pressHome();
                        Thread.sleep(500);
                        //启动应用
                        UiObject uiVideo = new UiObject(new UiSelector().text(appName));
                        if (uiVideo.exists()) {
                            uiVideo.click();
                            Thread.sleep(2000);
                        }
                    }
                    errorCount++;//增加异常启动次数
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
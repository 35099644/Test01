package testpackage;

import android.app.Instrumentation;
import android.content.Context;
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
 * <p>快手极速版
 * Date 2019/12/3
 */
public class KuaiSJiSutest extends UiAutomatorTestCase {


    /*app 名字*/
    private String appName = "快手极速版";


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

            while (true) {

//                LogUtil.e("我运行了" + (count++));

                //首页
                UiObject uiHome = new UiObject(new UiSelector().resourceId("com.kuaishou.nebula:id/left_btn"));


                if (uiHome.exists()) {//是首页

                    Random r = new Random();
                    int number = r.nextInt(100) + 1;
                    /*随机数 进行判断 点击心或者滑动到下一个视频*/
                    if (number <= 10) {//上滑
                        uiDevice.swipe(534, 802, 400, 1200, 2);
                    } else if (number <= 94) {//下滑
                        uiDevice.swipe(400, 1200, 534, 802, 2);
                        Thread.sleep(5000);//播放 时长
                    } else if (number <= 97) {//3点击心
                        //心
                        UiObject uiHeart = new UiObject(new UiSelector().resourceId("com.kuaishou.nebula:id/like_icon"));
                        if (uiHeart.exists()) uiHeart.click();
                    } else {
                        //关注
                        UiObject uiFollow = new UiObject(new UiSelector().resourceId("com.kuaishou.nebula:id/follow_layout"));
                        if (uiFollow.exists()) uiFollow.click();
                    }

                    /*处理异常情况*/
                    UiObject uiCloseBtn = new UiObject(new UiSelector().resourceId("com.kuaishou.nebula:id/positive"));
                    UiObject uiSignIn = new UiObject(new UiSelector().className("android.view.View").description("立即签到"));
                    UiObject uiWebView = new UiObject(new UiSelector().resourceId("com.kuaishou.nebula:id/webView"));
                    UiObject uiCloseBtn02 = new UiObject(new UiSelector().resourceId("com.kuaishou.nebula:id/close"));

                    if (uiCloseBtn.exists()) {//青少年保护弹框
                        uiCloseBtn.click();
                    } else if (uiCloseBtn02.exists()) {//邀请
                        uiCloseBtn02.click();
                    } else if (uiSignIn.exists()) {//签到
                        uiSignIn.click();
                    } else if (uiWebView.exists()) {
                        uiDevice.pressBack();
                        Thread.sleep(2000);
                    }

                } else {//处理异常情况  1.0 点击重播 2.0 广告滑动一下
                    UiObject uiReplay = new UiObject(new UiSelector().resourceId("com.kuaishou.nebula:id/replay_ad_video"));


                    if (uiReplay.exists()) {
                        uiDevice.swipe(400, 1200, 534, 802, 2);
                    }  else {//最终的强制搞一波

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
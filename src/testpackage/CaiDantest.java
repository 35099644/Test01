package testpackage;

import android.app.Instrumentation;
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
 * <p>彩蛋视频 测试用例
 * Date 2019/12/3
 */
public class CaiDantest extends UiAutomatorTestCase {


    /*app 名字*/
    private String appName = "彩蛋视频";


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

                //首页
                UiObject uiHome = new UiObject(new UiSelector().resourceId("com.jifen.dandan:id/view_home_top_shadow"));
                //心
                UiObject uiHeart = new UiObject(new UiSelector().resourceId("com.jifen.dandan:id/iv_like_icon"));


                if (uiHome.exists()) {//是首页

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

                } else {//处理异常情况  1.0 点击重播 2.0 广告滑动一下
                    UiObject uiDialogClose = new UiObject(new UiSelector().resourceId("com.jifen.dandan:id/iv_close"));
                    UiObject uiDialogClose02 = new UiObject(new UiSelector().resourceId("com.jifen.dandan:id/close_bottom_button"));
                    UiObject uiCloseBtn = new UiObject(new UiSelector().resourceId(""));
                    UiObject uiWebView = new UiObject(new UiSelector().resourceId("com.jifen.dandan:id/q_web_view"));


                    if (uiDialogClose.exists()) {//弹框（邀请好友）
                        uiDialogClose.click();
                    } else if (uiDialogClose02.exists()) {
                        uiDialogClose02.click();
                    } else if (uiCloseBtn.exists()) {//青少年保护弹框
                        uiCloseBtn.click();
                    } else if (uiWebView.exists()) {//个人中心 webView 控件
                        uiDevice.pressBack();
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
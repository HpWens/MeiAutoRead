一个方向，你能否改变世界？

先来看看效果图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190322142823783.gif#pic_center)

由于 Pay 的效果图被屏蔽，这里以刷文章的方式呈上。

注意：打开「某度」，输入地址，浏览，点击广告等操作都是手机自动完成，非人为手指控制，同时手机非 Root 非无障碍并没有连接 usb。

亲测还能自动 Pay，自动回复，自动 Play Music 等等，用句夸大词说：只有你想不到，没有做不到。

### 前言
说来话长，那我就长话短说，在去年有个朋友让我帮忙刷某度文章阅读量，作为一名程序员，首先想到模拟接口访问，经常一番折腾就放弃了，大厂的网络传输加密不是那么好破解的，尝试了几种抓包，拦截，改变参数，再次发送的方式，测试并没有成功。

同许多程序员一样，喜欢一条路走到黑，不见南墙不回头，通过网上搜索大量资料来破解加密，一次次失败，最终还是失败了，不得不佩服某度的网络安全体系，不是我一个[「嫩头青」](https://baike.baidu.com/item/%E6%84%A3%E5%A4%B4%E9%9D%92)可以破解的。

那么问题还是回到起点，既然不能改变接口环境，那么只剩下一个笨办法，手动输入地址，查阅文章，来达到阅读量的增加。不过接下来的问题就让人太尴尬了，那就是效率问题，手动查阅太枯燥效率太低了，那能不能手机自动完成查阅文章的动作，并不需要人工操作呢。

答案是肯定的，虽然效率低了点，依旧是一种解决方案。说好的长话短说，这话又说多了。

### 实践

大家都知道 [Android 调试桥 (adb)](https://developer.android.com/studio/command-line/adb.html) 是一个通用命令行工具，其允许您与模拟器实例或连接的 Android 设备进行通信。它可为各种设备操作提供便利，如安装和调试应用，并提供对 Unix shell（可用来在模拟器或连接的设备上运行各种命令）的访问。

比如模拟按键点击：

```java
	adb shell input tap 460 410
```

点击屏幕 (460 410) 这点，模拟输入文本：

```java
	adb shell input text hello
```

输入文本「hello」，模拟滑动：

```java
	adb shell input swipe 300 1000 300 500
```

参数 300 1000 300 500 分别表示起始点 x 坐标，起始点 y 坐标，结束点 x 坐标，结束点 y 坐标。模拟回车，返回键：

```java
	adb shell input keyevent 66
	adb shell input keyevent 4
```

66 表示回车，4 表示返回键。还有常见的 <font color=#606C8F>adb install</font> ，<font color=#606C8F>adb push</font> 录屏截图等等。想查看更多 adb 命令，请链接 [awesome-adb](https://github.com/mzlogin/awesome-adb)。好了，具体看案例。

#### 「某度」自动浏览文章

「某度」自动浏览文章为了以下几步：

 1. 打开「某度」app
 2. 点击顶部输入框
 3. 输入浏览的文章地址
 4. 回车，搜索文章
 5. 滑动（浏览）
 6. 点击推荐广告
 7. 点击返回键退出

第一步，启动应用 / 调起 Activity 的命令如下：

```java
	adb shell am start [options] <INTENT>
```

例如：

```java
	adb shell am start -n com.tencent.mm/.ui.LauncherUI
```

表示打开「微信」主界面。参数 <font color=#606C8F>com.tencent.mm</font> 表示微信包名 <font color=#606C8F>.ui.LauncherUI</font> 表示打开的 Activity 的名称。查看当前界面 Activity 名称的方式有许多，这里推荐 [android-TopActivity](https://github.com/109021017/android-TopActivity)。

如下图左上角：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190321145829920.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTI1NTEzNTA=,size_16,color_FFFFFF,t_70)

获取到「某度」的包名与主界面的名称为，<font color=#606C8F>com.baidu.searchbox</font> 与 <font color=#606C8F>.MainActivity </font>，那么打开「某度」的 adb 命令如下：

```java
	adb shell am start -n com.baidu.searchbox/com.baidu.searchbox.MainActivity
```

第二步，点击顶部输入框区域，那么需要获取到点击点的坐标位置，可以借助「开发者选择」的「指针位置」来获取：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190321152048984.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTI1NTEzNTA=,size_16,color_FFFFFF,t_70)
那么我们通过模拟点击 (431 380) 来模拟点击输入框：

```java
	adb shell input tap 431 380
```

第三步，输入浏览文章的地址，adb 命令如下：

```java
	adb shell input text 'https://na.mbd.baidu.com/je3rqk2?f=cp'
```

注意：如果你安装了第三方输入法，可能会导致输入错乱，请在「设置」「语言与输入法」「虚拟键盘」下关闭第三方输入法。

第四步，回车搜索，adb 命令如下：

```java
	adb shell input keyevent 66
```

注意：文章搜索是异步，需延迟后续操作，在后文中会讲到。

第五步，模拟滑动，adb 命令如下：

```java
	adb shell input swipe  200 1800  200 0
```

起点 y 坐标 1800 与结束点 y 坐标 0 ，相差越大滑动越大，在每个机型上需要调整，同时滑动到广告出现在屏幕内的次数可能也不一样。具体请在真机上模拟调整。

第六步，点击推荐广告，同上获取到广告区域的坐标点：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190321170751932.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTI1NTEzNTA=,size_16,color_FFFFFF,t_70)

对应的 adb 命令如下：

```java
	adb shell input tap 583 339
```

第七步，点击返回键的 adb 命令：

```java
	adb shell input keyevent 4
```

总共七步就完成了一次自动浏览文章，有小伙伴肯定会有疑问，不会每步都执行 DOS 命令吧，这样比手动点击还慢呢，那有没有脚本可以批处理。

[bat](https://baike.baidu.com/item/bat/365230) （批处理文件类型）就是解决这样的问题。新建 xx.bat 文件，把以下代码拷入：

```java
ping 127.0.0.1 -n 2
adb shell am start -n com.baidu.searchbox/com.baidu.searchbox.MainActivity
ping 127.0.0.1 -n 3
adb shell input tap 431 380
adb shell input text  https://na.mbd.baidu.com/je3rqk2?f=cp
ping 127.0.0.1 -n 1
adb shell input keyevent 66
ping 127.0.0.1 -n 3
adb shell input swipe  200 1800  200 0
ping 127.0.0.1 -n 2
adb shell input swipe  200 1800  200 0
ping 127.0.0.1 -n 2
adb shell input swipe  200 1800  200 0
ping 127.0.0.1 -n 2
adb shell input swipe  200 1200  200 0
ping 127.0.0.1 -n 2
adb shell input tap 583 339
ping 127.0.0.1 -n 5
```

保存，确保手机连上电脑，双击 xx.bat 文件，发现手机自动打开百度，输入地址，浏览文章，哈哈，这样方便多了。但还有一个小小的不足，浏览完一次文章 xx.bat 就结束了，能不能加个循环语句，让文章间断性被浏览。由于并不熟悉 .bat 的写法，研究了一下，功夫不负有心人，我们可以这么做：

```java
:start 
ping 127.0.0.1 -n 2
adb shell am start -n com.baidu.searchbox/com.baidu.searchbox.MainActivity
ping 127.0.0.1 -n 3
adb shell input tap 431 380
adb shell input text  https://na.mbd.baidu.com/je3rqk2?f=cp
ping 127.0.0.1 -n 1
adb shell input keyevent 66
ping 127.0.0.1 -n 3
adb shell input swipe  200 1800  200 0
ping 127.0.0.1 -n 2
adb shell input swipe  200 1800  200 0
ping 127.0.0.1 -n 2
adb shell input swipe  200 1800  200 0
ping 127.0.0.1 -n 2
adb shell input swipe  200 1200  200 0
ping 127.0.0.1 -n 2
adb shell input tap 583 339
ping 127.0.0.1 -n 5
adb shell input keyevent 4
adb shell input keyevent 4
adb shell input keyevent 4
adb shell input keyevent 4
adb shell input keyevent 4
goto start
pause
```

`ping 127.0.0.1 -n 1` 用于延迟执行，由于每步操作都是异步，延时时间你可以根据具体情况而定。emmm，大功告成，文章被无限周期性浏览，但还是有两个小小的瑕疵，一是要运行 bat 脚本；二是手机必须连上电脑。针对第一种情况，可不可以在 app 内执行 adb shell 命令，最初尝试提示「permission denied」权限被拒绝，需要 Root 权限，Root 太麻烦，而且用户也不会同意，那么我们可不可以绕过 Root 权限？

这个问题一直困扰着我，在这里非常感谢 gtf 同学的[免Root实现静默安装和点击任意位置（非无障碍）](https://www.jianshu.com/p/86253b2c49f3)文章，这里引用他的一段话：

> 我来问大家个新问题，怎样让 app 获取 root 权限？这个问题答案已经有不少了，网上一查便可知其实是获取「Runtime.getRuntime().exec」的流，在里面用su提权，然后就可以执行需要 root 权限的 shell 命令，比如挂载 system 读写，访问 data 分区，用 shell 命令静默安装，等等。话说回来，是不是和我们今天的主题有点像，如何使 app 获取 shell 权限？嗯，其实差不多，思路也类似，因为本来 root 啦， shell 啦，根本就不是 Android 应用层的名词呀，他们本来就是 Linux 里的名词，只不过是 Android 框架运行于 Linux 层之上， 我们可以调用 shell 命令，也可以在shell 里调用 su 来使shell 获取 root 权限，来绕过 Android 层做一些被限制的事。然而在 app 里调用 shell 命令，其进程还是 app 的，权限还是受限。所以就不能在 app 里运行 shell 命令，那么问题来了，不在 app 里运行在哪运行？答案是在 pc 上运行。当然不可能是 pc 一直连着手机啦，而是 pc 上在 shell 里运行独立的一个 java 程序，这个程序因为是在 shell 里启动的，所以具有 shell 权限。我们想一下，这个 Java 程序在 shell 里运行，建立本地 socket 服务器，和 app 通信，远程执行 app 下发的代码。因为即使拔掉了数据线，这个 Java 程序也不会停止，只要不重启他就一直活着，执行我们的命令，这不就是看起来 app 有了 shell 权限？现在真相大白，飞智和黑域用 usb 调试激活的那一下，其实是启动那个 Java 程序，飞智是执行模拟按键，黑域是监听系统事件，你想干啥就任你开发了。**「注：黑域和飞智由于进程管理的需要，其实是先用 shell 启动一个 so ，然后再用 so 做跳板启动 Java 程序，而且 so 也充当守护进程，当 Java 意外停止可以重新启动，读着有兴趣可以自行研究，在此不多做说明」**

这里有一句话说的非常好，**「你想干啥就任你开发了。」** 如果调整好参数，我可以拿到「某信」的付款二维码，并能截图上传，就问你怕不怕？

`gtf` 同学的思路，让我醍醐灌顶，**并不是绕过 Root 权限，而是在 pc 上在 shell 里运行独立的一个 java 程序，，这个程序因为是在 shell 里启动的，所以具有 shell 权限。这个 Java 程序在 shell 里运行，建立本地 socket 服务器，和 app 通信，远程执行 app 下发的代码。因为即使拔掉了数据线，这个 Java 程序也不会停止，只要不重启他就一直活着，执行我们的命令，这不就是看起来 app 有了 shell 权限？**

大家想一想，如果我把 Java 程序部署到远程服务器上，那么我能随时随地都可以建立 socket ，从而控制手机自动完成想做的事情。

还得再次感谢 `gtf` 同学分享了一个简单的 socket 程序，亲测后的效果图如下：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190322103313156.gif)

在通过 app_process 在环境下运行 java 程序有以下几个细节：

细节一，通过 `javac ` 运行多个 `.java` 文件：

```java
D:\>cd D:\AndroidSpace\app_process-shell-use\app\src\main\java\shellService

D:\AndroidSpace\app_process-shell-use\app\src\main\java\shellService>javac -encoding UTF-8 Main.java Service.java ServiceShellUtils.java ServiceThread.java
```

首先 cd 到 java 目录，然后执行 javac 命令。注意：指定编码格式为 UTF-8 ，不然中文乱码会导致编译不通过。编译过的目录如下图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190322111749455.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTI1NTEzNTA=,size_16,color_FFFFFF,t_70)

细节二，多个 `.class` 文件生成 `.dex` 文件

```java
D:\AndroidSpace\app_process-shell-use\app\src\main\java\shellService>cd..

D:\AndroidSpace\app_process-shell-use\app\src\main\java>dx --dex --output=D:\hello.dex shellService/Main.class shellService/Service.class shellService/Service$CreateServerThread.class shellService/Service$ServiceGetText.class shellService/ServiceShellUtils.class shellService/ServiceShellUtils$ServiceShellCommandResult.class shellService/ServiceThread.class shellService/ServiceThread$1.class
```

注意，首先需要 cd 到 java 目录，不然会提示类文件找不到，然后通过 dx --dex 命令生成 .dex 文件，所生成的所有 .class 文件都需要加到命令中。生成的 .dex 文件如下图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190322113404466.jpg)

细节三，app_process 运行 java 程序：

```java
D:\AndroidSpace>cd..

D:\>adb push hello.dex /data/local/tmp
hello.dex: 1 file pushed. 1.5 MB/s (8096 bytes in 0.005s)

D:\>adb shell app_process -Djava.class.path=/data/local/tmp/hello.dex /data/local/tmp shellService.Main
>>>>>>Shell鏈嶅姟绔▼搴忚璋冪敤<<<<<<
鏈嶅姟绔繍琛屽湪4521绔彛
```

首先 cd 到 .dex 目录，接着把 .dex 文件推到手机 `/data/local/tmp` 目录下，最后执行 app_process 命令，因为 utf8 在 Windows shell 里有问题，所以乱码了，但是还是说明我们成功了。

由于 adb shell 需要 usb 连上手机才能运行，那么我们可以通过 ADB WiFi 来连接手机，运行 app ，DOS 的执行情况如下：

```java
鍏抽棴Socket
鐩戝惉璇锋眰鍒版潵鍒欎骇鐢熶竴涓猄ocket瀵硅薄锛屽苟缁х画鎵ц
鍒涘缓浜嗕竴涓柊鐨勮繛鎺ョ嚎绋?
鐢盨ocket瀵硅薄寰楀埌杈撳叆娴侊紝骞舵瀯閫犵浉搴旂殑BufferedReader瀵硅薄
鐢盨ocket瀵硅薄寰楀埌杈撳嚭娴侊紝骞舵瀯閫燩rintWriter瀵硅薄
while寰幆锛氳幏鍙栦粠瀹㈡埛绔鍏ョ殑瀛楃涓?
while寰幆锛氬鎴风杩斿洖 : adb shell input tap 545 980
while寰幆锛氭湇鍔″櫒灏嗚繑鍥烇細###ShellError#sh: <stdin>[1]: adb: not found
while寰幆锛氬噯澶囧埛鏂拌繑鍥?
while寰幆锛氬凡鍒锋柊杩斿洖
```

报了 `adb: not found` ，缺少 adb 的运行环境。那么我们可以在手机上搭建 adb 运行环境，具体可以参考[手机对手机进行adb指令（OTG)](https://www.jianshu.com/p/5d93c6d6cfb1?utm_campaign)，本篇并不会讲解如何在手机上搭建 adb 环境，因为在多数情况下我们并不会把 Socket 的服务端部署在手机上，而是部署在 pc 上，步骤如下。

运行这个服务端：

```java
public class Main {
    public static void main(String[] args){
        new ServiceThread().start();
        while (true);
    }
}
```

运行客户端，建立连接。注意：HOST = "192.168.xxx.xx" 换成局域网 ip 地址。在 MainActivity 类中直接调用：

```java
	runShell("adb shell am start -n com.baidu.searchbox/com.baidu.searchbox.MainActivity");
	Thread.sleep(1000);
	runShell("adb shell input tap 460 410");
	...... 省略其他
```

手机自动浏览「某度」文章，当这个时候拔去 usb 连接，会提示 `no devices/emulators found` 表明无设备连接，在上文中已经提到，这个时候可以通过 ADB WIFI 连接。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190322143055490.gif#pic_center)

当然我觉得 ADB WIFI 连接也比较麻烦，如果能一键启动那不更好，那么接下来就需要把 ADB WIFI 的实现原理接入到服务器。这样就能真正的做到「一键启动」。

### 总结，思考
总结，手机自动操作操作还不够成熟，还有一些技术需要攻破，同时也存在了安全隐患。思考，汽车有了自动驾驶，为啥安卓不能有「自动操作」。

如果觉得本篇文章对你有用，别忘记给小编控件库**点 star ，点 star，点 star**

https://github.com/HpWens/MeiWidgetView

源码如下：

https://github.com/HpWens/MeiAutoRead

如果对「自动操作」感兴趣的小伙伴，点击下面二维码，关注：控件人生

![qrcode_for_gh_232b5a56667d_258.jpg](https://upload-images.jianshu.io/upload_images/2258857-27f4e8c3d79e6204.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240#pic_center)

<center > 扫一扫 关注我的公众号 </center> 
<center> 想了解更多自动操作的动态吗~ </center>


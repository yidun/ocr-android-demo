# 身份认证
身份证采集

## 兼容性
| 条目        | 说明                                                                      |
| ----------- | -----------------------------------------------------------------------  |
| 适配版本    | minSdkVersion 16 及以上版本                                                 |
| cpu 架构    | 内部提供了 armeabi-v7a 和 arm64-v8a 两种 so ，对于不兼容 arm 的 x86 机型不适配 |

## 资源引入

### 远程仓库依赖(推荐)
从 1.0.1 版本开始，提供远程依赖的方式，本地依赖的方式逐步淘汰。本地依赖集成替换为远程依赖请先去除干净本地包，避免重复依赖冲突

确认 Project 根目录的 build.gradle 中配置了 mavenCentral 支持

```
buildscript {
    repositories {
        mavenCentral()
    }
    ...
}

allprojects {
    repositories {
        mavenCentral()
    }
}
```
在对应 module 的 build.gradle 中添加依赖

```
implementation 'io.github.yidun:ocr:1.0.8'
```
### 本地手动依赖

#### 获取 SDK 

从易盾官网下载身份认证ocr sdk 的 aar 包 [包地址](https://support.dun.163.com/documents/391676076156063744?docId=535291444139208704)

#### 添加 aar 包依赖

将获取到的 aar 文件拷贝到对应 module 的 libs 文件夹下（如没有该目录需新建），然后在 build.gradle 文件中增加如下代码

```
android{
    repositories {
        flatDir {
            dirs 'libs'
        }
    } 
}    

dependencies {
     implementation(name: 'ocr-sdk', ext: 'aar') //aar名称和版本号以下载下来的最新版为准
     implementation(name: 'openCVLibrary343-release', ext: 'aar')  // 添加对OpenCV库的依赖   
}
```

如果同时使用易盾的活体检测和身份证OCR SDK，请务必先引用OCR SDK； 遇到so冲突，请用以下方式解决

```
packagingOptions {
        pickFirst  'lib/arm64-v8a/libc++_shared.so'
        pickFirst  'lib/armeabi-v7a/libc++_shared.so'
    }
```
## 各种配置

### 权限配置

SDK 依赖如下权限

```
<uses-permission android:name="android.permission.CAMERA" />
```

CAMERA 权限是隐私权限，Android 6.0 及以上需要动态申请。使用前务必先动态申请权限

```
ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
```

### 混淆配置

在 proguard-rules.pro 文件中添加如下混淆规则

```
-keeppackagenames com.netease.nis.ocr
-keepattributes SourceFile,LineNumberTable
-keep class !com.netease.nis.**,** {*;}
-dontwarn **
-keep class com.netease.nis.ocr.OcrScanner  {
    public <methods>;
    public <fields>;
}
-keep class com.netease.nis.ocr.OcrScanView  {
    public <methods>;
    public <fields>;
}
-keep class com.netease.nis.ocr.OcrEngine{
    native <methods>;
}
-keep class com.netease.nis.ocr.CameraView  {
    public <methods>;
}
-keep class com.netease.nis.ocr.OcrCropListener{*;}
```

## 使用说明

1. 在xml布局文件中使用OCR扫描预览View
注意: 预览宽高不要随意设置，请遵守大部分相机支持的预览宽高比，3：4或9：16

如下是个简单示例：

```
<com.netease.nis.ocr.OcrScanView
 	android:id="@+id/ocr_view"
 	android:layout_width="match_parent"
 	android:layout_height="match_parent"
 	android:layout_gravity="center_horizontal" />
```
 
OcrScanView.setMaskColor(String colorRGB) 可以设置遮罩层的颜色

2. 获取AliveDetector对象，进行初始化
将前面布局中获取到的相机预览View以及从易盾官网申请的业务id传给init()接口进行初始化

```
OcrScanner.getInstance().init(applicationContext, ocr_view, "your_business_id")
```

检测国徽面

```
OcrScanner.getInstance().setScanType(OcrScanner.SCAN_TYPE_NATIONAL_EMBLEM)
```

检测头像面

```
OcrScanner.getInstance().setScanType(OcrScanner.SCAN_TYPE_AVATAR)
```

3. 设置回调监听器，在监听器中根据相应回调做自己的业务处理

```
 OcrScanner.getInstance().setCropListener(object : OcrCropListener {
            override fun onSuccess(picturePath: String?) {
                Log.d(TAG, "保存的图片路径为$picturePath")
            }

            override fun onError(code: Int, msg: String?) {
                showToast("onError:$msg")
                Log.e(TAG, "ocr扫描出错，原因:$msg")
            }

            override fun onOverTime() {
                showToast("检测超时")
                Log.e(TAG, "ocr扫描检测超时")
            }
   })
```

4. 开始/停止检测

```
OcrScanner.getInstance().start();
OcrScanner.getInstance().stop();
```


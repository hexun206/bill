# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\AndroidStudioSdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
# 指定代码的压缩级别
-optimizationpasses 5

# 包明不混合大小写
-dontusemixedcaseclassnames

# 不去忽略非公共的库类
-dontskipnonpubliclibraryclasses

 # 优化不优化输入的类文件
-dontoptimize

-dontshrink
 # 预校验
-dontpreverify

 # 混淆时是否记录日志
-verbose

 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# 保护注解
-keepattributes *Annotation*

# 保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService


-keep class com.alipay.**{*;}
-keep class com.alibaba.**{*;}
-keep class com.umeng.**{*;}
-keep class com.android.u1city.common.**{*;}
-keep class  com.android.volley.**{*;}
-keep class  org.apache.http.entity.mime.**{*;}
-keep class  com.tencent.**{*;}
-keep class  com.ta.utdid2.**{*;}
-keep class  org.apache.commons.codec.**{*;}
-keep class  org.apache.commons.lang.**{*;}
-keep class  com.amap.**{*;}
-keep class  com.aps.**{*;}
-keep class com.autonavi.**  {*;}
-keep class  com.google.zxing.**{*;}
-keep class  com.google.gson.**{*;}
-keep class  com.sina.**{*;}
-keep class org.achartengine.**{*;}
-keep class android.support.v7.**{*;}
-keep class android.support.v4.**{*;}
-keep class com.taobao.**{*;}
-keep class com.taobao.tae.sdk.TaeSDK{*;}
-keep class com.ut.mini.**{*;}
# 如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment



# 忽略警告
-ignorewarning

#记录生成的日志数据,gradle build时在本项目根目录输出

# apk 包内所有 class 的内部结构
-dump class_files.txt
# 未混淆的类和成员
-printseeds seeds.txt
# 列出从 apk 中删除的代码
-printusage unused.txt
# 混淆前后的映射
-printmapping mapping.txt

#记录生成的日志数据，gradle build时 在本项目根目录输出-end


#####混淆保护自己项目的部分代码以及引用的第三方jar包library

#-libraryjars libs/umeng-analytics-v5.2.4.jar

#三星应用市场需要添加:sdk-v1.0.0.jar,look-v1.0.1.jar
#-libraryjars libs/sdk-v1.0.0.jar
#-libraryjars libs/look-v1.0.1.jar

#如果不想混淆 keep 掉
-keep class com.lippi.recorder.iirfilterdesigner.** {*; }
#友盟
-dontwarn com.ut.mini.**
-dontwarn okio.**
-dontwarn com.xiaomi.**
-dontwarn com.squareup.wire.**
-dontwarn android.support.v4.**

-keepattributes *Annotation*

-keep interface android.support.v4.app.** { *; }

-keep class okio.** {*;}
-keep class com.squareup.wire.** {*;}

-keep class com.umeng.message.protobuffer.* {
	 public <fields>;
         public <methods>;
}

-keep class com.umeng.message.* {
	 public <fields>;
         public <methods>;
}

-keep class org.android.agoo.impl.* {
	 public <fields>;
         public <methods>;
}

-keep class org.android.agoo.service.* {*;}

-keep class org.android.spdy.**{*;}

-keep public class **.R$*{
    public static final int *;
}
#如果compileSdkVersion为23，请添加以下混淆代码
-dontwarn org.apache.http.**
-dontwarn android.webkit.**
-keep class org.apache.http.** { *; }
-keep class org.apache.commons.codec.** { *; }
-keep class org.apache.commons.logging.** { *; }
-keep class android.net.compatibility.** { *; }
-keep class android.net.http.** { *; }


#项目特殊处理代码

#忽略警告
-dontwarn com.lippi.recorder.utils**
#保留一个完整的包
-keep class com.lippi.recorder.utils.** {
    *;
 }

-keep class  com.lippi.recorder.utils.AudioRecorder{*;}


#如果引用了v4或者v7包
-dontwarn android.support.**

#混淆保护自己项目的部分代码以及引用的第三方jar包library-end

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable

#保持 Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
#-keepclassmembers enum * {
#  public static **[] values();
#  public static ** valueOf(java.lang.String);
#

-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}

#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}

#避免混淆泛型 如果混淆报错建议关掉
#–keepattributes Signature

#移除log 测试了下没有用还是建议自己定义一个开关控制是否输出日志
#-assumenosideeffects class android.util.Log {
#    public static boolean isLoggable(java.lang.String, int);
#    public static int v(...);
#    public static int i(...);
#    public static int w(...);
#    public static int d(...);
#    public static int e(...);
#

#如果用用到Gson解析包的，直接添加下面这几行就能成功混淆，不然会报错。

# For using GSON @Expose annotation
-keepattributes *Annotation*
#gson
#-libraryjars libs/gson-2.2.2.jar
-keepattributes Signature
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.bluelinelabs.** { *; }
-keep class com.bluelinelabs.logansquare.** { *; }
-keep class java.lang.** { *; }
#华图相关类
-keep class com.huatu.teacheronline.bean { *; }
-keep class com.huatu.teacheronline.personal.bean.** { *; }
-keep class com.huatu.teacheronline.exercise.bean.erbean { *; }
-keep class com.huatu.teacheronline.vipexercise.vipbean { *; }
-keep class com.huatu.teacheronline.widget.PopwindowUtil { *; }
-keep class com.huatu.teacheronline.widget.ChooseSpeedPopwindows { *; }
-keep class com.bluelinelabs.logansquare.** { *; }
-keep @com.bluelinelabs.logansquare.annotation.JsonObject class *
-keep class **$$JsonObjectMapper { *; }
#greendao混淆
-keep class de.greenrobot.dao.** {*;}

-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
     public static java.lang.String TABLENAME;
 }
 -keep class **$Properties
 # Fresco混淆配置
 -keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip
-keep class com.facebook.imagepipeline.** {*;}
 -keep @com.facebook.common.internal.DoNotStrip class *
 -keepclassmembers class * {
     @com.facebook.common.internal.DoNotStrip *;
 }
# -libraryjars libs/alipaySdk-20160120.jar
# -libraryjars libs/CCSDK.jar
# -libraryjars libs/gensee_baseplayer.jar
# -libraryjars libs/gensee_common.jar
# -libraryjars libs/gensee_doc.jar
# -libraryjars libs/gensee_hb.jar
# -libraryjars libs/gensee_rt_audio.jar
# -libraryjars libs/gensee_rtsdk.jar
# -libraryjars libs/gensee_vod_ol.jar
# -libraryjars libs/gson-2.5.jar
# -libraryjars libs/pinyin4j-2.5.0.jar
 -dontwarn com.gensee.**
 -keep class com.bokecc.sdk.mobile.**{*;}
 -keep class tv.danmaku.ijk.media.**{*;}
 -keep class com.gensee.**{*;}
 -keep class com.java.util.**{*;}
 -keep class rx.**{*;}
#qq登陆相关混淆
 -keep class com.tencent.open.TDialog$*
 -keep class com.tencent.open.TDialog$* {*;}
 -keep class com.tencent.open.PKDialog
 -keep class com.tencent.open.PKDialog {*;}
 -keep class com.tencent.open.PKDialog$*
 -keep class com.tencent.open.PKDialog$* {*;}
 #H5脚本相关
 -keep public class com.huatu.teacheronline.H5DetailActivity$JsInteration {
 	public void skipDirectDetail(java.lang.String);
 }
 -keep public class com.huatu.teacheronline.H5DetailActivity$JsInteration {
 	public void onSumResult(int);
 }
 -keepclassmembers class com.huatu.teacheronline.H5DetailActivity$JsInteration {*;}
 -keepattributes JavascriptInterface
  #极光统计相关
 -keep public class cn.jiguang.analytics.android.api.** {*;}
  #腾讯统计分析相关
 -keep class com.tencent.stat.**  {* ;}
 -keep class com.tencent.mid.**  {* ;}
  #百度统计分析相关
-keep class com.baidu.bottom.** { *; }
-keep class com.baidu.kirin.** { *; }
-keep class com.baidu.mobstat.** { *; }
  #eventbus混淆
-keep class org.greenrobot.eventbus.** { *; }
-dontwarn org.greenrobot.eventbus.**
-keepclassmembers class ** {
    public void onEvent*(**);
    public void onMessageEvent*(**);
    void onEvent*(**);
}

#友盟混淆
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-dontwarn com.facebook.**
-keep public class javax.**
-keep public class android.webkit.**
-dontwarn android.support.v4.**
-keep enum com.facebook.**
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public interface com.facebook.**
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**
-keep class com.android.dingtalk.share.ddsharemodule.** { *; }
-keep public class com.umeng.socialize.* {*;}


-keep class com.facebook.**
-keep class com.facebook.** { *; }
-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**
-keep class com.umeng.socialize.handler.**
-keep class com.umeng.socialize.handler.*
-keep class com.umeng.weixin.handler.**
-keep class com.umeng.weixin.handler.*
-keep class com.umeng.qq.handler.**
-keep class com.umeng.qq.handler.*
-keep class UMMoreHandler{*;}
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements   com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
-keep class im.yixin.sdk.api.YXMessage {*;}
-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}
-keep class com.tencent.mm.sdk.** {
 *;
}
-keep class com.tencent.mm.opensdk.** {
   *;
}
-dontwarn twitter4j.**
-keep class twitter4j.** { *; }

-keep class com.tencent.** {*;}
-dontwarn com.tencent.**
-keep public class com.umeng.com.umeng.soexample.R$*{
public static final int *;
}
-keep public class com.linkedin.android.mobilesdk.R$*{
public static final int *;
}
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}

-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}

-keep class com.sina.** {*;}
-dontwarn com.sina.**
-keep class  com.alipay.share.sdk.** {
   *;
}
-keepnames class * implements android.os.Parcelable {
public static final ** CREATOR;
}

-keep class com.linkedin.** { *; }
-keepattributes Signature

#友盟3.1.1
-dontwarn anetwork.channel.**
-dontwarn org.apache.thrift.**
-dontwarn com.huawei.**


-keep class com.taobao.** {*;}
-keep class org.android.** {*;}
-keep class anet.channel.** {*;}
-keep class com.umeng.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.huawei.** {*;}
-keep class org.apache.thrift.** {*;}

-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}

-keep public class **.R$*{
   public static final int *;
}

#（可选）避免Log打印输出
-assumenosideeffects class android.util.Log {
   public static *** v(...);
   public static *** d(...);
   public static *** i(...);
   public static *** w(...);
 }
#mupdf相关
-keep class com.artifex.** {*;}

#小能
-dontwarn cn.xiaoneng.**
-dontwarn android.support.v4xn.**
-dontwarn org.fusesource.**
-dontwarn net.sf.retrotranslator.**
-dontwarn edu.emory.mathcs.backport.java.util.**
-keep class cn.xiaoneng.** {*;}
-keep class android.support.v4xn.** {*;}
-keep class org.fusesource.** {*;}
-keep class net.sf.retrotranslator.** {*;}
-keep class edu.emory.mathcs.backport.java.util.** {*;}
-ignorewarnings

#百家云混淆规则
-dontwarn com.baijiahulian.**
-dontwarn com.bjhl.**
-keep public class com.baijiahulian.**{*;}
-keep public class com.bjhl.**{*;}
-keep public class com.baijia.**{*;}
-keep public class com.baijiayun.**{*;}
# 点播SDK
-keep class tv.danmaku.ijk.**{*;}
# RxJava混淆规则
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}


#bean
-keep public class com.huatu.teacheronline.direct.bean.**{*;}

#okgo
-dontwarn com.lzy.okgo.**
-keep class com.lzy.okgo.**{*;}

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

#dbflow
-keep class * extends com.raizlabs.android.dbflow.config.DatabaseHolder { *; }

#PictureSelector 2.0
-keep class com.luck.picture.lib.** { *; }

-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

 #rxjava
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
 long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#rxandroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

#switchbutton
-keep class com.kyleduo.switchbutton.** {*;}

#brvah
-keep class com.chad.library.adapter.** {*;}
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers  class **$** extends com.chad.library.adapter.base.BaseViewHolder {
     <init>(...);
}


-keep class com.github.LuckSiege.** {*;}
-keep class com.github.dmytrodanylyk.** {*;}
-keep class com.aohanyao.transformer.library.** {*;}
-keep class com.luck.picture.lib.** {*;}

-keep class com.huatu.teacheronline.personal.db.** {*;}

#mp4parser
-keep class com.coremedia.iso.** { *; }
-keep interface com.coremedia.iso.** { *; }

-keep class com.mp4parser.** { *; }
-keep interface com.mp4parser.** { *; }

-keep class com.googlecode.mp4parser.** { *; }
-keep interface com.googlecode.mp4parser.** { *; }
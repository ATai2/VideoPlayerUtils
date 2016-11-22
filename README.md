# VideoPlayerUtils
exoplayer自定义包装lib

链接地址：http://wear.techbrood.com/guide/topics/media/exoplayer.html


在安卓设备上播放视频和音乐是一种流行的活动。Android框架提供的MediaPlayer播放媒体以最少的代码快速的解决方案，并mediacodec与MediaExtractor类提供定制的媒体播放器。开放源代码项目，ExoPlayer，是两者之间的一个解决方案，提供预建的播放器，你可以延长。
ExoPlayer支持目前提供的媒体播放器的功能，包括动态自适应流HTTP（DASH），smoothstreaming和常见的加密。它的设计是很容易定制和扩展，允许许多组件被替换与自定义实现。因为能力是一个库，包括在您的应用程序，它可以很容易地更新，随着你的应用程序。
注：ExoPlayer是一个开源项目，是不是Android框架的一部分，是从Android SDK分别分布。访问下面的链接，找出更多。
*ExoPlayer的项目主页。  http://google.github.io/ExoPlayer/
*开发指南-提供了丰富的信息来帮助你开始。   http://google.github.io/ExoPlayer/guide.html
*GitHub项目包含源代码，以及演示应用程序。   https://github.com/google/ExoPlayer

本指南描述了ExoPlayer库及其应用。它是指在演示应用程序中的代码，以提供具体的例子。该指南涉及使用ExoPlayer的利弊。它显示了如何使用ExoPlayer玩短跑，smoothstreaming和HLS自适应流，如MP4，M4A，fMP4，WebM，MKV，MP3、OGG、WAV、MPEG、将格式，FLV和ADT（AAC）。还讨论了ExoPlayer事件、消息、定制和支持DRM。

#利弊
ExoPlayer已建立Android MediaPlayer数优势：
动态自适应HTTP流的支持（DASH）和smoothstreaming，既不支持的媒体播放器。许多其他格式也支持。查看支持的格式页的详细信息。
先进的电子特性的支持，如正确处理# ext-x-discontinuity标签。
无缝融合的能力，连接和循环介质。
自定义和扩展播放器，以适合您的用例的能力。ExoPlayer是专为此，让许多部件可以自定义实现替换。
轻松地更新您的应用程序与您的应用程序。因为能力是一个库，你在你的应用程序中包含的apk，您可以控制您使用哪个版本，你可以很容易的更新作为一个普通的应用程序的新版本更新内容。
较少的设备特定问题。
在Android 4.3 Widevine常见加密支持（API级别18）和更高的。
重要的是要注意，也有一些缺点：
ExoPlayer的标准音频和视频组件依赖于Android的mediacodec API，这是在Android 4.1（API Level 16）发布。因此，他们不工作的早期版本的机器人。Widevine常见加密可在Android 4.3（API级别18）和更高的。

#库概况
ExoPlayer的核心是exoplayer的接口。一个ExoPlayer暴露了传统高级媒体播放器的功能如媒体缓冲，播放，暂停和寻找。实现的目的是很少的假设
（因此限制了一些限制）正在播放的媒体类型，如何和在哪里存储，以及它是如何呈现。而不是直接执行加载和渲染的媒体，ExoPlayer implementaitons将它的工作委托
给注入的组件，创建或当它准备播放。所有的能力实现共同组成的：

* 一个mediasource定义要发挥媒体、荷载的媒体，并从中加载的媒体可以读。一个mediasource注入通过在播放开始exoplayer.prepare。
渲染器渲染媒体的单个组件。渲染器注入当玩家创建。
* 一个trackselector选择曲目的mediasource提供是由每一个可用的渲染消耗。一个trackselector注入当玩家创建。
* 一个流量控制，控制的时候，mediasource缓冲更多的媒体，和多少媒体缓冲。一个流量控制注入当玩家创建。
该库提供了这些组件的默认实现，为常见的用例，如下面更详细的描述。一个ExoPlayer可以利用这些组件，也可以使用自定义的实现如果不规范行为需要建立。例如，一个自定义的流量控制可以注射，改变球员的缓冲策略，或自定义渲染器可以注射，使用视频编解码器不支持原生的Android。
注入组件的概念，实现了播放器的功能是目前在整个库。在上面列出的组件的默认实现，以进一步注入组件。这允许许多子组件被单独替换与自定义实现。例如，默认mediasource的实现需要一个或多个数据源的工厂是通过构造函数注入。通过提供一个自定义工厂，它可以从一个非标准源或通过一个不同的网络堆栈加载数据。

#入门
对于简单的情况下，开始使用ExoPlayer包括实施步骤：
. 添加Exoplayer作为一个依赖于你的项目。
. 创建一个simpleexoplayer实例。
. 将播放器连接到一个视图（视频输出和用户输入）。
. 准备一个mediasource播放的Player。
. 完成释放。
这些步骤是在下面更详细的概述。一个完整的例子，请参阅ExoPlayer演示应用程序playeractivity。


# 增加ExoPlayer依赖
开始的第一步是要确保在项目的根build.gradle文件。
<code>
repositories {
  jcenter()
}
</code>
接下来添加工具编译ExoPlayer库应用程序模块的build.gradle文件依赖。
compile 'com.google.android.exoplayer:exoplayer:r2.X.X'

# 创建播放器
现在你可以创建一个使用exoplayerfactory ExoPlayer实例。工厂提供了一系列的方法与不同级别的定制创造能力的实例。对于绝大多数情况下使用标准库提供的默认渲染器的实现是足够的。一exoplayerfactory.newsimpleinstance方法的应用。这些方法返回simpleexoplayer，延伸ExoPlayer添加额外的高水平球员的功能。下面的代码创建一个simpleexoplayer例。
<code>
// 1. Create a default TrackSelector
Handler mainHandler = new Handler();
BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
TrackSelection.Factory videoTrackSelectionFactory =
    new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
TrackSelector trackSelector =
    new DefaultTrackSelector(mainHandler, videoTrackSelectionFactory);

// 2. Create a default LoadControl
LoadControl loadControl = new DefaultLoadControl();

// 3. Create the player
SimpleExoPlayer player =
    ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);
</code>


Attaching the player to a view

The ExoPlayer library provides a SimpleExoPlayerView, which encapsulates a PlaybackControlView and a Surface onto which video is rendered. A SimpleExoPlayerView can be included in your application’s layout xml. Binding the player to the view is as simple as:

// Bind the player to the view.
simpleExoPlayerView.setPlayer(player);
If you require fine-grained control over the player controls and the Surface onto which video is rendered, you can set the player’s target SurfaceView, TextureView, SurfaceHolder or Surface directly using SimpleExoPlayer’s setVideoSurfaceView, setVideoTextureView, setVideoSurfaceHolder and setVideoSurface methods respectively. You can use PlaybackControlView as a standalone component, or implement your own playback controls that interact directly with the player. setTextOutput and setId3Output can be used to receive caption and ID3 metadata output during playback.

Preparing the player

In ExoPlayer every piece of media is represented by MediaSource. To play a piece of media you must first create a corresponding MediaSource and then pass this object to ExoPlayer.prepare. The ExoPlayer library provides MediaSource implementations for DASH (DashMediaSource), SmoothStreaming (SsMediaSource), HLS (HlsMediaSource) and regular media files (ExtractorMediaSource). These implementations are described in more detail later in this guide. The following code shows how to prepare the player with a MediaSource suitable for playback of an MP4 file.

// Measures bandwidth during playback. Can be null if not required.
DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
// Produces DataSource instances through which media data is loaded.
DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
    Util.getUserAgent(this, "yourApplicationName"), bandwidthMeter);
// Produces Extractor instances for parsing the media data.
ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
// This is the MediaSource representing the media to be played.
MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri,
    dataSourceFactory, extractorsFactory, null, null);
// Prepare the player with the source.
player.prepare(videoSource);
Once the player has been prepared, playback can be controlled by calling methods on the player. For example setPlayWhenReady can be used to start and pause playback, and the various seekTo methods can be used to seek within the media. If the player was bound to a SimpleExoPlayerView or PlaybackControlView then user interaction with these components will cause corresponding methods on the player to be invoked.

Releasing the player

It’s important to release the player when it’s no longer needed, so as to free up limited resources such as video decoders for use by other applications. This can be done by calling ExoPlayer.release.

MediaSource

In ExoPlayer every piece of media is represented by MediaSource. The ExoPlayer library provides MediaSource implementations for DASH (DashMediaSource), SmoothStreaming (SsMediaSource), HLS (HlsMediaSource) and regular media files (ExtractorMediaSource). Examples of how to instantiate all four can be found in PlayerActivity in the ExoPlayer demo app.

In addition to the MediaSource implementations described above, the ExoPlayer library also provides MergingMediaSource, LoopingMediaSource and ConcatenatingMediaSource. These MediaSource implementations enable more complex playback functionality through composition. Some of the common use cases are described below. Note that although the following examples are described in the context of video playback, they apply equally to audio only playback too, and indeed to the playback of any supported media type(s).

Side-loading a subtitle file

Given a video file and a separate subtitle file, MergingMediaSource can be used to merge them into a single source for playback.

MediaSource videoSource = new ExtractorMediaSource(videoUri, ...);
MediaSource subtitleSource = new SingleSampleMediaSource(subtitleUri, ...);
// Plays the video with the sideloaded subtitle.
MergingMediaSource mergedSource =
    new MergingMediaSource(videoSource, subtitleSource);
Seamlessly looping a video

A video can be seamlessly looped using a LoopingMediaSource. The following example loops a video indefinitely. It’s also possible to specify a finite loop count when creating a LoopingMediaSource.

MediaSource source = new ExtractorMediaSource(videoUri, ...);
// Loops the video indefinitely.
LoopingMediaSource loopingSource = new LoopingMediaSource(source);
Seamlessly playing a sequence of videos

ConcatenatingMediaSource enables sequential playback of two or more individual MediaSources. The following example plays two videos in sequence. Transitions between sources are seamless. There is no requirement that the sources being concatenated are of the same format (e.g. it’s fine to concatenate a video file containing 480p H264 with one that contains 720p VP9). The sources may even be of different types (e.g. it’s fine to concatenate a video with an audio only stream).

MediaSource firstSource = new ExtractorMediaSource(firstVideoUri, ...);
MediaSource secondSource = new ExtractorMediaSource(secondVideoUri, ...);
// Plays the first video, then the second video.
ConcatenatingMediaSource concatenatedSource =
    new ConcatenatingMediaSource(firstSource, secondSource);
Advanced composition

It’s possible to further combine composite MediaSources for more unusual use cases. Given two videos A and B, the following example shows how LoopingMediaSource and ConcatenatingMediaSource can be used together to loop the sequence (A,A,B) indefinitely.

MediaSource firstSource = new ExtractorMediaSource(firstVideoUri, ...);
MediaSource secondSource = new ExtractorMediaSource(secondVideoUri, ...);
// Plays the first video twice.
LoopingMediaSource firstSourceTwice = new LoopingMediaSource(firstSource, 2);
// Plays the first video twice, then the second video.
ConcatenatingMediaSource concatenatedSource =
    new ConcatenatingMediaSource(firstSourceTwice, secondSource);
// Loops the sequence indefinitely.
LoopingMediaSource compositeSource = new LoopingMediaSource(concatenatedSource);
The following example is equivalent, demonstrating that there can be more than one way of achieving the same result.

MediaSource firstSource = new ExtractorMediaSource(firstVideoUri, ...);
MediaSource secondSource = new ExtractorMediaSource(secondVideoUri, ...);
// Plays the first video twice, then the second video.
ConcatenatingMediaSource concatenatedSource =
    new ConcatenatingMediaSource(firstSource, firstSource, secondSource);
// Loops the sequence indefinitely.
LoopingMediaSource compositeSource = new LoopingMediaSource(concatenatedSource);
It is important to avoid using the same MediaSource instance multiple times in a composition, unless explicitly allowed according to the documentation. The use of firstSource twice in the example above is one such case, since the Javadoc for ConcatenatingMediaSource explicitly states that duplicate entries are allowed. In general, however, the graph of objects formed by a composition should be a tree. Using multiple equivalent MediaSource instances in a composition is allowed.

Player events

During playback, your app can listen for events generated by ExoPlayer that indicate the overall state of the player. These events are useful as triggers for updating the app user interface such as playback controls. Many ExoPlayer components also report their own component specific low level events, which can be useful for performance monitoring.

High level events

ExoPlayer allows instances of ExoPlayer.EventListener to be added and removed using its addListener and removeListener methods. Registered listeners are notified of changes in playback state, as well as when errors occur that cause playback to fail.

Developers who implement custom playback controls should register a listener and use it to update their controls as the player’s state changes. An app should also show an appropriate error to the user if playback fails.

When using SimpleExoPlayer, additional listeners can be set on the player. In particular setVideoListener allows an application to receive events related to video rendering that may be useful for adjusting the UI (e.g. the aspect ratio of the Surface onto which video is being rendered). Other listeners can be set to on a SimpleExoPlayer to receive debugging information, for example by calling setVideoDebugListener and setAudioDebugListener.

Low level events

In addition to high level listeners, many of the individual components provided by the ExoPlayer library allow their own event listeners. You are typically required to pass a Handler object to such components, which determines the thread on which the listener’s methods are invoked. In most cases, you should use a Handler associated with the app’s main thread.

Sending messages to components

Some ExoPlayer components allow changes in configuration during playback. By convention, you make these changes by passing messages through the ExoPlayer to the component, using the sendMessages or blockingSendMessages methods. This approach ensures both thread safety and that the configuration change is executed in order with any other operations being performed on the player.

Customization

One of the main benefits of ExoPlayer over Android’s MediaPlayer is the ability to customize and extend the player to better suit the developer’s use case. The ExoPlayer library is designed specifically with this in mind, defining a number of interfaces and abstract base classes that make it possible for app developers to easily replace the default implementations provided by the library. Here are some use cases for building custom components:

Renderer – You may want to implement a custom Renderer to handle a media type not supported by the default implementations provided by the library.
Extractor – If you need to support a container format not currently supported by the library, consider implementing a custom Extractor class, which can then be used to together with ExtractorMediaSource to play media of that type.
MediaSource – Implementing a custom MediaSource class may be appropriate if you wish to obtain media samples to feed to renderers in a custom way, or if you wish to implement custom MediaSource compositing behavior.
TrackSelector – Implementing a custom TrackSelector allows an app developer to change the way in which tracks exposed by a MediaSource are selected for consumption by each of the available Renderers.
DataSource – ExoPlayer’s upstream package already contains a number of DataSource implementations for different use cases. You may want to implement you own DataSource class to load data in another way, such as over a custom protocol, using a custom HTTP stack, or through a persistent cache.
Customization guidelines

If a custom component needs to report events back to the app, we recommend that you do so using the same model as existing ExoPlayer components, where an event listener is passed together with a Handler to the constructor of the component.
We recommended that custom components use the same model as existing ExoPlayer components to allow reconfiguration by the app during playback, as described in Sending messages to components. To do this, you should implement an ExoPlayerComponent and receive configuration changes in its handleMessage method. Your app should pass configuration changes by calling ExoPlayer’s sendMessages and blockingSendMessages methods.
Digital Rights Management

On Android 4.3 (API level 18) and higher, ExoPlayer supports Digital Rights Managment (DRM) protected playback. In order to play DRM protected content with ExoPlayer, your app must inject a DrmSessionManager when instantiating the player. ExoPlayerFactory provides factory methods allowing this. A DrmSessionManager object is responsible for providing DrmSession instances, which provide MediaCrypto objects for decryption as well as ensuring that the required decryption keys are available to the underlying DRM module being used.

The ExoPlayer library provides a default implementation of DrmSessionManager, called StreamingDrmSessionManager, which uses MediaDrm. The session manager supports any DRM scheme for which a modular DRM component exists on the device. All Android devices are required to support Widevine modular DRM (with L3 security, although many devices also support L1). Some devices may support additional schemes such as PlayReady. All Android TV devices support PlayReady.

PlayerActivity in the ExoPlayer demo app demonstrates how a DrmSessionManager can be created and injected when instantiating the player.

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
*一个trackselector选择曲目的mediasource提供是由每一个可用的渲染消耗。一个trackselector注入当玩家创建。
*一个流量控制，控制的时候，mediasource缓冲更多的媒体，和多少媒体缓冲。一个流量控制注入当玩家创建。
该库提供了这些组件的默认实现，为常见的用例，如下面更详细的描述。一个ExoPlayer可以利用这些组件，也可以使用自定义的实现如果不规范行为需要建立。例如，一个自定义的流量控制可以注射，改变球员的缓冲策略，或自定义渲染器可以注射，使用视频编解码器不支持原生的Android。
注入组件的概念，实现了播放器的功能是目前在整个库。在上面列出的组件的默认实现，以进一步注入组件。这允许许多子组件被单独替换与自定义实现。例如，默认mediasource的实现需要一个或多个数据源的工厂是通过构造函数注入。通过提供一个自定义工厂，它可以从一个非标准源或通过一个不同的网络堆栈加载数据。

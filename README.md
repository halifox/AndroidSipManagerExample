# Android SIP (android.net.sip.SipManager) 示例

## 简介

这是一个基于Android平台实现SIP（会话发起协议）功能的简单Demo，展示了如何在Android应用中接入和处理SIP音频通话。通过该示例，可以了解如何使用Android的SIP API进行呼叫接入、监听、管理SIP通话等功能。

> **注意**：该功能已在API Level 31及之后版本中被弃用，开发者请谨慎使用。建议开发者对其替代方案进行评估，以确保未来应用的兼容性和稳定性。

## 重要说明

### 1. Deprecated in API Level 31

`SipManager`和相关SIP API已在Android API Level 31中被弃用。如果您正在开发新的应用，建议考虑使用其他替代技术（例如WebRTC或其他第三方库）进行音视频通话的实现。

### 2. `takeAudioCall`方法行为不符合文档描述

在官方文档中，`takeAudioCall`方法的行为描述为：

> "Before the call is returned, the listener will receive a `SipAudioCall.Listener#onRinging` callback."

然而，根据源代码，`takeAudioCall`方法中的`callbackImmediately`参数被设置为`false`，这意味着`onRinging`回调在呼叫被返回之前不会触发。

源代码分析如下：

```java
public SipAudioCall takeAudioCall(Intent incomingCallIntent, SipAudioCall.Listener listener) throws SipException {
    ...
    call.setListener(listener);
    ...
}

public void setListener(SipAudioCall.Listener listener) {
    setListener(listener, false);  // callbackImmediately = false
}

public void setListener(SipAudioCall.Listener listener, boolean callbackImmediately) {
    if ((listener == null) || !callbackImmediately) {
        // 不做任何处理
    } 
}
```

由此可见，`callbackImmediately`参数为`false`，因此在接听电话之前，不会触发`onRinging`回调。这与官方文档中的描述不一致，开发者需要对此有所了解。

### 3. SipManager 仅支持 RTP/AVP 协议。

```java
private SimpleSessionDescription createAnswer(String offerSd) {
    ...
    if ((codec == null) && (media.getPort() > 0)
            && "audio".equals(media.getType())
            && "RTP/AVP".equals(media.getProtocol())) {
        ...
        Media reply = answer.newMedia(
                "audio", mAudioStream.getLocalPort(), 1, "RTP/AVP");
        reply.setRtpPayload(codec.type, codec.rtpmap, codec.fmtp);
        ...
    }
}
```

### 4. SipManager 存在诸多问题

`SipManager`有很多已知的问题和限制，导致其无法在某些场景下正常使用。这些问题包括但不限于：

- API不一致，部分功能无法按预期工作。
- 对音视频的支持有限，不能满足复杂需求。
- 系统稳定性差，容易出现崩溃和卡顿等问题。

因此，强烈推荐开发者考虑使用第三方库来替代`SipManager`，其中 **Linphone Android SDK** 是一个优秀的选择，提供了更稳定且功能更全的SIP支持，能够更好地满足音视频通话的需求。

### 5. 示例未开发完成

需要注意的是，该示例仅展示了SIP通话的基本功能，并未涵盖完整的SIP流程或处理各种网络和媒体流的情况。因此，开发者在实际应用中需要进一步补充完整的功能和错误处理。
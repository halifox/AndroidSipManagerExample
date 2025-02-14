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
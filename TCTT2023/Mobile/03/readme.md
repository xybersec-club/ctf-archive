# Write-ups for TCTT2023/Mobile/03

## Flag pattern

`CTT23{xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx}`

## Challenge Files

[mflag.zip](./mflag.zip)

## Solution

1. Start by opening [AVD](https://developer.android.com/studio/run/managing-avds) in Android Studio.

![AVD_result_01](./write-ups/01.png)

2. Install the [app](./write-ups/mflag.apk) in AVD.

![AVD_result_02](./write-ups/02.png)

3. After launching the app, we can see that it is a simple app with button. But after we press the button, nothing happens. Only the text is shown `The flag was returned but cannot be displayed. Can you help find the flag ???`.

![AVD_result_03](./write-ups/03.png)

![AVD_result_04](./write-ups/04.png)

This make me think that the flag is hidden outside the app, the app should be using some kind of API to get the flag. So I use [burpsuite](https://portswigger.net/burp/communitydownload) to intercept the traffic.

4. To intercept the traffic, we need to set the proxy in AVD. Go to `Settings > Proxy` and set the proxy to your burpsuite proxy. Also we need to start the intercept in burpsuite.

![AVD_result_05](./write-ups/05.png)

![AVD_result_06](./write-ups/06.png)

5. After pressing the button, we can see that there is a request to `http://18.143.235.72/getmflag`

![AVD_result_07](./write-ups/07.png)

![AVD_result_08](./write-ups/08.png)

6. I use `curl` to send the request to the server again.

![curl_result](./write-ups/09.png)

7. The result is `CTT23{c00l_int3rc3pt0r}`

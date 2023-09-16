# Write-ups for TCTT2023/Network/05

## Flag pattern

`CTT23{xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx}`

## Challenge Files

[SOMEWHERE.zip](./SOMEWHERE.zip)

## Solution

1. First, Let open the pcap file with wireshark and analyze it with `Statistics > Protocol Hierarchy`.

![wireshark_result_1](./write-ups/01.png)

![wireshark_result_2](./write-ups/02.png)

2. It look like there is a lot of UDP request, but most them are ICMP and DNS. So we need to filter them out with `!icmp && !dns`.

![wireshark_result_3](./write-ups/03.png)

3. Let follow the TCP stream of the first packet.

![wireshark_result_4](./write-ups/04.png)

![wireshark_result_5](./write-ups/05.png)

4. Let look at the stream number `3`, you can find the png file is sent over this stream.

![wireshark_result_6](./write-ups/06.png)

5. Let switch to `server` conversion mode, change show data as to `RAW` and save the data as [raw file](./write-ups/raw).

![wireshark_result_7](./write-ups/07.png)

![wireshark_result_8](./write-ups/08.png)

6. Upload [raw file](./write-ups/raw) to [online hex editor](https://hexed.it/).

![hex_edit_01](./write-ups/09.png)

Remove the content before `89 50 4E 47`.

![hex_edit_02](./write-ups/10.png)

And save it to [secret.png](./write-ups/secret.png)

![hex_edit_02](./write-ups/11.png)

![secret.png](./write-ups/secret.png)

7. The result is `CTT23{SOME_TIME_ESAY_SOME_THING_ESAY.}`

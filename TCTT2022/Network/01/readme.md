# Write-ups for TCTT2022/Network/01

## Flag pattern

`TCTT2022{xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx}`

## Challenge Files

[network-challenge01](./network-challenge01.zip)

## Solution

1. First, Let open the pcap file with wireshark and analyze it with `Statistics > Protocol Hierarchy`.

![wireshark_result_1](./write-ups/01.png)

![wireshark_result_2](./write-ups/02.png)

2. It look like there is a lot of FTP request, so export the FTP data with `File > Export Objects > FTP-DATA`.

![wireshark_result_3](./write-ups/03.png)

3. We found [secret.zip](./write-ups/secret.zip), so let save it.

![wireshark_result_4](./write-ups/04.png)

4. Trying to unzip it, but it need a password.

![wireshark_result_5](./write-ups/05.png)

5. Let crack it with fcrackzip and rockyou wordlist with this command.

```bash
fcrackzip -u -D -p <PATH-TO-WORDLIST> <PATH-TO-ZIP-FILE>
```

![fcrackzip_result](./write-ups/06.png)

As you can see, the password is `password`.

6. We use previous password to unzip the zip file and we got [secret](./write-ups/secret).

![secret_file](./write-ups/07.png)

7. We `cat` that file, and the result is `tctt2022{Steal_Data_via_FTP}`

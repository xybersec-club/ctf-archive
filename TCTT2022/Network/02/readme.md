# Write-ups for TCTT2022/Network/02

## Flag pattern

`TCTT2022{xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx}`

## Challenge Files

[network-challenge02](./network-challenge02.zip)

## Solution

1. First, Let open the pcap file with wireshark and analyze it with `Statistics > Protocol Hierarchy`.

![wireshark_result_1](./write-ups/01.png)

![wireshark_result_2](./write-ups/02.png)

2. It look like there is a lot of Telnet request, so filter it with `Right Click > Apply as Filter > Selected`.

![wireshark_result_3](./write-ups/03.png)

3. We sort the results by length.

![wireshark_result_4](./write-ups/04.png)

4. Then, we follow the TCP stream of the longest one.

![wireshark_result_5](./write-ups/05.png)

5. And that is the TCP stream no.20128, which looks like SSH connection.

![wireshark_result_6](./write-ups/06.png)

6. We can see that, they are doing something with secret.zip

![wireshark_result_7](./write-ups/07.png)

7. Let go to the next stream and you can see that the stream is starting with `PK` which is the signature of zip file.

![wireshark_result_8](./write-ups/08.png)

8. If you want to save the zip file, you can do it by changing the stream to raw data and save it as zip file.

![wireshark_result_9](./write-ups/09.png)

9. We got [secret.zip](./write-ups/secret.zip) file but it has password.

![zip_password](./write-ups/10.png)

10. Let crack it with fcrackzip and rockyou wordlist with this command.

```bash
fcrackzip -u -D -p <PATH-TO-WORDLIST> <PATH-TO-ZIP-FILE>
```

![fcrackzip_result](./write-ups/11.png)

As you can see, the password is `P@ssw0rd`.

11. We use previous password to unzip the zip file and we got [secret](./write-ups/secret).

![secret_file](./write-ups/12.png)

12. We `cat` that file, and the result is `tctt2022{Welcome_R00t_T3ln3t}`

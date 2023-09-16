from cryptography.fernet import Fernet

fm = open("flag.txt", "r")
fs = open("secret.txt", "wb")
fk = open("key.txt", "wb")

message = fm.read()

# สร้างคีย์สำหรับการเข้ารหัส
key = Fernet.generate_key()
fk.write(key)

cipher_suite = Fernet(key)

# เข้ารหัสข้อความ
encrypted_message = cipher_suite.encrypt(message.encode())
fs.write(encrypted_message)

fm.close()
fs.close()
fk.close()

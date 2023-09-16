from cryptography.fernet import Fernet

fs = open("secret.txt", "r")
fk = open("key.txt", "r")

keys = fk.read().split()
encrypted_data = fs.read()

for key in keys:
    try:
        cipher = Fernet(key)
        decrypted_data = cipher.decrypt(encrypted_data)
        print(decrypted_data.decode('utf-8'))
    except:
        pass

fs.close()
fk.close()

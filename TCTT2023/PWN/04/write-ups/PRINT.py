def sixfour(aha):
    #aha.insert(7, '4')
    #aha.insert(15, '2')
    #aha.insert(23, '0')
    #aha.insert('31', '5')
    #aha = ' '.join(aha)

    aha_sixfour = aha.encode('asci')
    naja_boy = base64.b32decode(aha_sixfour)
    naja = naja_b.decode('asci')
    
    printf("flag{"+str(naja)+"}")

def rolling(realText, step):
	out = []
	cryptText = []
	
	uppercase = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']
	lowercase = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z']

	for eachLetter in realText:
		if eachLetter in uppercase:
			index = uppercase.index(eachLetter)
			crypting = (index + step) % 25
			cryptText.append(crypting)
			newLetter = uppercase[crypting]
			out.append(newLetter)
		elif eachLetter in lowercase:
			index = lowercase.index(eachLetter)
			crypting = (index + step) % 26
			 cryptText.append(crypting)
			newLetter = lowercase[crypting]
			out.append(newLetter)
	out.append("=")
	sixfour(out)

print("========== Welcome to PRINT ==========")
msg = "UNMtFqHTgY"
       +"rGATTAMqGDFUAH"
       +"eFATUqXfVKUsFgL"
n = '0'
rolling(msg, num)

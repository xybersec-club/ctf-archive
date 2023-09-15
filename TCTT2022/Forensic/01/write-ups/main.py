import re

# Open the file for reading
with open("challenge.txt", "r") as file:
    # Read the content of the file into input_text
    input_text = file.read()

print(input_text)

# Replace '\t' with 0
input_text = input_text.replace('\t', '0')

# Replace '\n' with 1
input_text = input_text.replace('\n', '1')

# Replace all characters that are not '0' or '1' with an empty string
input_text = re.sub(r'[^01]', '', input_text)

print(input_text)
In this repo, we put together a comparision between base64 encoding, blowfish, AES and cha-cha encryption methods. For each methods, we have the options to include padding and as well as choose block patterns.

## Glossary of some methods:
The base64 method is not really an encryption but rather it is a technique to convert the string into a medium where other systems are able to recognise it without loss in it. I have added it here just to compare it as a way of masking our strings, which might not be understandable by a very simple user. 

Shifting is a another form of encoding, where we we just shift the values of our characters to a certain extent, so that user see intangible characters. You do have to store the shifting amount, which might be accessible by hacker during reverse engineering.

XOR method is a form of encryption used to encrypt data and is difficult to break by brute force, i.e. generating random encryption keys to match the correct one. It is infact used by many encryption algorithms under the hood. With this logic, by applying the bitwise XOR operator to any character using a given key, a string of text can be encrypted. To decrypt the output, the cipher will be extracted by simply reapplying the XOR function with the key.

The chacha method is only available for Android by default natively from SDK 28 but we still kept it here because it's known to be reasonably fast.

AESCBC is a custom method that used some best practices for encryption as mentioned in here.

## Testing conditions
Device Info: Mi A3, Android 11

Note: I did not run the app in a unit test because I wanted to see how encryption happens in a close to real world scenario.

# Test 1:
Iterations: 10000

Text length: 307

As can be seen from the graph the fastest method for encoding using base64. Shifting does take quite a lot of a lot amount of time. XOR method took a lot of time which I believe is due to not using native methods. The fastest method for encryption is chacha20. AES/CTR/NoPadding is pretty fast. 

As for the decryption graph chacha decryption is even faster than decoding base64.

# Test 2:
Longer Strings Like a blog article: 6684 

Iterations: 100

Base64 encoding still the fastes to encode. However, for encryption, AES/CTR/NoPadding is a bit faster for encrypting and decrypting. Chacha is 3rd position. 

# Test 3:
Short String: 16
Iterations: 10000
For encryption and decryption of a very short text, it seems AES/CTR/NoPadding and Chacha20 are not that well suited. I am sure cryptography experts can explain to me why that is the case, which I assume is because of some kind of overhead that is negligible in longer strings. 
So in case of encrypting and decrypting a short string, AES/OFB/NoPadding or Blowfish/CTR/Nopadding would be much better.

## Conclusion
I think based on my understanding and performance analysis, AES/CTR/NoPadding is a good choice for all android versions. If we are supporting SDK 28 and up then we can utilise Chacha Algorithm, which is very very impressive. If saving the encryption is the most important thing, then one should consider asssymettric encryption like RSA or others. 
However, even for symmetric encryption, keeping the passphrase secure in the most important part. Lets see in the next section ways we can improve that. 

## Tips for storing the passphrase
- In order for hackers not to obtain them using password stuffing or brute-forcing, keys should be complicated and well-protected. To distribute keys and store keys and keep them out of the wrong hands, you should consider a secure process. Keys should not be transmitted over the network in plain text or stored as a string in the app.
- Instead of using a string as a passphrase, use a value that is unique to the local device, so that hackers would not detect that particular values as a passphrase. 
- Better yet, use the local statis value, apply some other encoding or encryption method and then use that converted string as a passphrase for the actual encryption 
- Store the passphrase in keystore

Thanks for reading my article.
Would love to see how the performance changes with different devices and OSs. 

"""Static APK checks only. Does not install, execute, sign, or contact a device."""
import argparse, hashlib, json, pathlib, struct, zipfile, zlib
p=argparse.ArgumentParser();p.add_argument("apk",type=pathlib.Path);p.add_argument("--public",action="store_true");a=p.parse_args()
data=a.apk.read_bytes()
with zipfile.ZipFile(a.apk) as z:
 assert z.testzip() is None
 names=z.namelist();dex=z.read("classes.dex")
 assert dex[:8]==b"dex\n035\x00"
 assert struct.unpack_from("<I",dex,8)[0]==zlib.adler32(dex[12:])&0xffffffff
 assert dex[12:32]==hashlib.sha1(dex[32:]).digest()
 assert "resources.arsc" in names and "AndroidManifest.xml" in names
 image=any(n.lower().endswith((".png",".jpg",".jpeg",".webp")) for n in names)
 if a.public:assert not image,"Public build must contain no private/custom images"
 assert not any(n.startswith("lib/") for n in names)
 assert not any(n.upper().startswith("META-INF/") and n.upper().endswith((".RSA",".SF",".EC",".DSA")) for n in names)
e=data.rfind(b"PK\x05\x06");cd=struct.unpack_from("<I",data,e+16)[0];assert data[cd-16:cd]!=b"APK Sig Block 42"
print(json.dumps({"sha256":hashlib.sha256(data).hexdigest(),"size":len(data),"DEX":"035","DEX_checksums":"pass","ZIP_CRC":"pass","custom_images":image,"native_libraries":False,"signature":"unsigned","runtime_test":"not performed"},indent=2))

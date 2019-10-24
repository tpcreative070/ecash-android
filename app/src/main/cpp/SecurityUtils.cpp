#include <jni.h>
#include <string>
extern "C"
JNIEXPORT jstring JNICALL
Java_ecpay_ewallet_common_utils_SecurityNDK_getPrivateKey(JNIEnv *env, jclass type){
    std::string privateKey = "AIEGIA+wEeww8G/u3FF2/839hgTYb+8xD0gWo3X/Me/+";
    return env->NewStringUTF(privateKey.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_ecpay_ewallet_common_utils_SecurityNDK_getPublicKey(JNIEnv *env, jclass type) {
    std::string publicKey = "BPKwnS/5ZO9EQTgIh4SsJfdDnjTaUltcatm6lMl8AH/n4K47E7cW5bIAmE5aI7C9KwmcUqsvccV9tUWY7wBuauM=";
    return env->NewStringUTF(publicKey.c_str());
}

# Gradle打包流程

大概分为以下几个步骤： 
 
1、使用aapt工具将res资源文件生成R.java文件  
2、使用aidl工具将aidl文件生成对应java文件  
3、使用javac命令编译工程源代码和上面两步生成的文件，生成class文件  
使用proguard进行资源优化和混淆（可选）  
4、通过dex工具将class文件和第三方jar包打成dex文件  
5、用aapt工具将res下的资源文件编译成二进制文件，然后将其和上一步中的dex文件以及assets中的文件通过apkbuilder工具打包成apk文件  
6、通过jarSigner对apk进行签名  
7、利用zipalign工具对apk进行字节对齐优化操作
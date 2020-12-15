# Gradle构建和打包流程

## 构建流程

>Gradle的实质是配置脚本，执行一种类型的配置脚本时就会创建一个关联的对象，譬如执行build.gradle脚本就会创建一个Project对象，这个对象其实就是Gradle的代理对象

**1、settings.gradle**

settings.gradle是用于配置整个项目结构的脚本，对应Gradle中的Settings对象，其中最常用的几个方法：

```java
include ':app'
project(':app').projectDir = new File("./...")
```

**2、rootProject/build.gradle**

位于项目根目录的build.gradle脚本，负责项目整体的配置，对应一个Project对象，其中最常用的几个方法：

```java
buildscript { // 配置构建脚本
    repositories { // 构建脚本的仓库
        google()
        jcenter()
    }
    dependencies { // 构建脚本的依赖，默认依赖Android Gradle Plugin，也可以添加自定义的Plugin
        classpath "com.android.tools.build:gradle:3.6.0"
    }
}
allprojects {
    repositories {
        google()
        jcenter()
    }
}
```

**3、mudule/build.gradle**

位于子模块的build.gradle脚本，负责这个模块的配置，也对应一个Project对象，类似根目录的build.gradle。

**Gradle 构建生命周期**

Gradle build的生命周期主要分为三大部分：初始化阶段，配置阶段和执行阶段。

1、初始化阶段：Gradle支持单工程或者多工程构建，初始化阶段的任务是确定有多少工程需要构建，创建整个项目的层次结构，并且为每一个项目创建一个Project实例对象。  
<span>通过解析根目录下settings.gradle脚本，读取include信息，确定有多少个Project需要构建</span>

2、配置阶段：配置阶段的主要任务是生成整个构建过程的有向无环图。确定了所有需要参与构建的工程后，通过读取解析各个工程对应的build.gradle脚本，构造Task任务，并根据Task的依赖关系，生成一个基于Task的有向无环图TaskExecutionGraph。

3、执行阶段：通过读取配置阶段生成有向无环图TaskExecutionGraph，按顺序依此执行各个Task，像流水线一样，一步一步构建整个工程，这也是构建过程中最耗时的阶段。

## 打包流程

大概分为以下几个步骤： 
 
1、通过AAPT工具对资源文件（包括AndroidManifest.xml、布局文件、各种xml资源等）进行打包，生成R.java文件

2、通过AIDL工具处理AIDL文件，生成相应的Java文件

3、使用Javac命令编译工程源代码和上面两步生成的文件，生成class文件(开启混淆会使用proguard进行资源优化和混淆）

4、通过Dex工具将Class文件和第三方Jar包打成dex文件（该过程主要完成Java字节码转换成Dalvik字节码，压缩常量池以及清除冗余信息等工作）

5、用AAPT工具将res下的资源文件编译成二进制文件，然后将其和上一步中的dex文件以及assets中的文件通过ApkBuilder工具打包成apk文件

6、通过KeyStore对Apk进行签名

7、利用ZipAlign工具对apk进行字节对齐优化操作（对齐的过程就是将APK文件中所有的资源文件举例文件的起始距离都偏移4字节的整数倍，这样通过内存映射访问APK文件的速度会更快）


>[https://blog.csdn.net/ithe1001/article/details/109393350](https://blog.csdn.net/ithe1001/article/details/109393350)
# AutoPreload

    repositories {
        maven { url 'https://jitpack.io' }
    }

    kapt "com.github.Jackduhai.AutoPreload:processor:1.0.1.8"
    implementation 'com.github.Jackduhai.AutoPreload:autopreload:1.0.1.8'

    AutoPreload初始化
      Preload.setMultiProcess(true)//是否开启多进程支持 如false则多进程配置无效 init方法调用时不区分进程 会全部调用预加载方法
      Preload.init(application)


# 普通对象预加载
    @AutoPreload(process = ":p2") //声明多进程配置生效时 只在:p2进程中初始化
    public class Load2 {
        @LoadMethod
        public void loadMyMessagePre2(){
            System.out.println("==========loadMyMessagePre2============");
        }
    }


# 单例对象预加载
    @AutoPreload //不配置进程名则默认在主进程中初始化
    object LoadNews {
        @LoadMethod
        fun loadMyMessagePre(){
            println("==========loadMyMessagePre============")
        }
    }

# MemoryCache配套使用 可以进行预加载缓存 effectTime put时可设置生效时间 默认5分钟 如需永久有效则设置成-1
    MemoryCache.put("","")
    MemoryCache.get("")
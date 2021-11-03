# AutoPreload

    repositories {
        maven { url 'https://jitpack.io' }
    }

    kapt "com.github.Jackduhai.AutoPreload:processor:1.0.1.8"
    implementation 'com.github.Jackduhai.AutoPreload:autopreload:1.0.1.8'

    AutoPreload初始化
      Preload.init(application)


#普通对象预加载
    @AutoPreload
    public class Load2 {
        @LoadMethod
        public void loadMyMessagePre2(){
            System.out.println("==========loadMyMessagePre2============");
        }
    }


#单例对象预加载
    @AutoPreload
    object LoadNews {
        @LoadMethod
        fun loadMyMessagePre(){
            println("==========loadMyMessagePre============")
        }
    }

#MemoryCache配套使用 可以进行预加载缓存
    MemoryCache.put("","")
    MemoryCache.get("")
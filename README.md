# AutoPreload 内部使用kotlin协程进行高效调用，避免开启多线程造成的资源浪费，并且可以与activity或fragment进行生命周期绑定，当监听到对应的组件开启会进行自动调用

    repositories {
        maven { url 'https://jitpack.io' }
    }

    kapt "com.github.Jackduhai.AutoPreload:processor:1.0.1.16"
    implementation 'com.github.Jackduhai.AutoPreload:autopreload:1.0.1.16'

    //需要导入kotlin协程包
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2'

    AutoPreload初始化
      Preload.setMultiProcess(true)//是否开启多进程支持 如false则多进程配置无效 init方法调用时不区分进程 会全部调用预加载方法
      Preload.init(application)


# 普通对象预加载
    @AutoPreload(process = ":p2") //声明多进程配置生效时 只在:p2进程中初始化
    public class Load2 {
        @LoadMethod(threadMode = ThreadMode.MAIN)//声明是否在ui线程初始化
        public void loadMyMessagePre2(){
            System.out.println("==========loadMyMessagePre2============");
        }
    }


# 单例对象预加载
    @AutoPreload //不配置进程名则默认在主进程中初始化
    object LoadNews {
        @LoadMethod(threadMode = ThreadMode.BACKGROUND)
        fun loadMyMessagePre(){
            println("==========loadMyMessagePre============")
        }
    }

#以上是在Application初始化时需要直接启动的配置方式，下面介绍一下绑定生命周期的配置方式
 框架支持Activity和fragment生命周期感知能力，配置好target后可以自动进行资源的管理完全与fragment和activity进行分离，如不配置则默认在application初始化时进行调用
 demo如下
 
      @AutoPreload(process = ":p2",target = "com.jack.autopreload.MainActivity")//target是需要绑定的fragment或activity全路径
      object LoadNews {

        @TargetInject                       //自动注入 此注解会自动注入target对应的组件对象，会自动进行维护不需要手动维护，当生命周期结束时会自动null
        public var context: AppCompatActivity? = null

         @LoadMethod(threadMode = ThreadMode.MAIN)  //生命周期开始时会自动调用loadMethod注解的方法 同样可以对线程类型进行声明
         fun loadMyMessagePre(){
             println("${this}==========LoadNews============${Thread.currentThread().name}")
         }

         @CleanMethod(threadMode = ThreadMode.MAIN) //当生命周期结束时会自动调用cleanMethod注解的方法并且按照threadMode执行，可以将需要释放的资源放在此方法中进行维护
         fun cleanTwo(){
             println("${this}========cleanTwo=========${Thread.currentThread().name}")
         }

     }

 非单例的demo如下

     @AutoPreload(target = "com.jack.autopreload.SettingsFragment",process = ":p2")
     class LoadFragment {

        @TargetInject
        public var fragment : Fragment? = null

        @LoadMethod(threadMode = ThreadMode.BACKGROUND)
        fun loadFragmentPre(){
            println("${this}==========LoadFragment============${Thread.currentThread().name}")
        }

        @CleanMethod(threadMode = ThreadMode.MAIN)
        fun cleanFragment(){
            println("${this}========cleanLoadFragment=========${Thread.currentThread().name}")
        }

    }

# MemoryCache配套使用 可以进行预加载缓存 effectTime put时可设置生效时间 默认5分钟 如需永久有效则设置成-1
    MemoryCache.put("","")
    MemoryCache.get("")

# 混淆规则
    -keep class com.jack.auto.** { *; }

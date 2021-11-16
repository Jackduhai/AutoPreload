package com.jack.processor

import com.google.auto.service.AutoService
import com.jack.annotation.AutoPreload
import com.jack.annotation.CleanMethod
import com.jack.annotation.Const.Companion.AUTO_CODE_PACKAGE
import com.jack.annotation.Const.Companion.LOAD_CLASS
import com.jack.annotation.InvokeBase
import com.jack.annotation.LoadMethod
import com.jack.processor.util.PrivateMethodException
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.lang.Exception
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
import javax.tools.Diagnostic
import kotlin.io.path.Path
import kotlin.properties.Delegates

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8) // to support Java 8
@SupportedOptions("kapt.kotlin.generated")
class PreloadProcessor : AbstractProcessor(){

    private var messager : Messager? = null
    private var filer: Filer by Delegates.notNull()
    val context = ClassName("android.content", "Context")
    val processUtil = ClassName("com.jack.library.utils", "ProcessUtil")
    val preload = ClassName("com.jack.library", "Preload")
    val threadModel = ClassName("com.jack.annotation", "ThreadMode")
    val dispatcher = ClassName("kotlinx.coroutines", "Dispatchers")
    val globalScope = ClassName("kotlinx.coroutines", "GlobalScope")
    val launch = ClassName("kotlinx.coroutines", "launch")
    val activity = ClassName("android.app", "Activity")
    val fragment = ClassName("androidx.fragment.app", "Fragment")

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        filer = processingEnv.filer
        messager = processingEnv.messager

    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return hashSetOf(AutoPreload::class.java.canonicalName)
    }

    override fun process(annotations: MutableSet<out TypeElement>?,roundEnv: RoundEnvironment?): Boolean {
        val elements = roundEnv?.getElementsAnnotatedWith(AutoPreload::class.java)
        if(elements.isNullOrEmpty()){
            return false
        }
        val builder = createFileBuilder()
        builder.addType(createFun(annotations,roundEnv).build())
        createFile(builder)
       return false
    }

    fun createFun(annotations: MutableSet<out TypeElement>?,roundEnv: RoundEnvironment?):TypeSpec.Builder{
        val typeSpec = TypeSpec.classBuilder(LOAD_CLASS)
//            .addSuperinterface(InvokeBase::class)
        //保存非单例对象类路径
        typeSpec.addProperty(PropertySpec.builder("map",LinkedHashMap::class.asClassName()
            .parameterizedBy(String::class.asClassName(),String::class.asClassName()),KModifier.PRIVATE)
            .initializer("LinkedHashMap<String,String>()").build())
        //保存非单例对象的调用方法
        typeSpec.addProperty(PropertySpec.builder("mapFunctionLoad",LinkedHashMap::class.asClassName()
            .parameterizedBy(String::class.asClassName(),String::class.asClassName()),KModifier.PRIVATE)
            .initializer("LinkedHashMap<String,String>()").build())
        //保存非单例对象的资源清理方法
        typeSpec.addProperty(PropertySpec.builder("mapFunctionClean",LinkedHashMap::class.asClassName()
            .parameterizedBy(String::class.asClassName(),String::class.asClassName()),KModifier.PRIVATE)
            .initializer("LinkedHashMap<String,String>()").build())
        typeSpec.addProperty(PropertySpec.builder("mapTempObj",LinkedHashMap::class.asClassName()
                .parameterizedBy(String::class.asClassName(),Pair::class.asClassName()
                    .parameterizedBy(Any::class.asClassName(),Any::class.asClassName())),KModifier.PRIVATE)
                .initializer("LinkedHashMap<String,Pair<Any,Any>>()").build())

        val f = FunSpec.builder("load").addParameter("applicationContext",context)
        f.addStatement("register()")
        f.addStatement("val mainProcess = applicationContext.packageName")
        f.addStatement("var curProcess = %T.getCurrentProcessName(applicationContext)",processUtil)
        f.beginControlFlow("if(!%T.isMultiProcess())",preload)
        f.addStatement("curProcess = \"all\"")
        f.endControlFlow()

        val loadTempCode = CodeBlock.builder()
        loadTempCode.addStatement("val mainProcess = activity.packageName")
        loadTempCode.addStatement("var curProcess = %T.getCurrentProcessName(activity)",processUtil)
        loadTempCode.beginControlFlow("if(!%T.isMultiProcess())",preload)
        loadTempCode.addStatement("curProcess = \"all\"")
        loadTempCode.endControlFlow()

        val fLoadActivity = FunSpec.builder("loadActivity").addParameter("activity",activity)
        val destroyActivity = FunSpec.builder("destroyActivity").addParameter("activity",activity)
        val fLoadFragment = FunSpec.builder("loadFragment").addParameter("fragment",fragment)
        val register = FunSpec.builder("register")

        val elements = roundEnv!!.getElementsAnnotatedWith(AutoPreload::class.java)!!
        //获取注解对应的运行进程名称
        try {
            elements.forEach {annotatedElement->
                val needsElements = annotatedElement.childElementsAnnotatedWith(LoadMethod::class.java)
                val cleanElements = annotatedElement.childElementsAnnotatedWith(CleanMethod::class.java)
                val processName = annotatedElement.getAnnotation(AutoPreload::class.java)
                println("11=====${processName.process}=====${annotatedElement.simpleName}  " +
                        "${annotatedElement.enclosedElements}")
                var containssington = false
                var invokeMethod : Element? = null
                run outside@{
                    annotatedElement.enclosedElements.forEach {
                        containssington = it.simpleName.toString() == "INSTANCE"
                        if(containssington){
                            return@outside
                        }
                    }
                }
                needsElements.forEach {
                    if(!it.modifiers.contains(Modifier.PRIVATE)){
                        invokeMethod = it
                    } else {
                        throw PrivateMethodException(it, LoadMethod::class.java)
                    }
                }

                println("00==containssington:${containssington}==========needsElements:${needsElements}=======")
                val cls = ClassName(annotatedElement.enclosingElement.toString(),annotatedElement.simpleName.toString())
                invokeMethod?.let {
                    //获取方法对应的注解值
                    val methodAnnotation = it.getAnnotation(LoadMethod::class.java)
                    val pName = if(processName.process == "main"){
                        ""
                    } else {
                        processName.process
                    }

                    if(processName.target == "application"){
                        f.beginControlFlow("if(\"\${mainProcess}${pName}\" == curProcess || curProcess == \"all\")")
                        f.beginControlFlow("if(%T.${methodAnnotation.threadMode} == %T.MAIN)",threadModel,threadModel)
                        f.addStatement("%T.%T(%T.Main)",globalScope,launch,dispatcher)
                        f.beginControlFlow("")
                        if(containssington){
                            f.addStatement("%T.${needsElements[0]}",cls)
                        } else {
                            f.addStatement("%T().${needsElements[0]}",cls)
                        }
                        f.endControlFlow()
                        f.nextControlFlow("else")
                        if(containssington){
                            f.addStatement("%T.${needsElements[0]}",cls)
                        } else {
                            f.addStatement("%T().${needsElements[0]}",cls)
                        }
                        f.endControlFlow()
                        f.endControlFlow()
                    } else {
                        loadTempCode.beginControlFlow("if(\"\${mainProcess}${pName}\" == curProcess || curProcess == \"all\")")
                        loadTempCode.beginControlFlow("if(%T.${methodAnnotation.threadMode} == %T.MAIN " +
                                "&& targetPath == \"${cls}\")",threadModel,threadModel)
                        loadTempCode.addStatement("%T.%T(%T.Main)",globalScope,launch,dispatcher)
                        loadTempCode.beginControlFlow("")
                        if(containssington){
                            loadTempCode.addStatement("%T.${needsElements[0]}",cls)
                        } else {
                            loadTempCode.addStatement("%T().${needsElements[0]}",cls)
                        }
                        loadTempCode.endControlFlow()
                        loadTempCode.nextControlFlow("else")
                        if(containssington){
                            loadTempCode.addStatement("%T.${needsElements[0]}",cls)
                        } else {
                            loadTempCode.addStatement("%T().${needsElements[0]}",cls)
                        }
                        loadTempCode.endControlFlow()
                        loadTempCode.endControlFlow()
                    }

                    //register 目标不是application且不是单例时启动时则注册到map中
                    if (processName.target != "application" && !containssington) {
                        register.addStatement(" map[\"${processName.target}\"] = \"${cls.reflectionName()}\"")

                        register.addStatement(" mapFunctionLoad[\"${cls.reflectionName()}\"] = \"${needsElements[0].simpleName}\"")

                        register.addStatement(" mapFunctionClean[\"${cls.reflectionName()}\"] = \"${cleanElements[0].simpleName}\"")
                    }
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }

        createLoadActivityFun(fLoadActivity,loadTempCode)
        createdestroyActivityFun(destroyActivity)
        typeSpec.addFunction(f.build())
        typeSpec.addFunction(fLoadActivity.build())
        typeSpec.addFunction(destroyActivity.build())
        typeSpec.addFunction(fLoadFragment.build())
        typeSpec.addFunction(register.build())
        return typeSpec

    }

    private fun createRegisterFun(processName: AutoPreload,register: FunSpec.Builder,cls: ClassName) {
        if (processName.target != "application") {
            register.addStatement(" map[\"${processName.target}\"] = \"${cls.reflectionName()}\"")
        }
    }

    fun createRegisterFun(builder: FunSpec.Builder){
        builder.addStatement("println(\"=====11========\${activity.javaClass.name}============\")")
    }

    fun createLoadActivityFun(builder: FunSpec.Builder,codeTemp:CodeBlock.Builder){
        builder.addStatement("val targetPath = map[activity.javaClass.name]")
        builder.beginControlFlow("if(targetPath != null)")
        val codeBlock = CodeBlock.builder()
        codeBlock.add("            val cls = Class.forName(targetPath)\n" +
                "            val obj = cls?.getConstructor()?.newInstance()!!\n" +
                "            val load = cls?.getDeclaredMethod(mapFunctionLoad[targetPath])\n" +
                "            load?.isAccessible = true\n" +
                "            load?.invoke(obj)\n" +
                "            val pair = Pair<Any,Class<*>>(obj,cls)\n" +
                "            mapTempObj[activity.javaClass.name] = pair")
        builder.addCode(codeBlock.build())
        builder.nextControlFlow("else")
        builder.addCode(codeTemp.build())
        builder.endControlFlow()
//        builder.addCode(codeBlock.build())
    }

    fun createdestroyActivityFun(builder: FunSpec.Builder){
        val codeBlock = CodeBlock.builder()
        codeBlock.addStatement("        val targetPath = map[activity.javaClass.name]\n" +
                "        val pair = mapTempObj[activity.javaClass.name]\n" +
                "        val cls = pair?.second\n" +
                "        val obj = pair?.first\n" +
                "        val cleanMethod = (cls as? Class<*>)?.getDeclaredMethod(mapFunctionClean[targetPath])\n" +
                "        cleanMethod?.isAccessible = true\n" +
                "        cleanMethod?.invoke(obj)\n" +
                "        mapTempObj.remove(activity.javaClass.name)")
        builder.addCode(codeBlock.build())
    }

    fun createFileBuilder(): FileSpec.Builder {
        return FileSpec.builder(AUTO_CODE_PACKAGE, LOAD_CLASS)
    }

    fun createFile(builder : FileSpec.Builder){
        builder.build().writeTo(Path(processingEnv.options["kapt.kotlin.generated"]!!))
//        file.writeTo(filer)
    }

}
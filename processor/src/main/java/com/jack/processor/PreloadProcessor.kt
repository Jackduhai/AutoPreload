package com.jack.processor

import com.google.auto.service.AutoService
import com.jack.annotation.*
import com.jack.annotation.Const.Companion.AUTO_CODE_PACKAGE
import com.jack.annotation.Const.Companion.LOAD_CLASS
import com.jack.processor.util.PrivateMethodException
import com.jack.processor.util.childElementsAnnotatedWith
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.lang.Exception
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
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
        //保存类和方法运行的线程相关信息
        typeSpec.addProperty(PropertySpec.builder("mapThreadInfo",LinkedHashMap::class.asClassName()
            .parameterizedBy(String::class.asClassName(),String::class.asClassName())
            ,KModifier.PRIVATE)
            .initializer("LinkedHashMap<String,String>()").build())
        //保存依赖注入的目标类
        typeSpec.addProperty(PropertySpec.builder("mapTargetInject",LinkedHashMap::class.asClassName()
            .parameterizedBy(String::class.asClassName(),String::class.asClassName())
            ,KModifier.PRIVATE)
            .initializer("LinkedHashMap<String,String>()").build())

        val f = FunSpec.builder("load").addParameter("applicationContext",context)
        f.addStatement("register()")
        f.addStatement("val mainProcess = applicationContext.packageName")
        f.addStatement("var curProcess = %T.getCurrentProcessName(applicationContext)",processUtil)
        f.beginControlFlow("if(!%T.isMultiProcess())",preload)
        f.addStatement("curProcess = \"all\"")
        f.endControlFlow()

        val loadTempCode = CodeBlock.builder()

        val cleanTempCode = CodeBlock.builder()
        cleanTempCode.addStatement("val mainProcess = context.packageName")
        cleanTempCode.addStatement("var curProcess = %T.getCurrentProcessName(context)",processUtil)
        cleanTempCode.beginControlFlow("if(!%T.isMultiProcess())",preload)
        cleanTempCode.addStatement("curProcess = \"all\"")
        cleanTempCode.endControlFlow()

        val fLoadPublicMethod = FunSpec.builder("loadPublic").addModifiers(KModifier.PRIVATE)
            .addParameter("context",context).addParameter("path",String::class)
            .addParameter("content",Any::class)
        val destroyPublicMethod = FunSpec.builder("destroyPublic").addModifiers(KModifier.PRIVATE)
            .addParameter("context",context).addParameter("path",String::class)
        val fLoadActivity = FunSpec.builder("loadActivity").addParameter("context",activity)
        val destroyActivity = FunSpec.builder("destroyActivity").addParameter("context",activity)
        val fLoadFragment = FunSpec.builder("loadFragment").addParameter("context",fragment)
        val destroyFragment = FunSpec.builder("destroyFragment").addParameter("context",fragment)
        val register = FunSpec.builder("register")

        val elements = roundEnv!!.getElementsAnnotatedWith(AutoPreload::class.java)!!
        //获取注解对应的运行进程名称
        try {
            elements.forEach {annotatedElement->
                val needsElements = annotatedElement.childElementsAnnotatedWith(LoadMethod::class.java)
                val cleanElements = annotatedElement.childElementsAnnotatedWith(CleanMethod::class.java)
                val targetInjectElements = annotatedElement.childElementsAnnotatedWith(TargetInject::class.java)
                val processName = annotatedElement.getAnnotation(AutoPreload::class.java)
                println("00=====${processName.process}=====${annotatedElement.simpleName}  " +
                        "${annotatedElement.enclosedElements}")
                var containssington = false
                var invokeMethod : Element? = null
                var cleanMethod : Element? = null
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

                cleanElements.forEach {
                    if(!it.modifiers.contains(Modifier.PRIVATE)){
                        cleanMethod = it
                    } else {
                        throw PrivateMethodException(it, CleanMethod::class.java)
                    }
                }

                println("11==containssington:${containssington}==========needsElements:${needsElements}=======")
                val cls = ClassName(annotatedElement.enclosingElement.toString(),annotatedElement.simpleName.toString())
                if(invokeMethod != null){
                    //获取方法对应的注解值
                    val methodAnnotation = invokeMethod!!.getAnnotation(LoadMethod::class.java)
                    val cleanAnnotation = cleanMethod?.getAnnotation(CleanMethod::class.java)
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
                        if(containssington){
                            loadTempCode.beginControlFlow("if((\"\${mainProcess}${pName}\" == curProcess || curProcess == \"all\") " +
                                    "&& path == \"${processName.target}\")")
                            loadTempCode.beginControlFlow("if(%T.${methodAnnotation.threadMode} == %T.MAIN " +
                                    ")",threadModel,threadModel)
                            loadTempCode.addStatement("%T.%T(%T.Main)",globalScope,launch,dispatcher)
                            loadTempCode.beginControlFlow("")
                            val suffix = if(containssington){
                                ""
                            } else {
                                "()"
                            }
                            loadTempCode.addStatement("%T${suffix}.${targetInjectElements[0]} = content as ${targetInjectElements[0].asType()}",cls)
                            loadTempCode.addStatement("%T${suffix}.${needsElements[0]}",cls)
                            loadTempCode.endControlFlow()
                            loadTempCode.nextControlFlow("else")
                            loadTempCode.addStatement("%T${suffix}.${targetInjectElements[0]} = content as ${targetInjectElements[0].asType()}",cls)
                            loadTempCode.addStatement("%T${suffix}.${needsElements[0]}",cls)
                            loadTempCode.endControlFlow()
                            loadTempCode.endControlFlow()

                            if(cleanAnnotation != null){
                                cleanTempCode.beginControlFlow("if((\"\${mainProcess}${pName}\" == curProcess || curProcess == \"all\") " +
                                        "&& path == \"${processName.target}\")")
                                cleanTempCode.beginControlFlow("if(%T.${cleanAnnotation?.threadMode} == %T.BACKGROUND " +
                                        ")",threadModel,threadModel)
                                cleanTempCode.addStatement("%T.%T(%T.IO)",globalScope,launch,dispatcher)
                                cleanTempCode.beginControlFlow("")
                                if(targetInjectElements.isNotEmpty()){
                                    cleanTempCode.addStatement("%T${suffix}.${targetInjectElements[0]} = null",cls)
                                }
                                cleanTempCode.addStatement("%T${suffix}.${cleanElements[0]}",cls)
                                cleanTempCode.endControlFlow()
                                cleanTempCode.nextControlFlow("else")
                                if(targetInjectElements.isNotEmpty()){
                                    cleanTempCode.addStatement("%T${suffix}.${targetInjectElements[0]} = null",cls)
                                }
                                cleanTempCode.addStatement("%T${suffix}.${cleanElements[0]}",cls)
                                cleanTempCode.endControlFlow()
                                cleanTempCode.endControlFlow()
                            }
                        }
                    }
                    register.addStatement(" mapThreadInfo[\"${cls.reflectionName()}\"] = \"${pName}\"")
                    register.addStatement(" mapThreadInfo[\"${cls.reflectionName()}.${needsElements[0].simpleName}\"] = \"${methodAnnotation?.threadMode}\"")
                    register.addStatement(" mapThreadInfo[\"${cls.reflectionName()}.${cleanElements[0].simpleName}\"] = \"${cleanAnnotation?.threadMode}\"")
                }

                //register 目标不是application且不是单例时启动时则注册到map中
                if (processName.target != "application" && !containssington) {
                    register.addStatement(" map[\"${processName.target}\"] = \"${cls.reflectionName()}\"")

                    register.addStatement(" mapFunctionLoad[\"${cls.reflectionName()}\"] = \"${needsElements[0].simpleName}\"")

                    register.addStatement(" mapFunctionClean[\"${cls.reflectionName()}\"] = \"${cleanElements[0].simpleName}\"")
                }
                if(targetInjectElements.isNotEmpty()){
                    register.addStatement(" mapTargetInject[\"${cls.reflectionName()}\"] = \"${targetInjectElements[0]}\"")//|${targetInjectElements[0].asType()
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }

        createLoadPublicFun(fLoadPublicMethod,loadTempCode)
        createdestroyPublicFun(destroyPublicMethod,cleanTempCode)
        createLoadActivityFun(fLoadActivity,loadTempCode)
        createLoadFragmentFun(fLoadFragment,loadTempCode)
        createdestroyActivityFun(destroyActivity,cleanTempCode)
        createdestroyFragmentFun(destroyFragment,cleanTempCode)
        typeSpec.addFunction(f.build())
        typeSpec.addFunction(fLoadActivity.build())
        typeSpec.addFunction(destroyActivity.build())
        typeSpec.addFunction(fLoadFragment.build())
        typeSpec.addFunction(destroyFragment.build())
        typeSpec.addFunction(register.build())
        typeSpec.addFunction(fLoadPublicMethod.build())
        typeSpec.addFunction(destroyPublicMethod.build())
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

    fun createLoadPublicFun(builder: FunSpec.Builder,codeTemp:CodeBlock.Builder){
        builder.addStatement("val mainProcess = context.packageName")
        builder.addStatement("var curProcess = %T.getCurrentProcessName(context)",processUtil)
        builder.beginControlFlow("if(!%T.isMultiProcess())",preload)
        builder.addStatement("curProcess = \"all\"")
        builder.endControlFlow()
        builder.addStatement("val targetPath = map[path]")
        builder.beginControlFlow("if(targetPath != null)")
        val codeBlock = CodeBlock.builder()
        codeBlock.add("if (\"\${mainProcess}\${mapThreadInfo[targetPath]}\" != curProcess && curProcess != \"all\") {\n" +
                "                return\n" +
                "            }\n" +
                "            val cls = Class.forName(targetPath)\n" +
                "            val obj = cls?.getConstructor()?.newInstance()!!\n" +
                "            val load = cls?.getDeclaredMethod(mapFunctionLoad[targetPath])\n" +
                "            load?.isAccessible = true\n" +
                "            val context = cls?.getDeclaredField(mapTargetInject[targetPath])\n" +
                "            context?.isAccessible = true\n" +
                "            val threadInfo = mapThreadInfo[\"\${targetPath}.\${mapFunctionLoad[targetPath]}\"]\n" +
                "            if (threadInfo == \"BACKGROUND\") {\n" +
                "                GlobalScope.launch(Dispatchers.IO)\n" +
                "                {\n" +
                "                    context.set(obj,content)\n"+
                "                    load?.invoke(obj)\n" +
                "                }\n" +
                "            } else {\n" +
                "                GlobalScope.launch(Dispatchers.Main)\n" +
                "                {\n" +
                "                    context.set(obj,content)\n"+
                "                    load?.invoke(obj)\n" +
                "                }\n" +
                "            }\n" +
                "            val pair = Pair<Any, Class<*>>(obj, cls)\n" +
                "            mapTempObj[path] = pair")
        builder.addCode(codeBlock.build())
        builder.nextControlFlow("else")
        builder.addCode(codeTemp.build())
        builder.endControlFlow()
    }

    fun createLoadActivityFun(builder: FunSpec.Builder,codeTemp:CodeBlock.Builder){
        builder.addStatement("loadPublic(context,context.javaClass.name,context)")
    }

    fun createLoadFragmentFun(builder: FunSpec.Builder,codeTemp:CodeBlock.Builder){
        builder.addStatement("loadPublic(context.requireContext(),context.javaClass.name,context)")
    }

    fun createdestroyActivityFun(builder: FunSpec.Builder,codeTemp:CodeBlock.Builder){
        builder.addStatement("destroyPublic(context,context.javaClass.name)")
    }

    fun createdestroyFragmentFun(builder: FunSpec.Builder,codeTemp:CodeBlock.Builder){
        builder.addStatement("destroyPublic(context.requireContext(),context.javaClass.name)")
    }

    fun createdestroyPublicFun(builder: FunSpec.Builder,codeTemp:CodeBlock.Builder){
        builder.addStatement("val targetPath = map[path]")
        builder.beginControlFlow("if(targetPath != null)")
        val codeBlock = CodeBlock.builder()
        codeBlock.add("val pair = mapTempObj[path]\n" +
                "            val cls = pair?.second\n" +
                "            val obj = pair?.first\n" +
                "            val cleanMethod = (cls as?\n" +
                "                    Class<*>)?.getDeclaredMethod(mapFunctionClean[targetPath])\n" +
                "            cleanMethod?.isAccessible = true\n" +
                "            val feid = (cls as? Class<*>)?.getDeclaredField(mapTargetInject[targetPath])\n" +
                "            feid?.isAccessible = true\n" +
                "            val threadInfo =\n" +
                "                mapThreadInfo[\"\${targetPath}.\${mapFunctionClean[targetPath]}\"]\n" +
                "            if (threadInfo == \"BACKGROUND\") {\n" +
                "                GlobalScope.launch(Dispatchers.IO)\n" +
                "                {\n" +
                "                    feid?.set(obj,null)\n"+
                "                    cleanMethod?.invoke(obj)\n" +
                "                }\n" +
                "            } else {\n" +
                "                GlobalScope.launch(Dispatchers.Main)\n" +
                "                {\n" +
                "                    feid?.set(obj,null)\n"+
                "                    cleanMethod?.invoke(obj)\n" +
                "                }\n" +
                "            }\n" +
                "            mapTempObj.remove(path)")
        builder.addCode(codeBlock.build())
        builder.nextControlFlow("else")
        builder.addCode(codeTemp.build())
        builder.endControlFlow()
    }

    fun createFileBuilder(): FileSpec.Builder {
        return FileSpec.builder(AUTO_CODE_PACKAGE, LOAD_CLASS)
    }

    fun createFile(builder : FileSpec.Builder){
        builder.build().writeTo(Path(processingEnv.options["kapt.kotlin.generated"]!!))
//        file.writeTo(filer)
    }

}
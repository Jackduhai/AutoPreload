package com.jack.processor

import com.google.auto.service.AutoService
import com.jack.annotation.AutoPreload
import com.jack.annotation.Const.Companion.AUTO_CODE_PACKAGE
import com.jack.annotation.Const.Companion.LOAD_CLASS
import com.jack.annotation.InvokeBase
import com.jack.annotation.LoadMethod
import com.jack.processor.util.PrivateMethodException
import com.squareup.kotlinpoet.*
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
            .addSuperinterface(InvokeBase::class)

        val f = FunSpec.builder("load").addModifiers(KModifier.OVERRIDE)
        val codeBlock = CodeBlock.builder()
        val elements = roundEnv!!.getElementsAnnotatedWith(AutoPreload::class.java)!!
        try {
            elements.forEach {annotatedElement->
                val needsElements = annotatedElement.childElementsAnnotatedWith(LoadMethod::class.java)
//                println("11=====${needsElements}=====${annotatedElement.simpleName}  " +
//                        "${annotatedElement.enclosedElements}")
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
//                codeBlock.add("%T().${needsElements[0]}",cls)
                invokeMethod?.let {
                    if(containssington){
                        f.addStatement("%T.${needsElements[0]}",cls)
                    } else {
                        f.addStatement("%T().${needsElements[0]}",cls)
                    }
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
        typeSpec.addFunction(f.build())
        return typeSpec

    }

    fun createFileBuilder(): FileSpec.Builder {
        return FileSpec.builder(AUTO_CODE_PACKAGE, LOAD_CLASS)
    }

    fun createFile(builder : FileSpec.Builder){
        builder.build().writeTo(Path(processingEnv.options["kapt.kotlin.generated"]!!))
//        file.writeTo(filer)
    }

}
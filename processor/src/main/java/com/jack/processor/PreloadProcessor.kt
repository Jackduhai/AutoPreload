package com.jack.processor

import com.google.auto.service.AutoService
import com.jack.annotation.AutoPreload
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8) // to support Java 8
@SupportedOptions("kapt.kotlin.generated")
class PreloadProcessor : AbstractProcessor(){

    private var messager : Messager? = null

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        messager = processingEnv?.messager;
        messager?.printMessage(Diagnostic.Kind.OTHER,"========process=====init1=======")
        println("========process=====init=======")

    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        println("========process=====getSupportedAnnotationTypes=======")
        return hashSetOf(AutoPreload::class.java.canonicalName)
    }

    override fun process(annotations: MutableSet<out TypeElement>?,roundEnv: RoundEnvironment?): Boolean {
        messager?.printMessage(Diagnostic.Kind.OTHER,"=======process start=========")
        println("=======process start=========")
        roundEnv?.getElementsAnnotatedWith(AutoPreload::class.java)
            ?.forEach {
                messager?.printMessage(Diagnostic.Kind.OTHER,"====================${it.simpleName}")
            }
       return true
    }
}
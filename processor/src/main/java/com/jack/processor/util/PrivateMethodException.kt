package com.jack.processor.util

import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

class PrivateMethodException(e: Element, annotationType: Class<*>) : RuntimeException("Method '${e.simpleString()}()' annotated with '@${annotationType.simpleName}' must not be private")
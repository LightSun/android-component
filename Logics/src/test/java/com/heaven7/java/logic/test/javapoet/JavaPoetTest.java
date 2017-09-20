package com.heaven7.java.logic.test.javapoet;

import com.heaven7.java.base.util.DefaultPrinter;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import junit.framework.TestCase;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;

/**
 * Created by heaven7 on 2017/8/28.
 */
public class JavaPoetTest extends TestCase{

    public static final String TAG = "JavaPoetTest";

    //ok
    public void testPrimitive(){
        final String path = getClass().getClassLoader().getResource("").getPath();
        DefaultPrinter.getDefault().info(TAG, "testPrimitive", "path = " + path);
        final MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getAge")
                .returns(int.class)
                .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC);
        final MethodSpec.Builder methodBuilder2 = MethodSpec.methodBuilder("setAge")
                .returns(void.class)
                .addParameter(int.class, "age")
                .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC);

        final TypeSpec typeSpec = TypeSpec.interfaceBuilder("TestPoet__module")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodBuilder.build())
                .addMethod(methodBuilder2.build())
                .build();

        try {
            JavaFile.builder("com.heaven7.java.logic.test.javapoet", typeSpec)
                    .build()
                    .writeTo(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

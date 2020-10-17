package com.huang.java00.lesson01;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

public class ClassLoaderTest extends ClassLoader{
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        InputStream in = null;
        byte[] buff = null;
        try {
            in = new FileInputStream("E:/Hello.xlass");
            buff = new byte[in.available()];
            in.read(buff);
        } catch (IOException e) {
            throw new ClassNotFoundException(" 文件Hello.xlass不存在", e);
        }finally{
            if(null != in){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        for(int i=0; i<buff.length; i++){
            buff[i] = (byte)(255 - buff[i]);
        }
        return this.defineClass(name, buff, 0, buff.length);
    }

    public static void main(String[] args) throws Exception {
        Class clz  = new ClassLoaderTest().findClass("Hello");
        Method ms = clz.getDeclaredMethod("hello", null);
        ms.invoke(clz.newInstance());
    }

}

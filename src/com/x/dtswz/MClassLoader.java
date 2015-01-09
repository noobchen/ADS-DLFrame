package com.x.dtswz;

import android.content.Context;
import android.content.Intent;
import dalvik.system.DexClassLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2014/11/6.
 */
public class MClassLoader {
    public static String loadedSdkVersion;
    private static Class<?> clazz;
    private static Object instance;

    private static Class<?> loadJar(Context context) {
        String sdkVersion = Config.instanse(context).getSdkVersion();

        String destFilePath = Config.instanse(context).jarloadPath + "ads_" + sdkVersion + ".jar";

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        DexClassLoader dexClassLoader = new DexClassLoader(destFilePath, Config.instanse(context).optimizedDirectory, null, classLoader);

        try {
            loadedSdkVersion = sdkVersion;
            return dexClassLoader.loadClass(Config.serviceClassPATH);


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static void getInstance() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor constructor = clazz.getConstructor(new Class[]{});

        instance = constructor.newInstance(new Object[]{});
    }

    public static void reLoad() {
        clazz = null;
        instance = null;
    }

    public static void setProxy(Context context) {
        if (clazz == null) {
            clazz = loadJar(context);
        }

        if (clazz != null) {


            try {
                if (instance == null) {
                    getInstance();
                }

                if (clazz != null && instance != null) {
                    Method setProxy = clazz.getMethod("setProxy", new Class[]{Context.class});
                    setProxy.invoke(instance, context);
                }

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    public static void onStartCommand(Context context, Intent intent, int flags, int startId) {
        if (clazz == null) {
            clazz = loadJar(context);
        }

        if (clazz != null) {
            try {
                if (instance == null) {
                    getInstance();
                }

                if (clazz != null && instance != null) {
                    Method onStartCommand = clazz.getMethod("onStartCommand", new Class[]{Intent.class, int.class, int.class});
                    onStartCommand.invoke(instance, new Object[]{intent, flags, startId});
                }

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }
}

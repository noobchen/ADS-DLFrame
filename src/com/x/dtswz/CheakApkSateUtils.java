package com.x.dtswz;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 14-11-3.
 */
public class CheakApkSateUtils {

    public static boolean cheakByPackageName(Context context,String packageName,String sdkVersion){
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        List<String> pName = new ArrayList<String>();
        List<String> versions = new ArrayList<String>();

        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                String appVersion = pinfo.get(i).versionName;
                pName.add(pn);
                versions.add(appVersion);
            }
        }

        if (!pName.contains(packageName)){
            return false;
        }else {
            if (!versions.contains(sdkVersion)){
                return false;
            }

        }
        return true;
    }

    public static boolean cheakFileExist(String apkPath){
        File file = new File(apkPath);

        return file.exists();
    }

    public static File createFolder(String folder) {
        File destDir = new File(folder);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return destDir;
    }
}

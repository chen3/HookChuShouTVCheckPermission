package cn.qiditu.hookchushoutvcheckpermission

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.reflect.Method

/**
 * Created by on 2018-07-15.
 */
class HookChuShouTVCheckPermission : IXposedHookLoadPackage {

    companion object {

        private val targetPackageName = "com.kascend.chushou";

        fun <T> getNeedHookMethodNames(clazz: Class<T>): List<Method> {
            return clazz.declaredMethods.filter {
                method ->  method.parameterTypes.isEmpty()
                    && method.returnType == Array<String>::class.java;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Throws(Throwable::class)
    override fun handleLoadPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        if (loadPackageParam.packageName != targetPackageName) {
            return;
        }

        XposedBridge.log("开始去除权限认证方法");
//        XposedBridge.log("应用版本:${loadPackageParam.appInfo.}");
        val clazz = loadPackageParam.classLoader.loadClass(
                "com.kascend.chushou.view.base.BaseActivity");
        val emptyStringArrayResult = object : XC_MethodHook() {
            /**
             * {@inheritDoc}
             */
            @Throws(Throwable::class)
            override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                super.afterHookedMethod(param);
                param!!.result = arrayOf<String>();
            }
        }

        getNeedHookMethodNames(clazz).forEach {
            method ->
                XposedBridge.log("hook chushou method: ${method.name}");
                XposedBridge.hookMethod(method, emptyStringArrayResult);
        }
        XposedBridge.log("结束去除权限认证方法");
    }

//
//    @Throws(Throwable::class)
//    override fun handleInitPackageResources(
//            resparam: XC_InitPackageResources.InitPackageResourcesParam) {
//        if (resparam.packageName != targetPackageName) {
//            return;
//        }
//
//        XposedBridge.log("开始修改字符串资源");
//        addIndexBeforeString(arrayOf("common_message_permission_always_failed",
//                                     "gallery_permissions_always_failed",
//                                     "im_permissions_always_failed",
//                                     "main_permissions_always_failed"),
//                resparam.res);
//    }
//
//    private fun addIndexBeforeString(@Size(min = 1) names: Array<String>,
//                                     resources: XResources) {
//        XposedBridge.log("addIndexBeforeString:" + names.joinToString(", "));
//        for ((index, name) in names.withIndex()) {
//            val id = resources.getIdentifier(name, "string", targetPackageName);
//            XposedBridge.log("string name:$name\tid:$id");
//            val originText = resources.getString(id);
//            val newText = String.format(Locale.getDefault(), "%1\$d%1\$d%1\$d-%2\$s", index + 1, originText);
//            resources.setReplacement(id, newText);
//        }
//    }

}

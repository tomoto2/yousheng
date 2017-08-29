package com.joe.scan;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class类扫描
 *
 * @author joe
 */
public class ClassScanner implements Scanner<Class<?>, ClassFilter> {
    private static final Logger logger = LoggerFactory.getLogger(ClassScanner.class);
    private static final Object lock = new Object();
    private static volatile ClassScanner classScanner;

    private ClassScanner() {
    }

    ;

    public static ClassScanner getInstance() {
        if (classScanner == null) {
            synchronized (lock) {
                if (classScanner == null) {
                    classScanner = new ClassScanner();
                }
            }
        }
        return classScanner;
    }

    /**
     * 参数必须为String数组，该参数为要扫描的包名
     */
    public List<Class<?>> scan(Object... args) {
        if (args == null || args.length == 0) {
            return Collections.emptyList();
        }
        List<Class<?>> result = null;
        for (Object obj : args) {
            List<Class<?>> list = scan((String) obj);
            if (result == null) {
                result = list;
            } else {
                result.addAll(list);
            }
        }
        return result;
    }

    /**
     * 扫描指定的包中的所有class
     *
     * @param filters 过滤器
     * @param args    参数（String类型，要扫描的包的集合）
     * @return 扫描结果
     * @throws ScannerException
     */
    public List<Class<?>> scan(List<ClassFilter> filters, Object... args) throws ScannerException {
        logger.debug("搜索扫描所有的类，过滤器为：{}，参数为：{}", filters, args);

        if (args == null || args.length == 0) {
            return Collections.emptyList();
        }

        if (args != null) {
            for (Object arg : args) {
                if (!(arg instanceof String)) {
                    logger.debug("参数类型为：{}", args.getClass());
                    throw new ScannerException("参数类型为：" + args.getClass());
                }
            }
        }

        List<Class<?>> result = null;
        for (Object obj : args) {
            List<Class<?>> list = scan((String) obj, filters);
            if (result == null) {
                result = list;
            } else {
                result.addAll(list);
            }
        }
        return result;
    }

    /**
     * 根据包名扫描类
     *
     * @param packageName 包名
     * @return
     */
    private List<Class<?>> scan(String packageName) {
        return (scan(packageName, null));
    }

    /**
     * 根据包名扫描类，并且用过滤器过滤
     *
     * @param packageName 包名
     * @param filters     过滤器，过滤器必须是ClassFilter的子类
     * @return
     */
    private List<Class<?>> scan(String packageName, List<ClassFilter> filters) {
        long start = System.currentTimeMillis();
        List<Class<?>> list = new ArrayList<Class<?>>();
        // 根据包名获取路径
        String path = packageName.replaceAll("\\.", "/");
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        // 文件集合
        List<File> fileList = new ArrayList<File>();
        fileList.add(new File(url.getFile()));
        for (int i = 0; i < fileList.size(); i++) {
            File file = fileList.get(i);
            // 文件是目录
            if (file.isDirectory()) {
                fileList.addAll(Arrays.asList(file.listFiles()));
            } else if (file.canRead()) {
                // 文件不是目录且可读
                String filePath = file.getAbsolutePath().replaceAll("\\\\", ".");
                String classPath = filePath.substring(filePath.indexOf(packageName));

                // 只扫描class类文件
                Pattern pattern = Pattern.compile(".*\\.class$");
                Matcher matcher = pattern.matcher(classPath);
                if (!matcher.matches()) {
                    // 该文件不是class文件
                    continue;
                }

                classPath = classPath.substring(0, classPath.length() - 6);
                try {
                    // 加载class
                    Class<?> clazz = Class.forName(classPath);
                    if (filters == null || filters.isEmpty()) {
                        // 没有filter
                        list.add(clazz);
                    } else {
                        // 存在filter
                        boolean flag = true;
                        for (ClassFilter filter : filters) {
                            flag = filter.filter(clazz);
                            if (flag == false) {
                                break;
                            }
                        }
                        if (flag) {
                            list.add(clazz);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Class加载失败，失败Class：" + classPath, e);
                    continue;
                }
            }
        }
        long end = System.currentTimeMillis();
        logger.info("此次扫描用时" + (end - start) + "ms");
        return list;
    }

}

package com.joe.type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joe.utils.BeanUtils;
import com.joe.utils.BeanUtils.CustomPropertyDescriptor;

/**
 * java类型相关工具类
 * 
 * @author joe
 *
 */
public class JavaTypeUtil {
	private static final Logger logger = LoggerFactory.getLogger(JavaTypeUtil.class);
	private static final Pattern superPattern = Pattern.compile("(.*) super.*");
	private static final Pattern extendsPattern = Pattern.compile("(.*) extends.*");

	/**
	 * 构建map类型的JavaType
	 * 
	 * @param m
	 *            map的具体类型
	 * @param k
	 *            key的类型
	 * @param v
	 *            value的类型
	 * @return 对应的JavaType
	 */
	public static <K, V, M extends Map<K, V>> JavaType createMapType(Class<M> m, Class<K> k, Class<V> v) {
		BaseType mapType = new BaseType();
		BaseType keyType = new BaseType();
		BaseType valueType = new BaseType();
		keyType.setType(k);
		valueType.setType(v);
		JavaType[] generics = { keyType, valueType };
		mapType.setType(m);
		mapType.setGenerics(generics);
		return mapType;
	}

	/**
	 * 构建集合类型的JavaType
	 * 
	 * @param k
	 *            集合的基本类型
	 * @param t
	 *            集合
	 * @return
	 */
	public static <T, K extends Collection<T>> JavaType createCollectionType(Class<K> k, Class<T> t) {
		BaseType collectionType = new BaseType();
		BaseType genericType = new BaseType();
		genericType.setType(t);
		JavaType[] generics = { genericType };
		collectionType.setType(k);
		collectionType.setGenerics(generics);
		return collectionType;
	}

	/**
	 * 根据java系统类型得出自定义类型
	 * 
	 * @param type
	 *            java反射取得的类型
	 * @return 自定义java类型说明
	 */
	@SuppressWarnings({ "rawtypes", "restriction" })
	public static JavaType createJavaType(Type type) {
		String typeName = type.getTypeName();
		JavaType javaType = null;

		Class<?> generalType = null;
		if (typeName.endsWith("[]")) {
			// 类型是系统数组类型（格式：String[]）
			logger.debug("系统类型为：{}，转换为相应的Array类型", typeName);
			if (isGeneralArrayType(typeName)) {
				generalType = getGeneralTypeByName(typeName.replaceAll("\\[\\]", ""));
				logger.debug("{}是基本类型，对应的包装类型为：{}", typeName, generalType);
			} else {
				typeName = typeName.replaceAll("\\[\\]", "");
			}
			logger.debug("数组类型为：{}", typeName);
			ArrayType arrayType = new ArrayType();
			try {
				generalType = generalType == null ? Class.forName(typeName) : generalType;
				arrayType.setBaseType(generalType);
				arrayType.setName(typeName);
				javaType = arrayType;
			} catch (Exception e) {
				logger.error("反射获取类型{}错误", typeName, e);
				throw new RuntimeException(e);
			}
		} else if (type instanceof sun.reflect.generics.reflectiveObjects.WildcardTypeImpl) {
			// 该类型是不确定的泛型，即泛型为 ?
			logger.debug("类型{}是不确定的泛型", typeName);
			sun.reflect.generics.reflectiveObjects.WildcardTypeImpl wildcardTypeImpl = (sun.reflect.generics.reflectiveObjects.WildcardTypeImpl) type;
			Type[] child = wildcardTypeImpl.getLowerBounds();// 子类
			Type[] parent = wildcardTypeImpl.getUpperBounds();// 父类
			javaType = new GenericType();
			GenericType genericType = (GenericType) javaType;
			if (child.length > 0) {
				logger.debug("类型{}必须是{}的父类型", typeName, child[0]);
				genericType.setChild(createJavaType(child[0]));
			} else {
				logger.debug("类型{}必须是{}的子类型", typeName, parent[0]);
				genericType.setParent(createJavaType(parent[0]));
			}
			genericType.setName(dealName(typeName));
		} else if (type instanceof sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) {
			// 该类型存在泛型
			logger.debug("类型{}存在泛型", typeName);
			sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl parameterizedTypeImpl = (sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) type;
			Type[] types = parameterizedTypeImpl.getActualTypeArguments();
			JavaType[] generics = new JavaType[types.length];
			for (int i = 0; i < types.length; i++) {
				generics[i] = createJavaType(types[i]);
			}
			javaType = new BaseType();
			BaseType baseType = (BaseType) javaType;
			baseType.setType(parameterizedTypeImpl.getRawType());
			baseType.setGenerics(generics);
			baseType.setName(baseType.getType().getSimpleName());
		} else if (type instanceof sun.reflect.generics.reflectiveObjects.TypeVariableImpl) {
			// 该类型是泛型
			logger.debug("类型{}是泛型", typeName);
			sun.reflect.generics.reflectiveObjects.TypeVariableImpl typeVariableImpl = (sun.reflect.generics.reflectiveObjects.TypeVariableImpl) type;
			javaType = new GenericType();
			GenericType genericType = (GenericType) javaType;
			// 指定名字的泛型只能继承，不能使用关键字super，所以getBounds该方法得出的是泛型的父类型
			genericType.setParent(createJavaType(typeVariableImpl.getBounds()[0]));
			genericType.setName(dealName(type.getTypeName()));
		} else if (type instanceof Class) {
			// 该类型是普通类型（没有泛型，本身也不是泛型参数）
			javaType = new BaseType();
			BaseType baseType = (BaseType) javaType;
			Class<?> clazz = null;

			try {
				if (isGeneralType(typeName)) {
					clazz = getGeneralTypeByName(typeName);
				}
				baseType.setType(clazz == null ? Class.forName(typeName) : clazz);
			} catch (Exception e) {
				logger.error("反射获取类型{}错误", typeName, e);
			}
			baseType.setName(((Class) type).getSimpleName());
		}

		// 判断如果是基本类型的话是不是自定义类型，如果是的话对自定义类型的各个属性进行描述
		if (javaType instanceof BaseType) {
			BaseType baseType = (BaseType) javaType;
			if (!isBasic(baseType.getType())) {
				// 是自定义类型，对自定义类型的各个属性进行描述
				Map<String, JavaType> params = new TreeMap<String, JavaType>();
				// 获取该自定义类型的各个属性
				CustomPropertyDescriptor[] props = BeanUtils.getPropertyDescriptors(baseType.getType());
				for (CustomPropertyDescriptor prop : props) {
					if ("class".equals(prop.getName())) {
						continue;
					}
					// 遍历属性，将属性转换为JavaType
					// 获取属性读取方法上的注解
					Annotation[] methodAnnotations = prop.getReadMethod().getAnnotations();
					Annotation[] annotations = null;
					try {
						// 获取属性字段上的注解
						Field field = baseType.getType().getField(prop.getName());
						Annotation[] fieldAnnotations = field.getAnnotations();
						annotations = new Annotation[methodAnnotations.length + fieldAnnotations.length];
						System.arraycopy(methodAnnotations, 0, annotations, 0, methodAnnotations.length);
						System.arraycopy(fieldAnnotations, 0, annotations, methodAnnotations.length,
								fieldAnnotations.length);
					} catch (Exception e) {
						annotations = new Annotation[methodAnnotations.length];
						System.arraycopy(methodAnnotations, 0, annotations, 0, methodAnnotations.length);
					}
					JavaType filedType = createJavaType(prop.getReadMethod().getGenericReturnType());
					filedType.setAnnotations(annotations);
					params.put(prop.getName(), filedType);
				}
				baseType.setIncludes(params);
			}
		}

		return javaType;
	}

	/**
	 * 判断Class对象是否为基本类型（java自带简单类型包括Boolean、Character、Number、Map、String、Collection、Enum，如果不是
	 * 这六种类型将会认为该类型是一个复杂类型）
	 * 
	 * @param clazz
	 *            Class对象
	 * @return 如果是基本类型则返回<code>true</code>
	 */
	public static boolean isBasic(Class<?> clazz) {
		if (clazz == null) {
			throw new NullPointerException("Class不能为null");
		} else if (Boolean.class.isAssignableFrom(clazz) || Character.class.isAssignableFrom(clazz)
				|| Number.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz)
				|| String.class.isAssignableFrom(clazz) || Collection.class.isAssignableFrom(clazz)
				|| Enum.class.isAssignableFrom(clazz)) {
			// 该Class为基本类型
			return true;
		} else {
			// 该Class为自定义类型
			return false;
		}
	}

	/**
	 * 从JavaType中抽取真实的基类
	 * 
	 * @param type
	 *            指定的JavaType
	 * @return 该指定JavaType对应的基类
	 */
	public static Class<?> getRealType(JavaType type) {
		if (type instanceof BaseType) {
			logger.debug("参数不是泛型的");
			return ((BaseType) type).getType();
		} else {
			logger.debug("参数是泛型的");
			JavaType parent = ((GenericType) type).getParent();
			JavaType child = ((GenericType) type).getChild();
			return parent == null ? getRealType(child) : getRealType(parent);
		}
	}

	/**
	 * 判断指定类型是否是8大基本类型
	 * 
	 * @param name
	 *            指定类型
	 * @return 如果是基本类型则返回<code>true</code>
	 */
	private static boolean isGeneralType(String name) {
		if (name.equals("byte") || name.equals("short") || name.equals("int") || name.equals("long")
				|| name.equals("double") || name.equals("float") || name.equals("boolean") || name.equals("char")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断指定数组名称是否是java八大基本类型
	 * 
	 * @param name
	 *            指定数组名称
	 * @return 如果是基本类型则返回<code>true</code>
	 */
	private static boolean isGeneralArrayType(String name) {
		if (name.equals("byte[]") || name.equals("short[]") || name.equals("int[]") || name.equals("long[]")
				|| name.equals("double[]") || name.equals("float[]") || name.equals("boolean[]")
				|| name.equals("char[]")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据基本类型的名称获取对应的类型（名称应为：byte、short、int、long、double、float、boolean、char其中之一）
	 * 
	 * @param name
	 *            反射获取到的基本类型名称
	 * @return 对应的包装类型
	 */
	private static Class<?> getGeneralTypeByName(String name) {
		switch (name) {
		case "byte":
			return Byte.class;
		case "short":
			return Short.class;
		case "int":
			return Integer.class;
		case "long":
			return Long.class;
		case "double":
			return Double.class;
		case "float":
			return Float.class;
		case "boolean":
			return Boolean.class;
		case "char":
			return Character.class;
		default:
			logger.warn("{}不是基本类型", name);
			return null;
		}
	}

	/**
	 * 处理泛型名称
	 * 
	 * @param fullName
	 *            泛型全名
	 * @return 泛型的名称
	 */
	private static String dealName(String fullName) {
		Matcher matcher = null;
		matcher = superPattern.matcher(fullName);
		String name = null;
		if (matcher.find()) {
			name = matcher.group(1);
		} else {
			matcher = extendsPattern.matcher(fullName);
			if (matcher.find()) {
				name = matcher.group(1);
			} else {
				name = fullName;
			}
		}
		return name;
	}
}

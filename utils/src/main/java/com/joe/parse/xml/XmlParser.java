package com.joe.parse.xml;

import com.joe.utils.BeanUtils;
import com.joe.utils.BeanUtils.CustomPropertyDescriptor;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * XML解析
 * 
 * @author Administrator
 *
 */
public class XmlParser {
	private static final Logger logger = LoggerFactory.getLogger(XmlParser.class);
	private static final XmlParser xmlParser = new XmlParser();

	private XmlParser() {
	};

	public static XmlParser getInstance() {
		return xmlParser;
	}

	/**
	 * 解析XML，将xml解析为map（注意：如果XML是&lt;a&gt;data&lt;b&gt;bbb&lt;/b&gt;&lt;/a&gt;
	 * 这种格式那么data将不被解析，对于list可以正确解析）
	 * 
	 * @param xml
	 *            xml字符串
	 * @return 由xml解析的TreeMap
	 */
	@SuppressWarnings("unchecked")
	public TreeMap<String, Object> parse(String xml) {
		try {
			Document document = DocumentHelper.parseText(xml);
			Element root = document.getRootElement();
			if (root.elements().size() == 0) {
				TreeMap<String, Object> map = new TreeMap<String, Object>();
				map.put(root.getName(), root.getText());
				return map;
			} else {
				return (TreeMap<String, Object>) parse(root);
			}
		} catch (Exception e) {
			logger.error("xml格式不正确", e);
			return null;
		}
	}

	/**
	 * 将XML解析为POJO对象（只能解析简单的对象（对象字段只能包含八大基本类型），复杂对象无法解析）
	 * 
	 * @param xml
	 *            XML源
	 * @param clazz
	 *            POJO对象的class
	 * @return 解析结果
	 */
	public <T extends Object> T parse(String xml, Class<T> clazz) {
		if (xml == null || clazz == null || xml.isEmpty()) {
			return null;
		}
		T pojo = null;
		Document document = null;
		Map<String, CustomPropertyDescriptor> fields = null;

		// 获取pojo对象的实例
		try {
			// 没有权限访问该类或者该类（为接口、抽象类）不能实例化时将抛出异常
			pojo = clazz.newInstance();
		} catch (Exception e) {
			logger.error("class对象生成失败，请检查代码；失败原因：" + e.toString());
			return null;
		}

		// 解析XML
		try {
			document = DocumentHelper.parseText(xml);
		} catch (Exception e) {
			logger.error("xml解析错误", e);
			return null;
		}

		// 获取pojo对象的说明
		CustomPropertyDescriptor[] propertyDescriptor = BeanUtils.getPropertyDescriptors(clazz);
		fields = new TreeMap<String, CustomPropertyDescriptor>();
		for (CustomPropertyDescriptor descript : propertyDescriptor) {
			String name = descript.getName();
			fields.put(name.toLowerCase(), descript);
		}

		Element root = document.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> elements = root.elements();

		// 遍历xml节点，查看要生成的pojo对象中是否有该节点对应的字段，如果有，那么将对应的值赋值进去
		for (Element element : elements) {
			String elementName = element.getName();
			CustomPropertyDescriptor desc = fields.get(elementName.toLowerCase());
			String fieldName = desc.getName();
			logger.debug("elementName为{}，fieldName为{}", elementName, fieldName);
			if (fieldName != null) {
				if (!BeanUtils.setProperty(pojo, fieldName, element.getText())) {
					logger.debug("copy中复制" + fieldName + "时发生错误");
				}
			}
		}
		return pojo;
	}

	/**
	 * 将Object解析为xml，字段值为null的将不包含在xml中，暂时只能解析基本类型（可以正确解析list、map）
	 * 
	 * @param source
	 *            bean
	 * @param rootName
	 *            根节点名称
	 * @param hasNull
	 *            是否包含null元素（true：包含）
	 * @return 解析结果
	 */
	public String toXml(Object source, String rootName, boolean hasNull) {
		Long start = System.currentTimeMillis();
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement(rootName);
		// 获取bean的字段的说明
		CustomPropertyDescriptor[] propertyDescriptor = BeanUtils.getPropertyDescriptors(source.getClass());
		for (CustomPropertyDescriptor descrip : propertyDescriptor) {
			Element element = null;
			// 获取字段名称
			String fieldName = descrip.getName();
			String elementName = fieldName;
			if ("class".equals(fieldName)) {
				continue;
			}
			try {
				Object obj = BeanUtils.getProperty(source, fieldName);

				if (obj == null && !hasNull) {
					continue;
				}

				Field field = descrip.getField();
				boolean cdata = false;
				// 判断该字段是否有XmlNode注解
				if (field.isAnnotationPresent(XmlNode.class)
						|| descrip.getReadMethod().isAnnotationPresent(XmlNode.class)) {
					// 获取注解
					XmlNode xmlNode = field.getAnnotation(XmlNode.class) != null ? field.getAnnotation(XmlNode.class)
							: descrip.getReadMethod().getAnnotation(XmlNode.class);
					// 是否忽略该节点
					if (xmlNode.ignore()) {
						continue;
					}
					// 判断是否是CDATA
					if (xmlNode.isCDATA()) {
						cdata = true;
					}
					// 解析该节点的名称
					if (!"".equals(xmlNode.name())) {
						elementName = xmlNode.name();
					}
				}
				final String NAME = elementName;
				final boolean CDATA = cdata;
				Class<?> clazz = descrip.getField().getType();
				// 根据不同的类型解析
				if (List.class.isAssignableFrom(clazz) || Set.class.isAssignableFrom(clazz)) {
					// LIST
					Collection<?> collection = (Collection<?>) obj;
					collection.forEach(o -> {
						root.add(createElement(NAME, String.valueOf(o), CDATA));
					});
				} else if (Map.class.isAssignableFrom(clazz)) {
					// MAP
					Map<?, ?> map = (Map<?, ?>) obj;
					Element ele = createElement(NAME, "", false);
					map.forEach((key, value) -> {
						ele.add(createElement(String.valueOf(key), String.valueOf(value), CDATA));
					});
					root.add(ele);
				} else {
					// 基本类型（此处有可能不是基本类型，如果不是基本类型此处就可能出错）
					element = createElement(elementName, String.valueOf(obj), cdata);
					root.add(element);
				}
			} catch (Exception e) {
				logger.info("bean的" + fieldName + "字段解析失败，该字段将不包含在XML中", e);
				continue;
			}
		}
		Long end = System.currentTimeMillis();
		logger.info("解析xml用时" + (end - start) + "ms");
		return root.asXML();
	}

	@SuppressWarnings("unchecked")
	private Object parse(Element element) {
		List<Element> elements = element.elements();
		if (elements.size() == 0) {
			return element.getText();
		} else {
			Map<String, Object> map = new TreeMap<String, Object>();
			for (Element ele : elements) {
				if (map.containsKey(ele.getName())) {
					// 如果map中已经包含该key，说明该key有多个，是个list
					Object obj = map.get(ele.getName());
					List<String> list = null;
					if (obj != null && obj instanceof List) {
						// 如果obj不等于null并且是list对象，说明map中存的已经是一个list
						list = (List<String>) obj;
					} else {
						// 如果obj等于null或者不是list对象，那么新建一个list对象
						list = new ArrayList<>();
						list.add(obj == null ? null : String.valueOf(obj));
					}
					Object result = parse(ele);
					list.add(result == null ? null : String.valueOf(result));
					map.put(ele.getName(), list);
				} else {
					map.put(ele.getName(), parse(ele));
				}
			}
			return map;
		}
	}

	/**
	 * 创建一个Element
	 * 
	 * @param name
	 *            element的name
	 * @param text
	 *            内容
	 * @param isCDATA
	 *            text是否需要用CDATA包裹（true：需要）
	 * @return element 生成的element
	 */
	private Element createElement(String name, String text, boolean isCDATA) {
		Element element = DocumentHelper.createElement(name);
		if (isCDATA) {
			element.add(DocumentHelper.createCDATA(text));
		} else {
			element.setText(text);
		}
		return element;
	}
}

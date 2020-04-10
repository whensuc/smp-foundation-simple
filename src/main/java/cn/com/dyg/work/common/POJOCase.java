package cn.com.dyg.work.common;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.com.dyg.work.annotation.Adapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class POJOCase {

	private static Logger logger = LoggerFactory.getLogger(POJOCase.class);

	/**
	 *
	 * 标题: convert <br/>
	 * 描述: 单个对象转换<br/>
	 * 作者 : wzy<br/>
	 * 版本号: 1.0<br/>
	 */
	public static <V, T> T convert(V source, T target) throws BusinessException {
		List<Field> fieldList_target = new ArrayList<Field>();
		Class<?> cls_target = target.getClass();
		while (cls_target != null) {
			fieldList_target.addAll(Arrays.asList(cls_target.getDeclaredFields()));
			cls_target = cls_target.getSuperclass();
		}
		List<Field> fieldList_source = new ArrayList<Field>();
		Class<?> cls_source = source.getClass();
		while (cls_source != null) {
			fieldList_source.addAll(Arrays.asList(cls_source.getDeclaredFields()));
			cls_source = cls_source.getSuperclass();
		}
		List<String> fieldString_source = new ArrayList<String>();
		for (Field fd : fieldList_source) {
			fieldString_source.add(fd.getName());
		}
		try {
			for (Field fd : fieldList_target) {
				if (!fieldString_source.contains(fd.getName()))
					continue;
				PropertyDescriptor pd = new PropertyDescriptor(fd.getName(), source.getClass());
				Method getMethod = pd.getReadMethod();
				if (getMethod != null) {
					Object value = getMethod.invoke(source);
					if (fd.isAnnotationPresent(Adapter.class)) {
						Adapter adapter = fd.getAnnotation(Adapter.class);
						String toFd = adapter.toCaseFild();
						PropertyDescriptor pd2 = new PropertyDescriptor(toFd, target.getClass());
						Method setMethod = pd2.getWriteMethod();
						setMethod.invoke(target, value);
					} else {
						PropertyDescriptor pd2 = new PropertyDescriptor(fd.getName(), target.getClass());
						Method setMethod = pd2.getWriteMethod();
						setMethod.invoke(target, value);
					}

				}
			}
		} catch (Exception e) {
			logger.error("error:{}",e);
			throw new BusinessException("POJO转换错误:" + e.getMessage());
		}

		return target;
	}

	/**
	 *
	 * 标题: convert <br/>
	 * 描述: 对象列表集合转换<br/>
	 * 作者 : wzy<br/>
	 * 版本号: 1.0<br/>
	 */
	public static <V, T> List<T> convert(List<V> source, Class<T> cls) throws BusinessException {
		List<T> target;
		try {
			target = new ArrayList<T>();
			for (V v : source) {
				T t = cls.newInstance();
				convert(v, t);
				target.add(t);
			}
		} catch (Exception e) {
			logger.error("error:{}",e);
			throw new BusinessException("POJO转换错误:" + e.getMessage());
		}
		return target;
	}

	public static <V, T> PaginationBO<T> convert(PaginationBO<V> sourcebo, Class<T> cls) throws BusinessException {
		PaginationBO<T> pagebo = new PaginationBO<T>(sourcebo.getCurrentPage(),sourcebo.getPagenum(),sourcebo.getTotalNum());
		List<T> target;
		List<V> source = sourcebo.getDatas();
		try {
			target = new ArrayList<T>();
			for (V v : source) {
				T t = cls.newInstance();
				convert(v, t);
				target.add(t);
			}
			pagebo.setDatas(target);
		} catch (Exception e) {
			logger.error("error:{}",e);
			throw new BusinessException("POJO转换错误:" + e.getMessage());
		}
		return pagebo;
	}
}

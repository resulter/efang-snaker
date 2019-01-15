package com.efangtec.project.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class BaseParam implements Serializable
{
	private static final long serialVersionUID = 1L;


	private Map<String,Object> paramMap =new HashMap<>();


	public Map<String, Object> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, Object> paramMap) {
		this.paramMap = paramMap;
	}

	public Map<String, Object> build()
	{
		Map<String, Object> params = new HashMap<String, Object>(5);
		params.putAll(paramMap);
		return params;
	}

	protected void getParameter(Class<?> clazz, Map<String, Object> map, Object obj) throws Exception
	{
		if (clazz.getSimpleName().equals("Object"))
		{
			return;
		}
		Field[] fields = clazz.getDeclaredFields();
		if ((fields != null) && (fields.length > 0))
		{
			for (int i = 0; i < fields.length; i++)
			{
				fields[i].setAccessible(true);
				String name = fields[i].getName();
				if (Modifier.isStatic(fields[i].getModifiers()))
				{
					continue;
				}
				Object value = fields[i].get(obj);
				map.put(name, value);
			}
		}
		//Class<?> superClzz = clazz.getSuperclass();
		//getParameter(superClzz, map, obj);
	}

}

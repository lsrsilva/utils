/*
 * Utils.java
 * Copyright (c) 2020 Ourcycle Sistemas.
 *
 * This software is confidential and Ourycle Sistemas property.
 * Its distribution or dissemination of its content is not permitted without
 * express authorization from Ourcycle Sistemas.
 * This archive contains proprietary information
 */

package dev.ourcycle.cm.util.utils;

import dev.ourcycle.cm.util.generic.entity.GenericEntity;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class Utils {

    public static <T extends Object> List<T> toList(Set<T> set) {
        List<T> list;
        if (set != null) {
            list = new ArrayList<T>(set);
        } else {
            list = new ArrayList<T>();
        }

        return list;
    }

    public static <T extends Object> Set<T> toSet(List<T> list) {
        Set<T> set;
        if (list != null) {
            set = new HashSet<T>(list);
        } else {
            set = new HashSet<T>();
        }

        return set;
    }

    public static String replaceAll(String valor, String regex, String replacement) {
        return valor.replaceAll(regex, replacement);
    }

    public static boolean isEmpty(Object o) {
        if (o == null) {
            return true;
        } else if (o instanceof String) {
            return ((String) o).isEmpty();
        } else if (o instanceof Collection) {
            return ((Collection) o).size() == 0;
        } else if (o instanceof Map) {
            return ((Map) o).isEmpty();
        }
        return true;
    }

    public static <T extends GenericEntity<T>> boolean hasAllAtributesNull(GenericEntity<T> entity) throws Exception {
        String methodName = "";
        try {
            Method[] methods = entity.getClass().getMethods();
            int count = 0;

            for (Method method : methods) {
                method.setAccessible(true);
                if (method.getName().startsWith("get") || method.getName().startsWith("is")) {
                    methodName = method.getName();
                    if (((String) method.invoke(entity)).isEmpty()) {
                        count++;
                    }
                    if (method.invoke(entity) == null) {
                        count++;
                    }
                }
            }
            return count > 0;
        } catch (InvocationTargetException e) {

            throw new Exception("Error trying to access method " + methodName, e);
        }
    }

    /**
     * @param newEntity The entity for populate null values
     * @param oldEntity The entity used to populate the null values
     */
    public static void setNullValues(GenericEntity<?> newEntity, GenericEntity<?> oldEntity) {
        Field[] fields = newEntity.getClass().getDeclaredFields();

        for (var field : fields) {
            field.setAccessible(true);

            try {
                if (field.get(newEntity) == null) {
                    Field[] oldFields = oldEntity.getClass().getDeclaredFields();
                    for (var oldField : oldFields) {
                        oldField.setAccessible(true);
                        if (oldField.getName().equals(field.getName())) {
                            field.set(newEntity, oldField.get(oldEntity));
                            oldField.setAccessible(false);
                            break;
                        }
                        oldField.setAccessible(false);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            field.setAccessible(false);
        }
    }

    public static void copyProperties(Object source, Object target, String... ignoreProperties) {
        Field[] sourceFields = source.getClass().getDeclaredFields();
        Field[] targetFields = target.getClass().getDeclaredFields();
        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

        try {
            for (Field sourceField : sourceFields) {
                sourceField.setAccessible(true);

                if (ignoreList == null || !ignoreList.contains(sourceField.getName()) && !(sourceField.getType().getPackageName().equals(HibernateProxy.class.getPackageName()))) {
                    for (Field targetField : targetFields) {
                        targetField.setAccessible(true);

                        if (sourceField.getName().equals(targetField.getName())) {
                            if (sourceField.getType().getSuperclass() != null && sourceField.getType().getSuperclass().equals(GenericEntity.class)) {
                                if (targetField.get(target) == null)
                                    targetField.set(target, targetField.getType().getConstructor().newInstance());
                                copyProperties(sourceField.get(source), targetField.get(target));
                            } /*else if (sourceField.get(source) instanceof Collection<?>) {
                                Collection<?> collectionsSource = (Collection<?>) sourceField.get(source);
                                collectionsSource.forEach(
                                        value -> {
                                            try {
                                                ParameterizedType parameterizedType = (ParameterizedType) targetField.getGenericType();
                                                Class<?> clazz = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                                                Object target2 = clazz.getConstructor().newInstance();
                                                copyProperties(value, target2);
                                            } catch (IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {

                                            }
                                        }
                                );
                            }*/ else if (sourceField.getType().equals(targetField.getType())) {
                                targetField.set(target, sourceField.get(source));
                                targetField.setAccessible(false);
                                break;
                            }
                        }

                        targetField.setAccessible(false);
                    }
                }

                sourceField.setAccessible(false);
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }

    }

    /**
     * Return all fields of a class.
     *
     * @param fields    Array instance to populate
     * @param classType Class object
     * @return
     */
    public static List<Field> getAllFields(List<Field> fields, Class<?> classType) {
        fields.addAll(Arrays.asList(classType.getDeclaredFields()));

        if (classType.getSuperclass() != null) {
            getAllFields(fields, classType.getSuperclass(), true);
        }

        return fields;
    }

    /**
     * Return all fields of a class.
     *
     * @param fields              Array instance to populate
     * @param classType           Class object
     * @param includeSuperclasses If it is or not to include superclass fields on array
     * @return Array of Fields
     */
    public static List<Field> getAllFields(List<Field> fields, Class<?> classType, boolean includeSuperclasses) {
        fields.addAll(Arrays.asList(classType.getDeclaredFields()));

        if (classType.getSuperclass() != null && includeSuperclasses) {
            getAllFields(fields, classType.getSuperclass(), includeSuperclasses, null);
        }

        return fields;
    }

    /**
     * Return all fields of a class.
     *
     * @param fields              Array instance to populate
     * @param classType           Class object
     * @param includeSuperclasses If it is or not to include superclass fields on array
     * @param level               The level of how much superclasses is to get fields
     * @return All fields
     */
    public static List<Field> getAllFields(List<Field> fields, Class<?> classType, boolean includeSuperclasses, Integer level) {
        level = level;
        fields.addAll(Arrays.asList(classType.getDeclaredFields()));

        if (classType.getSuperclass() != null && includeSuperclasses && (level == null || level > 0)) {
            getAllFields(fields, classType.getSuperclass(), includeSuperclasses, level == null ? level : level--);
        }

        return fields;
    }
}


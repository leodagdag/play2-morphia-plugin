package leodagdag.play2morphia;

import com.google.code.morphia.annotations.Transient;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import leodagdag.play2morphia.utils.MorphiaLogger;
import play.Application;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * User: leodagdag
 * Date: 09/04/12
 * Time: 19:06
 */
class MorphiaEnhancer {

    /**
     * Enhance Model classes wit annotation
     *  com.google.code.morphia.annotations.Entity
     *  com.google.code.morphia.annotations.Embedded
     *
     * Add @com.google.code.morphia.annotations.Transient to all Blob fields
     * @param application
     */
    public static void enhanceModels(final Application application) {
        ClassPool classPool = ClassPool.getDefault();
        String classPath = "models";
        try {
            classPool.appendClassPath(classPath);
        } catch (NotFoundException e) {
            MorphiaLogger.error(e, "Error in appendClassPath(%s)", classPath);
            throw new RuntimeException(e);
        }
        Set<String> classes = new HashSet<String>();
        classes.addAll(application.getTypesAnnotatedWith("models", com.google.code.morphia.annotations.Entity.class));
        classes.addAll(application.getTypesAnnotatedWith("models", com.google.code.morphia.annotations.Embedded.class));
        for (String clazz : classes) {
            try {
                Class c = Class.forName(clazz);
                final Field[] fields = c.getFields();
                for (Field field : fields) {
                    // Add @com.google.code.morphia.annotations.Transient to all Blob.class fields
                    if (Blob.class.equals(field.getType())) {
                        try {
                            MorphiaLogger.debug("Start enhancement of [%s]", clazz);
                            ClassPath cp = new ClassClassPath(c);
                            classPool.appendClassPath(cp);
                            CtClass ctClass = classPool.get(clazz);
                            CtField ctField = ctClass.getField(field.getName());
                            AnnotationsAttribute aa = createAnnotation(ctClass, Transient.class.getName());
                            ctField.getFieldInfo().addAttribute(aa);
                            MorphiaLogger.debug("End enhancement of [%s]", clazz);
                        } catch (NotFoundException e) {
                            MorphiaLogger.error(e, "Error in Enhancement of class [%s]", clazz);
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                MorphiaLogger.error(e, "Error in Enhancement of class [%s]", clazz);
                throw new RuntimeException(e);
            }
        }

    }

    private static AnnotationsAttribute createAnnotation(final CtClass ctClass, String annotation) {
        final ConstPool csp = ctClass.getClassFile().getConstPool();
        final AnnotationsAttribute aa = new AnnotationsAttribute(csp, AnnotationsAttribute.visibleTag);
        aa.addAnnotation(new Annotation(annotation, csp));
        return aa;
    }
}

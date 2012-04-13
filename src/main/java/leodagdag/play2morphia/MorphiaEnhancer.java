package leodagdag.play2morphia;

import com.google.code.morphia.annotations.Transient;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import leodagdag.play2morphia.utils.MorphiaLogger;
import play.Application;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: leodagdag
 * Date: 09/04/12
 * Time: 19:06
 */
class MorphiaEnhancer {

    /**
     * Enhance Model classes wit annotation
     * com.google.code.morphia.annotations.Entity
     * com.google.code.morphia.annotations.Embedded
     * <p/>
     * Add @com.google.code.morphia.annotations.Transient to all Blob fields
     *
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
        boolean hasBlobField = false;
        List<String> blobsFields = new ArrayList<String>();

        for (String clazz : classes) {
            try {
                Class c = Class.forName(clazz);
                CtClass ctClass = null;
                final Field[] fields = c.getFields();
                for (Field field : fields) {
                    // Add @com.google.code.morphia.annotations.Transient to all Blob.class fields
                    if (Blob.class.equals(field.getType())) {
                        try {
                            hasBlobField = true;
                            blobsFields.add(field.getName());

                            MorphiaLogger.debug("Start enhancement of [%s]", clazz);
                            ClassPath cp = new ClassClassPath(c);
                            classPool.appendClassPath(cp);
                            ctClass = classPool.get(clazz);
                            CtField ctField = ctClass.getField(field.getName());
                            AnnotationsAttribute aa = createAnnotation(ctClass, Transient.class.getName());
                            ctField.getFieldInfo().addAttribute(aa);
                            MorphiaLogger.debug("End enhancement of [%s]", clazz);
                        } catch (NotFoundException e) {
                            MorphiaLogger.error(e, "Error in Enhancement of class [%s]", clazz);
                        }
                    }
                }
                if (hasBlobField) {
                    try {
                        // Enhance method loadBlobs
                        StringBuilder sb = new StringBuilder("protected void loadBlobs() {");

                        for (String blob : blobsFields) {
                            sb.append(String.format("{System.out.println(\"debut: \" + %s);String fileName = computeBlobFileName(\"%s\"); System.out.println(fileName); leodagdag.play2morphia.Blob b = new leodagdag.play2morphia.Blob(fileName); if (b.exists()) {%s = b;}} ", blob, blob, blob));
                        }
                        /*sb.append("blobFieldsTracker.clear();");*/
                        sb.append("}");


                        CtMethod method = CtMethod.make(sb.toString(), ctClass);
                        ctClass.addMethod(method);
                        ctClass.defrost();
                    } catch (CannotCompileException e) {
                        MorphiaLogger.error(e, "Error in Enhancement of class [%s]", clazz);
                        throw new RuntimeException(e);
                    } finally {
                        blobsFields.clear();
                        hasBlobField = false;
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

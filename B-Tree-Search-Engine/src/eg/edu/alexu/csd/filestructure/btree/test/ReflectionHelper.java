package eg.edu.alexu.csd.filestructure.btree.test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

class ReflectionHelper {

    static List<Class<?>> findClassesImplementing(final Class<?> interfaceClass, final Package fromPackage) {

        if (interfaceClass == null) {
            System.out.println("Unknown subclass.");
            return null;
        }

        if (fromPackage == null) {
            System.out.println("Unknown package.");
            return null;
        }

        final List<Class<?>> rVal = new ArrayList<>();
        try {
            final Class<?>[] targets = getAllClassesFromPackage(fromPackage.getName());
            for (Class<?> aTarget : targets) {
                if (aTarget != null && !aTarget.equals(interfaceClass) && interfaceClass.isAssignableFrom(aTarget)) {
                    rVal.add(aTarget);
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Error reading package name.");
            //System.out.printStackTrace(e, System.out.LOW_LEVEL);
        } catch (IOException e) {
            System.out.println("Error reading classes in package.");
            //System.out.printStackTrace(e, System.out.LOW_LEVEL);
        }

        return rVal;
    }

    /**
     * Load all classes from a package.
     *
     * @param packageName package name.
     * @return classes.
     * @throws ClassNotFoundException exception.
     * @throws IOException exception.
     */
    private static Class<?>[] getAllClassesFromPackage(final String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[0]);
    }

    /**
     * Find file in package.
     *
     * @param directory dir.
     * @param packageName package name.
     * @return list of classes
     * @throws ClassNotFoundException exception.
     */
    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return filterConcerteClasses(classes);
    }

    private static List<Class<?>> filterConcerteClasses(List<Class<?>> classes) {

        List<Class<?>> filteredClasses = null;

        for (Class<?> fetchedClass : classes) {
            int modifiers = fetchedClass.getModifiers();

            if (!Modifier.isInterface(modifiers) && !Modifier.isAbstract(modifiers) && Modifier.isPublic(modifiers)) {
                if (filteredClasses == null) {
                    filteredClasses = new ArrayList<>();
                }

                filteredClasses.add(fetchedClass);
            }
        }

        return filteredClasses;
    }

}

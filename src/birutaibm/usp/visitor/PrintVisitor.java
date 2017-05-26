package birutaibm.usp.visitor;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

/**
 * Based on: http://www.javaworld.com/article/2077602/learn-java/java-tip-98--reflect-on-the-visitor-design-pattern.html
 * @author rafael
 *
 */
public class PrintVisitor implements ReflectiveVisitor {
	public void visitCollection(Collection<?> collection) {
		Iterator<?> iterator = collection.iterator();
		while (iterator.hasNext()) {
			Object o = iterator.next();
			visit(o);
		}
	}
	
	public void visitString(String string) {
		System.out.println("'"+string+"'");
	}
	
	public void visitFloat(Float f) {
		System.out.println(f.toString()+"f");
	}
	
	public void visit(Object o) {
		try {
			Method method = getMethod(o.getClass());
			method.invoke(this, new Object[] {o});
		} catch (Exception e) { }
	}
	

	protected Method getMethod(Class<?> c) {
		Class<?> newc = c;
		Method m = null;
		// Try the superclasses
		while (m == null && newc != Object.class) {
			String method = newc.getName();
			method = "visit" + method.substring(method.lastIndexOf('.') + 1);
			try {
				m = getClass().getMethod(method, new Class[] {newc});
			} catch (NoSuchMethodException e) {
				newc = newc.getSuperclass();
			}
		}
		// Try the interfaces.  If necessary, you
		// can sort them first to define 'visitable' interface wins
		// in case an object implements more than one.
		if (newc == Object.class) {
			Class[] interfaces = c.getInterfaces();
			for (int i = 0; i < interfaces.length; i++) {
				String method = interfaces[i].getName();
				method = "visit" + method.substring(method.lastIndexOf('.') + 1);
				try {
					m = getClass().getMethod(method, new Class[] {interfaces[i]});
				} catch (NoSuchMethodException e) {}
			}
		}
		if (m == null) {
			try {
				m = getClass().getMethod("visitObject", new Class[] {Object.class});
			} catch (Exception e) {
				// Can't happen
			}
		}
		return m;
	}
}
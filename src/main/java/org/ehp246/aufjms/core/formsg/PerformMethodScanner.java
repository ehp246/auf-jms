package org.ehp246.aufjms.core.formsg;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ehp246.aufjms.api.endpoint.PerformAufM;
import org.springframework.util.StringUtils;

/**
 * Utility to collect Perform methods of a given type.
 * 
 * @author Lei Yang
 *
 */
public class PerformMethodScanner {

	private PerformMethodScanner() {
	}

	public static List<Method> listPerform(Class<?> type) {
		return Arrays.stream(type.getMethods())
				.filter(method -> method.getName().equals("perform") || method.getAnnotation(PerformAufM.class) != null)
				.collect(Collectors.toList());
	}

	/**
	 * Returns one if any is found. Or null.
	 * 
	 * @param type
	 * @return
	 */
	public static Method findPerform(Class<?> type) {
		return listPerform(type).stream().findAny().orElse(null);
	}

	public static Map<String, Method> mapPerform(Class<?> type) {
		// Split named-by-convention from annotated
		final Map<Boolean, List<Method>> split = Arrays.stream(type.getMethods())
				.filter(method -> method.getName().equals("perform") || method.getAnnotation(PerformAufM.class) != null)
				.collect(Collectors.partitioningBy(method -> method.getAnnotation(PerformAufM.class) != null));

		final Map<String, Method> performs = new HashMap<>();
				
		split.get(true).stream().forEach(method -> {
			final String[] value = method.getAnnotation(PerformAufM.class).type();
			if (value.length == 0) {
				performs.put(null, method);
			} else {
				Arrays.stream(value).forEach(name -> {
					if (performs.containsKey(name)) {
						throw new RuntimeException(
								"Duplicate named Perform " + StringUtils.quote(name) + " on " + type.getName());
					}
					performs.put(name, method);
				});
			}
		});

		if (performs.get(null) == null) {
			// Search for named by convention as an optional default.
			List<Method> named = split.get(false);
			if (named.size() > 0) {
				if (named.size() != 1) {
					throw new RuntimeException("Found multiple default Perform on " + type.getName());
				}
				performs.put(null, named.get(0));
			}
		}
		return performs;
	}

}

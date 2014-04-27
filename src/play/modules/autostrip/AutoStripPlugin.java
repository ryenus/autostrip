package play.modules.autostrip;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import play.PlayPlugin;
import play.data.binding.ParamNode;
import play.data.binding.RootParamNode;

/**
 * This plugin automatically strip parameter values if the bean class
 * has the {@link play.modules.autostrip.AutoStrip} annotation.
 * 
 * Internally we use {@link org.apache.commons.lang.StringUtils#stripToEmpty(String)}
 * to strip Strings and eliminate null values.
 * 
 * @author ryenus
 *
 */
public class AutoStripPlugin extends PlayPlugin {

	/**
	 * Strip all parameters values if the bean class has the
	 * {@link play.modules.autostrip.AutoStrip} annotation.
	 * <p>
	 * This relies on the fact that<br>
	 * 1. we have a reference to the rootParamNode, not a copy,
	 *    so that we can modify its content directly.
	 * 2. we always return null to let the built-in Binder class
	 *    continue to bind our modified rootParamNode.
	 */
	@Override
	public Object bind(RootParamNode rootParamNode, String name, Class<?> clazz, Type type, Annotation[] annotations) {
		AutoStrip stripAnno = clazz.getAnnotation(AutoStrip.class);
		if (stripAnno != null) {
			ParamNode paramNode = rootParamNode.getChild(name);
			if (paramNode != null) {
				strip(paramNode);
			}
		}

		// always return null, this is intentional.
		return null;
	}

	private void strip(ParamNode paramNode) {
		String[] values = paramNode.getValues();
		if (values != null && values.length > 0) {
			for (int i = 0; i < values.length; i++) {
				String value = values[i];
				values[i] = StringUtils.stripToEmpty(value);
			}
			//paramNode.setValue(values, paramNode.getOriginalKey());
		}

		Collection<ParamNode> children = paramNode.getAllChildren();
		for (ParamNode child : children) {
			strip(child);
		}
	}

}


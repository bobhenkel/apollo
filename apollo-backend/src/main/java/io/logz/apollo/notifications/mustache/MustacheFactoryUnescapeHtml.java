package io.logz.apollo.notifications.mustache;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheException;

import java.io.IOException;
import java.io.Writer;

/**
 * The mustache factory supplied by default escapes html characters
 * In order to prevent this we extended the class and override the encode method to inject the value as it is without html escaping
 **/
public class MustacheFactoryUnescapeHtml extends DefaultMustacheFactory {

    @Override
    public void encode(String value, Writer writer) {
        int position = 0;
        int length = value.length();
        try {
            writer.append(value, position, length);
        } catch (IOException e) {
            throw new MustacheException("Failed to encode value: " + value);
        }
    }
}

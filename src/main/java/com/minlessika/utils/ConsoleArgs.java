package com.minlessika.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ConsoleArgs implements Args {

	private final String prefix;
	private final String[] args;
	
	public ConsoleArgs(final String[] args) {
		this("", args);
	}
	
	public ConsoleArgs(final String prefix, final String[] args) {
		this.prefix = prefix;
		this.args = args;
	}
	
	@Override
	public Map<String, String> asMap() {
		final Map<String, String> map = new HashMap<>(0);
        final Pattern ptn = Pattern.compile(String.format("%s([a-z\\-]+)(=.+)?", prefix));
        for (final String arg : args) {
            final Matcher matcher = ptn.matcher(arg);
            if (!matcher.matches()) {
                throw new IllegalStateException(
                    String.format("can't parse this argument: '%s'", arg)
                );
            }
            final String value = matcher.group(2);
            if (value == null) {
                map.put(matcher.group(1), "");
            } else {
                map.put(matcher.group(1), value.substring(1));
            }
        }
        return map;
	}

}

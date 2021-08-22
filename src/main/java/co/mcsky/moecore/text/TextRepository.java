package co.mcsky.moecore.text;

import me.lucko.helper.utils.annotation.NonnullByDefault;

import java.util.function.Function;

@NonnullByDefault
public class TextRepository {

    protected String placeholderPrefix = "{";
    protected String placeholderSuffix = "}";
    protected int decimalPrecision = 3;
    protected Function<String, String> provider;

    /**
     * @param provider the provider of message dictionary
     */
    public TextRepository(Function<String, String> provider) {
        this.provider = provider;
    }

    public void placeholderPrefix(String prefix) {
        placeholderPrefix = prefix;
    }

    public void placeholderSuffix(String suffix) {
        placeholderSuffix = suffix;
    }

    public void decimalPrecision(int precision) {
        decimalPrecision = precision;
    }

    public Text get(String key) {
        return new Text(this, provider.apply(key));
    }

}

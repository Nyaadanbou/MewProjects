package cc.mewcraft.mewcore.util;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RangeUtils {

    // --- Common symbols ---
    private static final String POSITIVE_INFINITY = "+∞";
    private static final String NEGATIVE_INFINITY = "-∞";

    // --- Common predicates ---
    private static final Predicate<String> TEST_POSITIVE_INFINITY = s -> s.equals(POSITIVE_INFINITY);
    private static final Predicate<String> TEST_NEGATIVE_INFINITY = s -> s.equals(NEGATIVE_INFINITY);
    private static final Predicate<String> TEST_OPEN = s -> s.equals("(") || s.equals(")");
    private static final Predicate<String> TEST_CLOSED = s -> s.equals("[") || s.equals("]");

    /**
     * Debugger of this regex: <a href="https://regex101.com/r/6cDUDB/1">regex101</a>
     */
    private static final Pattern INTERVAL_PATTERN = Pattern.compile(
        "([\\[(])([-+]?(?:\\d+|∞)),([-+]?(?:\\d+|∞))([])])"
    );

    /**
     * Parse the {@link String} into {@link Range<Integer>}.
     * <p>
     * It accepts the following notations:
     *
     * <blockquote>
     *
     * <table>
     * <caption>Range Types</caption>
     * <tr><th>Notation        <th>Definition               <th>Factory method
     * <tr><td>{@code (a..b)}  <td>{@code {x | a < x < b}}  <td>{@link Range#open open}
     * <tr><td>{@code [a..b]}  <td>{@code {x | a <= x <= b}}<td>{@link Range#closed closed}
     * <tr><td>{@code (a..b]}  <td>{@code {x | a < x <= b}} <td>{@link Range#openClosed openClosed}
     * <tr><td>{@code [a..b)}  <td>{@code {x | a <= x < b}} <td>{@link Range#closedOpen closedOpen}
     * <tr><td>{@code (a..+∞)} <td>{@code {x | x > a}}      <td>{@link Range#greaterThan greaterThan}
     * <tr><td>{@code [a..+∞)} <td>{@code {x | x >= a}}     <td>{@link Range#atLeast atLeast}
     * <tr><td>{@code (-∞..b)} <td>{@code {x | x < b}}      <td>{@link Range#lessThan lessThan}
     * <tr><td>{@code (-∞..b]} <td>{@code {x | x <= b}}     <td>{@link Range#atMost atMost}
     * <tr><td>{@code (-∞..+∞)}<td>{@code {x}}              <td>{@link Range#all all}
     * </table>
     *
     * </blockquote>
     *
     * @param val a string in interval representation
     *
     * @return the Range parsed from given String
     */
    public static @NotNull Range<Integer> of(@NotNull String val) {
        // Cleanse the string
        val = val.replace(" ", "").trim();

        Matcher matcher = INTERVAL_PATTERN.matcher(val);
        if (!matcher.matches() || matcher.groupCount() != 4) {
            throw new IllegalArgumentException("Failed to parse: " + val);
        }

        String lowerBoundTypeRaw = matcher.group(1);
        String lowerEndpointValueRaw = matcher.group(2);
        String upperEndpointValueRaw = matcher.group(3);
        String upperBoundTypeRaw = matcher.group(4);

        if (TEST_NEGATIVE_INFINITY.test(lowerEndpointValueRaw)) {
            if (TEST_POSITIVE_INFINITY.test(upperEndpointValueRaw)) {
                // (-∞..+∞)
                return Range.all();
            }
            if (parseBoundType(upperBoundTypeRaw) == BoundType.OPEN) {
                // (-∞..b)
                return Range.lessThan(Integer.parseInt(upperEndpointValueRaw));
            }
            if (parseBoundType(upperBoundTypeRaw) == BoundType.CLOSED) {
                // (-∞..b]
                return Range.atMost(Integer.parseInt(upperEndpointValueRaw));
            }
        }

        if (TEST_POSITIVE_INFINITY.test(upperEndpointValueRaw)) {
            if (parseBoundType(lowerBoundTypeRaw) == BoundType.OPEN) {
                // (a..+∞)
                return Range.greaterThan(Integer.parseInt(lowerEndpointValueRaw));
            }
            if (parseBoundType(lowerBoundTypeRaw) == BoundType.CLOSED) {
                // [a..+∞)
                return Range.atLeast(Integer.parseInt(lowerEndpointValueRaw));
            }
        }

        if (TEST_OPEN.test(lowerBoundTypeRaw)) {
            if (TEST_OPEN.test(upperBoundTypeRaw)) {
                // (a..b)
                return Range.open(Integer.parseInt(lowerEndpointValueRaw), Integer.parseInt(upperEndpointValueRaw));
            }
            if (TEST_CLOSED.test(upperBoundTypeRaw)) {
                // (a..b]
                return Range.openClosed(Integer.parseInt(lowerEndpointValueRaw), Integer.parseInt(upperEndpointValueRaw));
            }
        }

        if (TEST_CLOSED.test(lowerBoundTypeRaw)) {
            if (TEST_OPEN.test(upperBoundTypeRaw)) {
                // [a..b)
                return Range.closedOpen(Integer.parseInt(lowerEndpointValueRaw), Integer.parseInt(upperEndpointValueRaw));
            }
            if (TEST_CLOSED.test(upperBoundTypeRaw)) {
                // [a..b]
                return Range.closed(Integer.parseInt(lowerEndpointValueRaw), Integer.parseInt(upperEndpointValueRaw));
            }
        }

        throw new IllegalArgumentException("Failed to parse: " + val);
    }

    private static @NotNull BoundType parseBoundType(String val) {
        if (TEST_CLOSED.test(val)) return BoundType.CLOSED;
        if (TEST_OPEN.test(val)) return BoundType.OPEN;
        throw new IllegalArgumentException("Failed to parse: " + val);
    }

}

package skillbox.code.utils;

import org.apache.commons.lang3.StringUtils;

public class ReportUtil {

    public static String getSeparatorTemplate(int[] columnsWidth, int size) {
        StringBuilder builder = new StringBuilder("+");
        for (int i = 0; i < size; i++) {
            builder.append(StringUtils.repeat("-", columnsWidth[i] + 2));
            builder.append("+");
        }
        builder.append("\n");
        return builder.toString();
    }
    public static String getTableTemplate(int[] columnsWidth, int size) {
        StringBuilder builder = new StringBuilder("|");
        for (int i = 0; i < size; i++) {
            builder.append(" %-");
            builder.append(columnsWidth[i]);
            builder.append("s |");
        }
        builder.append("\n");
        return builder.toString();
    }
}

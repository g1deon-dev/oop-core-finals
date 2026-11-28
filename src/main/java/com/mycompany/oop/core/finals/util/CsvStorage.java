package com.mycompany.oop.core.finals.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Thread-safe RFC-4180 CSV table storage helper under the local "data/" directory.
 */
public final class CsvStorage {

    private static final Path DATA_DIR = Paths.get("data");
    private static final Object LOCK = new Object();

    private CsvStorage() {
    }

    public static void ensureTable(String fileName, String[] headers) throws SQLException {
        synchronized (LOCK) {
            try {
                if (!Files.exists(DATA_DIR)) {
                    Files.createDirectories(DATA_DIR);
                }
                Path file = DATA_DIR.resolve(fileName);
                if (!Files.exists(file)) {
                    Files.writeString(file, toCsvLine(headers) + System.lineSeparator(), StandardCharsets.UTF_8);
                }
            } catch (IOException e) {
                throw new SQLException("Failed to initialize CSV table " + fileName + ": " + e.getMessage(), e);
            }
        }
    }

    public static List<String[]> readAllRows(String fileName, String[] headers) throws SQLException {
        synchronized (LOCK) {
            ensureTable(fileName, headers);
            Path file = DATA_DIR.resolve(fileName);
            try {
                String content = Files.readString(file, StandardCharsets.UTF_8);
                List<String[]> parsed = parseCsv(content);
                if (parsed.isEmpty()) {
                    return new ArrayList<>();
                }
                // Skip header row
                return new ArrayList<>(parsed.subList(1, parsed.size()));
            } catch (IOException e) {
                throw new SQLException("Failed to read CSV table " + fileName + ": " + e.getMessage(), e);
            }
        }
    }

    public static void writeAllRows(String fileName, String[] headers, List<String[]> rows) throws SQLException {
        synchronized (LOCK) {
            ensureTable(fileName, headers);
            Path file = DATA_DIR.resolve(fileName);
            StringBuilder sb = new StringBuilder();
            sb.append(toCsvLine(headers)).append(System.lineSeparator());
            for (String[] row : rows) {
                sb.append(toCsvLine(row)).append(System.lineSeparator());
            }
            try {
                Files.writeString(file, sb.toString(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new SQLException("Failed to write CSV table " + fileName + ": " + e.getMessage(), e);
            }
        }
    }

    public static int nextIntId(List<String[]> rows, int idCol) {
        int max = 0;
        for (String[] row : rows) {
            if (row.length > idCol && !row[idCol].isEmpty()) {
                int val = Integer.parseInt(row[idCol]);
                if (val > max) {
                    max = val;
                }
            }
        }
        return max + 1;
    }

    public static long nextLongId(List<String[]> rows, int idCol) {
        long max = 0;
        for (String[] row : rows) {
            if (row.length > idCol && !row[idCol].isEmpty()) {
                long val = Long.parseLong(row[idCol]);
                if (val > max) {
                    max = val;
                }
            }
        }
        return max + 1;
    }

    public static String str(Object val) {
        return val == null ? "" : val.toString();
    }

    public static Integer parseNullableInt(String s) {
        return (s == null || s.isEmpty()) ? null : Integer.parseInt(s);
    }

    public static Long parseNullableLong(String s) {
        return (s == null || s.isEmpty()) ? null : Long.parseLong(s);
    }

    public static LocalDateTime parseNullableDateTime(String s) {
        return (s == null || s.isEmpty()) ? null : LocalDateTime.parse(s);
    }

    public static String toCsvLine(String[] fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            String f = fields[i] == null ? "" : fields[i];
            boolean needsQuotes = f.indexOf(',') >= 0 || f.indexOf('"') >= 0
                    || f.indexOf('\n') >= 0 || f.indexOf('\r') >= 0;
            if (needsQuotes) {
                sb.append('"');
                for (int j = 0; j < f.length(); j++) {
                    char c = f.charAt(j);
                    if (c == '"') {
                        sb.append("\"\"");
                    } else {
                        sb.append(c);
                    }
                }
                sb.append('"');
            } else {
                sb.append(f);
            }
        }
        return sb.toString();
    }

    private static List<String[]> parseCsv(String content) {
        List<String[]> rows = new ArrayList<>();
        if (content == null || content.isEmpty()) {
            return rows;
        }
        List<String> currentRow = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < content.length() && content.charAt(i + 1) == '"') {
                        field.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    field.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    currentRow.add(field.toString());
                    field.setLength(0);
                } else if (c == '\r') {
                    if (i + 1 < content.length() && content.charAt(i + 1) == '\n') {
                        i++;
                    }
                    currentRow.add(field.toString());
                    field.setLength(0);
                    if (!(currentRow.size() == 1 && currentRow.get(0).isEmpty())) {
                        rows.add(currentRow.toArray(new String[0]));
                    }
                    currentRow.clear();
                } else if (c == '\n') {
                    currentRow.add(field.toString());
                    field.setLength(0);
                    if (!(currentRow.size() == 1 && currentRow.get(0).isEmpty())) {
                        rows.add(currentRow.toArray(new String[0]));
                    }
                    currentRow.clear();
                } else {
                    field.append(c);
                }
            }
        }
        if (field.length() > 0 || !currentRow.isEmpty()) {
            currentRow.add(field.toString());
            if (!(currentRow.size() == 1 && currentRow.get(0).isEmpty())) {
                rows.add(currentRow.toArray(new String[0]));
            }
        }
        return rows;
    }
}

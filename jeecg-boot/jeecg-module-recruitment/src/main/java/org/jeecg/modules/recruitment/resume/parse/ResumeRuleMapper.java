package org.jeecg.modules.recruitment.resume.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 将 Tika 纯文本用规则/正则映射到现有简历 JSON 模板字段。
 * 目标字段与智谱抽取模板对齐，便于前端 setDataByPDF 回填。
 */
@Component
public class ResumeRuleMapper {

    private static final Pattern EMAIL = Pattern.compile(
            "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
    private static final Pattern MOBILE = Pattern.compile(
            "(?<!\\d)(1[3-9]\\d{9})(?!\\d)");
    private static final Pattern ID_CARD = Pattern.compile(
            "(?<!\\d)([1-9]\\d{5}(?:19|20)\\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx])(?!\\d)");
    private static final Pattern BIRTHDAY = Pattern.compile(
            "(?:出生(?:年月|日期)?|生日)[:：\\s]*((?:19|20)\\d{2}[-/.年](?:0?[1-9]|1[0-2])(?:[-/.月](?:0?[1-9]|[12]\\d|3[01]))?)");
    private static final Pattern NAME_LINE = Pattern.compile(
            "(?:姓\\s*名|姓名)[:：\\s]*([\\u4e00-\\u9fa5·]{2,20})");
    private static final Pattern SEX = Pattern.compile(
            "(?:性\\s*别|性别)[:：\\s]*(男|女)");
    private static final Pattern NATION = Pattern.compile(
            "(?:民\\s*族|民族)[:：\\s]*([\\u4e00-\\u9fa5]{1,10})");
    private static final Pattern POLITIC = Pattern.compile(
            "(?:政治面貌)[:：\\s]*([\\u4e00-\\u9fa5]{2,20})");
    private static final Pattern NATIVE = Pattern.compile(
            "(?:籍\\s*贯|籍贯)[:：\\s]*([\\u4e00-\\u9fa5A-Za-z0-9\\-、，,]{2,40})");
    private static final Pattern HUKOU = Pattern.compile(
            "(?:户口所在地|户籍|生源地)[:：\\s]*([\\u4e00-\\u9fa5A-Za-z0-9\\-、，,]{2,60})");
    private static final Pattern GRADUATE_COLLEGE = Pattern.compile(
            "(?:毕业院校|院校|学校)[:：\\s]*([\\u4e00-\\u9fa5A-Za-z0-9（）()]{2,60})");
    private static final Pattern EDUCATION = Pattern.compile(
            "(?:最高学历|学\\s*历|学历)[:：\\s]*(博士研究生|硕士研究生|研究生|大学本科|本科|大专|专科|高中|中专)");
    private static final Pattern DEGREE = Pattern.compile(
            "(?:最高学位|学\\s*位|学位)[:：\\s]*(博士|硕士|学士|无)");
    private static final Pattern PROFESSION = Pattern.compile(
            "(?:专\\s*业|专业)[:：\\s]*([\\u4e00-\\u9fa5A-Za-z0-9（）()/\\-]{2,40})");
    private static final Pattern WORK_UNIT = Pattern.compile(
            "(?:目前工作单位|工作单位)[:：\\s]*([\\u4e00-\\u9fa5A-Za-z0-9（）()]{2,60})");

    private static final String[] CORE_KEYS = {
            "name", "sex", "birthday", "nation", "idNumber", "politicBackground",
            "hukou", "nativePlace", "graduateCollege", "education", "degree",
            "profession", "mobile", "email", "workUnit"
    };

    public static class MapResult {
        public final JSONObject json;
        public final List<String> filled;
        public final List<String> missed;

        public MapResult(JSONObject json, List<String> filled, List<String> missed) {
            this.json = json;
            this.filled = filled;
            this.missed = missed;
        }
    }

    public MapResult map(String rawText) {
        String text = normalize(rawText);
        JSONObject out = new JSONObject(new LinkedHashMap<>());

        put(out, "name", first(NAME_LINE, text));
        if (!out.containsKey("name")) {
            // 启发式：首行短中文名
            String guess = guessNameFromHeader(text);
            if (guess != null) {
                out.put("name", guess);
            }
        }
        String sex = first(SEX, text);
        if (sex != null) {
            out.put("sex", "女".equals(sex) ? "2" : "1");
        }
        put(out, "birthday", normalizeDate(first(BIRTHDAY, text)));
        put(out, "nation", first(NATION, text));
        put(out, "idNumber", first(ID_CARD, text));
        put(out, "politicBackground", first(POLITIC, text));
        put(out, "hukou", first(HUKOU, text));
        put(out, "nativePlace", first(NATIVE, text));
        put(out, "graduateCollege", first(GRADUATE_COLLEGE, text));
        put(out, "education", first(EDUCATION, text));
        put(out, "degree", first(DEGREE, text));
        put(out, "profession", first(PROFESSION, text));
        put(out, "mobile", first(MOBILE, text));
        put(out, "email", first(EMAIL, text));
        put(out, "workUnit", first(WORK_UNIT, text));

        out.put("xgsResumeWorks", extractWorkSection(text));
        out.put("xgsResumeEdus", extractEduSection(text));
        out.put("xgsResumeHome", new JSONArray());

        List<String> filled = new ArrayList<>();
        List<String> missed = new ArrayList<>();
        for (String key : CORE_KEYS) {
            Object v = out.get(key);
            if (v != null && String.valueOf(v).trim().length() > 0) {
                filled.add(key);
            } else {
                missed.add(key);
            }
        }
        if (out.getJSONArray("xgsResumeWorks") != null && !out.getJSONArray("xgsResumeWorks").isEmpty()) {
            filled.add("xgsResumeWorks");
        } else {
            missed.add("xgsResumeWorks");
        }
        if (out.getJSONArray("xgsResumeEdus") != null && !out.getJSONArray("xgsResumeEdus").isEmpty()) {
            filled.add("xgsResumeEdus");
        } else {
            missed.add("xgsResumeEdus");
        }
        return new MapResult(out, filled, missed);
    }

    private static String normalize(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.replace('\u00A0', ' ')
                .replaceAll("[ \\t\\x0B\\f\\r]+", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    private static void put(JSONObject o, String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            o.put(key, value.trim());
        }
    }

    private static String first(Pattern p, String text) {
        Matcher m = p.matcher(text);
        if (m.find()) {
            return m.group(1).trim();
        }
        return null;
    }

    private static String normalizeDate(String raw) {
        if (raw == null) {
            return null;
        }
        String s = raw.replace('年', '-').replace('月', '-').replace('日', ' ')
                .replace('/', '-').replace('.', '-').trim();
        s = s.replaceAll("-+", "-").replaceAll("\\s+", "");
        if (s.endsWith("-")) {
            s = s.substring(0, s.length() - 1);
        }
        // yyyy-M -> yyyy-MM-01 ; yyyy-M-d -> pad
        String[] parts = s.split("-");
        if (parts.length >= 2) {
            String y = parts[0];
            String mo = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
            String d = parts.length >= 3
                    ? (parts[2].length() == 1 ? "0" + parts[2] : parts[2])
                    : "01";
            return y + "-" + mo + "-" + d;
        }
        return s;
    }

    private static String guessNameFromHeader(String text) {
        String[] lines = text.split("\\n");
        for (int i = 0; i < Math.min(lines.length, 8); i++) {
            String line = lines[i].trim();
            if (line.matches("[\\u4e00-\\u9fa5·]{2,4}") && !line.contains("简历") && !line.contains("个人")) {
                return line;
            }
        }
        return null;
    }

    private JSONArray extractWorkSection(String text) {
        JSONArray arr = new JSONArray();
        String section = sliceSection(text, "(工作经历|工作经验|任职经历)", "(教育经历|学习经历|教育背景|项目经历|技能|自我评价|家庭)");
        if (section == null || section.isEmpty()) {
            return arr;
        }
        // 简单按行块：含日期区间的行作为一条
        Pattern block = Pattern.compile(
                "((?:19|20)\\d{2}[-/.年](?:0?[1-9]|1[0-2])?)\\s*[-—~至到]+\\s*((?:19|20)\\d{2}[-/.年](?:0?[1-9]|1[0-2])?|至今|现在)?\\s*([^\\n]{2,80})");
        Matcher m = block.matcher(section);
        while (m.find() && arr.size() < 8) {
            JSONObject item = new JSONObject();
            item.put("beginDate", normalizeDate(m.group(1)));
            String end = m.group(2);
            if (end != null && !end.contains("今") && !end.contains("现在")) {
                item.put("endDate", normalizeDate(end));
            }
            String rest = m.group(3).trim();
            item.put("workUnit", rest.length() > 40 ? rest.substring(0, 40) : rest);
            arr.add(item);
        }
        return arr;
    }

    private JSONArray extractEduSection(String text) {
        JSONArray arr = new JSONArray();
        String section = sliceSection(text, "(教育经历|学习经历|教育背景)", "(工作经历|工作经验|项目经历|技能|自我评价|家庭|论文)");
        if (section == null || section.isEmpty()) {
            // 回退：用已识别的毕业院校拼一条
            return arr;
        }
        Pattern block = Pattern.compile(
                "((?:19|20)\\d{2}[-/.年](?:0?[1-9]|1[0-2])?)\\s*[-—~至到]+\\s*((?:19|20)\\d{2}[-/.年](?:0?[1-9]|1[0-2])?|至今|现在)?\\s*([^\\n]{2,80})");
        Matcher m = block.matcher(section);
        while (m.find() && arr.size() < 6) {
            JSONObject item = new JSONObject();
            item.put("beginDate", normalizeDate(m.group(1)));
            String end = m.group(2);
            if (end != null && !end.contains("今") && !end.contains("现在")) {
                item.put("endDate", normalizeDate(end));
            }
            item.put("graduateCollege", m.group(3).trim());
            arr.add(item);
        }
        return arr;
    }

    private static String sliceSection(String text, String startPat, String endPat) {
        Pattern start = Pattern.compile(startPat);
        Matcher sm = start.matcher(text);
        if (!sm.find()) {
            return null;
        }
        int from = sm.end();
        Pattern end = Pattern.compile(endPat);
        Matcher em = end.matcher(text);
        int to = text.length();
        while (em.find()) {
            if (em.start() > from) {
                to = em.start();
                break;
            }
        }
        if (to <= from) {
            return null;
        }
        return text.substring(from, to).trim();
    }
}

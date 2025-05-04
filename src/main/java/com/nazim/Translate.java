package com.nazim;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class Translate {
    private static final String GOOGLE_TRANSLATE_URL = "http://translate.google.com/translate_a/single";

    private Translate() {
    }

    public static String getDisplayLanguage(String languageCode) {
        return (new Locale(languageCode)).getDisplayLanguage();
    }

    private static String generateURL(String sourceLanguage, String targetLanguage, String text) throws UnsupportedEncodingException {
        String encoded = URLEncoder.encode(text, "UTF-8");
        StringBuilder sb = new StringBuilder();
        sb.append("http://translate.google.com/translate_a/single");
        sb.append("?client=webapp");
        sb.append("&hl=en");
        sb.append("&sl=");
        sb.append(sourceLanguage);
        sb.append("&tl=");
        sb.append(targetLanguage);
        sb.append("&q=");
        sb.append(encoded);
        sb.append("&multires=1");
        sb.append("&otf=0");
        sb.append("&pc=0");
        sb.append("&trs=1");
        sb.append("&ssel=0");
        sb.append("&tsel=0");
        sb.append("&kc=1");
        sb.append("&dt=t");
        sb.append("&ie=UTF-8");
        sb.append("&oe=UTF-8");
        sb.append("&tk=");
        sb.append(generateToken(text));
        return sb.toString();
    }

    public static String detectLanguage(String text) throws IOException {
        String urlText = generateURL("auto", "en", text);
        URL url = new URL(urlText);
        String rawData = urlToText(url);
        return findLanguage(rawData);
    }

    public static String translate(String text) throws IOException {
        return translate(Locale.getDefault().getLanguage(), text);
    }

    public static String translate(String targetLanguage, String text) throws IOException {
        return translate("auto", targetLanguage, text);
    }

    public static String translate(String sourceLanguage, String targetLanguage, String text) throws IOException {
        String urlText = generateURL(sourceLanguage, targetLanguage, text);
        URL url = new URL(urlText);
        String rawData = urlToText(url);
        if (rawData == null) {
            return null;
        } else {
            String[] raw = rawData.split("\"");
            return raw.length < 2 ? null : raw[1];
        }
    }

    private static String urlToText(URL url) throws IOException {
        URLConnection urlConn = url.openConnection();
        urlConn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:2.0) Gecko/20100101 Firefox/4.0");
        Reader r = new InputStreamReader(urlConn.getInputStream(), Charset.forName("UTF-8"));
        StringBuilder buf = new StringBuilder();

        while(true) {
            int ch = r.read();
            if (ch < 0) {
                String str = buf.toString();
                return str;
            }

            buf.append((char)ch);
        }
    }

    private static String findLanguage(String rawData) {
        for(int i = 0; i + 5 < rawData.length(); ++i) {
            boolean dashDetected = rawData.charAt(i + 4) == '-';
            if (rawData.charAt(i) == ',' && rawData.charAt(i + 1) == '"' && (rawData.charAt(i + 4) == '"' && rawData.charAt(i + 5) == ',' || dashDetected)) {
                if (dashDetected) {
                    int lastQuote = rawData.substring(i + 2).indexOf(34);
                    if (lastQuote > 0) {
                        return rawData.substring(i + 2, i + 2 + lastQuote);
                    }
                } else {
                    String possible = rawData.substring(i + 2, i + 4);
                    if (containsLettersOnly(possible)) {
                        return possible;
                    }
                }
            }
        }

        return null;
    }

    private static boolean containsLettersOnly(String text) {
        for(int i = 0; i < text.length(); ++i) {
            if (!Character.isLetter(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static int[] TKK() {
        return new int[]{406398, 2087938574};
    }

    private static int shr32(int x, int bits) {
        if (x < 0) {
            long x_l = 4294967295L + (long)x + 1L;
            return (int)(x_l >> bits);
        } else {
            return x >> bits;
        }
    }

    private static int RL(int a, String b) {
        for(int c = 0; c < b.length() - 2; c += 3) {
            int d = b.charAt(c + 2);
            d = d >= 65 ? d - 87 : d - 48;
            d = b.charAt(c + 1) == '+' ? shr32(a, d) : a << d;
            a = b.charAt(c) == '+' ? a + (d & -1) : a ^ d;
        }

        return a;
    }

    private static String generateToken(String text) {
        int[] tkk = TKK();
        int b = tkk[0];
        int e = 0;
        int f = 0;

        List<Integer> d;
        for(d = new ArrayList(); f < text.length(); ++f) {
            int g = text.charAt(f);
            if (128 > g) {
                d.add(e++, g);
            } else {
                if (2048 > g) {
                    d.add(e++, g >> 6 | 192);
                } else if (55296 == (g & 'ﰀ') && f + 1 < text.length() && 56320 == (text.charAt(f + 1) & 'ﰀ')) {
                    int var10000 = 65536 + ((g & 1023) << 10);
                    ++f;
                    g = var10000 + (text.charAt(f) & 1023);
                    d.add(e++, g >> 18 | 240);
                    d.add(e++, g >> 12 & 63 | 128);
                } else {
                    d.add(e++, g >> 12 | 224);
                    d.add(e++, g >> 6 & 63 | 128);
                }

                d.add(e++, g & 63 | 128);
            }
        }

        int a_i = b;

        for(int var12 = 0; var12 < d.size(); ++var12) {
            a_i += (Integer)d.get(var12);
            a_i = RL(a_i, "+-a^+6");
        }

        a_i = RL(a_i, "+-3^+b+-f");
        a_i ^= tkk[1];
        long a_l;
        if (0 > a_i) {
            a_l = 2147483648L + (long)(a_i & Integer.MAX_VALUE);
        } else {
            a_l = (long)a_i;
        }

        a_l = (long)((double)a_l % Math.pow((double)10.0F, (double)6.0F));
        return String.format(Locale.US, "%d.%d", a_l, a_l ^ (long)b);
    }
}


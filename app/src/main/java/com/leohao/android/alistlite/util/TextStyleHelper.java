package com.leohao.android.alistlite.util;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import static com.leohao.android.alistlite.AlistLiteApplication.context;

/**
 * @author LeoHao
 */
public class TextStyleHelper {
    private static TextStyleHelper instance = null;

    private TextStyleHelper(Context context) {
    }

    public synchronized static TextStyleHelper getInstance() {
        if (instance == null) {
            instance = new TextStyleHelper(context);
        }
        return instance;
    }

    /**
     * 将 MarkDown 文本转换为 HTML SPANNED 对象
     *
     * @param markdown MarkDown 文本
     * @return HTML SPANNED 对象
     */
    public Spanned parseMarkdownToSpanned(String markdown) {
        //创建Markdown解析器
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        //创建HTML渲染器
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String html = renderer.render(document).replaceAll("\n", "<br>");
        //使用Jsoup解析HTML并转换为Spanned
        String safeHtml = Jsoup.clean(html, Safelist.basic());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            //对于 Android 7.0 (API level 24) 及以上版本
            return Html.fromHtml(safeHtml, Html.FROM_HTML_MODE_LEGACY);
        } else {
            //对于 Android 7.0 以下版本
            return Html.fromHtml(safeHtml);
        }
    }
}

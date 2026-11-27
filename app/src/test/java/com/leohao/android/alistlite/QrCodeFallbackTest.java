package com.leohao.android.alistlite;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;

import org.junit.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * 二维码降级路径的验证：部分畸形 ROM 上 Charset.name() 非法会让 zxing 的 BYTE 模式崩溃，
 * 此时改用纯大写（ALPHANUMERIC 模式）绕开。这里确认降级后的内容仍是可解码的有效二维码。
 */
public class QrCodeFallbackTest {

    private static final String ALPHANUMERIC_SET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ $%*+-./:";

    private String toAlphanumericSafe(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        String upper = content.toUpperCase(Locale.US);
        for (int i = 0; i < upper.length(); i++) {
            if (ALPHANUMERIC_SET.indexOf(upper.charAt(i)) < 0) {
                return null;
            }
        }
        return upper;
    }

    private String roundTrip(String content) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1);
        int size = 320;
        com.google.zxing.common.BitMatrix matrix =
                new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints);

        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y * width + x] = matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
            }
        }
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        Result result = new MultiFormatReader().decode(new BinaryBitmap(new HybridBinarizer(source)));
        return result.getText();
    }

    @Test
    public void alphanumericTransformKeepsUrlUsable() {
        assertEquals("HTTPS://192.168.1.2:5245", toAlphanumericSafe("https://192.168.1.2:5245"));
        assertEquals("HTTPS://MYNAS.LOCAL:5245", toAlphanumericSafe("https://mynas.local:5245"));
        // IPv6 带方括号，不在 ALPHANUMERIC 集合内 —— 应判定为不可降级
        assertNull(toAlphanumericSafe("https://[fe80::1]:5245"));
        // 含下划线的主机名同理
        assertNull(toAlphanumericSafe("https://my_nas:5245"));
    }

    @Test
    public void uppercaseUrlStillDecodes() throws Exception {
        String transformed = toAlphanumericSafe("https://192.168.1.2:5245");
        assertEquals("HTTPS://192.168.1.2:5245", roundTrip(transformed));
    }

    @Test
    public void plainUrlDecodesToo() throws Exception {
        String text = roundTrip("https://192.168.1.2:5245");
        assertTrue(text.startsWith("https://"));
    }
}

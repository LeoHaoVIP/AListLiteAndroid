package com.leohao.android.alistlite;

import com.leohao.android.alistlite.util.ThemeHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.leohao.android.alistlite.util.StorageDiagnostic;

import java.util.List;

/**
 * 存储诊断页：逐项检查「能不能写」，把结果翻译成人话。
 *
 * @author LeoHao
 */
public class StorageDiagnosticActivity extends AppCompatActivity {

    private LinearLayout container;
    private Button refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.apply(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_diagnostic);
        container = findViewById(R.id.diag_container);
        refreshButton = findViewById(R.id.btn_diag_refresh);
        refreshButton.setOnClickListener(v -> runDiagnostic());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 从系统设置授权后返回会自动重新检测
        runDiagnostic();
    }

    private void runDiagnostic() {
        refreshButton.setEnabled(false);
        container.removeAllViews();
        new Thread(() -> {
            final List<StorageDiagnostic.Item> items = StorageDiagnostic.run(this);
            runOnUiThread(() -> render(items));
        }, "storage-diagnostic").start();
    }

    private void render(List<StorageDiagnostic.Item> items) {
        if (isFinishing()) {
            return;
        }
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (StorageDiagnostic.Item item : items) {
            View row = inflater.inflate(R.layout.diagnostic_item_view, container, false);
            TextView title = row.findViewById(R.id.tv_diag_item_title);
            TextView status = row.findViewById(R.id.tv_diag_item_status);
            TextView detail = row.findViewById(R.id.tv_diag_item_detail);
            title.setText(item.title);
            detail.setText(item.detail);
            switch (item.level) {
                case StorageDiagnostic.LEVEL_OK:
                    status.setText("正常");
                    status.setTextColor(ContextCompat.getColor(this, R.color.ios_success));
                    break;
                case StorageDiagnostic.LEVEL_WARN:
                    status.setText("注意");
                    status.setTextColor(ContextCompat.getColor(this, R.color.ios_text_secondary));
                    break;
                default:
                    status.setText("异常");
                    status.setTextColor(ContextCompat.getColor(this, R.color.ios_destructive));
                    break;
            }
            container.addView(row);
        }
        if (items.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("没有可检测的存储");
            empty.setTextColor(Color.GRAY);
            container.addView(empty);
        }
        refreshButton.setEnabled(true);
    }
}

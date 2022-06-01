package com.example.firewallminor.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PlusSpan extends ReplacementSpan {

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fontMetricsInt) {
        float padding = paint.measureText("+", 0, 1);
        float textSize = paint.measureText(text, start, end);
        return (int) (padding + textSize);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        canvas.drawText("+" + text.subSequence(start, end).toString(), x, y, paint);
    }
}

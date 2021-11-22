package br.edu.uemg.progv.simplepaint;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;

public class ViewCanvas {
    private Path path;
    private Bitmap bitmap;
    private Canvas canvas;
    private float fixoX,fixoY;
    private int TOLERANCIA_MOVIMENTO = 5;
}

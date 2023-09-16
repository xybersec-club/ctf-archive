package io.flutter.plugin.mouse;

import android.view.PointerIcon;
import androidx.core.view.PointerIconCompat;
import io.flutter.embedding.engine.systemchannels.MouseCursorChannel;
import java.util.HashMap;
/* loaded from: classes.dex */
public class MouseCursorPlugin {
    private static HashMap<String, Integer> systemCursorConstants;
    private final MouseCursorViewDelegate mView;
    private final MouseCursorChannel mouseCursorChannel;

    /* loaded from: classes.dex */
    public interface MouseCursorViewDelegate {
        PointerIcon getSystemPointerIcon(int i);

        void setPointerIcon(PointerIcon pointerIcon);
    }

    public MouseCursorPlugin(MouseCursorViewDelegate view, MouseCursorChannel mouseCursorChannel) {
        this.mView = view;
        this.mouseCursorChannel = mouseCursorChannel;
        mouseCursorChannel.setMethodHandler(new MouseCursorChannel.MouseCursorMethodHandler() { // from class: io.flutter.plugin.mouse.MouseCursorPlugin.1
            @Override // io.flutter.embedding.engine.systemchannels.MouseCursorChannel.MouseCursorMethodHandler
            public void activateSystemCursor(String kind) {
                MouseCursorPlugin.this.mView.setPointerIcon(MouseCursorPlugin.this.resolveSystemCursor(kind));
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public PointerIcon resolveSystemCursor(String kind) {
        if (systemCursorConstants == null) {
            systemCursorConstants = new HashMap<String, Integer>() { // from class: io.flutter.plugin.mouse.MouseCursorPlugin.2
                private static final long serialVersionUID = 1;

                {
                    put("alias", Integer.valueOf((int) PointerIconCompat.TYPE_ALIAS));
                    Integer valueOf = Integer.valueOf((int) PointerIconCompat.TYPE_ALL_SCROLL);
                    put("allScroll", valueOf);
                    put("basic", 1000);
                    put("cell", Integer.valueOf((int) PointerIconCompat.TYPE_CELL));
                    put("click", Integer.valueOf((int) PointerIconCompat.TYPE_HAND));
                    put("contextMenu", Integer.valueOf((int) PointerIconCompat.TYPE_CONTEXT_MENU));
                    put("copy", Integer.valueOf((int) PointerIconCompat.TYPE_COPY));
                    Integer valueOf2 = Integer.valueOf((int) PointerIconCompat.TYPE_NO_DROP);
                    put("forbidden", valueOf2);
                    put("grab", Integer.valueOf((int) PointerIconCompat.TYPE_GRAB));
                    put("grabbing", Integer.valueOf((int) PointerIconCompat.TYPE_GRABBING));
                    put("help", Integer.valueOf((int) PointerIconCompat.TYPE_HELP));
                    put("move", valueOf);
                    put("none", 0);
                    put("noDrop", valueOf2);
                    put("precise", Integer.valueOf((int) PointerIconCompat.TYPE_CROSSHAIR));
                    put("text", Integer.valueOf((int) PointerIconCompat.TYPE_TEXT));
                    Integer valueOf3 = Integer.valueOf((int) PointerIconCompat.TYPE_HORIZONTAL_DOUBLE_ARROW);
                    put("resizeColumn", valueOf3);
                    Integer valueOf4 = Integer.valueOf((int) PointerIconCompat.TYPE_VERTICAL_DOUBLE_ARROW);
                    put("resizeDown", valueOf4);
                    Integer valueOf5 = Integer.valueOf((int) PointerIconCompat.TYPE_TOP_RIGHT_DIAGONAL_DOUBLE_ARROW);
                    put("resizeUpLeft", valueOf5);
                    Integer valueOf6 = Integer.valueOf((int) PointerIconCompat.TYPE_TOP_LEFT_DIAGONAL_DOUBLE_ARROW);
                    put("resizeDownRight", valueOf6);
                    put("resizeLeft", valueOf3);
                    put("resizeLeftRight", valueOf3);
                    put("resizeRight", valueOf3);
                    put("resizeRow", valueOf4);
                    put("resizeUp", valueOf4);
                    put("resizeUpDown", valueOf4);
                    put("resizeUpLeft", valueOf6);
                    put("resizeUpRight", valueOf5);
                    put("resizeUpLeftDownRight", valueOf6);
                    put("resizeUpRightDownLeft", valueOf5);
                    put("verticalText", Integer.valueOf((int) PointerIconCompat.TYPE_VERTICAL_TEXT));
                    put("wait", Integer.valueOf((int) PointerIconCompat.TYPE_WAIT));
                    put("zoomIn", Integer.valueOf((int) PointerIconCompat.TYPE_ZOOM_IN));
                    put("zoomOut", Integer.valueOf((int) PointerIconCompat.TYPE_ZOOM_OUT));
                }
            };
        }
        int cursorConstant = systemCursorConstants.getOrDefault(kind, 1000).intValue();
        return this.mView.getSystemPointerIcon(cursorConstant);
    }

    public void destroy() {
        this.mouseCursorChannel.setMethodHandler(null);
    }
}

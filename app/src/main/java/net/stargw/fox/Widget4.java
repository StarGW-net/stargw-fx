package net.stargw.fox;


public class Widget4 extends WidgetProvider {

    @Override
    public int getView() {
        Global.Log("Widget Layout 4 (" + R.layout.widget4_layout + ")",3);
        return R.layout.widget4_layout;
    }

    @Override
    public int getWidgetLayout() {
        return 4;
    }
}

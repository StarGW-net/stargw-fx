package net.stargw.fx;


public class Widget1 extends WidgetProvider {

    @Override
    public int getView() {
        // Global.Log("Widget Layout 1 (" + R.layout.widget1_layout + ")",3);
        return R.layout.widget1_layout;
    }

    @Override
    public int getWidgetLayout() {
        return 1;
    }
}

package net.stargw.fx;


public class Widget2 extends WidgetProvider {

    @Override
    public int getView() {
        Global.Log("Widget Layout 2 (" + R.layout.widget2_layout + ")",3);
        return R.layout.widget2_layout;
    }

    @Override
    public int getWidgetLayout() {
        return 2;
    }
}
